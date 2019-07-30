package serveur;

import java.util.Date;

/*
 * This file is part of "M�ta-moteur".
 *
 * (c) M�ta-moteur 2005-2006. All Rights Reserved.
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
 * Classe modelisant les differentes options que peut contenir une requete HTTP
 * 
 * @see RequeteHTTP
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class OptionReponseHTTP {
	private String nom;
	private String valeur;

	/**
	 * Constructeur d'une OptionReponse HTTP
	 * 
	 * @param nom
	 *            nom de l'option
	 * @param valeur
	 *            valeur de l'option
	 */
	public OptionReponseHTTP(String nom, String valeur) {
		this.nom = new String(nom);
		this.valeur = new String(valeur);
	}

	/*
	 * autres options HTTP Content-Encoding Content-Language Expires Forwarded
	 * Location
	 */
	/**
	 * Constructeur d'une OptionReponse HTTP
	 * 
	 * @param nom
	 *            nom de l'option
	 */
	public OptionReponseHTTP(String nom) {
		if (nom.equals("Server"))
			this.construcServ();
		if (nom.equals("Date"))
			this.construcDate();
	}

	/**
	 * Constructeur d'une OptionReponse HTTP
	 */
	public OptionReponseHTTP() {
		this.nom = new String("");
		this.valeur = new String("");
	}

	/**
	 * Methode construisant un nouvel objet OptionRequeteHTTP de type Server
	 */
	public void construcServ() {
		// construction d'une option "Server"
		this.nom = new String("Server");
		this.valeur = new String("MetaMoteur de recherche oriente communaute");
	}

	/**
	 * Methode construisant un nouvel objet OptionRequeteHTTP de type Date
	 */
	public void construcDate() {
		// construction d'une option "Date"
		this.nom = new String("Date");
		Date date = new Date();
		this.valeur = new String(date.toString());
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
	 * Methode toString de cette classe
	 * 
	 * @return retourne une chaine representant l'objet
	 */
	public String toString() {
		return (this.nom + " : " + this.valeur);
	}
}
