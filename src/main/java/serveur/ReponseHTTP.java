package serveur;

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

import agent.Agent;
import agent.GestionMessage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Vector;

/**
 * Classe modelisant une reponse HTTP du serveur
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 * @see Serveur
 */
public class ReponseHTTP {
    private String corps;
    private Fichier fichierPointe;
    private Vector options;

    /**
     * Constructeur d'une Reponse HTTP
     *
     * @param fichierPointe fichier sur lequel va pointer la requete
     * @param options       options de la requete
     */
    public ReponseHTTP(Fichier fichierPointe, Vector options) {
        this.fichierPointe = fichierPointe;
        this.options = options;
    }

    /**
     * Constructeur d'une Reponse HTTP
     *
     * @param fichierPointe fichier sur lequel va pointer la requete
     */
    public ReponseHTTP(Fichier fichierPointe) {
        this.fichierPointe = fichierPointe;
        this.options = new Vector();
        this.options.add(new OptionReponseHTTP("Server"));
        this.options.add(new OptionReponseHTTP("Date"));
    }

    /**
     * Constructeur d'une Reponse HTTP
     *
     * @param fichierPointe fichier sur lequel va pointer la requete
     * @param URL           url sur laquelle doit etre operee la redirection (ne fonctionne
     *                      pas)
     */
    public ReponseHTTP(Fichier fichierPointe, String URL) {
        this.fichierPointe = fichierPointe;
        this.options = new Vector();
        this.options.add(new OptionReponseHTTP("Server"));
        this.options.add(new OptionReponseHTTP("Date"));
        this.options.add(new OptionReponseHTTP("location", URL));
    }

    /**
     * Methode d'acces a l'attribut prive FichierPointe
     *
     * @return retourne le nom du fichier de la reponse a envoyer au client
     */
    public String getFichierPointe() {
        if (!this.fichierPointe.toString().endsWith("/"))
            return (this.fichierPointe.toString());
        else
            return (this.fichierPointe.toString() + "index.html");
    }

    /**
     * Methode incluant les options de la Reponse dans le corps de la reponse
     */
    private void inclueOptions() {
        int taille = this.options.size();
        // parcours des options et ajout au corps de la reponse
        for (int i = 0; i < taille; i++) {
            this.corps += this.options.get(i).toString();
            this.corps += Constantes.RETOUR_CHARIOT;
        }
        this.corps += Constantes.RETOUR_CHARIOT;
    }

    /**
     * Methode d'envoi d'un fichier texte sur le flux de sortie
     *
     * @param sortie  flux de sortie ouvert sur le socket vers le client
     * @param code    code retour HTTP
     * @param fichier fichier a envoyer
     */
    public void envoieFichierTexte(OutputStream sortie, int code, Fichier fichier) {
        long taille = fichier.getTaille();
        this.corps = "";
        // creation du debut de la requete
        this.corps += "HTTP/1.0 " + code + " OK" + Constantes.RETOUR_CHARIOT;
        // ajout des options de la reponse
        this.options.add(new OptionReponseHTTP("Content-Length", String.valueOf(taille)));
        this.options.add(new OptionReponseHTTP("Content-Type", "text/html"));
        // inclusion des options
        inclueOptions();
        // ouverture des flux
        OutputStreamWriter sortie2 = new OutputStreamWriter(sortie);
        try {
            // test inutile, ne fonctionne pas, a voir
            if (code == 301)
                sortie2.write(this.corps);
            else
                sortie.write(this.corps.getBytes());
            // ouverture du fichier
            File fich = new File(Agent.Path, fichier.toString().substring(1));
            try {
                try (FileInputStream fluxFichier = new FileInputStream(fich)) {
                    int buffer;
                    // lecture et ecriture du fichier
                    while (taille > 0) {
                        buffer = fluxFichier.read();
                        sortie.write(buffer);
                        taille--;
                    }
                }
            } catch (java.io.FileNotFoundException e) {
                // affichage d'un message en cas d'erreur
                GestionMessage.message(1, "ReponseHTTP", "Fichier non trouve");
            }
        } catch (IOException e) {
            // affichage d'un message en cas d'erreur
            GestionMessage.message(1, "ReponseHTTP",
                    "Erreur d'entree sorties dans l'envoi du fichier texte " + fichier + " : " + e);
        }
    }

