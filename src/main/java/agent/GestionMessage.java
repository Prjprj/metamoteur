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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Titre : classe publique GestionMessage Description : classe permettant de
 * g�r�e les messages interne de l'agent et permet soit d'�crire sur la sortie
 * standard le message, soit d'enregistrer le message dans un fichier log ou
 * bien les 2 en m�me temps. Les messages sont de trois types diff�rents : -
 * [ERR] les erreurs du syst�me - 2 - [WAR] les erreurs non-bloquantes - 1 -
 * [MES] les messages de traitements - 0
 * <p>
 * Les messages sont �crit de la mani�re standardis� suivante : [ERR] date -
 * heure - classeEmettrice � Message
 * <p>
 * La m�thode Write permet d'�crire le message soit dans le fichier log, soit en
 * sortie standard La m�thode gestionMessage permet de d�clench�e la m�thode
 * adapt�e suivant les configuration de l'agent. # Sortie 0 : aucune sortie # 1
 * : FichierLog # 2 : Sortie standard # 3 : FichierLog + Sortie Standard # Debug
 * 0 : aucun message # 1 : que les messages ERR # 2 : ERR + WAR # 3 : ERR + WAR
 * + MES
 *
 * @author Jeremy FRECHARD
 * @author Cecile GIRARD
 * @author Aysel GUNES
 * @author Pierre RAMOS
 * @version 0.9
 */

public class GestionMessage {

    /**
     * Methode d'envoie de message
     *
     * @param typeErreur     int le type d'erreur
     * @param classeEmetrice String la classe qui appelle la methode
     * @param message        String le message a traiter
     */
    public static void message(int typeErreur, String classeEmetrice, String message) {

        switch (Agent.Debug) {
            case 0: // on fait rien ;)
                break;
            case 1: // WAR
                if (typeErreur >= 2) { // WAR
                    switch (Agent.Sortie) {
                        case 0: // aucun message
                            break;
                        case 1: // fichierLog
                            ecrireDansFichier(formatageMessage(typeErreur, classeEmetrice, message));
                            break;
                        case 2: // sortieSandard
                            ecrireSortieStandard(formatageMessage(typeErreur, classeEmetrice, message));
                            break;
                        default: // sortieSandard + fichierLog
                            String mess = formatageMessage(typeErreur, classeEmetrice, message);
                            ecrireSortieStandard(mess);
                            ecrireDansFichier(mess);
                    }
                }
                break;
            case 2: // ERR + WAR
                if (typeErreur >= 1) { // ERR + WAR
                    switch (Agent.Sortie) {
                        case 0: // aucun message
                            break;
                        case 1: // fichierLog
                            ecrireDansFichier(formatageMessage(typeErreur, classeEmetrice, message));
                            break;
                        case 2: // sortieSandard
                            ecrireSortieStandard(formatageMessage(typeErreur, classeEmetrice, message));
                            break;
                        default: // sortieSandard + fichierLog
                            String mess = formatageMessage(typeErreur, classeEmetrice, message);
                            ecrireSortieStandard(mess);
                            ecrireDansFichier(mess);
                    }
                }
                break;
            default: // ERR + WAR + MES
                switch (Agent.Sortie) {
                    case 0: // aucun message
                        break;
                    case 1: // fichierLog
                        ecrireDansFichier(formatageMessage(typeErreur, classeEmetrice, message));
                        break;
                    case 2: // sortieSandard
                        ecrireSortieStandard(formatageMessage(typeErreur, classeEmetrice, message));
                        break;
                    default: // sortieSandard + fichierLog
                        String mess = formatageMessage(typeErreur, classeEmetrice, message);
                        ecrireSortieStandard(mess);
                        ecrireDansFichier(mess);
                }
        }
    }

    /**
     * Methode privee de formatage de message
     *
     * @param typeErreur     int le type d'erreur
     * @param classeEmetrice String la classe qui appelle la methode
     * @param message        String le message a traiter
     * @return String le message bien formatee
     */
    private static String formatageMessage(int typeErreur, String classeEmetrice, String message) {
        String messageFormate = "";
        switch (typeErreur) {
            case 0: // message de traitement
                messageFormate = "[MES]";
                break;
            case 1: // les erreurs non-bloquantes
                messageFormate = "[WAR]";
                break;
            default: // les erreurs du systeme
                messageFormate = "[ERR]";
        }

        messageFormate = messageFormate + " - " + dateFormat() + " - " + classeEmetrice + " - " + message;
        return messageFormate;
    }

    /**
     * Methode privee d'ecriture sur la sortie standard
     *
     * @param message String le message a traiter
     */
    private static void ecrireSortieStandard(String message) {
        System.out.println(message);
    }

    /**
     * Methode de formatage de la date du jour Attention c'est classe de gere pas
     * l'ephemeride ;)
     *
     * @return une date en string pre-formatee
     */
    private static String dateFormat() {
        Date maDateAvecFormat = new Date();
        DateFormat dateFormat = new SimpleDateFormat("[hh:mm:ss] - [dd/MM/yy]");
        return dateFormat.format(maDateAvecFormat);
    }

    /**
     * Methode d'ecrirture dans un fichier
     *
     * @param message String le message a traiter
     */
    private static void ecrireDansFichier(String message) {
        FileWriter fw = null;
        BufferedWriter output = null;
        try {
            // BufferedWriter a besoin d un FileWriter,
            // les 2 vont ensemble, on donne comme argument le nom du fichier
            // true signifie qu on ajoute dans le fichier (append), on ne marque pas par
            // dessus
            fw = new FileWriter(Agent.FichierLog, true);

            // le BufferedWriter output auquel on donne comme argument le FileWriter fw cree
            // juste au dessus
            output = new BufferedWriter(fw);

            // on marque dans le BufferedWriter qui sert
            // comme un tampon(stream)

            output.write(message);
            output.newLine();
            output.flush();
            // ensuite flush envoie dans le fichier
            output.close();
        }
        // on "catch" l exception ici si il y en a une, et on l affiche sur la console
        catch (IOException ioe) {
            System.out.println("[ERR] - " + dateFormat() + " - GestionMessage - " + ioe);
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
