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
import agent.Enregistrement;
import agent.EnregistrementHandler;
import agent.GestionMessage;
import bdd.GestionBDD;
import client.Client;
import moteur.ParsingGoogle;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import tri.Permutations;
import tri.Tri;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Vector;

/**
 * Classe representant un thread de traitement d'une requete HTTP cette classe
 * est utilisee par Serveur.java
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 * @see Serveur
 */

public class TraitementRequete extends Thread {
    private RequeteHTTP requete;
    private ReponseHTTP reponse;
    private Socket socket;
    private BufferedReader entree;
    private OutputStream sortie2;
    private PrintWriter sortie;

    /**
     * Constructeur permettant de construire traitement requete.
     *
     * @param socket Socket sur lequel va s'etablir la connexion
     */
    public TraitementRequete(Socket socket) {
        this.socket = socket;
        try {
            // creation des flux d'entrees-sorties
            entree = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            sortie2 = this.socket.getOutputStream();
            sortie = new PrintWriter(new OutputStreamWriter(sortie2), true);
            // Le traitement est execute via run(), appele par l'ExecutorService
        } catch (IOException e) {
            GestionMessage.message(2, "TraitementRequete", "Erreur a l'ouverture des flux de communication " + e);
            try {
                // en cas d'erreur, fermeture du socket et des flux
                this.entree.close();
                this.sortie.close();
                this.sortie2.close();
                this.socket.close();
            } catch (IOException e2) {
                GestionMessage.message(1, "TraitementRequete",
                        "Erreur a la fermeture du socket de communication suite a une erreur a l'ouverture des flux de communication"
                                + e2);
            }
        }
    }

