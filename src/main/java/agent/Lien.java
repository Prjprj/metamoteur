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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Titre : classe publique Lien Description : classe permettant de representer
 * un lien, ce dernier etant compose de: - d'une url, - d'un titre, - d'une
 * description, - d'un rang.
 *
 * @author Jeremy FRECHARD
 * @author Cecile GIRARD
 * @author Aysel GUNES
 * @author Pierre RAMOS
 * @version 1.0
 */

public class Lien {

    private String url;
    private String titre;
    private String desc;
    private int rang;
    private int score;

    /**
     * Constructeur par defaut. Initilisation de: - la chaine de carateres url a
     * vide, - la chaine de carateres titre a vide, - la chaine de carateres desc a
     * vide, - l'entier rang a 0, - l'entier score a 0.
     */
    public Lien() {
        this.url = "";
        this.titre = "";
        this.desc = "";
        this.rang = 0;
        this.score = 0;
    }

    /**
     * Constructeur permettant d'initialiser un Lien par le biais d'une chaine de
     * caracteres representant l'url, une chaine de caracteres representant le
     * titre, une chaine de caracteres representant la description et un entier
     * representant le rang. Le tout est passe en parametre.
     *
     * @param url   String : Une chaine de caracteres representant une url.
     * @param titre String : Une chaine de caracteres representant un titre.
     * @param desc  String : Une chaine de caracteres representant une description.
     * @param rang  int : Un entier representant le rang.
     * @param score int : score
     */
    public Lien(String url, String titre, String desc, int rang, int score) {
        this.url = url;
        this.titre = titre;
        this.desc = desc;
        this.rang = rang;
        this.score = score;
    }

    /**
     * Retourne la varible "url", une chaine de caracteres representant une url.
     *
     * @return String : Une chaine de caracteres representant une url.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Retourne la varible "titre", une chaine de caracteres representant un titre.
     *
     * @return String : Une chaine de caracteres representant un titre.
     */
    public String getTitre() {
        return this.titre;
    }

    /**
     * Retourne la varible "desc", une chaine de caracteres representant une
     * description.
     *
     * @return String : Une chaine de caracteres representant une description.
     */
    public String getDesc() {
        return this.desc;
    }

    /**
     * Retourne la varible "rang", un entier representant un rang.
     *
     * @return int : Un entier representant un rang.
     */
    public int getRang() {
        return this.rang;
    }

    /**
     * Retourne la varible "score", un entier representant un score.
     *
     * @return int : Un entier representant un score.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Methode permettant de modifier le contenu de la variable "titre" par la
     * chaine de caracteres passee en parametre.
     *
     * @param titre String : Une chaine de caracteres.
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * Methode permettant de modifier le contenu de la variable "url" par la chaine
     * de caracteres passee en parametre.
     *
     * @param url String : Une chaine de caracteres.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Methode permettant de modifier le contenu de la variable "desc" par la chaine
     * de caracteres passee en parametre.
     *
     * @param desc String : Une chaine de caracteres.
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Methode permettant de modifier le contenu de la variable "rang" par l'entier
     * passe en parametre.
     *
     * @param rang int : Un entier.
     */
    public void setRang(int rang) {
        this.rang = rang;
    }

    /**
     * Methode permettant de modifier le contenu de la variable "score" par l'entier
     * passe en parametre.
     *
     * @param score int : Un entier.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Retourne une valeur booleenne. - Vrai si la variable entiere "rang" est
     * paire. - Faux sinon.
     *
     * @return boolean : Un booleen.
     */
    public boolean estPairRang() {
        return (this.rang % 2) == 0;
    }

    /**
     * Retourne une chaine de caracteres. Affichage des informations sur un Lien
     * (url, titre, desc, rang et score) en XHTML.
     *
     * @return String : Une chaine de caracteres.
     */
    public String toXhtml() {
        String xHtml = "";
        if (this.estPairRang()) {
            xHtml = "<div class=\"blockDistinct\">\n";
        } else {
            xHtml = "<div class=\"block\">\n";
        }
        // affichage du lien en xhtml !!!
        xHtml = xHtml + "<h2><a href=\"" + "redirect.html?" + this.url.replace("&", "&amp;") + "\" title=\""
                + this.titre + "\">" + this.titre + "</a>";
        // affichage du score en etoile
        if (this.score > 0) {
            xHtml = xHtml + " <img src=\"images/etoile.png\" alt=\"etoile\" />";
        }
        xHtml = xHtml + "</h2>\n<p>\n";
        // affichage du descriptif
        xHtml = xHtml + this.desc + "<br/>\n";
        // affichage du lien en xhtml une nouvelle fois!!!
        xHtml = xHtml + "<a href=\"" + "redirect.html?" + this.url.replace("&", "&amp;") + "\" title=\"" + this.titre
                + "\">" + this.titre + "</a>\n";
        String finxHtml = "</p>\n" + "<p class=\"right\">\n" + "	<a href=\"#Haut\" title=\"Haut de page\">"
                + "Haut de page<img src=\"images/arrow.up.png\" width=\"7\" height=\"8\" alt=\"haut\" /></a>\n"
                + "</p>\n" + "</div>\n";

        return xHtml + finxHtml;
    }

    /**
     * Retourne une chaine de caracteres. Affichage des informations sur un Lien
     * (url, titre, desc, rang et score) en texte.
     *
     * @return String : Une chaine de caracteres.
     */
    public String toString() {
        return this.url + "," + this.titre + "," + this.desc + "," + this.rang;
    }

    /**
     * Retourne une chaine de caracteres. Affichage des informations sur un Lien
     * (url, titre, desc, rang et score) en chaine SQL.
     *
     * @return String : Une chaine de caracteres.
     */
    public String toSql() {
        return "'" + this.url + "','" + this.titre.replaceAll("'", " ") + "','" + this.desc.replaceAll("'", " ")
                + "','" + this.rang + "','" + this.score + "',";
    }

    /**
     * Retourne une chaine de caracteres. Affichage des informations sur un Lien
     * (url, titre, desc, rang et score) en XML.
     *
     * @return String : Une chaine de caracteres.
     */
    public String toXml() {
        String xml;
        try {
            xml = "<link rank=\"" + this.rang + "\" ";
            xml += "score=\"" + this.score + "\">";
            xml += "<title>" + URLDecoder.decode(this.titre.replaceAll("&", "&amp;"), "UTF-8") + "</title>";
            xml += "<url>" + URLDecoder.decode(this.url.replaceAll("&", "&amp;"), "UTF-8") + "</url>";
            xml += "<desc>" + this.desc.replaceAll("&", "&amp;") + "</desc>";
            xml += "</link>";
        } catch (UnsupportedEncodingException e) {
            GestionMessage.message(0, "Lien", "Erreur de d�codage URL : " + e);
            xml = "";
        }
        return xml;
    }
}
