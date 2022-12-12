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

import java.util.Vector;

/**
 * Classe modelisant les differentes options que peut contenir une requete HTTP
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 * @see RequeteHTTP
 */
public class OptionRequeteHTTP {
    private String nom;
    private String valeur;

    /**
     * Constructeur d'une OptionRequete HTTP
     *
     * @param nom    nom de l'option
     * @param valeur valeur de l'option
     */
    public OptionRequeteHTTP(String nom, String valeur) {
        this.nom = nom;
        this.valeur = valeur;
    }

    /**
     * Constructeur d'une OptionRequete HTTP
     */
    public OptionRequeteHTTP() {
        this.nom = "";
        this.valeur = "";
    }

    /**
     * Methode permettant d'acceder a l'attribut prive nom de cette option
     *
     * @return retourne le nom de l'option
     */
    public String getNom() {
        return (this.nom);
    }

    /**
     * Methode permettant d'acceder a l'attribut prive valeur de cette option
     *
     * @return retourne la valeur de l'option
     */
    public String getValeur() {
        return (this.valeur);
    }

    /**
     * Methode renvoyant un vecteur contenant les differents options des requetes
     * HTTP
     *
     * @return retourne un vecteur contenant les differents options des requetes
     * HTTP
     */
    public static Vector listeOptions() {
        Vector vecteur = new Vector();
        // construction d'un vecteur contenant les options reconnues par le serveur
        vecteur.add("Accept");
        vecteur.add("Accept-Charset");
        vecteur.add("Accept-Encoding");
        vecteur.add("Accept-Language");
        vecteur.add("Authorization");
        vecteur.add("Content-Encoding");
        vecteur.add("Content-Language");
        vecteur.add("Content-Length");
        vecteur.add("Content-Type");
        vecteur.add("Date");
        vecteur.add("Fowarded");
        vecteur.add("From");
        vecteur.add("Link");
        vecteur.add("Orig-URL");
        vecteur.add("Referer");
        vecteur.add("User-Agent");
        vecteur.add("Keep-Alive");
        vecteur.add("Connection");
        vecteur.add("Host");
        return vecteur;
    }

    /**
     * Methode toString de cette classe
     *
     * @return retourne une chaine representant l'objet
     */
    public String toString() {
        return (this.nom + " " + this.valeur);
    }
}
