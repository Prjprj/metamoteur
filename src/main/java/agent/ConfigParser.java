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

import java.io.*;
import java.util.Properties;

/**
 * Gestionnaire du fichier de configuration {@code metaMoteur.conf}.
 * <p>
 * Fournit des methodes statiques pour lire ({@link #GetProperty(String)}),
 * modifier ({@link #SetProperty(String, String)}) et verifier l'existence
 * du fichier de configuration. La methode {@link #load()} permet de
 * charger le fichier de maniere generique.
 * <p>
 * La classe {@link AppConfigLoader} offre une alternative plus robuste et
 * immuable pour le chargement initial au demarrage.
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 * @see AppConfig
 * @see AppConfigLoader
 */
public class ConfigParser {

    /** Properties chargees depuis le fichier de configuration lors de la derniere lecture. */
    static Properties P = null;

    /** Chemin relatif vers le fichier de configuration lu et ecrit par cette classe. */
    static String nomFichier = new String("metaMoteur.conf");

    /**
     * Cette methode permet de retourner la valeur de la propriete etant une chaine
     * de caractere
     *
     * @param Item Une chaine de caractere
     * @return Une chaine de caractere
     */
    static public String GetProperty(String Item) {
        try {
            P = new Properties();
            FileInputStream in = new FileInputStream(nomFichier);
            P.load(in);
            in.close();
        } catch (Exception e) {
            System.out.println("Erreur d'ouverture du fichier de configuration");
            e.printStackTrace();
            return null;
        }
        String S = P.getProperty(Item);
        if (S == null)
            return null;
        else
            return (S.trim());

    }

    /**
     * Methode permettant d'ouvrir le fichier pour modifier des propietes dans le
     * fichier de configuration
     *
     * @param Item   Une chaine de caractere
     * @param Valeur Une chaine de caractere
     */
    static public void SetProperty(String Item, String Valeur) {
        P = new Properties();
        try {
            System.out.println("Erreur d'ouverture du fichier de configuration");
            String nomFichier = new String("metaMoteur.conf");
            FileInputStream in = new FileInputStream(nomFichier);
            P.load(in);
            in.close();
        } catch (Exception e2) {
            System.out.println("Erreur d'ouverture du fichier de configuration");
            e2.printStackTrace();
        }

        P.put(Item, Valeur);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(nomFichier);
        } catch (FileNotFoundException e) {
            System.out.println("Erreur de creation de fichier");
            e.printStackTrace();
        }
        try {
            P.store(out, null);
        } catch (IOException e1) {
            System.out.println("erreur sauvegarde");
            e1.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e3) {
            System.out.println("erreur de fermeture");
            e3.printStackTrace();
        }

    }

    /**
     * Cette methode retourne le nom du fichier
     *
     * @return une chaine de caractere
     */
    public static String getNomFichier() {
        return nomFichier;
    }

    /**
     * Modification du nom de fichier
     *
     * @param nomFichier Une chaine de caractere
     */
    public static void setNomFichier(String nomFichier) {
        ConfigParser.nomFichier = nomFichier;
    }

    /**
     * Tente d'ouvrir le fichier de configuration dont le nom est derive
     * du nom de la classe ({@code .<NomClasse>.conf}).
     * <p>
     * <b>Note :</b> cette methode est une ebauche ; elle ouvre et ferme
     * le fichier mais ne lit aucune propriete. Privilegier
     * {@link #GetProperty(String)} pour la lecture effective.
     *
     * @return null (aucun objet charge dans l'implementation actuelle)
     */
    public Object load() {
        String NomFichier = "." + this.getClass().getName() + ".conf";

        Object o = null;
        Reader lecteur = null;

        try {
            lecteur = new FileReader(NomFichier);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            lecteur.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return o;
    }

    /**
     * Tente de creer le fichier de configuration dont le nom est derive
     * du nom de la classe ({@code .<NomClasse>.conf}).
     * <p>
     * <b>Note :</b> cette methode est une ebauche ; elle cree et ferme
     * le fichier mais n'y ecrit aucune propriete. Privilegier
     * {@link #SetProperty(String, String)} pour l'ecriture effective.
     */
    public void sauver() {
        String NomFichier = "." + this.getClass().getName() + ".conf";
        File file = new File(NomFichier);
        Writer writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("fichier ecrit " + file.getAbsoluteFile());

    }

    /**
     * Cette methode permet de retourner la valeur True si le fichier de
     * configuration existe deja.
     *
     * @return une valeur booleenne
     */
    public boolean ExistanceFichier() {
        File f = new File(nomFichier);
        return f.exists();
    }
}
