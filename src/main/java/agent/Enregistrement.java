package agent;

/*
 * This file is part of "Méta-moteur".
 *
 * (c) Méta-moteur 2005-2006. All Rights Reserved.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */

/**
 * Titre       : classe publique Enregistrement
 * Description : classe permettant de recuperer les differents champs
 * d'un enregistrement de la base de donnees.
 *
 * @author Jeremy FRECHARD
 * @author C�cile GIRARD
 * @author Aysel GUNES
 * @author Pierre RAMOS
 * @version 1.0
 */

import java.util.Vector;

import bdd.GestionBDD;

public class Enregistrement {

    /*
     * Constante entiere pemettant d'indiquer le nombre de liens contenus dans un
     * enregistrement.
     */
    private final static int NB_LIENS = 20;

    /*
     * Entier representant l'identifiant d'un enregistrement.
     */
    private int id;

    /*
     * Chaine de caracteres representant les mots cles associes a un enregistrement.
     */
    private String motsCles;

    /*
     * Vecteur de Lien representant l'ensemble des liens associes a un
     * enregistrement, en sachant qu'un lien correspond a une url, un titre et une
     * description et un rang.
     */
    private Vector liens;

    /**
     * Constructeur par defaut. Initialisation de: - l'identifiant a 0, - des mots
     * cles a une chaine de caracteres vide, - un vecteur de liens a la valeur par
     * defaut.
     */
    public Enregistrement() {
        this.id = 0;
        this.motsCles = "";
        this.liens = new Vector();
    }

    /**
     * Constructeur permettant d'initialiser un enregistrement a parir de: son
     * identifiant, ses mots cles, ses liens (20). Un lien contient une url, un
     * titre, une description et un rang.
     *
     * @param id
     *            Un entier representant un identifiant d'enregistrement.
     * @param motsCles
     *            Une chaine de caracteres representant les mots cles associes a cet
     *            enregistrement.
     * @param liens
     *            Un vecteur de Liens regroupant tous les titres, urls, descriptions
     *            et rangs.
     */
    public Enregistrement(int id, String motsCles, Vector liens) {
        this.id = id;
        this.motsCles = motsCles;
        this.liens = liens;
    }

    /**
     * Constructeur permettant d'initialiser un Enregistrement a parir d'un vecteur
     * contenant les informations prises dans la base de donnees. Le rang de chaque
     * Lien etant recalcule selon la valeur de son score. En effet, nous promouvons
     * les liens dont les scores sont superieur a 0. Le lien ayant le score le plus
     * eleve prend la premiere place et a donc la valeur du rang egale a 1.
     *
     * @param vecteur
     *            Un vecteur contenant des informations recuperees de la base de
     *            donnees.
     */
    public Enregistrement(Vector vecteur) {
        this.id = ((Integer) (vecteur.get(0))).intValue();
        this.motsCles = (String) (vecteur.get(1));
        Vector liens = new Vector();
        int j = 1;
        this.liens = new Vector();
        for (int i = 0; i < NB_LIENS; i++) {
            Lien lien = new Lien((String) (vecteur.get(j + 1)), (String) (vecteur.get(j + 2)),
                    (String) (vecteur.get(j + 3)), ((Integer) (vecteur.get(j + 4))).intValue(),
                    ((Integer) (vecteur.get(j + 5))).intValue());
            j = j + 5;
            this.liens.add(lien);
        }
        this.liens = promotionLiensCliques(liens);
    }

    /**
     * Constructeur initialisant l'enregistrement courant a partir d'un
     * enregistrement passe en parametre.
     *
     * @param enr
     *            Enregistrement
     */
    public Enregistrement(Enregistrement enr) {
        this.id = enr.getId();
        this.motsCles = enr.getKeywords();
        this.liens = enr.getLiens();
    }

    /**
     * Retourne l'entier de valeur maximum parmi les entiers contenus dans un
     * vecteur d'entiers.
     *
     * @param vecteurEntiers
     *            Vector : un vecteur d'entiers
     * @return int
     */
    public static int entierMaximum(Vector vecteurEntiers) {
        int max = -1, entier;
        for (int i = 0; i < vecteurEntiers.size(); i++) {
            entier = ((Integer) vecteurEntiers.elementAt(i)).intValue();
            if (entier > max) {
                max = entier;
            }
        }
        return max;
    }

