package serveur;

import java.io.File;

import agent.Agent;

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
 * Classe modelisant un fichier et permettant de le manipuler
 * 
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class Fichier {
	private String nom;
	private int type;
	private long taille;

	/**
	 * Constructeur d'un Fichier
	 * 
	 * @param nom
	 *            nom du fichier
	 */
	public Fichier(String nom) {
		this.nom = new String(nom);
		// detection du type
		this.type = detectType();
		// ouverture du fichier pour en avoir la taille
		File fich = new File(System.getProperty("user.dir") + Agent.Path, this.nom);
		this.taille = fich.length();
	}

	/**
	 * Methode permettant d'acceder a l'attribut prive type
	 * 
	 * @return retourne le type de fichier (par rapport a son extension)
	 */
	public int getType() {
		return (this.type);
	}

	/**
	 * Methode toString heritee de Object reimplementee pour renvoyer le nom du
	 * fichier
	 * 
	 * @return retourne le nom du fichier
	 */
	public String toString() {
		return (this.nom);
	}

	/**
	 * Methode permettant d'acceder a l'attribut prive taille
	 * 
	 * @return retourne la taille du fichier
	 */
	public long getTaille() {
		return (this.taille);
	}

	/**
	 * Methode permettant de trouver le type du fichier en fonction de son extension
	 * 
	 * @return retourne le type du fichier
	 */
	private int detectType() {
		// recuperation de l'extension pour en connaitre le type
		String extension = getExtension();
		if (extension.equals("conf") || extension.equals("html") || extension.equals("htm") || extension.equals("css")
				|| extension.equals("js") || extension.equals("txt"))
			return Constantes.TEXTE;
		else {
			if (extension.equals("jpg") || extension.equals("jpeg"))
				return Constantes.IMAGE_JPG;
			else {
				if (extension.equals("bmp"))
					return Constantes.IMAGE_BMP;
				else {
					if (extension.equals("gif"))
						return Constantes.IMAGE_GIF;
					else {
						if (extension.equals("png"))
							return Constantes.IMAGE_PNG;
					}
				}
			}
			return Constantes.INCONNU;
		}
	}

	/**
	 * Methode permettant de recuperer l'extension d'un fichier
	 * 
	 * @return retourne un String contenant l'extension du fichier
	 */
	private String getExtension() {
		return (new String(this.nom.substring(this.nom.lastIndexOf(".") + 1)));
	}

	/**
	 * Methode renvoyant true si le fichier existe dans le systeme de fichier
	 * 
	 * @return retourne true si le fichier existe dans le systeme de fichier
	 */
	public boolean fichierExiste() {
		// verification que le fichier existe
		File fich = new File(System.getProperty("user.dir") + Agent.Path, this.nom);
		if ((fich.exists()) && (fich.isFile()))
			return true;
		else
			return false;
	}
}
