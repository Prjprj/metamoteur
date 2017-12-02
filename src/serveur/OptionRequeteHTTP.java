package serveur;

import java.util.Vector;

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
 * Classe modelisant les differentes options que peut contenir une requete HTTP
 * 
 * @see RequeteHTTP
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class OptionRequeteHTTP {
	private String nom;
	private String valeur;
	
	/**
	 * Constructeur d'une OptionRequete HTTP
	 * 
	 * @param nom nom de l'option
	 * @param valeur valeur de l'option
	 */
	public OptionRequeteHTTP(String nom,String valeur){
		this.nom=new String(nom);
		this.valeur=new String (valeur);
	}
	/**
	 * Constructeur d'une OptionRequete HTTP
	 */
	public OptionRequeteHTTP(){
		this.nom=new String("");
		this.valeur=new String("");
	}
	
	/**
	 * Methode permettant d'acceder a l'attribut prive nom de cette option
	 * 
	 * @return retourne le nom de l'option
	 */
	public String getNom(){
		return(this.nom);
	}
	/**
	 * Methode permettant d'acceder a l'attribut prive valeur de cette option
	 * 
	 * @return retourne la valeur de l'option
	 */
	public String getValeur(){
		return(this.valeur);
	}
	
	/**
	 * Methode renvoyant un vecteur contenant les differents options des requetes HTTP
	 * 
	 * @return retourne un vecteur contenant les differents options des requetes HTTP
	 */
	public static Vector listeOptions(){
		Vector vecteur=new Vector();
		//construction d'un vecteur contenant les options reconnues par le serveur
		vecteur.add(new String("Accept"));
		vecteur.add(new String("Accept-Charset"));
		vecteur.add(new String("Accept-Encoding"));
		vecteur.add(new String("Accept-Language"));
		vecteur.add(new String("Authorization"));
		vecteur.add(new String("Content-Encoding"));
		vecteur.add(new String("Content-Language"));
		vecteur.add(new String("Content-Length"));
		vecteur.add(new String("Content-Type"));
		vecteur.add(new String("Date"));
		vecteur.add(new String("Fowarded"));
		vecteur.add(new String("From"));
		vecteur.add(new String("Link"));
		vecteur.add(new String("Orig-URL"));
		vecteur.add(new String("Referer"));
		vecteur.add(new String("User-Agent"));
		vecteur.add(new String("Keep-Alive"));
		vecteur.add(new String("Connection"));
		vecteur.add(new String("Host"));
		return vecteur;
	}
	/**
	 * Methode toString de cette classe
	 * 
	 * @return retourne une chaine representant l'objet
	 */
	public String toString(){
		return(this.nom+" "+this.valeur);
	}
}