    /**
     * Methode permettant la redirection d'un lien a l'aide du code HTML
     *
     * @param sortie flux permettant de transferer la reponse
     * @param url    url de la page sur laquelle la redirection doit etre operee
     */
    public void redirection(PrintWriter sortie, String url) {
        this.corps = "";
        // creation du debut de la requete
        this.corps += "HTTP/1.0 200 OK" + Constantes.RETOUR_CHARIOT;
        // ajout de l'option representant le type
        this.options.add(new OptionReponseHTTP("Content-Type", "text/html"));
        // inclusion des options
        inclueOptions();
        // construction du fichier HTML pour la redirection
        corps += "<HTML>" + Constantes.RETOUR_CHARIOT + "<HEAD>" + Constantes.RETOUR_CHARIOT
                + "<META http-EQUIV=\"Refresh\" CONTENT=\"0; url=" + url + "\">" + Constantes.RETOUR_CHARIOT;
        corps += "</HEAD>" + Constantes.RETOUR_CHARIOT + "<BODY>" + Constantes.RETOUR_CHARIOT + "<P>"
                + Constantes.RETOUR_CHARIOT + "Redirection vers <a href=\"" + url + "\">" + url + "</a>"
                + Constantes.RETOUR_CHARIOT;
        corps += "</P>" + Constantes.RETOUR_CHARIOT + "</BODY>" + Constantes.RETOUR_CHARIOT + "</HTML>"
                + Constantes.RETOUR_CHARIOT + Constantes.RETOUR_CHARIOT + Constantes.RETOUR_CHARIOT;
        // envoi de la requete construite
        sortie.println(this.corps);
    }

    /**
     * Methode d'envoi d'une image via le flux de sortie
     *
     * @param sortie  flux de sortie ouvert sur le socket vers le client
     * @param fichier fichier a envoyer
     */
    public void envoieImage(OutputStream sortie, Fichier fichier) {
        long taille = fichier.getTaille();
        this.corps = "";
        // creation du debut de la requete
        this.corps += "HTTP/1.0 200 OK" + Constantes.RETOUR_CHARIOT;
        // ajout des options a la requete
        this.options.add(new OptionReponseHTTP("Accept-Ranges", "bytes"));
        this.options.add(new OptionReponseHTTP("Content-Length", String.valueOf(taille)));
        switch (this.fichierPointe.getType()) {
            case Constantes.IMAGE_PNG: {
                this.options.add(new OptionReponseHTTP("Content-Type", "image/png"));
                break;
            }
            case Constantes.IMAGE_GIF: {
                this.options.add(new OptionReponseHTTP("Content-Type", "image/gif"));
                break;
            }
            case Constantes.IMAGE_JPG: {
                this.options.add(new OptionReponseHTTP("Content-Type", "image/jpeg"));
                break;
            }
            case Constantes.IMAGE_BMP: {
                this.options.add(new OptionReponseHTTP("Content-Type", "image/bmp"));
                break;
            }
        }
        // inclusion des options
        inclueOptions();
        // ouverture du flux d'envoie des donnees
        OutputStreamWriter sortie2 = new OutputStreamWriter(sortie);
        try {
            // ecriture du debut de la requete sur le flux
            sortie2.write(this.corps);
            // ouverture du fichier
            File fich = new File(Agent.Path, fichier.toString().substring(1));
            RenderedImage image = ImageIO.read(fich);
            String type = null;
            // recuperation du type du fichier
            switch (this.fichierPointe.getType()) {
                case Constantes.IMAGE_PNG: {
                    type = "png";
                    break;
                }
                case Constantes.IMAGE_GIF: {
                    type = "gif";
                    break;
                }
                case Constantes.IMAGE_JPG: {
                    type = "jpg";
                    break;
                }
                case Constantes.IMAGE_BMP: {
                    type = "bmp";
                    break;
                }
            }
            // ecriture de l'image sur le flux
            ImageIO.write(image, type, sortie);
        } catch (IOException e) {
            // affichage d'un message en cas d'erreur
            GestionMessage.message(1, "ReponseHTTP",
                    "Erreur d'entree sorties dans l'envoi de l'image " + fichier + " : " + e);
        }
    }

