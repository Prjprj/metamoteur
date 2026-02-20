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

import agent.Agent;
import agent.GestionMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe contenant la methode main permettant de lancer le serveur HTTP cette
 * classe contient les methodes de gestion des clients pour ce serveur
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */

public class Serveur {
    private static int port;
    private static int clientsEnCours;
    private static int maxClients;
    private static boolean serveurON;

    /**
     * Methode permettant de lancer le service.
     */
    public static void service() {
        ServerSocket serveur = null;

        port = Agent.CONFIG.getPortServeur();
        ouvertureService();

        try {
            // ouverture du socket d'attente des clients
            serveur = new ServerSocket(port);
            GestionMessage.message(0, "Serveur", "Ouverture du service sur le port : " + port);
            while (etatService()) { // boucle infinie permettant d'attendre
                // la connexion d'un client
                ajoutClient(serveur);
            }
        } catch (IOException e) {
            GestionMessage.message(1, "Serveur", "Erreur lors de l'acceptation de la connexion : " + e);
        } finally {
            try {
                // fermeture du socket
                serveur.close();
            } catch (Exception e) {
                GestionMessage.message(1, "Serveur", "Erreur lors de la fermeture du socket d'ecoute : " + e);
            }
        }
    }

    /**
     * Cette methode permet de lancer un thread client, elle verifie s'il est
     * possible d'ajouter ce client avant de le faire
     *
     * @param serveur socket sur lequel le client communiquera
     */
    public static void ajoutClient(ServerSocket serveur) {
        Socket socket;
        try {
            socket = serveur.accept();
            if (!peutAjouterClient()) {// evite l'ajout de threads surnumeraires
                try {
                    // mise en attente du thread si necessaire
                    Thread.currentThread().join();
                } catch (Exception e) {
                    GestionMessage.message(1, "Serveur", "Probleme d'interruption du Thread : " + e);
                }
            }
            // lancement du thread
            new TraitementRequete(socket);
            clientsEnCours++;
        } catch (IOException e) {
            GestionMessage.message(1, "Serveur", "Erreur lors de la creation du socket de communication : " + e);
        }
    }

    /**
     * Cette methode permet de supprimer un thread client
     */
    public static synchronized void supprClient() {
        clientsEnCours--;
    }

    /**
     * Methode permettant de savoir si le service est en cours d'execution ou non
     *
     * @return retourne true si le service est en cours d'execution, false sinon
     */
    public static synchronized boolean etatService() {
        return (serveurON);
    }

    /**
     * Methode initialisant le service de la classe
     */
    public static synchronized void ouvertureService() {
        serveurON = true;
        clientsEnCours = 0;
        maxClients = Agent.CONFIG.getNbThread();
    }

    /**
     * Methode fermant le service lance par la classe
     */
    public static synchronized void fermetureService() {
        serveurON = false;
    }

    /**
     * Methode d'acces a l'attribut prive contenant le nombre de threads
     * actuellement lances
     *
     * @return retourne le nombre de clients courant
     */
    public static synchronized int getNbClients() {
        return (clientsEnCours);
    }

    /**
     * Methode permettant de verifier si l'ajout d'un thread de traitement de
     * requete est possible
     *
     * @return retourne true si l'on peut lancer un nouveau thread, false sinon
     */
    private static synchronized boolean peutAjouterClient() {
        if (getNbClients() < maxClients)
            return (true);
        else
            return (false);
    }
}
