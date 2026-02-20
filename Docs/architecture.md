# Architecture - MetaMoteur

> Document de référence technique generé le 2026-02-20.  
> Version du projet : 0.0.1-SNAPSHOT — Java 11 — Maven

---

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Structure du projet](#2-structure-du-projet)
3. [Modules et classes](#3-modules-et-classes)
4. [Modèle de données](#4-modèle-de-données)
5. [Flux principaux](#5-flux-principaux)
6. [Architecture réseau](#6-architecture-réseau)
7. [Configuration](#7-configuration)
8. [Build, tests et CI/CD](#8-build-tests-et-cicd)
9. [Décisions techniques et dette connue](#9-décisions-techniques-et-dette-connue)

---

## 1. Vue d'ensemble

MetaMoteur est un **méta-moteur de recherche coopératif** :  
il agrège les résultats de moteurs de recherche externes (Google, Altavista…),  
les fusionne avec ceux d'agents pairs, et les **re-classe** en s'appuyant sur  
l'historique communautaire stocké en base de données.

```
Navigateur
    |  HTTP (port 12000 par défaut)
    v
[Agent / Serveur HTTP maison]
    |
    +-- GET fichier statique  -->  www/ (HTML, images)
    |
    +-- POST /rechercheServeur.html
    |       |
    |       +-- [Client] --> Moteur externe (Google GET) --> [ParsingGoogle]
    |       |                                                       |
    |       |                                              Enregistrement (liens)
    |       |
    |       +-- [Client] --> Agents pairs (POST XML SAX)
    |       |
    |       +-- [Permutations] : re-ordonnancement local via historique BDD
    |       +-- [GestionBDD]   : INSERT résultats
    |       +-- [Tri]          : fusion multi-agents + classement final
    |       +-- [ReponseHTTP]  : génération XHTML --> navigateur
    |
    +-- GET /redirect.html  --> HTTP 302 + [GestionBDD] updateURL (compteur clics)
```

**Principe de l'amélioration communautaire :**  
Chaque recherche insère un enregistrement en base.  
Les prochaines recherches similaires consultent cet historique pour ajuster les  
rangs via un algorithme de vote (rang actuel vs rang historique).  
Plus une URL est cliquée, plus son rang est promu.

---

## 2. Structure du projet

```
metamoteur/
├── pom.xml                        Build Maven (Java 11)
├── metaMoteur.conf                Configuration applicative principale
├── MetaMoteur.log                 Fichier de logs (généré à l'exécution)
├── COPYING                        Licence GNU GPL v2
├── azure-pipelines.yml            Pipeline CI Azure DevOps
├── .travis.yml                    Pipeline CI Travis CI (legacy)
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── agent/             Noyau applicatif, configuration, modèle
│   │   │   ├── bdd/               Couche d'accès aux données (JDBC)
│   │   │   ├── client/            Client HTTP sortant
│   │   │   ├── moteur/            Parsing des résultats moteurs externes
│   │   │   ├── serveur/           Serveur HTTP et traitement des requêtes
│   │   │   └── tri/               Algorithmes de permutation et classement
│   │   └── resources/
│   │       ├── bdd/               Scripts DDL SQL
│   │       └── www/               Pages HTML statiques (interface utilisateur)
│   └── test/
│       └── java/                  Tests JUnit 4 (miroir des packages sources)
│
├── db/                            Fichiers HSQLDB persistants
│   ├── metamoteur.properties
│   └── metamoteur.script
│
├── Docs/                          Documentation projet
│   ├── DocTechnique.odt           Document technique original (2005-2006)
│   ├── design/                    Maquettes UI (apropos, index, recherche)
│   ├── Rendus/                    Livrables académiques
│   └── Fournis/                   Documents de référence fournis
│
└── target/                        Artefacts Maven (généré)
    ├── classes/www/               Pages web déployées
    └── surefire-reports/          Rapports tests JUnit
```

---

## 3. Modules et classes

### 3.1 Package `agent` — Noyau applicatif

Point d'entrée de l'application et modèle de domaine.

| Classe | Type | Rôle |
|---|---|---|
| `Agent` | Classe principale | `main()` : charge la config, lance le serveur |
| `AppConfig` | Valeur immuable (Builder) | Contient toutes les propriétés de configuration |
| `AppConfigLoader` | Utilitaire statique | Lit `metaMoteur.conf` → construit `AppConfig` |
| `ConfigParser` | Classe legacy | Lecture/écriture Properties (partiellement utilisée) |
| `GestionMessage` | Utilitaire de log | Niveaux 0=MES, 1=ERR, 2=WAR → fichier et/ou stdout |
| `Enregistrement` | Modèle de données | Un résultat de recherche : (id, mots-clés, `List<Lien>`) |
| `EnregistrementHandler` | Handler SAX | Désérialise un `Enregistrement` depuis XML agent-pair |
| `Lien` | Modèle de données | Un résultat individuel : (URL, rang, titre, description) |

**Détail `AppConfig` :** objet final construit via le pattern Builder.  
Tous les champs sont en lecture seule après construction. Les tableaux (`moteurs`,  
`contacts`) font l'objet de copies défensives à la construction et aux accès.

**Détail `GestionMessage` :**

```
message(niveau, module, texte)

  niveau  objet du message
  ------  ----------------------------
    0     MES  (informatif)
    1     ERR  (erreur)
    2     WAR  (avertissement)

  Sortie (config)  cible
  ---------------  ---------------------
    0              aucune sortie
    1              fichier log uniquement
    2              stdout uniquement
    3 (défaut)     fichier log + stdout
```

---

### 3.2 Package `serveur` — Serveur HTTP

Serveur TCP/HTTP artisanal sans framework tiers.

| Classe | Rôle |
|---|---|
| `Serveur` | `ServerSocket` + sémaphore (`nbThread`), boucle d'acceptation des connexions |
| `TraitementRequete` | `Runnable` — routage, orchestration du traitement complet d'une requête |
| `RequeteHTTP` | Parse la requête HTTP brute (type GET/POST, chemin, options, corps) |
| `ReponseHTTP` | Construit et envoie les réponses HTTP (HTML dynamique, fichiers, 304, 404, 302) |
| `OptionRequeteHTTP` | Représente un en-tête HTTP de requête (clé/valeur) |
| `OptionReponseHTTP` | Représente un en-tête HTTP de réponse (clé/valeur) |
| `Fichier` | Abstraction d'un fichier servi depuis le répertoire `Path` configuré |
| `Constantes` | Constantes partagées : `GET=0`, `POST=1`, `TEXTE=0`, `IMAGE=1` |

**Modèle de threading de `Serveur` :**

```
Serveur.service()
    |
    |  ServerSocket.accept() (boucle infinie)
    |        |
    |        v  [nouvelle connexion]
    |  sémaphore.acquire()   <-- bloque si nbThread actifs atteint
    |        |
    |        v
    |  new Thread(new TraitementRequete(socket)).start()
    |
    |  [fin de TraitementRequete]
    |  Serveur.supprClient()  -->  sémaphore.release()
```

---

### 3.3 Package `bdd` — Couche données

| Classe | Rôle |
|---|---|
| `GestionBDD` | Classe utilitaire statique ; toutes les opérations JDBC |

**Méthodes principales de `GestionBDD` :**

| Méthode | Description |
|---|---|
| `connectionBDD()` | Ouvre une connexion JDBC (HSQL ou MySQL selon config) |
| `insertEnregistrement(Enregistrement)` | Insère un enregistrement (jusqu'à 20 liens) |
| `updateURL(String url)` | Incrémente le compteur de clics (`SELECT_n`) pour une URL |
| `getAllEnregistrements()` | Retourne tous les enregistrements de la table BDD |
| `getEnregistrementByKeyword(String)` | Filtre par mots-clés |

Supports BDD :
- **HSQLDB** (défaut) : fichier `db/metamoteur` ; table `BDD` créée automatiquement au premier démarrage
- **MySQL** : nécessite une base existante avec le schéma fourni

---

### 3.4 Package `moteur` — Intégration moteurs externes

| Classe | Rôle |
|---|---|
| `ParsingGoogle` | Parse le HTML retourné par Google pour extraire URLs, titres et descriptions |

`ParsingGoogle` effectue une analyse lexicale du HTML brut de la page de résultats  
Google via des expressions régulières et/ou recherche de balises. Cette approche  
est fragile et dépendante du rendu HTML de Google (conçu pour le format 2005).

---

### 3.5 Package `tri` — Algorithmes de classement

Implémente le système coopératif de re-classement des résultats.

| Classe | Rôle |
|---|---|
| `Permutations` | Re-ordonnancement local : compare rang actuel (MR) vs rang historique (BDD) |
| `Tri` | Fusion multi-agents : agrège les réponses des agents pairs et produit le classement final |
| `Document` | Enveloppe `(idDoc, Lien)` utilisée par l'algorithme de permutation |

**Algorithme de `Permutations` :**

```
Pour chaque paire (d_i, d_k) de documents :
  vote = ((rang_i_MR - rang_k_MR) + (rang_i_BDD - rang_k_BDD)) / 2
  si vote < 0  --> insérer d_i avant d_k
  si vote = 0  --> insérer d_i immédiatement après d_k
  si vote > 0  --> conserver l'ordre actuel
```

Valeurs de seuil de similarité entre mots-clés et descriptions (`CoefSim = 0.7`  
par défaut) : détermine si un enregistrement historique est "pertinent" pour  
la recherche en cours.

**Algorithme de `Tri` :**

```
1. simMotsCles(keywords)        : cherche les enregistrements a mots-clés similaires
2. simCasSources(enrMR, simMC)  : retient les cas sources pertinents
3. associationRangCasSrc_MR     : calcule un rang moyen (ou minimum) associé
4. permutationDocuments         : applique le vote sur toutes les paires
5. Fusion avec réponses agents pairs (rang pondéré par CoefPonderation)
```

---

### 3.6 Package `client` — Client HTTP sortant

| Classe | Rôle |
|---|---|
| `Client` | Utilitaire statique : envoi de requêtes HTTP GET et POST |

| Méthode | Implémentation | Usage |
|---|---|---|
| `envoiRequeteGET(url)` | `HttpURLConnection` | Interrogation des moteurs externes |
| `envoiRequetePOST(host, path, body, port)` | Raw `Socket` + HTTP/1.0 artisanal | Communication inter-agents |

---

## 4. Modèle de données

### 4.1 Schéma de la table BDD (HSQLDB / MySQL)

La table `BDD` stocke jusqu'à **20 liens** par enregistrement sous forme dénormalisée  
(colonnes répétitives `URLn`, `TITLEn`, `DESCn`, `RANKn`, `SELECTn`).

```sql
CREATE TABLE BDD (
  UID        INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  KEYWORDS   VARCHAR(255),        -- mots-clés de la recherche

  URL1       VARCHAR(255),        -- URL du résultat 1
  TITLE1     VARCHAR(255),        -- Titre du résultat 1
  DESC1      VARCHAR(1000),       -- Description du résultat 1
  RANK1      INT,                 -- Rang retourné par le moteur
  SELECT1    INT,                 -- Compteur de clics sur ce lien

  -- ... colonnes URL2..URL20, TITLE2..TITLE20,
  --         DESC2..DESC20, RANK2..RANK20, SELECT2..SELECT20

  TIMEQUERY  VARCHAR(20)          -- Horodatage de la requête
);
```

### 4.2 Modèle objet

```
Enregistrement
  ├── id          : int
  ├── keywords    : String
  └── liens       : Vector<Lien>
                        ├── url   : String
                        ├── rang  : int
                        ├── titre : String
                        └── desc  : String

Document  (utilisé uniquement par le module tri)
  ├── idDoc  : int      (index dans le vecteur de résultats)
  └── lienDoc : Lien
```

---

## 5. Flux principaux

### 5.1 Flux de recherche (requête utilisateur)

```
Navigateur
  | POST /rechercheServeur.html  corps: keywords=<mots>
  v
TraitementRequete.run()
  |
  +-- 1. Parse mots-clés depuis RequeteHTTP.getSuiteRequete()
  |
  +-- 2. Pour chaque moteur configuré (ex: Google) :
  |       Client.envoiRequeteGET(urlMoteur + mots)
  |         --> HTML brut
  |       ParsingGoogle.parse(html)
  |         --> Enregistrement enr  (20 liens max)
  |
  +-- 3. Pour chaque agent pair configuré (Contacts) :
  |       Client.envoiRequetePOST(host, "/rechercheAgent.html", enr.toXml(), port)
  |         --> XML
  |       SAXParser + EnregistrementHandler
  |         --> Enregistrement reponse_agent
  |       reponses.add(reponse_agent)
  |
  +-- 4. Permutations.lancementPermutations(enr)
  |       GestionBDD.simMotsCles(keywords)       --> cas sources similaires
  |       GestionBDD.simCasSources(enr, simMC)   --> filtrage
  |       vote(rang_MR, rang_BDD) pour chaque paire
  |         --> Enregistrement enr_permute
  |
  +-- 5. GestionBDD.insertEnregistrement(enr_permute)
  |
  +-- 6. Tri.lancementTri(enr_permute, reponses)
  |         --> Enregistrement enr_trie
  |
  +-- 7. ReponseHTTP.debutReponseHTML()
  |       + enr_trie.toXhtml(avecReponsesAgents)
  |         --> page XHTML
  v
Navigateur reçoit les résultats
```

### 5.2 Flux de clic sur un lien (redirection)

```
Navigateur
  | GET /redirect.html?<url_cible>
  v
TraitementRequete.run()
  |
  +-- GestionBDD.updateURL(url_cible)   -- incrémente SELECT_n en BDD
  +-- ReponseHTTP.redirection(sortie, url_cible)
  |     --> HTTP 302 Location: <url_cible>
  v
Navigateur redirigé vers la ressource
```

### 5.3 Flux de communication inter-agents

```
Agent A                                    Agent B
   |                                           |
   |  POST /rechercheAgent.html               |
   |  Content-Type: text/xml                  |
   |  Corps: <enregistrement>...</enregistrement>
   |----------------------------------------->|
   |                                           |
   |                          TraitementRequete.run() sur B
   |                          --> Permutations locales de B
   |                          --> B repond avec son enregistrement permuté
   |                                           |
   |  HTTP 200 + XML enregistrement permuté   |
   |<-----------------------------------------|
   |
   +-- SAXParser + EnregistrementHandler --> Enregistrement reponse_B
   +-- ajout dans reponses[] pour le Tri final
```

### 5.4 Flux de démarrage

```
java -jar MetaMoteur.jar
  |
  Agent.main(args)
    |
    +-- AppConfigLoader.load("metaMoteur.conf")
    |     lecture Properties --> AppConfig (immuable)
    |     Agent.CONFIG = appConfig
    |
    +-- Serveur.service()
          |
          ServerSocket(portServeur)
          boucle d'acceptation...
```

---

## 6. Architecture réseau

```
                  Internet
                     |
          +----------+-----------+
          |                      |
     [Google / autre]     [Agent pair 2]
          |                      |
          | HTTP GET             | HTTP POST (XML)
          |                      |
     +----+----------------------+----+
     |                                |
     |       Agent MetaMoteur         |
     |    (Serveur HTTP port 12000)   |
     |                                |
     |  +----------+  +----------+   |
     |  | Serveur  |  | GestionBDD|  |
     |  | HTTP     |  | HSQLDB/   |  |
     |  | (threads)|  | MySQL     |  |
     |  +----------+  +----------+   |
     |                                |
     +--------------------------------+
          |
          | HTTP (port 12000)
          |
     [Navigateur utilisateur]
```

Un réseau d'agents MetaMoteur peut être formé en renseignant les adresses  
des autres agents dans la clé `Contacts` du fichier de configuration. Chaque  
agent interroge ses pairs lors de chaque recherche et fusionne leurs réponses.

---

## 7. Configuration

Fichier : `metaMoteur.conf` (format Java Properties), situé à la racine du projet.

### 7.1 Référence complète des propriétés

| Propriété | Type | Défaut | Description |
|---|---|---|---|
| `HostBDD` | String | `localhost` | Adresse (et port) du serveur BDD |
| `UserBDD` | String | — | Identifiant BDD |
| `PassBDD` | String | — | Mot de passe BDD |
| `BaseBDD` | String | — | Nom de la base de données (ou fichier HSQL) |
| `TypeBDD` | String | `HSQL` | `HSQL` ou `MySQL` |
| `TableBDD` | String | `BDD` | Nom de la table |
| `Sortie` | int | `3` | 0=muet, 1=fichier, 2=stdout, 3=les deux |
| `Debug` | int | `3` | 0=aucun, 1=ERR, 2=ERR+WAR, 3=ERR+WAR+MES |
| `FichierLog` | String | `metaMoteur.log` | Chemin du fichier de log |
| `PromLocale` | int | `2` | Facteur de promotion des liens locaux |
| `CoefPonderation` | int | `1` | Coefficient de pondération des contacts |
| `CoefSim` | double | `0.7` | Seuil de similarité (0.0–1.0) |
| `PortServeur` | int | `8080` | Port d'écoute HTTP |
| `NbThread` | int | `5` | Nombre de threads simultanés max |
| `PageIndex` | String | `index.html` | Page d'accueil |
| `Path` | String | `/www/` | Répertoire racine des fichiers statiques |
| `Moteurs` | String | — | Liste `nom url` séparés par espaces |
| `Contacts` | String | — | Liste `host:port` séparés par espaces |

### 7.2 Exemples de configuration

**Configuration minimale (dev local) :**
```properties
TypeBDD=HSQL
BaseBDD=metamoteur
UserBDD=SA
PassBDD=SA
PortServeur=12000
Path=target/classes/www/
Moteurs=Google http://www.google.fr/search?num=20&hl=fr&q=
```

**Configuration multi-agents :**
```properties
Contacts=192.168.1.10:12000 192.168.1.11:12000
```

---

## 8. Build, tests et CI/CD

### 8.1 Commandes Maven

| Commande | Action |
|---|---|
| `mvn compile` | Compilation Java 11 |
| `mvn test` | Exécution des tests JUnit 4 + rapport JaCoCo |
| `mvn package` | Génère `target/MetaMoteur-0.0.1-SNAPSHOT.jar` (fat JAR) |
| `mvn exec:java` | Lance l'application depuis Maven (répertoire de travail = racine) |
| `mvn site` | Génère le site Maven (Javadoc, rapports) |
| `java -jar target/MetaMoteur-*.jar` | Exécution directe du JAR |

### 8.2 Couverture de tests

Tous les packages ont au minimum un fichier de test JUnit 4 :

| Package | Classe de test |
|---|---|
| `agent` | `TestAppConfig`, `TestEnregistrement`, `TestEnregistrementHandler`, `TestEnregistrementStatiques`, `TestGestionMessage`, `TestLien` |
| `bdd` | `TestBDD`, `TestGestionBDDSQL` |
| `client` | `TestClient` |
| `moteur` | `TestParsingGoogle` |
| `serveur` | `TestOptionReponseHTTP`, `TestOptionRequeteHTTP`, `TestRequeteHTTP`, `TestServeur` |
| `tri` | `TestDocument`, `TestPermutations`, `TestTri` |

La couverture JaCoCo est configurée dans `pom.xml` (phase `prepare-package`).  
Le rapport est généré dans `target/site/jacoco/`.

### 8.3 CI/CD

Deux pipelines configurés (historique) :

| Pipeline | Fichier | Statut |
|---|---|---|
| Azure DevOps | `azure-pipelines.yml` | Actif |
| Travis CI | `.travis.yml` | Legacy (dupliqué) |

Les deux exécutent `mvn test`. Seul Azure DevOps est à maintenir.

---

## 9. Décisions techniques et dette connue

### 9.1 Décisions d'architecture

| Décision | Justification |
|---|---|
| Serveur HTTP artisanal (pas de Tomcat/Jetty) | Projet pédagogique 2005, objectif de comprendre le protocole HTTP |
| HSQLDB embarqué par défaut | Zéro dépendance infrastructure pour démarrer |
| Algorithme de vote par paires | Basé sur la recherche académique RK (Rank Aggregation) |
| Communication inter-agents en XML SAX | Légèreté, pas de dépendance JAXB en 2005 |
| `AppConfig` immuable + Builder | Refactoring 2024/2025 : remplacement des anciens champs statiques mutables |

### 9.2 Dette technique connue

| Priorité | Problème | Localisation | Impact |
|---|---|---|---|
| Critique | `ParsingGoogle` parse le HTML Google 2005 (cassé aujourd'hui) | `moteur/ParsingGoogle.java` | Aucun résultat réel en production |
| Critique | Etat statique mutable (`enrMR_liensPermutes`, `enrMR_TriFinal`) | `tri/Permutations.java`, `tri/Tri.java` | Race condition sous multi-thread |
| Haut | `Vector` raw sans génériques | Tout le module `tri`, `agent` | Casts non sûrs, API Java 1.1 |
| Haut | `Client.envoiRequetePOST` : raw `Socket` HTTP/1.0 artisanal | `client/Client.java` | Fragile, non maintenable |
| Haut | `GestionBDD` : une connexion ouverte/fermée par opération (pas de pool) | `bdd/GestionBDD.java` | Performance, scalabilité |
| Moyen | `ConfigParser` legacy redondant avec `AppConfigLoader` | `agent/ConfigParser.java` | Confusion, risque de divergence |
| Moyen | Schéma BDD dénormalisé (colonnes URL1..URL20) | `bdd/GestionBDD.java` | Limite à 20 résultats, non extensible |
| Moyen | JUnit 4 au lieu de JUnit 5 | `src/test/` | Syntaxe obsolète |
| Bas | Deux pipelines CI (Azure + Travis) | `.travis.yml` | Maintenance double inutile |
| Bas | `System.out.println` résiduels contournant `GestionMessage` | `bdd/GestionBDD.java` | Incohérence du log |