    /**
     * Methode renvoyant une chaine permettant l'envoi d'une page d'erreur 404 au
     * client
     *
     * @param requete requete du client
     * @return retourne la chaine permettant d'envoyer la page d'erreur au client
     */
    public static String erreur404(RequeteHTTP requete) {
        String chaine = "";
        System.out.println(Agent.Path);
        chaine += "HTTP/1.0 404 ERROR" + Constantes.RETOUR_CHARIOT;
        chaine += Constantes.RETOUR_CHARIOT;
        chaine += "<HTML><H1>Erreur 404</H1><p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<H2>Le fichier est inconnu</H2><p>" + Constantes.RETOUR_CHARIOT;
        chaine += "methode = ";
        if (requete.getType() == Constantes.GET)
            chaine += "GET";
        else
            chaine += "POST";
        chaine += "<br>" + Constantes.RETOUR_CHARIOT;
        chaine += "URL = " + requete.getFichierPointe().substring(1) + "<br>" + Constantes.RETOUR_CHARIOT;
        chaine += "</HTML>" + Constantes.RETOUR_CHARIOT;
        return chaine;
    }

    /**
     * Methode permettant de renvoyer le debut de la page a renvoyer au client,
     * toujours la meme donc inutile de la mettre dans toXhtml
     *
     * @return retourne une chaine contenant le debut de la page a envoyer au client
     */
    public static String debutReponseHTML() {
        String chaine = "";
        chaine += "HTTP/1.0 200 OK" + Constantes.RETOUR_CHARIOT;
        ReponseHTTP rep = new ReponseHTTP(new Fichier(""));
        rep.options.add(new OptionReponseHTTP("Content-Type", "text/html"));
        rep.inclueOptions();
        chaine += rep.corps;
        chaine += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"fr\" lang=\"fr\">"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<head>" + Constantes.RETOUR_CHARIOT;
        chaine += "<title>Network Search</title>" + Constantes.RETOUR_CHARIOT;
        chaine += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />" + Constantes.RETOUR_CHARIOT;
        chaine += includeCSSinHTML() + Constantes.RETOUR_CHARIOT;
        chaine += "<meta http-equiv=\"Content-Language\" content=\"fr\" />" + Constantes.RETOUR_CHARIOT;
        chaine += "<link rel=\"stylesheet\" href=\"style/default.css\" type=\"text/css\" />" + Constantes.RETOUR_CHARIOT;
        chaine += "<script type=\"text/javascript\" src=\"script/clicTous.js\"></script>" + Constantes.RETOUR_CHARIOT;
        chaine += "</head>" + Constantes.RETOUR_CHARIOT;
        chaine += "<body>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"topOfPage\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<a name=\"Haut\">&nbsp;</a>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"conteiner\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"header\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"logo\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"index.html\"><img src=\"images/logo.png\" alt=\"Network search logo\" /></a>"
                + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"bar\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<ul>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li class=\"active\">" + Constantes.RETOUR_CHARIOT;
        chaine += "Recherche" + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"aide.html\" accesskey=\"a\"><span class=\"underline\">A</span>ide</a>"
                + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"apropos.html\" accesskey=\"p\">A <span class=\"underline\">P</span>ropos</a>"
                + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "</ul>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"top\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"search\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<form action=\"recherche.html\" method=\"post\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"recherche\" id=\"recherche\" value=\"\" tabindex=\"1\" type=\"text\" />"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<input class=\"submit\" value=\"Recherche\" type=\"submit\" /></p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div class=\"rechercheliste\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<p>Choix du moteur de recherche : </p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<p><input id=\"moteurs\" name=\"moteur1\" value=\"0\" type=\"checkbox\" onclick=\"if (this.checked) { clicTous(this.form,true) } else { clicTous(this.form,false) };\" />"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">Tous</label>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"moteur2\" value=\"1\" type=\"checkbox\" checked />" + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">Google</label>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"moteur3\" value=\"2\" type=\"checkbox\" disabled />" + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">Yahoo</label>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"moteur4\" value=\"3\" type=\"checkbox\" disabled />" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"bourage\" value=\"0\" type=\"hidden\" />" + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">AltaVista</label></p>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</form>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        return chaine;
    }