    /**
     * Surcharge de la methode run heritee de Thread cette methode recoit la requete
     * envoyee par le client et fait appel aux autres methodes pour y repondre
     */
    public void run() {
        // appel de la methode de la superclasse Thread
        super.run();
        try {
            // recuperation de la requete du client
            String requete = new String("");
            boolean boucle = true;
            StringBuffer sb = new StringBuffer(8096);
            int debut = entree.read();
            sb.append((char) debut);
            // cas ou la requete est de type GET
            if ((char) debut == 'G') {
                while (boucle) {
                    if (entree.ready()) {
                        int i = 0;
                        while (i != -1) {
                            i = entree.read();
                            sb.append((char) i);
                            String temp = sb.toString();
                            // detection de la fin de la requete
                            if (temp.endsWith("HTTP/1.1"))
                                break;
                            if (temp.endsWith("HTTP/1.0"))
                                break;
                        }
                        boucle = false;
                    } else
                        boucle = false;
                }
            } else {
                // cas ou la requete est de type POST
                if ((char) debut == 'P') {
                    while (boucle) {
                        if (entree.ready()) {
                            int i = 0;
                            while (i != -1) {
                                i = entree.read();
                                sb.append((char) i);
                                String temp = sb.toString();
                                // detection de la fin de la requete
                                if (temp.endsWith("bourage=0"))
                                    break;
                                if (temp.endsWith("</search>"))
                                    break;
                            }
                            boucle = false;
                        } else
                            boucle = false;
                    }
                }
            }
            // finalisation de la recuperation de la requete
            requete = sb.toString() + Constantes.RETOUR_CHARIOT;
            // creation de l'objet requete, permettant de recuperer certaines informations
            // sur le type de requete
            this.requete = new RequeteHTTP(requete);
            // generation de la reponse appropriee a la requete
            // cas requete de type GET sur l'url de fermeture de l'agent
            if ((this.requete.getFichierPointe().substring(this.requete.getFichierPointe().lastIndexOf("/"))
                    .equals("/stoppeAgent.html")) && (this.requete.getType() == Constantes.GET)) {
                GestionMessage.message(0, "TraitementRequete", "Arret de l'agent");
                Serveur.fermetureService();
                // construction de la page de reponse
                String chaine = "HTTP/1.0 200 OK" + Constantes.RETOUR_CHARIOT + Constantes.RETOUR_CHARIOT;
                chaine += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                        + Constantes.RETOUR_CHARIOT;
                chaine += "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"fr\" lang=\"fr\">"
                        + Constantes.RETOUR_CHARIOT;
                chaine += "<head>" + Constantes.RETOUR_CHARIOT;
                chaine += "<title>Network Search</title>" + Constantes.RETOUR_CHARIOT;
                chaine += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />"
                        + Constantes.RETOUR_CHARIOT;
                chaine += "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />"
                        + Constantes.RETOUR_CHARIOT;
                chaine += "<meta http-equiv=\"Content-Style-Type\" content=\"text/css\" />" + Constantes.RETOUR_CHARIOT;
                chaine += "<meta http-equiv=\"Content-Language\" content=\"fr\" />" + Constantes.RETOUR_CHARIOT;
                chaine += "<link rel=\"stylesheet\" href=\"style/default.css\" type=\"text/css\" />"
                        + Constantes.RETOUR_CHARIOT;
                chaine += "</head>" + Constantes.RETOUR_CHARIOT;
                chaine += "<body>" + Constantes.RETOUR_CHARIOT;
                chaine += "<div id=\"body\">" + Constantes.RETOUR_CHARIOT;
                chaine += "<p>l'agent est maintenant ferm�</p>" + Constantes.RETOUR_CHARIOT;
                chaine += "</div>" + Constantes.RETOUR_CHARIOT;
                chaine += "</body>" + Constantes.RETOUR_CHARIOT;
                chaine += "</html>" + Constantes.RETOUR_CHARIOT;
                // envoi de la page de reponse
                sortie.println(chaine);
                sortie.close();
                sortie2.close();
            } else {
                // cas d'un contact d'un autre agent pour traitement d'une permutation
                if ((this.requete.getFichierPointe().substring(this.requete.getFichierPointe().lastIndexOf("/"))
                        .equals("/rechercheAgent.html")) && (this.requete.getType() == Constantes.POST)) {
                    this.reponse = new ReponseHTTP(new Fichier(this.requete.getFichierPointe()));
                    // recuperation du fichier XML dans la requete
                    String corps = this.requete.getSuiteRequete();
                    corps = corps.substring(0, corps.indexOf("</search>") + 9);
                    // affichage d'un message pour log
                    GestionMessage.message(0, "TraitementRequete", "Contact d'un autre agent");
                    Enregistrement enr = new Enregistrement();
                    // parsage du fichier XML
                    try {
                        // creation d'une fabrique de parseurs SAX
                        SAXParserFactory fabrique = SAXParserFactory.newInstance();

                        // creation d'un parseur SAX
                        SAXParser parseur = fabrique.newSAXParser();

                        EnregistrementHandler enregistrementHandler = new EnregistrementHandler();
                        InputSource xmlInputSource = new InputSource(new CharArrayReader(corps.toCharArray()));

                        parseur.parse(xmlInputSource, enregistrementHandler);
                        enr = enregistrementHandler.getEnregistrement();
                    } catch (ParserConfigurationException pce) {
                        GestionMessage.message(2, "TraitementRequete",
                                "Erreur de configuration du parseur, Lors de l'appel a SAXParser.newSAXParser() : "
                                        + pce);
                    } catch (SAXException se) {
                        GestionMessage.message(2, "TraitementRequete",
                                "Erreur de parsing, Lors de l'appel a parse() : " + se);
                    } catch (IOException ioe) {
                        GestionMessage.message(2, "TraitementRequete",
                                "Erreur d'entree/sortie, Lors de l'appel a parse() : " + ioe);
                    }
                    // permutation de l'enregistrement parse
                    Permutations.lancementPermutations(enr);
                    Enregistrement enre = Permutations.getEnrMR_liensPermutes();
                    // preparation et envoi de la reponse au serveur client
                    String renvoi = "HTTP/1.0 200 OK" + Constantes.RETOUR_CHARIOT + Constantes.RETOUR_CHARIOT;
                    renvoi += enre.toXml();
                    sortie.println(renvoi);
                    sortie.close();
                    sortie2.close();
                } else {
                    // cas requete de type post sur l'url recherche.html
                    if ((this.requete.getFichierPointe().substring(this.requete.getFichierPointe().lastIndexOf("/"))
                            .equals("/recherche.html")) && (this.requete.getType() == Constantes.POST)) {
                        this.reponse = new ReponseHTTP(new Fichier(this.requete.getFichierPointe()));
                        String motsCle = "";
                        // recuperation des mots cle
                        try {
                            motsCle = this.requete.getSuiteRequete().substring(
                                    this.requete.getSuiteRequete().indexOf("=") + 1,
                                    this.requete.getSuiteRequete().indexOf("&"));
                        } catch (StringIndexOutOfBoundsException e) {
                            motsCle = "meta moteur";
                        }
                        Vector moteur = new Vector();
                        Vector adresse = new Vector();
                        // construction du vecteur qui permettra de contacter les moteurs de recherche
                        // verification si la case representant Google est cochee
                        if (this.requete.getSuiteRequete().contains(new String("moteur2"))) {
                            int rep = 0;
                            String[] moteursConfig = Agent.CONFIG.getMoteurs();
                            // parcours du vecteur contenant les informations sur les moteurs de recherche
                            for (int i = 0; i < moteursConfig.length; i++) {
                                if (moteursConfig[i].equals("Google")) {
                                    rep = i + 1;
                                    // recuperation du nom du moteur de recherche
                                    moteur.add(moteursConfig[i]);
                                    break;
                                }
                            }
                            // recuperation de l'url de contact du moteur de recherche
                            adresse.add(moteursConfig[rep]);
                        }
                        // verification si la case representant Yahoo est cochee
                        if (this.requete.getSuiteRequete().contains(new String("moteur3"))) {
                            int rep = 0;
                            String[] moteursConfig = Agent.CONFIG.getMoteurs();
                            // parcours du vecteur contenant les informations sur les moteurs de recherche
                            for (int i = 0; i < moteursConfig.length; i++) {
                                if (moteursConfig[i].equals("Yahoo")) {
                                    rep = i + 1;
                                    // recuperation du nom du moteur de recherche
                                    moteur.add(moteursConfig[i]);
                                    break;
                                }
                            }
                            // recuperation de l'url de contact du moteur de recherche
                            adresse.add(moteursConfig[rep]);
                        }
                        // verification si la case representant Altavista est cochee
                        if (this.requete.getSuiteRequete().contains(new String("moteur4"))) {
                            int rep = 0;
                            String[] moteursConfig = Agent.CONFIG.getMoteurs();
                            // parcours du vecteur contenant les informations sur les moteurs de recherche
                            for (int i = 0; i < moteursConfig.length; i++) {
                                if (moteursConfig[i].equals("AltaVista")) {
                                    rep = i + 1;
                                    // recuperation du nom du moteur de recherche
                                    moteur.add(moteursConfig[i]);
                                    break;
                                }
                            }
                            // recuperation de l'url de contact du moteur de recherche
                            adresse.add(moteursConfig[rep]);
                        }
                        String url = "";
                        Enregistrement enr = new Enregistrement();
                        // parcours du vecteur contenant les moteurs
                        for (int i = 0; i < moteur.size(); i++) {
                            url = (String) adresse.get(i);
                            // creation de l'url a envoyer au moteur
                            url += motsCle.replaceAll(" ", "+");
                            // recuperation du nom du moteur courant
                            String temp = (String) moteur.get(i);
                            // affichage de messages pour log
                            GestionMessage.message(0, "TraitementRequete", "Contact d'un moteur de recherche");
                            GestionMessage.message(0, "TraitementRequete",
                                    "Recherche sur : " + URLDecoder.decode(motsCle, "ISO-8859-1"));
                            // cas Google (seul traite)
                            if (temp.equals("Google"))
                                // requete au moteur de recherche et parsage de sa reponse
                                enr = new Enregistrement(ParsingGoogle.htmlParsing(Client.envoiRequeteGET(url)));
                        }
                        if (enr.getLiens().size() == 0) {
                            // renvoie d'une page informant le client que le moteur de recherche n'a pas
                            // repondu
                            sortie.println(ReponseHTTP.pasDeReponse(URLDecoder.decode(motsCle, "ISO-8859-1")));
                        } else {
                            // stockage des mots cle dans l'enregistrement
                            enr.setKeywords(URLDecoder.decode(motsCle, "ISO-8859-1"));
                            Vector reponses = new Vector();
                            // verification que la liste des agents est non vide
                            String[] contactsConfig = Agent.CONFIG.getContacts();
                            if (!contactsConfig[0].equals("")) {
                                // parcours de la liste des agents pour les contacter
                                for (int i = 0; i < contactsConfig.length; i++) {
                                    String adresseAgent = contactsConfig[i];
                                    // construction des arguments pour la requete
                                    int port = Integer.parseInt(adresseAgent.substring(adresseAgent.indexOf(":") + 1));
                                    adresseAgent = adresseAgent.substring(0, adresseAgent.indexOf(":"));
                                    // affichage de message pour log
                                    GestionMessage.message(0, "TraitementRequete",
                                            "Contact d'un agent " + adresseAgent);
                                    // requete a l'agent courant courant
                                    String res = Client.envoiRequetePOST(adresseAgent, "/rechercheAgent.html",
                                            enr.toXml(), port);
                                    // si le client a repondu
                                    if (res != null) {
                                        Enregistrement enreg = new Enregistrement();
                                        try {
                                            // creation d'une fabrique de parseurs SAX
                                            SAXParserFactory fabrique = SAXParserFactory.newInstance();

                                            // creation d'un parseur SAX
                                            SAXParser parseur = fabrique.newSAXParser();

                                            // parsage et recuperation de l'enregistrement
                                            EnregistrementHandler enregistrementHandler = new EnregistrementHandler();
                                            InputSource xmlInputSource = new InputSource(
                                                    new CharArrayReader(res.toCharArray()));
                                            parseur.parse(xmlInputSource, enregistrementHandler);
                                            enreg = enregistrementHandler.getEnregistrement();
                                            // gestion des erreurs
                                        } catch (ParserConfigurationException pce) {
                                            GestionMessage.message(2, "TraitementRequete",
                                                    "Erreur de configuration du parseur, Lors de l'appel a SAXParser.newSAXParser() : "
                                                            + pce);
                                        } catch (SAXException se) {
                                            GestionMessage.message(2, "TraitementRequete",
                                                    "Erreur de parsing, Lors de l'appel a parse() : " + se);
                                        } catch (IOException ioe) {
                                            GestionMessage.message(2, "TraitementRequete",
                                                    "Erreur d'entree/sortie, Lors de l'appel a parse() : " + ioe);
                                        }
                                        // ajout de l'enregistrement dans le vecteur des reponses, si celui-ci est non
                                        // vide
                                        if (enreg.getLiens().size() != 0)
                                            reponses.add(enreg);
                                    }
                                }
                            }
                            // permutation locale

                            Permutations.lancementPermutations(enr);
                            Enregistrement enre = Permutations.getEnrMR_liensPermutes();
                            // mise a jour de la base de donnees
                            GestionBDD.insertEnregistrement(enre);
                            // tri des enregistrements
                            Tri.lancementTri(enre, reponses);
                            Enregistrement enr_trie = Tri.getEnrMR_TriFinal();
                            // generation d'une page de reponse
                            String page = ReponseHTTP.debutReponseHTML();
                            boolean reponsesClients;
                            if (reponses.size() == 0)
                                reponsesClients = false;
                            else
                                reponsesClients = true;
                            page += enr_trie.toXhtml(reponsesClients);
                            // envoi de la page de reponse generee
                            sortie.println(page);
                            // fermeture des flux
                            sortie.close();
                            sortie2.close();
                        }
                    } else {
                        // cas requete GET sur redirect.html (redirection)
                        if ((this.requete.getFichierPointe().substring(this.requete.getFichierPointe().lastIndexOf("/"))
                                .equals("/redirect.html")) && (this.requete.getType() == Constantes.GET)) {
                            this.reponse = new ReponseHTTP(new Fichier(this.requete.getFichierPointe()),
                                    this.requete.getSuiteRequete());
                            // affichage d'un message pour log
                            GestionMessage.message(0, "TraitementRequete",
                                    "Redirection vers " + this.requete.getSuiteRequete());
                            // envoi de la reponse
                            this.reponse.redirection(sortie, this.requete.getSuiteRequete());
                            // fermeture des flux
                            sortie.close();
                            sortie2.close();
                            // mise a jour de la base de donnees
                            GestionBDD.updateURL(this.requete.getSuiteRequete());
                        } else {
                            // cas demande d'un fichier quelconque
                            if (new Fichier(this.requete.getFichierPointe()).fichierExiste()) {
                                this.reponse = new ReponseHTTP(new Fichier(this.requete.getFichierPointe()));
                                // envoi de la reponse
                                if (new Fichier(this.requete.getFichierPointe()).getType() == Constantes.TEXTE)
                                    this.reponse.envoieFichierTexte(sortie2, 200,
                                            new Fichier(this.requete.getFichierPointe()));
                                else {
                                    this.reponse.envoieImage(sortie2, new Fichier(this.requete.getFichierPointe()));
                                }
                                // fermeture des flux
                                sortie.close();
                                sortie2.close();
                            } else {
                                // renvoi d'une page pour informer le client de l'indisponibilite de la page
                                // demandee
                                sortie.println(ReponseHTTP.erreur404(this.requete));
                                // fermeture des flux
                                sortie.close();
                                sortie2.close();
                            }
                        }
                    }
                }
            }
            socket.close();
            // construction et affichage du message pour log
            String type;
            if (this.requete.getType() == Constantes.GET)
                type = "GET";
            else
                type = "POST";
            GestionMessage.message(0, "TraitementRequete",
                    "Requete traitee type : " + type + " sur le fichier " + this.requete.getFichierPointe());
            Serveur.supprClient();
        } catch (IOException e) {
            GestionMessage.message(1, "TraitementRequete", "Erreur entree/sortie : " + e);
        } finally {
            try {
                // fermeture des flux et du socket
                sortie2.close();
                entree.close();
                sortie.close();
                this.socket.close();
            } catch (IOException e) {
                GestionMessage.message(1, "TraitementRequete",
                        "Erreur a la fermeture des flux et du socket de communication : " + e);
            }
        }
    }
}
