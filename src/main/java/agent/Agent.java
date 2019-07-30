package agent;

import bdd.GestionBDD;
import serveur.Serveur;

/* ---------
 * Agent.java
 * ---------
 *
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
 * Titre : classe publique Agent Description : classe principale du projet
 * initialisation de toutes les variables du fichier de conf et lancement du
 * serveur
 * 
 * @author Jeremy FRECHARD
 * @author C�cile GIRARD
 * @author Aysel GUNES
 * @author Pierre RAMOS
 * @version 1.0
 */

public class Agent {

	// variables d'un agent et leur configuration par d�faut
	public static String FichierConf = "metaMoteur.conf";

	public static String HostBDD;

	public static String UserBDD;

	public static String PassBDD;

	public static String BaseBDD;

	public static String TableBDD;

	public static String TypeBDD;

	public static int Sortie = 3;

	public static int Debug = 3;

	public static int PromLocale = 2;

	public static int CoefPonderation = 1;

	public static double CoefSim = 0.7;

	public static String FichierLog = "metaMoteur.log";

	public static int PortServeur = 8080;

	public static int NbThread = 5;

	public static String PageIndex = "index.html";

	public static String Path = "src/www/";

	public static String[] Moteurs;

	public static String[] Contacts;

	/*
	 * Constructeur de l'agent ( initialise les variables )
	 */
	public Agent() {

		if (!ConfigParser.GetProperty("HostBDD").contentEquals("")) {
			HostBDD = ConfigParser.GetProperty("HostBDD");
		}
		if (!ConfigParser.GetProperty("UserBDD").contentEquals("")) {
			UserBDD = ConfigParser.GetProperty("UserBDD");
		}
		if (!ConfigParser.GetProperty("PassBDD").contentEquals("")) {
			PassBDD = ConfigParser.GetProperty("PassBDD");
		}
		if (!ConfigParser.GetProperty("BaseBDD").contentEquals("")) {
			BaseBDD = ConfigParser.GetProperty("BaseBDD");
		}
		if (!ConfigParser.GetProperty("TableBDD").contentEquals("")) {
			TableBDD = ConfigParser.GetProperty("TableBDD");
		}
		if (!ConfigParser.GetProperty("TypeBDD").contentEquals("")) {
			TypeBDD = ConfigParser.GetProperty("TypeBDD");
		}
		if (!ConfigParser.GetProperty("Sortie").contentEquals("")) {
			Sortie = Integer.parseInt(ConfigParser.GetProperty("Sortie"));
		}
		if (!ConfigParser.GetProperty("Debug").contentEquals("")) {
			Debug = Integer.parseInt(ConfigParser.GetProperty("Sortie"));
		}
		if (!ConfigParser.GetProperty("FichierLog").contentEquals("")) {
			FichierLog = ConfigParser.GetProperty("FichierLog");
		}
		if (!ConfigParser.GetProperty("PromLocale").contentEquals("")) {
			PromLocale = Integer.parseInt(ConfigParser.GetProperty("PromLocale"));
		}
		if (!ConfigParser.GetProperty("CoefPonderation").contentEquals("")) {
			CoefPonderation = Integer.parseInt(ConfigParser.GetProperty("CoefPonderation"));
		}
		if (!ConfigParser.GetProperty("CoefSim").contentEquals("")) {
			CoefSim = Double.parseDouble(ConfigParser.GetProperty("CoefSim"));
		}
		if (!ConfigParser.GetProperty("PortServeur").contentEquals("")) {
			PortServeur = Integer.parseInt(ConfigParser.GetProperty("PortServeur"));
		}
		if (!ConfigParser.GetProperty("NbThread").contentEquals("")) {
			NbThread = Integer.parseInt(ConfigParser.GetProperty("NbThread"));
		}
		if (!ConfigParser.GetProperty("PageIndex").contentEquals("")) {
			PageIndex = ConfigParser.GetProperty("PageIndex");
		}
		if (!ConfigParser.GetProperty("Path").contentEquals("")) {
			Path = ConfigParser.GetProperty("Path");
		}
		if (!ConfigParser.GetProperty("Moteurs").contentEquals("")) {
			Moteurs = ConfigParser.GetProperty("Moteurs").split(" ");
		} else {
			Moteurs = new String[1];
			Moteurs[0] = "";
		}
		if (!ConfigParser.GetProperty("Contacts").contentEquals("")) {
			Contacts = ConfigParser.GetProperty("Contacts").split(" ");
		} else {
			Contacts = new String[1];
			Contacts[0] = "";
		}
	}

	public static void main(String args[]) {
		new Agent();
		// if(GestionBDD.testBDD()){
		Serveur.service();
		// }
	}

	/**
	 * Cette classe permet l'affichage des variables de l'agent
	 *
	 * @return String les variables de l'agent
	 */
	public String toString() {
		String agentVariables = "FichierConf = " + FichierConf + "\n";
		agentVariables += "HostBDD = " + HostBDD + "\n";
		agentVariables += "UserBDD = " + UserBDD + "\n";
		agentVariables += "PassBDD = " + PassBDD + "\n";
		agentVariables += "BaseBDD = " + BaseBDD + "\n";
		agentVariables += "TableBDD = " + TableBDD + "\n";
		agentVariables += "TypeBDD = " + TypeBDD + "\n";
		agentVariables += "Sortie = " + Sortie + "\n";
		agentVariables += "Debug = " + Debug + "\n";
		agentVariables += "FichierLog = " + FichierLog + "\n";
		agentVariables += "PromLocale = " + PromLocale + "\n";
		agentVariables += "CoefPonderation = " + CoefPonderation + "\n";
		agentVariables += "CoefSim = " + CoefSim + "\n";
		agentVariables += "PortServeur = " + PortServeur + "\n";
		agentVariables += "NbThread = " + NbThread + "\n";
		agentVariables += "PageIndex = " + PageIndex + "\n";
		agentVariables += "Path = " + Path + "\n";
		for (int i = 0; i < Moteurs.length; i++) {
			agentVariables += "Moteurs = " + Moteurs[i] + "\n";
		}
		for (int i = 0; i < Contacts.length; i++) {
			agentVariables += "Contacts = " + Contacts[i] + "\n";
		}

		return agentVariables;
	}
}
