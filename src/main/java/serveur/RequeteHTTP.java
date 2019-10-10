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

import java.util.Vector;

/**
 * Classe modelisant une requete HTTP geree par le serveur
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 * @see Serveur
 */
public class RequeteHTTP {
    private String corps;
    private Fichier fichierPointe;
    private String suiteRequete;
    private boolean suiteRequetePresente;
    private int type;
    private Vector options;

    /**
     * Constructeur d'une Requete HTTP
     *
     * @param corps corps de la requete a construire
     */
    public RequeteHTTP(String corps) {
        // recuperation du corps de la requete
        this.corps = corps;
        // detection du type de la requete
        this.detectType();
        // detection du fichier pointe
        this.detectFichierPointe();
        // test du type de la requete
        if (this.type == Constantes.GET) {
            // verification de la presence d'un point d'interrogation
            this.suiteRequetePresente = this.pointInterPresent();
            // recuperation de la suite de la requete si celle ci est presente
            if (this.suiteRequetePresente)
                this.suiteRequete = this.corps.substring(this.corps.indexOf('?') + 1,
                        this.corps.indexOf(' ', this.corps.indexOf('?')));
            else
                this.suiteRequete = null;
        } else {
            // recuperation de la suite de la requete si celle ci est presente
            if (this.corps.indexOf(Constantes.RETOUR_CHARIOT + Constantes.RETOUR_CHARIOT) != this.corps.length() - 5) {
                this.suiteRequetePresente = true;
                this.suiteRequete = this.corps
                        .substring(this.corps.indexOf(Constantes.RETOUR_CHARIOT + Constantes.RETOUR_CHARIOT) + 4);
            } else {
                this.suiteRequetePresente = false;
                this.suiteRequete = null;
            }
        }
        // recuperation des options de la requete
        this.options = new Vector();
        rechercheOptions();
    }

    /**
     * Methode d'acces a l'attribut prive type
     *
     * @return retourne le type de la requete envoyee par le client
     */
    public int getType() {
        return (this.type);
    }

    /**
     * Methode d'acces a l'attribut prive coprs
     *
     * @return retourne le corps de la requete envoyee par le client
     */
    public String getCorps() {
        return (this.corps);
    }

    /**
     * Methode d'acces a l'attribut prive fichierPointe
     *
     * @return retourne le fichier pointe par la requete envoyee par le client
     */
    public String getFichierPointe() {
        if (!this.fichierPointe.toString().endsWith("/"))
            return (this.fichierPointe.toString());
        else
            return (this.fichierPointe.toString() + Agent.PageIndex);
    }

    /**
     * Methode d'acces a l'attribut prive suiteRequete
     *
     * @return retourne la chaine qiu suit la requete
     */
    public String getSuiteRequete() {
        return (this.suiteRequete);
    }

    /**
     * Methode d'acces a l'attribut prive suiteRequetePresente
     *
     * @return retourne true si la chaine contient une suite
     */
    public boolean isSuitePresente() {
        return (this.suiteRequetePresente);
    }

    /**
     * Methode permettant de calculer le type de la requete envoyee par le client
     */
    private void detectType() {
        if (this.corps.startsWith("GET"))
            this.type = Constantes.GET;
        else {
            if (this.corps.startsWith("POST"))
                this.type = Constantes.POST;
            else
                this.type = Constantes.INCONNU;
        }
    }

    /**
     * Methode permettant de calculer le fichier pointe par la requete envoyee par
     * le client
     */
    private void detectFichierPointe() {
        if (this.type == Constantes.GET) {
            fichierPointe = new Fichier(this.corps.substring(4, this.detectDernierCarFichierPointe(4)));
        }
        if (this.type == Constantes.POST) {
            fichierPointe = new Fichier(this.corps.substring(5, this.detectDernierCarFichierPointe(5)));
        }
    }

    /**
     * Methode permettant de recuperer la position du dernier caractere du nom pour
     * la methode de detection du nom de fichier pointe
     *
     * @return retourne la position du dernier caractere du nom du fichier
     */
    private int detectDernierCarFichierPointe(int depart) {
        int pointInter;
        int espace;
        espace = this.corps.indexOf(' ', depart);
        pointInter = this.corps.indexOf('?', depart);
        if (pointInter == -1)
            return (espace);
        else {
            if (pointInter < espace)
                return (pointInter);
            else
                return (espace);
        }
    }

    /**
     * Methode verifiant si il y a une suite apres le fichier pointe (si c'est de la
     * forme "fichier"?"quelqueChose")
     *
     * @return retourne true un ? est detecte a la fin du nom du fichier pointe,
     * false sinon
     */
    private boolean pointInterPresent() {
        char caractere;
        if (this.type == Constantes.GET) {
            caractere = this.corps.charAt(4 + this.fichierPointe.toString().length());
        } else if (this.type == Constantes.POST) {
            caractere = this.corps.charAt(5 + this.fichierPointe.toString().length());
        } else
            caractere = ' ';
        return (caractere == '?');
    }

    /**
     * Methode permettant de recuperer les options contenue dans la requete HTTP
     */
    private void rechercheOptions() {
        // recuperation d'un vecteur contenant les options connues
        Vector vecteur = OptionRequeteHTTP.listeOptions();
        int taille = vecteur.size();
        // parsage de la requete pour en recuperer les options
        for (int i = 0; i < taille; i++) {
            // recuperation d'une option pour recherche dans la requete
            String nom = (String) vecteur.get(i);
            nom += ": ";
            // recherche de l'option
            int position = this.corps.indexOf(nom);
            // si l'option est trouvee
            if (position != -1) {
                String valeur;
                // recuperation de la taille de l'option
                position += nom.length();
                // recuperation de l'option
                valeur = this.corps.substring(position, this.corps.indexOf(Constantes.RETOUR_CHARIOT, position));
                this.options.add(new OptionRequeteHTTP(nom, valeur));
            }
        }
    }
}