    /**
     * Retourne l'entier localement maximum parmi les entiers contenus dans un
     * vecteur d'entiers. En effet, l'entier a retourner doit etre le maximum,
     * inferieur a celui passe en parametre.
     *
     * @param limite
     *            int
     * @param vecteurEntiers
     *            Vector : un vecteur d'entiers
     * @return int
     */
    public static int entierMaximumLocal(int limite, Vector vecteurEntiers) {
        int maxLocal = -1, entier;
        for (int i = 0; i < vecteurEntiers.size(); i++) {
            entier = ((Integer) vecteurEntiers.elementAt(i)).intValue();
            if ((entier > maxLocal) && (entier < limite)) {
                maxLocal = entier;
            }
        }
        return maxLocal;
    }

    /**
     * Retourne un vecteur d'entiers contenant les identifiants, c'est-a-dire les
     * index, des entiers egaux a celui passe en parametre.
     *
     * @param entier
     *            int
     * @param vecteurEntiers
     *            Vector : un vecteur d'entiers
     * @return Vector : un vecteur d'entiers
     */
    public static Vector idEntiersEgaux(int entier, Vector vecteurEntiers) {
        Vector resultat = new Vector();
        int nombre;

        for (int i = 0; i < vecteurEntiers.size(); i++) {
            nombre = ((Integer) vecteurEntiers.elementAt(i)).intValue();
            if (nombre == entier) {
                resultat.addElement(new Integer(i));
            }
        }
        return resultat;
    }

    /**
     * Retourne un vecteur d'entiers representant les index des entiers du vecteur
     * d'entiers, tries dans l'ordre decroissant. Si un ou plusieurs entiers
     * s'averent etre egaux, alors celui qui aura le plus petit index sera choisi en
     * premier et ainsi de suite.
     *
     * @param scores
     *            Vector : un vecteur d'entiers
     * @return Vector : un vecteur d'entiers
     */
    public static Vector triOrdreDecroissant(Vector scores) {
        Vector resultat = new Vector();
        int limite = entierMaximum(scores);
        while (limite > 0) {
            Vector sousResultat = idEntiersEgaux(limite, scores);
            for (int i = 0; i < sousResultat.size(); i++) {
                resultat.addElement(sousResultat.elementAt(i));
            }
            limite = entierMaximumLocal(limite, scores);
            ;
        }
        Vector sousResultat = idEntiersEgaux(0, scores);
        for (int i = 0; i < sousResultat.size(); i++) {
            resultat.addElement(sousResultat.elementAt(i));
        }
        return resultat;
    }

    /**
     * Retourne un vecteur de liens dont les liens ont ete promus selon la valeur de
     * leur score. Le lien ayant le score le plus eleve passe en tete de liste et
     * possede comme valeur de rang la valeur de l'index ou il se trouve dans la
     * tete de liste.
     *
     * @param liens
     *            Vector : un vecteur de liens
     * @return Vector : un vecteur de liens dont les liens ont ete promus selon la
     *         valeur de leur score
     */
    public static Vector promotionLiensCliques(Vector liens) {
        int id;
        Vector promotionLiens = new Vector();
        Vector scores = new Vector();
        for (int i = 0; i < liens.size(); i++) {
            scores.addElement(new Integer(((Lien) liens.elementAt(i)).getScore()));
        }
        Vector scoresTries = triOrdreDecroissant(scores);
        for (int i = 0; i < scoresTries.size(); i++) {
            id = ((Integer) scoresTries.elementAt(i)).intValue();
            int rang = i + 1;
            ((Lien) liens.elementAt(id)).setRang(rang);
            promotionLiens.addElement(liens.elementAt(id));
        }
        return promotionLiens;
    }

    /**
     * Retourne l'identifiant de l'enregistrement courant.
     *
     * @return int : un entier representant l'identifiant de l'enregistrement
     *         courant
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne l'ensemble des mots cles associe a l'enregistrement courant.
     *
     * @return String : une chaine de caracteres correspondant aux mots cles
     *         associes a l'enregistrement courant
     */
    public String getKeywords() {
        return motsCles;
    }

