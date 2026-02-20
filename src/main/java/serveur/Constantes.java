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
public class Constantes {

    // -------------------------------------------------------------------------
    // Types de methodes HTTP
    // -------------------------------------------------------------------------

    /** Code representant une requete HTTP de type GET. */
    public static final int GET = 1;

    /** Code representant une requete HTTP de type POST. */
    public static final int POST = 2;

    /** Code representant une requete HTTP de type inconnu ou non supporte. */
    public static final int INCONNU = 0;

    // -------------------------------------------------------------------------
    // Types MIME de contenu
    // -------------------------------------------------------------------------

    /** Code de type MIME pour les fichiers texte (HTML, CSS, JS, TXT). */
    public static final int TEXTE = 1;

    /** Code de type MIME pour les images JPEG ({@code .jpg} / {@code .jpeg}). */
    public static final int IMAGE_JPG = 2;

    /** Code de type MIME pour les images BMP ({@code .bmp}). */
    public static final int IMAGE_BMP = 3;

    /** Code de type MIME pour les images GIF ({@code .gif}). */
    public static final int IMAGE_GIF = 4;

    /** Code de type MIME pour les images PNG ({@code .png}). */
    public static final int IMAGE_PNG = 5;

    // -------------------------------------------------------------------------
    // Parametres de transfert
    // -------------------------------------------------------------------------

    /** Taille en octets du tampon de lecture lors de l'envoi de fichiers. */
    public static final int TAILLE_BLOC = 1024;

    /**
     * Sequence de retour a la ligne conforme au protocole HTTP (CR+LF).
     * Utilisee pour separer les lignes de l'en-tete et le corps d'une reponse.
     */
    public static final String RETOUR_CHARIOT = "\r\n";
}
