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
/**
 * Interface contenant toutes les constantes utilisees par le serveur
 * 
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public interface Constantes {
	public static final int GET = 1;
	public static final int POST = 2;
	public static final int INCONNU = 0;
	public static final int TEXTE = 1;
	public static final int IMAGE_JPG = 2;
	public static final int IMAGE_BMP = 3;
	public static final int IMAGE_GIF = 4;
	public static final int IMAGE_PNG = 5;
	public static final int TAILLE_BLOC = 1024;
	public static final String retourChariot = "\r\n";
}