    /**
     * Retourne un vecteur de Lien correspondant aux liens associes a
     * l'enregistrement courant..
     *
     * @return Vector : un vecteur de Lien
     */
    public Vector getLiens() {
        return liens;
    }

    /**
     * Methode permettant de modifier le contenu de la variable "motsCles" par la
     * chaine de caracteres passee en parametre.
     *
     * @param motsCles
     *            String : une chaine de caracteres
     */
    public void setKeywords(String motsCles) {
        this.motsCles = motsCles;
    }

    /**
     * Methode permettant de modifier le contenu de la variable "id" par l'entier
     * passe en parametre.
     *
     * @param id
     *            int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Methode permettant de modifier la variable "liens" par un vecteur de Liens
     * passe en parametre.
     *
     * @param liens
     *            Vector : un vecteur de liens
     */
    public void setLiens(Vector liens) {
        this.liens = liens;
    }

    /**
     * Methode permettant d'ajouter un Lien dans l'enregistrement courant, contenant
     * les informations: url, titre, desc et rang.
     *
     * @param lien
     *            Lien
     */
    public void add(Lien lien) {
        this.liens.add(lien);
    }

    /**
     * Retourne l'ensemble des url associe a l'enregistrement courant.
     *
     * @return Vector : un vecteur de chaine de caracteres contenant les urls de
     *         l'enregistrement courant
     */
    public Vector getUrls() {
        Lien lien;
        Vector urls = new Vector();
        for (int i = 0; i < liens.size(); i++) {
            lien = (Lien) liens.elementAt(i);
            urls.addElement(lien.getUrl());
        }
        return urls;
    }

    /**
     * Retourne l'ensemble des titres associe a l'enregistrement courant.
     *
     * @return Vector : Un vecteur de chaine de caracteres contenant les titres de
     *         l'enregistrement courant
     */
    public Vector getTitres() {
        Lien lien;
        Vector titres = new Vector();
        for (int i = 0; i < liens.size(); i++) {
            lien = (Lien) liens.elementAt(i);
            titres.addElement(lien.getTitre());
        }
        return titres;
    }

    /**
     * Retourne l'ensemble des descriptions associe a l'enregistrement courant.
     *
     * @return vector : un vecteur de chaine decaracteres contenant les descriptions
     *         de l'enregistrement courant
     */
    public Vector getDescs() {
        Lien lien;
        Vector descs = new Vector();
        for (int i = 0; i < liens.size(); i++) {
            lien = (Lien) liens.elementAt(i);
            descs.addElement(lien.getDesc());
        }
        return descs;
    }

    /**
     * Retourne l'ensemble des rangs associe a l'enregistrement courant, pour un
     * ensemble d'url sur un total de 20 url.
     *
     * @return Vector : un vecteur d'entiers contenant les rangs de l'enregistrement
     *         courant
     */
    public Vector getRangs() {
        Lien lien;
        Vector rangs = new Vector();
        for (int i = 0; i < liens.size(); i++) {
            lien = (Lien) liens.elementAt(i);
            rangs.addElement(new Integer(lien.getRang()));
        }
        return rangs;
    }

    /**
     * Retourne un entier contenant l'identifiant d'un enregistrement (dont
     * l'identifiant dans la base de donnees est passe en parametre).
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @return int : un entier contenant un identifiant
     */
    public static int recuperationId(int id) {
        String requete = GestionBDD.construitSelect("UID", Agent.TableBDD, "UID='" + id + "'");
        Vector vecteurId = GestionBDD.envoiRequete(requete);
        int identifiant = ((Integer) ((Vector) (vecteurId.get(0))).get(0)).intValue();
        return identifiant;
    }

    /**
     * Retourne une chaine de caracteres contenant les mots cles contenus dans un
     * enregistrement d'identifiant "id" dans la base de donnees, passe en
     * parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @return String : une chaine de caracteres contenant les mots cles du lien de
     *         numero passe en parametre associe a l'enregistrement courant
     */
    public static String recuperationMotsCles(int id) {
        Vector vecteurMotsCles = GestionBDD
                .envoiRequete(GestionBDD.construitSelect("Keywords", Agent.TableBDD, "Uid='" + id + "'"));
        String motsCles = ((String) ((Vector) (vecteurMotsCles.get(0))).get(0));
        return motsCles;
    }

