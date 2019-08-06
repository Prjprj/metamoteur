package moteur;

import agent.Enregistrement;
import client.Client;
import org.junit.Test;

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
 * Classe de tests pour la gestion de parsing Google
 * 
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 0.9
 */
public class TestParsingGoogle {
	@Test
	public void runTestParsingGoogle() {
		String url; // The URL to read
		// url avec r�sultats + pub + desc vide
		// url = new
		// URL("http://www.google.fr/search?num=20&hl=fr&q=business+intelligence&btnG=Rechercher&meta=");
		// url avec des r�sulats en japonais
		// url = new
		// URL("http://www.google.fr/search?num=20hl=fr&q=regex+java&btnG=Recherche+Google&meta=");
		// url avec des fichiers ppt
		// url = new
		// URL("http://www.google.fr/search?num=20&hl=fr&q=filetype%3Appt+lol&btnG=Rechercher&meta=");
		// url avec des r�sulats pdf
		// url = new
		// URL("http://www.google.fr/search?num=20&hl=fr&q=pdf&btnG=Rechercher&meta=");
		// url avec des r�sulats doc
		// url = new
		// URL("http://www.google.fr/search?q=document+word+*.doc&btnG=Rechercher&num=20&hl=fr");
		// url sans r�sultats
		// url = new
		// URL("http://www.google.fr/search?num=20&hl=fr&q=hdghgfghghfgh&btnG=Rechercher&meta=");
		// url pas de description pour certain liens
		url = "http://www.google.fr/search?num=20&hl=fr&q=google&btnG=Recherche+Google";
		// url fichier extention inconnue !!! 15 / 16

		Enregistrement enr = new Enregistrement(ParsingGoogle.htmlParsing(Client.envoiRequeteGET(url)));
		enr.setKeywords("google"); // on initialise les mots-Cl�s dans enr
		// appel de la m�thode !!!
		// Agent.BaseBDD

		// pour ins�rer un enr dans la Bdd
		// GestionBDD.envoiRequete(enr.toSql());

		// pour affichage
		enr.toString();

	}
}