    /**
     * Methode permettant de prevenir le client si le moteur de recherche n'a rien
     * repondu
     *
     * @param motsCle mots cles entres
     * @return page a envoyer au client
     */
    public static String pasDeReponse(String motsCle) {
        String chaine = "";
        chaine += "HTTP/1.0 200 OK" + Constantes.RETOUR_CHARIOT;
        ReponseHTTP rep = new ReponseHTTP(new Fichier(""));
        rep.options.add(new OptionReponseHTTP("Content-Type", "text/html"));
        rep.inclueOptions();
        chaine += rep.corps;
        chaine += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"fr\" lang=\"fr\">"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<head>" + Constantes.RETOUR_CHARIOT;
        chaine += "<title>Network Search</title>" + Constantes.RETOUR_CHARIOT;
        chaine += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />" + Constantes.RETOUR_CHARIOT;
        chaine += includeCSSinHTML() + Constantes.RETOUR_CHARIOT;
        chaine += "<meta http-equiv=\"Content-Language\" content=\"fr\" />" + Constantes.RETOUR_CHARIOT;
        chaine += "<link rel=\"stylesheet\" href=\"style/default.css\" type=\"text/css\" />" + Constantes.RETOUR_CHARIOT;
        chaine += "<script type=\"text/javascript\" src=\"script/clicTous.js\"></script>" + Constantes.RETOUR_CHARIOT;
        chaine += "</head>" + Constantes.RETOUR_CHARIOT;
        chaine += "<body>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"topOfPage\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<a name=\"Haut\">&nbsp;</a>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"conteiner\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"header\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"logo\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"index.html\"><img src=\"images/logo.png\" alt=\"Network search logo\" /></a>"
                + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"bar\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<ul>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li class=\"active\">" + Constantes.RETOUR_CHARIOT;
        chaine += "Recherche" + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"aide.html\" accesskey=\"a\"><span class=\"underline\">A</span>ide</a>"
                + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"apropos.html\" accesskey=\"p\">A <span class=\"underline\">P</span>ropos</a>"
                + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "</ul>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"top\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"search\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<form action=\"recherche.html\" method=\"post\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"recherche\" id=\"recherche\" value=\"\" tabindex=\"1\" type=\"text\" />"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<input class=\"submit\" value=\"Recherche\" type=\"submit\" /></p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div class=\"rechercheliste\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<p>Choix du moteur de recherche : </p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<p><input id=\"moteurs\" name=\"moteur1\" value=\"0\" type=\"checkbox\" onclick=\"if (this.checked) { clicTous(this.form,true) } else { clicTous(this.form,false) };\" />"
                + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">Tous</label>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"moteur2\" value=\"1\" type=\"checkbox\" checked />" + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">Google</label>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"moteur3\" value=\"2\" type=\"checkbox\" disabled />" + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">Yahoo</label>" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"moteur4\" value=\"3\" type=\"checkbox\" disabled />" + Constantes.RETOUR_CHARIOT;
        chaine += "<input name=\"bourage\" value=\"0\" type=\"hidden\" />" + Constantes.RETOUR_CHARIOT;
        chaine += "<label class=\"inline\" for=\"moteurs\">AltaVista</label></p>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</form>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"body\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<h1>" + Constantes.RETOUR_CHARIOT;
        chaine += " R�sultats de la recherche : " + motsCle + Constantes.RETOUR_CHARIOT;
        chaine += "</h1>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div class=\"blockDistinct\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<p>" + Constantes.RETOUR_CHARIOT;
        chaine += "D�sol�, mais le moteur de recherche ne nous a pas fourni de r�ponse" + Constantes.RETOUR_CHARIOT;
        chaine += "</p>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "<div id=\"footer\" title=\"Menu\">" + Constantes.RETOUR_CHARIOT;
        chaine += "<ul>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"index.html\">Recherche</a>" + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"apropos.html\">A Propos</a>" + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"aide.html\">Aide</a>" + Constantes.RETOUR_CHARIOT;
        chaine += "</li>" + Constantes.RETOUR_CHARIOT;
        chaine += "<li><a href=\"stoppeAgent.html\">Arr�t du serveur</a></li>";
        chaine += "</ul>" + Constantes.RETOUR_CHARIOT;
        chaine += "<p>" + Constantes.RETOUR_CHARIOT;
        chaine += "<a href=\"licence.html\">CopyLeft</a> &copy; M�ta-moteur Staff 2006" + Constantes.RETOUR_CHARIOT;
        chaine += "</p>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</div>" + Constantes.RETOUR_CHARIOT;
        chaine += "</body>" + Constantes.RETOUR_CHARIOT;
        chaine += "</html>" + Constantes.RETOUR_CHARIOT;
        return chaine;
    }