    /**
     * Retourne une chaine de caracteres contenant l'url, de numero "numero" passe
     * en parametre, contenu dans un enregistrement d'identifiant "id" dans la base
     * de donnees, passe en parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @param numero
     *            int : un entier representant le numero du lien
     * @return String : une chaine de caracteres contenant l'url du lien de numero
     *         passe en parametre associe a l'enregistrement courant
     */
    public static String recuperationUrlNumero(int id, int numero) {
        Vector vecteurUrl = GestionBDD
                .envoiRequete(GestionBDD.construitSelect("Url" + numero, Agent.TableBDD, "Uid='" + id + "'"));
        String url = ((String) ((Vector) (vecteurUrl.get(0))).get(0));
        return url;
    }

    /**
     * Retourne une chaine de caracteres contenant le titre, de numero "numero"
     * passe en parametre, contenu dans un enregistrement d'identifiant "id" dans la
     * base de donnees, passe en parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @param numero
     *            int : un entier representant le numero du lien
     * @return String : une chaine de caracteres contenant le titre du lien de
     *         numero passe en parametre associe a l'enregistrement courant
     */
    public static String recuperationTitreNumero(int id, int numero) {
        Vector vecteurTitre = GestionBDD
                .envoiRequete(GestionBDD.construitSelect("Title" + numero, Agent.TableBDD, "Uid='" + id + "'"));
        String titre = ((String) ((Vector) (vecteurTitre.get(0))).get(0));
        return titre;
    }

    /**
     * Retourne une chaine de caracteres contenant la description, de numero
     * "numero" passe en parametre, contenu dans un enregistrement d'identifiant
     * "id" dans la base de donnees, passe en parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @param numero
     *            int : un entier representant le numero du lien
     * @return String : une chaine de caracteres contenant la description du lien de
     *         numero passe en parametre associe a l'enregistrement courant
     */
    public static String recuperationDescNumero(int id, int numero) {
        Vector vecteurDesc = GestionBDD
                .envoiRequete(GestionBDD.construitSelect("Desc" + numero, Agent.TableBDD, "Uid='" + id + "'"));
        String desc = ((String) ((Vector) (vecteurDesc.get(0))).get(0));
        return desc;
    }

    /**
     * Retourne un entier contenant le rang, de numero "numero" passe en parametre,
     * contenu dans un enregistrement d'identifiant "id" dans la base de donnees,
     * passe en parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @param numero
     *            int : un entier representant le numero du lien.
     * @return int : un entier contenant le rang du lien de numero passe en
     *         parametre associe a l'enregistrement courant
     */
    public static int recuperationRangNumero(int id, int numero) {
        Vector vecteurRang = GestionBDD
                .envoiRequete(GestionBDD.construitSelect("Rank" + numero, Agent.TableBDD, "Uid='" + id + "'"));
        int rang = ((Integer) ((Vector) (vecteurRang.get(0))).get(0)).intValue();
        return rang;
    }

    /**
     * Retourne un entier contenant le score, de numero "numero" passe en parametre,
     * contenu dans un enregistrement d'identifiant "id" dans la base de donnees,
     * passe en parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @param numero
     *            int : un entier representant le score du lien
     * @return int : un entier contenant le rang du lien de numero passe en
     *         parametre associe a l'enregistrement courant
     */
    public static int recuperationScoreNumero(int id, int numero) {
        Vector vecteurRang = GestionBDD
                .envoiRequete(GestionBDD.construitSelect("Rank" + numero, Agent.TableBDD, "Uid='" + id + "'"));
        int rang = ((Integer) ((Vector) (vecteurRang.get(0))).get(0)).intValue();
        return rang;
    }

