package agent;

import org.xml.sax.*;

import javax.xml.parsers.*;

import java.io.*;

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
 * Classe de tests du parsing de fichiers Xml
 * 
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 0.9
 */
public class TestEnregistrementHandler {

	final static String xmlReponse = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\" ?>"
			+ "<!DOCTYPE search [" + "  <!ELEMENT search (keywords,list)>" + "  <!ELEMENT keywords (#PCDATA)>"
			+ "  <!ELEMENT list (link)*>" + "  <!ELEMENT link (title, url, desc)>" + "  <!ELEMENT title (#PCDATA)>"
			+ "  <!ELEMENT url (#PCDATA)>" + "  <!ELEMENT desc (#PCDATA)>" + "  <!ATTLIST list links CDATA #IMPLIED>"
			+ "  <!ATTLIST link rank CDATA #IMPLIED" + "  				score CDATA #IMPLIED>" + "]>" + "<search>"
			+ "	<keywords>Exemples de recherche</keywords>" + "			<list>"
			+ "				<link rank=\"1\" score=\"1\">"
			+ "					<title>INRA - Exemples de recherche</title>"
			+ "					<url>http://www.inra.fr/les_recherches/exemples_de_recherche</url>"
			+ "					<desc>Faits marquants de recherche, exemples d'activités.</desc>"
			+ "				</link>" + "				<link rank=\"2\" score=\"0\">"
			+ "					<title>INRA - Premier séquençage d'une bactérie du yaourt : Streptococcus ...</title>"
			+ "					<url>http://www.inra.fr/les_recherches/exemples_de_recherche/premier_sequencage_d_une_bacterie_du_yaourt_streptococcus_thermophilus__1</url>"
			+ "					<desc>La recherche Inra : pourquoi, sur quoi, comment ? | Exemples de recherche | Annuaire des sites Web | Ressources scientifiques ...</desc>"
			+ "				</link>" + "				<link rank=\"3\" score=\"0\">"
			+ "					<title>AERIS - Exercices/Exemples de recherche sur Internet</title>"
			+ "					<url>http://users.11vm-serv.net/aeris/exercices/exemples.html</url>"
			+ "					<desc>Aide aux étudiants pour la recherche d'information scientifique. Cours, exercices et outils (moteurs, annuaires etc.) Les principaux outils de recherche, ...</desc>"
			+ "				</link>" + "				<link rank=\"4\" score=\"0\">"
			+ "					<title>Développement de l'élevage en Afrique subsaharienne - Un exemple ...</title>"
			+ "					<url>http://www.diplomatie.gouv.fr/fr/ministere_817/publications_827/cooperation-internationale-developpement_3030/serie-reperes_3035/developpement-elevage-afrique-subsaharienne_2485/un-exemple-recherche-regionale-cirdes_4517.html</url>"
			+ "					<desc>Un exemple de recherche régionale : le CIRDES. En 1991, le Centre de Recherches sur les Trypanosomoses Animales (CRTA) de Bobo-Dioulasso (Burkina), ...</desc>"
			+ "				</link>" + "				<link rank=\"5\" score=\"0\">"
			+ "					<title>exemple de recherche : modéliser le renouvellement des conduites d'eau</title>"
			+ "					<url>http://www.cemagref.fr/INformations/Ex-rechr/eau-dechets/bremond/Bremond-exemple.htm</url>"
			+ "					<desc>Exemples de recherche Modéliser le renouvellement des conduites d'eau ... Cet exemple répond à un enjeu : technologies et services de l'eau , des effluents ...</desc>"
			+ "				</link>" + "				<link rank=\"6\" score=\"0\">"
			+ "					<title>exemple de recherche : chapo - Evaluation et cartographie du ...</title>"
			+ "					<url>http://www.cemagref.fr/Informations/Ex-rechr/rural/eval-carto/mariel.html</url>"
			+ "					<desc>Evaluation et cartographie du risque d'incendie de forêt sur le massif par Cemagref.</desc>"
			+ "				</link>" + "				<link rank=\"7\" score=\"0\">"
			+ "					<title>SAPRISTI ! SAPRISTI ! PRESENTATION TROUVER des documents ...</title>"
			+ "					<url>http://docinsa.insa-lyon.fr/sapristi/index.php?rub=0505</url>"
			+ "					<desc>CHOISIR des outils de recherche > Exemple de recherche sur Altavista > Accueil ... Altavista n'est pas forcément le moteur de recherche le plus exhaustif. ...</desc>"
			+ "				</link>" + "				<link rank=\"8\" score=\"0\">"
			+ "					<title>SAPRISTI ! SAPRISTI ! PRESENTATION TROUVER des documents ...</title>"
			+ "					<url>http://docinsa.insa-lyon.fr/sapristi/index.php?rub=0602</url>"
			+ "					<desc>APPLIQUER quelques conseils > Exemple de recherche sur Altavista > Accueil ... Altavista n'est pas forcément le moteur de recherche le plus exhaustif. ...</desc>"
			+ "				</link>" + "				<link rank=\"9\" score= \"0\">"
			+ "					<title>NotreTemps.com - Méthode : un exemple de recherche</title>"
			+ "					<url>http://www.notretemps.com/article/index.jsp?docId=1292205&amp;rubId=14095</url>"
			+ "					<desc>Méthode : un exemple de recherche Vous venez de dresser l'inventaire de toutes les sources d'information disponibles chez vous ou dans votre entourage et ...</desc>"
			+ "				</link>" + "				<link rank=\"10\" score=\"0\">"
			+ "					<title>Science Citation Index Expanded</title>"
			+ "					<url>http://www.inist.fr/evalsciences/documentation/wos/scisearch_fr.pdf</url>"
			+ "					<desc>Exemple de recherche dans le Science Citation Index Expanded. Le. texte complet. vous offre une liaison. impeccable au texte. complet de l'article ...</desc>"
			+ "				</link>" + "				<link rank=\"11\" score=\"0\">"
			+ "					<title>Un exemple de recherche en droit : le CRJO</title>"
			+ "					<url>http://www.spc.univ-rennes1.fr/Rennes1Campus/crjo.pdf</url>"
			+ "					<desc>Un exemple de recherche en droit :. le CRJO. Depuis sa création, le CRJO a toujours travaillé sur la justice. même si, avant 1992, elle s'intéressait ...</desc>"
			+ "				</link>" + "				<link rank=\"12\" score=\"0\">"
			+ "					<title>DSI. Le palmarès des villes, places d'affaires, best cities ou ...</title>"
			+ "					<url>http://www.dsi-info.ca/palmares-des-villes-best-cities.html</url>"
			+ "					<desc>La stratégie de recherche utilisée ici est un exemple. D'autres outils de recherche peuvent être utilisés comme le logiciel de recherche Copernic ou un ...</desc>"
			+ "				</link>" + "				<link rank=\"13\" score=\"0\">"
			+ "					<title>Exemple de recherche</title>"
			+ "					<url>http://www.pages-igbp.org/about/national/canada/fr/exemple.html</url>"
			+ "					<desc>Exemple de recherche. Holocene lake succession and palaeo-optics of a subarctic lake, northern Québec (Canada). Saulnier-Talbot, É., Pienitz, R., Vincent, ...</desc>"
			+ "				</link>" + "				<link rank=\"14\" score=\"0\">"
			+ "					<title>Gnomon Online: Pour faciliter votre recherche</title>"
			+ "					<url>http://www.gnomon.ku-eichstaett.de/Gnomon/fr/help.html</url>"
			+ "					<desc>Exemples de recherche; Thésaurus; Affichage des titres ... Les exemples suivants indiquent les options principales de la recherche dans \"Tous domaines\". ...</desc>"
			+ "				</link>" + "				<link rank=\"15\" score=\"0\">"
			+ "					<title>Référence rapide pour effectuer une recherche dans l'Encyclopédie ...</title>"
			+ "					<url>http://www.ilocis.org/fr/help.html</url>"
			+ "					<desc>Recherche au moyen de, Exemple(s), Trouver tous les enregistrements ... Consultez Recherche par zone particulière pour obtenir des exemples de la manière ...</desc>"
			+ "				</link>" + "				<link rank=\"16\" score=\"0\">"
			+ "					<title>Un exemple de recherche</title>"
			+ "					<url>http://www.bpi.fr/clips/wmv4.html</url>"
			+ "					<desc>4. Un exemple de recherche. Accès au clip en \"streaming\", ( liaisons haut débit, format Windows Media Player)</desc>"
			+ "				</link>" + "				<link rank=\"17\" score=\"0\">"
			+ "					<title>Exemple de recherche des k-itemsets fréquents</title>"
			+ "					<url>http://www.lifl.fr/west/publi/Marq00parm/parm-apr00005.html</url>"
			+ "					<desc>2.4 Exemple de recherche des k-itemsets fréquents. Soient. I = {A, B, C, D, E, F}; T = {AB, ABCD, ABD, ABDF, ACDE, BCDF}; smin = 1/2 ...</desc>"
			+ "				</link>" + "				<link rank=\"18\" score=\"0\">"
			+ "					<title>adipc - exemple de recherche sur les sucres en seconde</title>"
			+ "					<url>http://www.inrp.fr/Tecne/adipc/exs/exs-sucres-01.htm</url>"
			+ "					<desc>existence des différents sucres, qu'est-ce que \"le sucre\", exemples de formules et de ... Le travail de recherche d'informations est réparti sur les deux ...</desc>"
			+ "				</link>" + "				<link rank=\"19\" score=\"0\">"
			+ "					<title>Internet (Exemple de recherche : BAO 9) - guide FRI [free ...</title>"
			+ "					<url>http://www.agropolis.fr/ist/guide/bao/exemples/internet_esb/internet_esb.html</url>"
			+ "					<desc>(LANG=fr)Guide de Formation a la Recherche d'Information de la Commission IST Agropolis Montpellier - GFU : groupe de travail sur la formation des ...</desc>"
			+ "				</link>" + "				<link rank=\"20\" score=\"2\">"
			+ "					<title>Astuces de recherche</title>"
			+ "					<url>http://www.cr-rhone-alpes.fr/default_f.cfm?cd=1066&amp;depth=1&amp;dept0=1066&amp;doc=5246</url>"
			+ "					<desc>Par exemple une recherche sur \"aide et industrie et bois\" permet de trouver les documents contenant les trois mots à la fois. ...</desc>"
			+ "				</link>" + "			</list>" + "</search>";

	public static void main(String args[]) {
		Enregistrement enr = new Enregistrement();
		try {
			// création d'une fabrique de parseurs SAX
			SAXParserFactory fabrique = SAXParserFactory.newInstance();

			// création d'un parseur SAX
			SAXParser parseur = fabrique.newSAXParser();

			// DefaultHandler enregistrementHandler = new
			// EnregistrementHandler();
			EnregistrementHandler enregistrementHandler = new EnregistrementHandler();
			InputSource xmlInputSource = new InputSource(new CharArrayReader(xmlReponse.toCharArray()));
			parseur.parse(xmlInputSource, enregistrementHandler);
			enr = enregistrementHandler.getEnregistrement();
		} catch (ParserConfigurationException pce) {
			GestionMessage.message(2, "La_classe",
					"Erreur de configuration du parseur, Lors de l'appel à SAXParser.newSAXParser() : " + pce);
		} catch (SAXException se) {
			GestionMessage.message(2, "La_classe", "Erreur de parsing, Lors de l'appel à parse() : " + se);
		} catch (IOException ioe) {
			GestionMessage.message(2, "La_classe", "Erreur d'entrée/sortie, Lors de l'appel à parse() : " + ioe);
		}

		System.out.println(enr.toSql());

	}
}
