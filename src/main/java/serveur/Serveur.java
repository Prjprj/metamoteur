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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    /** Indique si le serveur est en cours d'execution. */
    private static volatile boolean serveurON = false;

    /** Pool de threads gérant les connexions clientes. */
    private static ExecutorService pool;

    /**
     * Lance le service HTTP : ouvre le socket d'écoute et distribue
     * chaque connexion cliente à un thread du pool.
     */
    public static void service() {
        int port    = Agent.CONFIG.getPortServeur();
        int nbThread = Agent.CONFIG.getNbThread();
        serveurON = true;
        pool = Executors.newFixedThreadPool(nbThread);

        try (ServerSocket serveur = new ServerSocket(port)) {
            GestionMessage.message(0, "Serveur", "Ouverture du service sur le port : " + port);
            while (etatService()) {
                try {
                    Socket socket = serveur.accept();
                    pool.submit(new TraitementRequete(socket));
                } catch (IOException e) {
                    if (etatService()) {
                        GestionMessage.message(1, "Serveur",
                                "Erreur lors de l'acceptation d'une connexion : " + e);
                    }
                }
            }
        } catch (IOException e) {
            GestionMessage.message(1, "Serveur", "Erreur lors de l'ouverture du service : " + e);
        } finally {
            if (pool != null) {
                pool.shutdown();
                try {
                    if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                        pool.shutdownNow();
                    }
                } catch (InterruptedException ie) {
                    pool.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Ferme le service : arrête la boucle d'attente et initie l'arrêt du pool.
     * Appelée depuis {@link TraitementRequete} sur la requête stoppeAgent.html.
     */
    public static synchronized void fermetureService() {
        serveurON = false;
    }

    /**
     * Retourne l'état du service.
     *
     * @return true si le service est actif
     */
    public static synchronized boolean etatService() {
        return serveurON;
    }

    /**
     * Méthode de compatibilité conservée - la gestion du nombre de threads
     * est désormais assurée par l'ExecutorService.
     */
    public static void supprClient() {
        // Géré par l'ExecutorService - aucune action manuelle requise
    }
}