    /**
     * Retourne un entier contenant le score, de numero "numero" passe en parametre,
     * contenu dans un enregistrement d'identifiant "id" dans la base de donnees,
     * passe en parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @param numero
     *            int : un entier representant le numero du lien
     * @return int : un entier contenant le score du lien de numero passe en
     *         parametre associe a l'enregistrement courant
     */
    public static Lien recuperationLienNumero(int id, int numero) {
        String url = recuperationUrlNumero(id, numero);
        String titre = recuperationTitreNumero(id, numero);
        String desc = recuperationDescNumero(id, numero);
        int rang = recuperationRangNumero(id, numero);
        int score = recuperationScoreNumero(id, numero);
        Lien lien = new Lien(url, titre, desc, rang, score);
        return lien;
    }

    /**
     * Retourne un vecteur de Liens contenant les liens contenus dans un
     * enregistrement d'identifiant "id" dans la base de donnees, passe en
     * parametre.
     *
     * @param id
     *            int : un entier representant l'identifiant d'un enregistrement de
     *            la base de donnees
     * @return Vector : un vecteur de Liens contenant les liens associe a
     *         l'enregistrement courant
     */
    public static Vector recuperationLiens(int id) {
        Vector liens = new Vector();
        Lien unLien;
        for (int i = 0; i < NB_LIENS; i++) {
            unLien = recuperationLienNumero(id, i + 1);
            liens.addElement(unLien);
        }
        return liens;
    }

    /**
     * Retourne l'enregistrement d'identifiant "id", passe en parametre.
     *
     * @param id
     *            int
     * @return Enregistrement
     */
    public static Enregistrement recuperationEnregistrement(int id) {
        int uid = recuperationId(id);
        String motsCles = recuperationMotsCles(id);
        Vector liens = recuperationLiens(id);
        Vector promotionLiens = promotionLiensCliques(liens);
        Enregistrement enr = new Enregistrement(uid, motsCles, promotionLiens);
        return enr;
    }

    /**
     * Recuperation de l'ensemble des enregistrements contenu dans la base de
     * donnees.
     *
     * @return Vector : un vecteur d'Enregistrement contenant les enregistrements de
     *         la base de donnees.
     */
    public static Vector recuperationEnregistrements() {
        Vector ensEnregistrements;
        Enregistrement unEnregistrement;

        Vector enregistrementsBDD = GestionBDD.envoiRequete(GestionBDD.construitSelect("*", Agent.TableBDD, " "));
        ensEnregistrements = new Vector();
        for (int i = 0; i < enregistrementsBDD.size(); i++) {
            unEnregistrement = new Enregistrement((Vector) enregistrementsBDD.get(i));
            ensEnregistrements.add(unEnregistrement);
        }
        return ensEnregistrements;
    }

    /**
     * Retourne une chaine de caracteres. Affichage des informations sur un
     * Enregistrement, a savoir son identifiant, ses mots cles et ses liens (url,
     * titre, desc, rang et score) en XHTML.
     *
     * @param reponsesClient booleen
     * @return String : une chaine de caracteres
     */
    public String toXhtml(boolean reponsesClient) {
        String xHtml = "		<div id=\"body\">\n" + "<h1> R�sultats de la recherche : "
                + this.motsCles.replace("&", "&amp;") + "</h1>\n";
        if (reponsesClient == false)
            xHtml += "<br/><h3>Les autres agents n'ayant pas repondu, les reponses affichees sont celles du tri local</h3>";
        String finxHtml = "	 			</div>\n" + "		<div id=\"footer\" title=\"Menu\">\n" + "			<ul>\n"
                + "				<li><a href=\"index.html\">Recherche</a></li>\n"
                + "				<li><a href=\"apropos.html\">A Propos</a></li>\n"
                + "				<li><a href=\"aide.html\">Aide</a></li>\n"
                + " 				<li><a href=\"stoppeAgent.html\">Arr�t du serveur</a></li>" + "			</ul>\n"
                + "			<p>\n"
                + "				<a href=\"licence.html\">CopyLeft</a> &copy; M�ta-moteur Staff 2006\n"
                + "			</p>\n" + "	</div>\n" + "	</div>\n" + "</body>\n" + "</html>\n";
        String liensXhtml = "";
        for (int i = 0; i < this.liens.size(); i++) {

            Lien enr = (Lien) this.liens.elementAt(i);
            String lienXhtml = enr.toXhtml();
            liensXhtml = liensXhtml + lienXhtml;
        }
        return xHtml + liensXhtml + finxHtml;
    }

