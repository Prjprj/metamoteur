package agent;

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

import serveur.Serveur;

/**
 * Titre : classe publique Agent Description : classe principale du projet
 * initialisation de toutes les variables du fichier de conf et lancement du
 * serveur
 *
 * @author Jeremy FRECHARD
 * @author Cecile GIRARD
 * @author Aysel GUNES
 * @author Pierre RAMOS
 * @version 1.0
 */

public class Agent {

    /** Chemin vers le fichier de configuration. */
    public static final String FichierConf = "metaMoteur.conf";

    /**
     * Configuration immuable de l'application.
     * Initialisée au démarrage via {@link AppConfigLoader} dans {@link #main(String[])}.
     * Remplace les anciens champs statiques mutables.
     */
    public static AppConfig CONFIG;

    /**
     * Point d'entrée de l'application.
     * Charge la configuration depuis {@link #FichierConf} puis lance le serveur HTTP.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        CONFIG = AppConfigLoader.load(FichierConf);
        Serveur.service();
    }

    /**
     * Retourne une représentation textuelle de la configuration courante.
     *
     * @return String les variables de la configuration
     */
    @Override
    public String toString() {
        if (CONFIG == null) return "Configuration non chargee";
        StringBuilder sb = new StringBuilder("FichierConf = ").append(FichierConf).append("\n");
        sb.append("HostBDD = ").append(CONFIG.getHostBDD()).append("\n");
        sb.append("UserBDD = ").append(CONFIG.getUserBDD()).append("\n");
        sb.append("PassBDD = ***\n");
        sb.append("BaseBDD = ").append(CONFIG.getBaseBDD()).append("\n");
        sb.append("TableBDD = ").append(CONFIG.getTableBDD()).append("\n");
        sb.append("TypeBDD = ").append(CONFIG.getTypeBDD()).append("\n");
        sb.append("Sortie = ").append(CONFIG.getSortie()).append("\n");
        sb.append("Debug = ").append(CONFIG.getDebug()).append("\n");
        sb.append("FichierLog = ").append(CONFIG.getFichierLog()).append("\n");
        sb.append("PromLocale = ").append(CONFIG.getPromLocale()).append("\n");
        sb.append("CoefPonderation = ").append(CONFIG.getCoefPonderation()).append("\n");
        sb.append("CoefSim = ").append(CONFIG.getCoefSim()).append("\n");
        sb.append("PortServeur = ").append(CONFIG.getPortServeur()).append("\n");
        sb.append("NbThread = ").append(CONFIG.getNbThread()).append("\n");
        sb.append("PageIndex = ").append(CONFIG.getPageIndex()).append("\n");
        sb.append("Path = ").append(CONFIG.getPath()).append("\n");
        for (String m : CONFIG.getMoteurs()) {
            sb.append("Moteurs = ").append(m).append("\n");
        }
        for (String c : CONFIG.getContacts()) {
            sb.append("Contacts = ").append(c).append("\n");
        }
        return sb.toString();
    }
}