    public static String includeCSSinHTML() {
        String css = "";
        css = "<style type=\"text/css\">";
        css += "body { padding: 0; margin: 0; font-size: 12px; font-family: tahoma, sans-serif; letter-spacing: 1px; line-height: 160%; background: #cdcdcd; color: #454545; }";
        css += "div { text-align: left;	}";
        css += "#conteiner { width: 730px; padding: 25px; background: #fff; margin-left: auto; margin-right: auto; }";
        css += "#header { height: 45px; }";
        css += "#logo {	width: 186px; float: left; }";
        css += "#search {padding-top: 50px;padding-left: 8px;width: 400px;float: left;color: #fff;}";
        css += "fieldset { padding: 0; border: 0; }";
        css += "#bar { clear: both; background: #cdcdcd; height: 25px; }";
        css += "#top { background: url(\"../images/top.png\") 100% 0 no-repeat #505050; height: 200px; clear: both; }";
        css += "#body { clear: both; margin-bottom: 15px; margin-top: 15px; }";
        css += "#footer { clear: both; padding-top: 5px; }";
        css += "div.block, div.blockDistinct { padding: 10px; padding-bottom: 3px; padding-top: 8px; }";
        css += "div.blockDistinct { background: url(\"../images/corner.png\") no-repeat 100% 100% #f5f5f5; }";
        css += "h1, h2 { margin: 0; margin-bottom: 10px; padding: 0; font-weight: normal; font-size: 25px; }";
        css += "#top h1 { color: #fff; padding-left: 25px; padding-top: 65px; line-height: 110%;}";
        css += "h2 { font-size: 16px; margin-bottom: 5px; color: #000; }";
        css += "p {	margin: 0; margin-bottom: 5px; }";
        css += "p.right { text-align: right; }";
        css += "#footer p {	clear: both; color: #c0c0c0; font-size: 11px; }";
        css += "form { margin: 0; }";
        css += "legend { display: none; }";
        css += "input {	color : #000;font-family : Verdana,Arial,Helvetica,sans-serif;font-size : 1em;border-width : 1px;}";
        css += "#recherche{ position: relative;top: 0px;width: 16em;background: #eef3f5 url(\"../images/search.png\") no-repeat 2% 50%;padding: 2px 44px;}";
        css += "input.submit {padding: 2px 0px;border-style : outset;background : #d2e0e6;font-weight : bold;}";
        css += "input.submit:hover, input.submit:focus {background : #fc3;}";
        css += "input.submit:active {border-style : inset;}";
        css += "ul { margin: 0; padding : 0; list-style : none; }";
        css += "#bar li { margin: 0; padding: 0; padding-top: 3px; padding-bottom: 4px;	padding-left: 10px;	padding-right: 10px; border-right: 1px solid #fff; float: left; }";
        css += "#bar li.active { background: #505050; color: #fff; padding-bottom: 4px; }";
        css += "#footer li { float: left; padding: 0; height: 15px;	vertical-align: middle;	padding-right: 25px;	font-size: 11px; }";
        css += "a:link, a:active, a:visited { text-decoration: none; }";
        css += "a:link, a:active { color: #0080c0; }";
        css += "a:visited { color: #800080; }";
        css += "#bar a:link, #bar a:active, #bar a:visited { color: #000; } ";
        css += "a img { border: 0; }";
        css += "a img:active { border: 0; }";
        css += "span.highlight { color: #abd6f1; }";
        css += "span.hide { display: none; }";
        css += "span.underline { text-decoration: underline; }";
        css += "</style>";
        return css;
    }
}