    /*
     * Export en sql
     */
    public String toSql() {
        String patternInsert = "INSERT INTO bdd ( " + "Uid,Keywords," + "Url1,Title1,Desc1,Rank1   ,   Select1   ,"
                + "Url2,Title2,Desc2,Rank2   ,   Select2   ," + "Url3,Title3,Desc3,Rank3,   Select3   ,"
                + "Url4,Title4,Desc4,Rank4,   Select4   ," + "Url5,Title5,Desc5,Rank5,   Select5   ,"
                + "Url6,Title6,Desc6,Rank6,   Select6   ," + "Url7,Title7,Desc7,Rank7,   Select7   ,"
                + "Url8,Title8,Desc8,Rank8,   Select8   ," + "Url9,Title9,Desc9,Rank9,   Select9   ,"
                + "Url10,   Title10   ,   Desc10   ,   Rank10   ,   Select10   ,"
                + "Url11,   Title11   ,   Desc11   ,   Rank11   ,   Select11   ,"
                + "Url12,   Title12   ,   Desc12   ,   Rank12   ,   Select12   ,"
                + "Url13,   Title13   ,   Desc13   ,   Rank13   ,   Select13   ,"
                + "Url14,   Title14   ,   Desc14   ,   Rank14   ,   Select14   ,"
                + "Url15,   Title15   ,   Desc15   ,   Rank15   ,   Select15   ,"
                + "Url16,   Title16   ,   Desc16   ,   Rank16   ,   Select16   ,"
                + "Url17,   Title17   ,   Desc17   ,   Rank17   ,   Select17   ,"
                + "Url18,   Title18   ,   Desc18   ,   Rank18   ,   Select18   ,"
                + "Url19,   Title19   ,   Desc19   ,   Rank19   ,   Select19   ,"
                + "Url20,   Title20   ,   Desc20   ,   Rank20   ,   Select20   ," + "TimeQuery) VALUES (" + "'0', " + // Id
                // =
                // 0
                // auto-incr�ment
                // automatique
                "'" + this.motsCles + "', ";
        String patternLiens = "";
        for (int i = 0; i < this.liens.size(); i++) {
            Lien enr = (Lien) this.liens.elementAt(i);
            String patternLien = enr.toSql();
            patternLiens = patternLiens + patternLien;
        }
        String patternInsertFin = "'" + System.currentTimeMillis() + "')";

        return patternInsert + patternLiens + patternInsertFin;

    }

    /**
     * Retourne une chaine de caracteres. Affichage des informations sur un
     * Enregistrement, a savoir son identifiant, ses mots cles et ses liens (url,
     * titre, desc, rang et score) en XML.
     *
     * @return String : une chaine de caracteres
     */
    public String toXml() {
        String xmlDTD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" + "<!DOCTYPE search ["
                + "  <!ELEMENT search (keywords,list)>" + "  <!ELEMENT keywords (#PCDATA)>"
                + "  <!ELEMENT list (link)*>" + "  <!ELEMENT link (title, url, desc)>" + "  <!ELEMENT title (#PCDATA)>"
                + "  <!ELEMENT url (#PCDATA)>" + "  <!ELEMENT desc (#PCDATA)>" + "  <!ENTITY middot \"&#183;\">"
                + "  <!ATTLIST list links CDATA #IMPLIED>" + "  <!ATTLIST link rank CDATA #IMPLIED"
                + "  				score CDATA #IMPLIED>" + "<!ENTITY middot \"&#183;\">" + "]>";
        String xmlStart = "<search>" + "<keywords>" + this.motsCles + "</keywords>" + "<list>";
        String xmlStop = "</list></search>";

        String xmlLiens = "";
        // le rang ici est trait� selon la variable rang
        for (int i = 0; i < this.liens.size(); i++) {
            Lien enr = (Lien) this.liens.elementAt(i);
            String xmlLien = enr.toXml();
            xmlLiens = xmlLiens + xmlLien;
        }
        return xmlDTD + xmlStart + xmlLiens + xmlStop;
    }

    /**
     * Affichage d'un enregistrement
     *
     * @return String
     */
    public String toString() {
        String affichage = this.id + this.motsCles.toString() + this.liens;
        return affichage;
    }

}
