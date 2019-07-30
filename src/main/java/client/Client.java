package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import agent.GestionMessage;

/*
 *------------
 *Client.java
 *------------
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
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */

/**
 * Cette classe permet de contacter d'autres agents ou des moteurs de recherches
 * par des requ�tes HTTP
 */

public class Client {
	/**
	 * Methode permettant d'envoyer une requete HTTP de type GET a l'url demandee
	 * 
	 * @param adresse
	 *            adresse complete a envoyer (contenant la partie apres le ? si
	 *            necessaire=
	 * @return retourne la reponse du serveur contacte
	 */
	public static String envoiRequeteGET(String adresse) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			// recuperation de l'url sur laquelle adresser la requete
			url = new URL(adresse);
			// ouverture de la connection HTTP
			conn = (HttpURLConnection) url.openConnection();
			// mise en place du timeout sur la requete
			// conn.setConnectTimeout(Agent.TimeOutMoteurRecherche);
			// ajout d'un user-agent pour eviter une erreur 403 sur google
			conn.setRequestProperty("User-Agent", "MetaBot/1.0");
			// choix du type de la methode
			conn.setRequestMethod("GET");
			// ouverture des flux de recuperation de la reponse
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			// recuperation de la reponse du serveur contacte
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			// fermeture du flux
			rd.close();
		} catch (Exception e) {
			// affichage d'un message en cas d'erreur
			GestionMessage.message(1, "Client", "Erreur pour la requete GET : " + adresse + ", " + e);
		}
		// renvoi du resultat de la requete
		return result;
	}

	/**
	 * Methode permettant d'envoyer une requete HTTP de type post a un serveur web
	 * 
	 * @param url
	 *            url du serveur (http://www.google.fr)
	 * @param adresse
	 *            adresse relative (suite de la requete)(search.html...)
	 * @param corps
	 *            corps de la requete a envoyer
	 * @param port
	 * 			  port pour la requete
	 * @return retourne la reponse du serveur contacte
	 */
	public static String envoiRequetePOST(String url, String adresse, String corps, int port) {
		String str = null;
		String retour = null;
		try {
			// ouverture du socket sur l'url donnee
			Socket socketToWeb = new Socket(url, port);
			// selection du timeout de la connection
			// socketToWeb.setSoTimeout(Agent.TimeOutAgent);
			// verification si la connexion a ete etablie
			if (socketToWeb.isConnected()) {
				// ouverture des flux de transfert de donnees
				PrintWriter toWeb = new PrintWriter(
						new BufferedWriter(new OutputStreamWriter(socketToWeb.getOutputStream())), true);
				BufferedReader fromWeb = new BufferedReader(new InputStreamReader(socketToWeb.getInputStream()));
				// construction de la requete
				str = "POST " + adresse + " HTTP/1.1\r\nUser-Agent: MetaBot/1.0\r\n\r\n" + corps + "\r\n\r\n";
				// envoi de la requete
				toWeb.println(str);
				retour = new String("");
				// recuperation de la reponse du serveur
				while (true) {
					str = fromWeb.readLine();
					if (str == null)
						break;
					retour += str + "\r\n";
				}
				// suppression de la partie inutile de la reponse
				retour = retour.substring(retour.indexOf("\r\n\r\n") + 4);
			} else {
				// renvoi pour la forme
				retour = "";
			}
		} catch (IOException e) {
			// affichage d'un message en cas d'erreur
			GestionMessage.message(1, "Client", "Erreur pour la requete POST : " + url + "/" + adresse + ", " + e);
		}
		// renvoi des donnees
		return retour;
	}
}