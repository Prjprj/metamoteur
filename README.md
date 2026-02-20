# MetaMoteur

> Méta-moteur de recherche orienté communauté — projet étudiant 2005/2006, modernisé en 2025/2026.

[![Build Status](https://dev.azure.com/Prjprj/metamoteur/_apis/build/status/Prjprj.metamoteur?branchName=master)](https://dev.azure.com/Prjprj/metamoteur/_build)

MetaMoteur est un **méta-moteur de recherche pair-à-pair** écrit en Java. Il agrège les résultats de plusieurs moteurs de recherche (Google, Altavista, …), les stocke dans une base de données locale, les trie par pertinence via un algorithme CBR, et les expose via un serveur HTTP embarqué. Plusieurs instances peuvent se fédérer en réseau pour partager leurs résultats.

---

## Table des matières

1. [Architecture](#architecture)
2. [Prérequis](#prérequis)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Base de données](#base-de-données)
6. [Build et lancement](#build-et-lancement)
7. [Tests](#tests)
8. [Structure du projet](#structure-du-projet)
9. [Description des modules](#description-des-modules)
10. [Licence](#licence)

---

## Architecture

```
Navigateur
    │  HTTP (port configurable, défaut 12000)
    ▼
┌─────────────────────────────────────────────┐
│               Serveur HTTP embarqué          │
│   ServerSocket → ExecutorService (pool)      │
│            TraitementRequete (Runnable)       │
└──────┬──────────────────────────┬────────────┘
       │ recherche                │ fichier statique
       ▼                          ▼
┌─────────────┐           src/main/resources/www/
│  Module Tri  │           (HTML/CSS/JS servis tel quel)
│  (CBR + pond)│
└──────┬───────┘
       │
   ┌───┴────────────────────────────────────────┐
   │                                            │
   ▼                                            ▼
Agents pairs                           Moteur de recherche
(contacts P2P)                         (ex. Google via HTTP GET)
       │                                        │
       └───────────────┬────────────────────────┘
                       ▼
              ┌──────────────────┐
              │  GestionBDD      │
              │  MySQL ou HSQLDB │
              └──────────────────┘
```

**Flux d'une requête de recherche :**

1. L'utilisateur soumet un mot-clé via l'interface web.
2. `TraitementRequete` reçoit la requête HTTP POST.
3. Si les résultats existent en base (`GestionBDD`), ils sont retournés directement.
4. Sinon, `ParsingGoogle` interroge les moteurs configurés et `GestionMessage` agrège les contacts P2P.
5. `Tri` combine et pondère les résultats (promotion locale, CBR, coefficient de similarité).
6. Les résultats sont insérés en base pour les requêtes futures.
7. La réponse HTTP est construite et renvoyée au navigateur.

---

## Prérequis

| Outil | Version minimale |
|---|---|
| Java JDK | 11 |
| Maven | 3.6 |
| Base de données | MySQL 5.x / 8.x **ou** HSQLDB 2.7 (embarqué, aucune installation requise) |

---

## Installation

```bash
# Cloner le dépôt
git clone https://github.com/Prjprj/metamoteur.git
cd metamoteur/metamoteur

# Compiler et packager
mvn package -DskipTests
```

---

## Configuration

Toute la configuration se trouve dans le fichier **`metaMoteur.conf`** à la racine du projet.  
Les valeurs par défaut s'appliquent si un paramètre est laissé vide.

### Base de données

```properties
# Hôte et port MySQL  (ex: localhost:3306)
HostBDD=localhost
UserBDD=metamoteur
PassBDD=motdepasse
BaseBDD=metamoteur
TypeBDD=MySQL        # MySQL | HSQL  (défaut: MySQL)
TableBDD=BDD
```

> **Mode HSQLDB (sans installation) :** mettre `TypeBDD=HSQL`. La base est créée automatiquement en mémoire — idéal pour les tests.

### Journalisation

```properties
Sortie=3             # 0=aucune | 1=fichier | 2=console | 3=les deux (défaut)
Debug=3              # 0=aucun  | 1=ERR only | 2=ERR+WAR | 3=tous (défaut)
FichierLog=MetaMoteur.log
```

### Serveur HTTP

```properties
PortServeur=12000    # Port d'écoute (défaut: 8080)
NbThread=5           # Taille du pool de threads (défaut: 5)
PageIndex=index.html # Page d'accueil (défaut: index.html)
Path=target/classes/www/   # Répertoire des fichiers statiques
```

### Tri et pondération

```properties
PromLocale=2         # Bonus multiplicatif pour les résultats locaux (défaut: 2)
CoefPonderation=1    # Poids des résultats des agents pairs (défaut: 1)
CoefSim=0.7          # Seuil de similarité CBR (défaut: 0.7)
```

### Réseau P2P et moteurs

```properties
# Plusieurs contacts séparés par des espaces
Contacts=192.168.1.10:12000 192.168.1.11:12000

# Plusieurs moteurs séparés par des espaces (paires nom url)
Moteurs=Google http://www.google.fr/search?num=20&hl=fr&q=
```

---

## Base de données

### MySQL

Créer la base et la table avec le script fourni :

```bash
mysql -u root -p < src/main/resources/bdd/bdd.sql
```

Puis renseigner les paramètres `HostBDD`, `UserBDD`, `PassBDD`, `BaseBDD` dans `metaMoteur.conf`.

### HSQLDB (embarqué)

Aucune action requise. Positionner `TypeBDD=HSQL` dans `metaMoteur.conf` — la base est initialisée automatiquement au démarrage.

### Schéma

La table principale stocke jusqu'à **20 résultats** par requête :

| Colonne | Type | Description |
|---|---|---|
| `Uid` | INT AUTO_INCREMENT | Identifiant unique |
| `Keywords` | VARCHAR(255) | Mots-clés de la recherche |
| `Url1`…`Url20` | VARCHAR(255) | URLs des résultats |
| `Title1`…`Title20` | VARCHAR(255) | Titres des résultats |
| `Desc1`…`Desc20` | TEXT | Descriptions des résultats |
| `Rank1`…`Rank20` | TINYINT | Rang initial |
| `Select1`…`Select20` | TINYINT | Nombre de sélections (CBR) |

---

## Build et lancement

### Compilation

```bash
mvn compile
```

### Lancement

```bash
# Via Maven (profil exec)
mvn exec:java -Dexec.mainClass="agent.Agent"

# Ou directement après package
java -cp target/MetaMoteur-0.0.1-SNAPSHOT.jar agent.Agent
```

L'interface web est accessible sur `http://localhost:<PortServeur>/` (par défaut : `http://localhost:12000/`).

### Génération de la Javadoc

```bash
mvn javadoc:javadoc
# Résultat dans target/site/apidocs/
```

### Rapport de couverture (JaCoCo)

```bash
mvn verify
# Rapport dans target/site/jacoco/
```

---

## Tests

Les tests unitaires utilisent **JUnit 4** et **HSQLDB** (base en mémoire, aucune connexion MySQL requise).

```bash
# Lancer tous les tests
mvn test

# Lancer un test spécifique
mvn test -Dtest=TestBDD
```

| Classe de test | Module testé |
|---|---|
| `TestBDD` | `GestionBDD` — opérations JDBC |
| `TestServeur` | `Serveur` — démarrage/arrêt |
| `TestClient` | `Client` — requêtes HTTP GET/POST |
| `TestTri` | `Tri` — algorithme de tri |
| `TestPermutations` | `Permutations` — CBR |
| `TestEnregistrement` | `Enregistrement` — modèle de données |
| `TestEnregistrementHandler` | `EnregistrementHandler` — sérialisation |
| `TestGestionMessage` | `GestionMessage` — journalisation |
| `TestParsingGoogle` | `ParsingGoogle` — parsing HTML |

---

## Structure du projet

```
metamoteur/
├── metaMoteur.conf              # Fichier de configuration principal
├── pom.xml                      # Descripteur Maven
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── agent/           # Point d'entrée, configuration, messagerie
│   │   │   │   ├── Agent.java               # main() — point d'entrée
│   │   │   │   ├── AppConfig.java           # Configuration immuable
│   │   │   │   ├── AppConfigLoader.java     # Chargement de metaMoteur.conf
│   │   │   │   ├── ConfigParser.java        # Parseur du fichier .conf
│   │   │   │   ├── Enregistrement.java      # Modèle : ensemble de résultats
│   │   │   │   ├── EnregistrementHandler.java # Sérialisation des résultats
│   │   │   │   ├── GestionMessage.java      # Système de journalisation
│   │   │   │   └── Lien.java                # Modèle : un lien (url+titre+desc)
│   │   │   ├── bdd/             # Couche d'accès aux données (JDBC)
│   │   │   │   └── GestionBDD.java
│   │   │   ├── client/          # Client HTTP sortant
│   │   │   │   └── Client.java
│   │   │   ├── moteur/          # Parsing des résultats des moteurs
│   │   │   │   └── ParsingGoogle.java
│   │   │   ├── serveur/         # Serveur HTTP embarqué
│   │   │   │   ├── Serveur.java             # ServerSocket + pool de threads
│   │   │   │   ├── TraitementRequete.java   # Traitement d'une requête HTTP
│   │   │   │   ├── RequeteHTTP.java         # Parsing de la requête entrante
│   │   │   │   ├── ReponseHTTP.java         # Construction de la réponse
│   │   │   │   ├── Fichier.java             # Service de fichiers statiques
│   │   │   │   ├── Constantes.java          # Codes HTTP, types MIME
│   │   │   │   ├── OptionRequeteHTTP.java   # Paramètres de requête
│   │   │   │   └── OptionReponseHTTP.java   # Options de réponse
│   │   │   └── tri/             # Algorithme de tri et CBR
│   │   │       ├── Tri.java                 # Tri pondéré des résultats
│   │   │       ├── Document.java            # Modèle : document pour CBR
│   │   │       └── Permutations.java        # Algorithme CBR (similarité)
│   │   └── resources/
│   │       ├── bdd/
│   │       │   ├── bdd.sql                  # Schéma MySQL
│   │       │   └── bdd2.sql
│   │       └── www/                         # Interface web statique
│   │           ├── index.html
│   │           ├── aide.html
│   │           ├── apropos.html
│   │           ├── licence.html
│   │           ├── images/
│   │           ├── script/clicTous.js
│   │           └── style/default.css
│   └── test/
│       └── java/                            # Tests unitaires JUnit 4
└── Docs/                                    # Documents de conception
    └── design/
```

---

## Description des modules

### `agent` — Cœur applicatif

| Classe | Rôle |
|---|---|
| `Agent` | Point d'entrée (`main`). Charge la configuration, démarre le serveur. |
| `AppConfig` | POJO immuable contenant tous les paramètres de configuration. |
| `AppConfigLoader` | Lit `metaMoteur.conf` via `ConfigParser` et construit un `AppConfig`. |
| `GestionMessage` | Journalisation multi-niveaux (ERR/WAR/MES) vers console et/ou fichier. |
| `Enregistrement` | Représente un ensemble de résultats pour un mot-clé donné. |
| `Lien` | Modèle d'un résultat individuel (URL, titre, description, rang). |

### `serveur` — Serveur HTTP embarqué

| Classe | Rôle |
|---|---|
| `Serveur` | Ouvre un `ServerSocket`, dispatche les connexions via un `ExecutorService`. |
| `TraitementRequete` | `Runnable` traitant une connexion : sert des fichiers, lance des recherches, gère le P2P. |
| `RequeteHTTP` | Parse la requête HTTP brute (méthode, chemin, paramètres). |
| `ReponseHTTP` | Construit et sérialise la réponse HTTP. |
| `Fichier` | Lit et sert les fichiers statiques du répertoire `www/`. |

### `bdd` — Persistance

| Classe | Rôle |
|---|---|
| `GestionBDD` | Toutes les opérations JDBC : connexion, SELECT, INSERT (`PreparedStatement`), UPDATE, test de connexion. Supporte MySQL et HSQLDB. |

### `client` — Client HTTP sortant

| Classe | Rôle |
|---|---|
| `Client` | Envoie des requêtes GET (moteurs de recherche) et POST (agents pairs) via `HttpURLConnection`. |

### `moteur` — Parsing

| Classe | Rôle |
|---|---|
| `ParsingGoogle` | Parse le HTML retourné par un moteur de recherche pour en extraire les liens, titres et descriptions. |

### `tri` — Algorithme de tri

| Classe | Rôle |
|---|---|
| `Tri` | Fusionne les résultats locaux et distants, applique les coefficients de pondération et la promotion locale. |
| `Permutations` | Algorithme CBR (*Case-Based Reasoning*) : réordonne les résultats selon la similarité avec les requêtes passées. |
| `Document` | Représentation vectorielle d'un document pour le calcul de similarité. |

---

## Capture d'écran

![Interface MetaMoteur](Docs/CaptureMetaMoteur.PNG)

---

## Auteurs

- Jeremy Frechard
- Cécile Girard
- Aysel Gunes
- Pierre Ramos

---

## Licence

Ce projet est distribué sous licence **GNU General Public License v2** — voir le fichier [COPYING](COPYING) pour le texte complet.
