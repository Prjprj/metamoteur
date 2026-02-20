package bdd;

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
import agent.AppConfigLoader;
import agent.Enregistrement;
import agent.Lien;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Classe de tests pour la gestion d'une base de donnees
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class TestBDD {
    @Test
    public void runTestBDD1() {
        // Initialisation de la configuration depuis le fichier .conf
        Agent.CONFIG = AppConfigLoader.load(Agent.FichierConf);
		/*try {
			// Test Connexion
			Connection connection = GestionBDD.connectionBDD();
			try (Statement statement = connection.createStatement()) {
				statement.executeQuery("select * from BDD");
			}
		} catch (Exception e) {
			// Affichage d'un message d'erreur en cas de non possibilite de la connection a
			// la base
			System.out.println(e.getMessage());
		}*/

        // Test Select
        Vector res = GestionBDD.envoiRequete(GestionBDD.construitSelect("*", "BDD", " "));
        assertNotNull("Select * from BDD", res);

        /*
         * Vector
         * res=GestionBDD.envoiRequete(GestionBDD.construitSelect("*","cles"," "));
         * System.out.println(res);
         * GestionBDD.envoiRequete(GestionBDD.construitInsert("cles",
         * "\"AZERTY\",\"1\",\"2006-05-24\""));
         * res=GestionBDD.envoiRequete(GestionBDD.construitSelect("*","cles"," "));
         * System.out.println(res);
         * GestionBDD.envoiRequete(GestionBDD.construitUpdate("cles","validite=6",
         * "cle=\"AZERTY\""));
         * res=GestionBDD.envoiRequete(GestionBDD.construitSelect("*","cles"," "));
         * System.out.println(res);
         */
    }

    @Test
    public void runTestBDD2() {
        // Initialisation de la configuration depuis le fichier .conf
        Agent.CONFIG = AppConfigLoader.load(Agent.FichierConf);
        // construction d'un lien pour test
        Lien lien = new Lien("url&�\"����^�~", "titre", "desc", 1, 0);
        // construction d'un vecteur de liens
        Vector vecteur = new Vector();
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        lien = new Lien("http://www.ilocis.org/fr/help.html", "titre", "desc", 1, 0);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        vecteur.add(lien);
        // insertion de l'enregistement
        assertTrue("Insert", GestionBDD.insertEnregistrement(new Enregistrement(-1, "mot cle", vecteur)));
    }

    @Test
    public void runTestBDD3() {
        // Initialisation de la configuration depuis le fichier .conf
        Agent.CONFIG = AppConfigLoader.load(Agent.FichierConf);
        // tentative d'update
        assertTrue("Update", GestionBDD.updateURL("http://www.ilocis.org/fr/help.html"));
    }
}