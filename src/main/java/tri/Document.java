package tri;

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
 * Titre       : classe publique Document
 * Description : classe permettant de creer un Document avec:
 * - un Identifiant unique,
 * - des mots cles,
 * - une url,
 * - un titre,
 * - une description,
 * - un rang.
 * Cette classe sera utilisee, dans la classe
 * Permutations, en ce qui concerne le vote
 * de la place d'un document.
 *
 * @author Jeremy FRECHARD
 * @author C�cile GIRARD
 * @author Aysel GUNES
 * @author Pierre RAMOS
 * @version 1.0
 */

import agent.*;

public class Document extends Lien {

    private int id; // Identifiant d'un document
    private Lien lien;

    /**
     * Constructeur par defaut. Initialisation de: - l'identifiant a -1, - des mots
     * cles a NULL, - de l'url a NULL, - du tire a NULL, - de la description a NULL,
     * - du rang a 0.
     */
    public Document() {
        id = -1;
        lien = new Lien();
    }

    /**
     * Constructeur permettant d'initialiser un document a parir de: son
     * identifiant, ses mots cles, son url, son titre, sa description et son rang.
     *
     * @param id
     *            Un entier representant un identifiant de document.
     * @param lien
     *            Un Lien
     */
    public Document(int id, Lien lien) {
        this.id = id;
        this.lien = lien;
    }

    /**
     * Retourne l'identifiant du document courant.
     *
     * @return int : Un entier representant l'identifiant du document courant.
     */
    public int getIdDoc() {
        return id;
    }

    /**
     * Retourne les liens correspondant au document courant.
     *
     * @return Lien
     */
    public Lien getLienDoc() {
        return lien;
    }

    /**
     * Affichage du contenu d'un lien.
     *
     * @return String
     */
    @Override
    public String toString() {
        return id + "," + lien.toString() + "\n";
    }
}
