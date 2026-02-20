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
import agent.Enregistrement;
import agent.GestionMessage;
import agent.Lien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Classe contenant toutes les methodes de gestion de base de donnees Mysql
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class GestionBDD {
    /**
     * Methode permettant de se connecter a la base de donnees dont les coordonnees
     * sont en attributs
     *
     * @return retourne l'objet representant la connection avec la base
     */
    public static Connection connectionBDD() {
        Connection connection = null;
        if (Agent.CONFIG.getTypeBDD().equals("MySQL")) {
            try {
                // Le driver MySQL est auto-découvert via ServiceLoader depuis Java 6 :
                // aucune instanciation manuelle nécessaire.
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + Agent.CONFIG.getHostBDD() + "/" + Agent.CONFIG.getBaseBDD() + "?useSSL=false&serverTimezone=UTC",
                        Agent.CONFIG.getUserBDD(), Agent.CONFIG.getPassBDD());
            } catch (SQLException e) {
                GestionMessage.message(2, "GestionBDD", "Erreur de connexion a la base de donnees : " + e.getMessage());
            }
        }
        if (Agent.CONFIG.getTypeBDD().equals("HSQL")) {
            try {
                // ouverture du pilote de connection avec la base
                Class.forName("org.hsqldb.jdbc.JDBCDriver");
                // connection a la base de donnees
                connection = DriverManager.getConnection("jdbc:hsqldb:file:db/" + Agent.CONFIG.getBaseBDD() + ";ifexists=false",
                        Agent.CONFIG.getUserBDD(), Agent.CONFIG.getPassBDD());

                String createStatement = "CREATE TABLE IF NOT EXISTS BDD (\n" + "  UID INT ,\n"
                        + "  KEYWORDS VARCHAR(255),\n" + "  URL1 VARCHAR(255),\n" + "  TITLE1 VARCHAR(255),\n"
                        + "  DESC1 VARCHAR(1000),\n" + "  RANK1 INT,\n" + "  SELECT1 INT,\n"
                        + "  URL2 VARCHAR(255),\n" + "  TITLE2 VARCHAR(255),\n" + "  DESC2 VARCHAR(1000),\n"
                        + "  RANK2 INT,\n" + "  SELECT2 INT,\n" + "  URL3 VARCHAR(255),\n"
                        + "  TITLE3 VARCHAR(255),\n" + "  DESC3 VARCHAR(1000),\n" + "  RANK3 INT,\n"
                        + "  SELECT3 INT,\n" + "  URL4 VARCHAR(255),\n" + "  TITLE4 VARCHAR(255),\n"
                        + "  DESC4 VARCHAR(1000),\n" + "  RANK4 INT,\n" + "  SELECT4 INT,\n"
                        + "  URL5 VARCHAR(255),\n" + "  TITLE5 VARCHAR(255),\n" + "  DESC5 VARCHAR(1000),\n"
                        + "  RANK5 INT,\n" + "  SELECT5 INT,\n" + "  URL6 VARCHAR(255),\n"
                        + "  TITLE6 VARCHAR(255),\n" + "  DESC6 VARCHAR(1000),\n" + "  RANK6 INT,\n"
                        + "  SELECT6 INT,\n" + "  URL7 VARCHAR(255),\n" + "  TITLE7 VARCHAR(255),\n"
                        + "  DESC7 VARCHAR(1000),\n" + "  RANK7 INT,\n" + "  SELECT7 INT,\n"
                        + "  URL8 VARCHAR(255),\n" + "  TITLE8 VARCHAR(255),\n" + "  DESC8 VARCHAR(1000),\n"
                        + "  RANK8 INT,\n" + "  SELECT8 INT,\n" + "  URL9 VARCHAR(255),\n"
                        + "  TITLE9 VARCHAR(255),\n" + "  DESC9 VARCHAR(1000),\n" + "  RANK9 INT,\n"
                        + "  SELECT9 INT,\n" + "  URL10 VARCHAR(255),\n" + "  TITLE10 VARCHAR(255),\n"
                        + "  DESC10 VARCHAR(1000),\n" + "  RANK10 INT,\n" + "  SELECT10 INT,\n"
                        + "  URL11 VARCHAR(255),\n" + "  TITLE11 VARCHAR(255),\n" + "  DESC11 VARCHAR(1000),\n"
                        + "  RANK11 INT,\n" + "  SELECT11 INT,\n" + "  URL12 VARCHAR(255),\n"
                        + "  TITLE12 VARCHAR(255),\n" + "  DESC12 VARCHAR(1000),\n" + "  RANK12 INT,\n"
                        + "  SELECT12 INT,\n" + "  URL13 VARCHAR(255),\n" + "  TITLE13 VARCHAR(255),\n"
                        + "  DESC13 VARCHAR(1000),\n" + "  RANK13 INT,\n" + "  SELECT13 INT,\n"
                        + "  URL14 VARCHAR(255),\n" + "  TITLE14 VARCHAR(255),\n" + "  DESC14 VARCHAR(1000),\n"
                        + "  RANK14 INT,\n" + "  SELECT14 INT,\n" + "  URL15 VARCHAR(255),\n"
                        + "  TITLE15 VARCHAR(255),\n" + "  DESC15 VARCHAR(1000),\n" + "  RANK15 INT,\n"
                        + "  SELECT15 INT,\n" + "  URL16 VARCHAR(255),\n" + "  TITLE16 VARCHAR(255),\n"
                        + "  DESC16 VARCHAR(1000),\n" + "  RANK16 INT,\n" + "  SELECT16 INT,\n"
                        + "  URL17 VARCHAR(255),\n" + "  TITLE17 VARCHAR(255),\n" + "  DESC17 VARCHAR(1000),\n"
                        + "  RANK17 INT,\n" + "  SELECT17 INT,\n" + "  URL18 VARCHAR(255),\n"
                        + "  TITLE18 VARCHAR(255),\n" + "  DESC18 VARCHAR(1000),\n" + "  RANK18 INT,\n"
                        + "  SELECT18 INT,\n" + "  URL19 VARCHAR(255),\n" + "  TITLE19 VARCHAR(255),\n"
                        + "  DESC19 VARCHAR(1000),\n" + "  RANK19 INT,\n" + "  SELECT19 INT,\n"
                        + "  URL20 VARCHAR(255),\n" + "  TITLE20 VARCHAR(255),\n" + "  DESC20 VARCHAR(1000),\n"
                        + "  RANK20 INT,\n" + "  SELECT20 INT,\n" + "  TIMEQUERY VARCHAR(20)\n" + ")";

                try (Statement statement = connection.createStatement()) {
                    statement.execute(createStatement);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
                // Affichage d'un message d'erreur en cas de non possibilite de la connection a
                // la base
                GestionMessage.message(2, "GestionBDD", "Erreur de connexion a la base de donnees");
            }
        }
        // renvoi de l'objet de connection a la base
        return connection;
    }

    /**
     * Methode permettant d'effectuer une requete sur une base de donnees, la
     * connection et la deconnection sont effectuees dans la methode
     *
     * @param requete Chaine de caracteres contenant la requete a effectuer
     * @return retourne un vecteur contenant le autant de vecteurs que de lignes de
     * la reponse, ces vecteurs contiennent des objets contenant le contenu
     * de chaque champ de la base
     */
    public static Vector envoiRequete(String requete) {
        // creation du vecteur qui sera retourne
        Vector retour = new Vector();
        try (Connection connection = connectionBDD()) {
            if (connection == null) {
                GestionMessage.message(2, "GestionBDD", "Connexion indisponible pour la requete : " + requete);
                return retour;
            }
            try (Statement statement = connection.createStatement()) {
                // en cas de requete SELECT il faut recuperer le resultat
                if (requete.substring(0, requete.indexOf(' ')).equals("SELECT")) {
                    try (ResultSet resultats = statement.executeQuery(requete)) {
                        ResultSetMetaData meta = resultats.getMetaData();
                        int taille = meta.getColumnCount();
                        while (resultats.next()) {
                            Vector temp = new Vector();
                            for (int i = 1; i <= taille; i++) {
                                temp.add(resultats.getObject(i));
                            }
                            retour.add(temp);
                        }
                    }
                } else {
                    statement.execute(requete);
                }
            }
            if (Agent.CONFIG.getTypeBDD().equals("HSQL")) {
                shutdownHSQL(connection);
            }
            GestionMessage.message(0, "GestionBDD", "requete " + requete + " envoyee");
        } catch (SQLException e) {
            GestionMessage.message(2, "GestionBDD", "Erreur d'envoi de la requete " + requete + " : " + e.getMessage());
        }
        return retour;
    }

    /**
     * Methode permettant de construire des requetes SQL de type SELECT
     *
     * @param restriction restriction des colones a envoyer dans la requete (* si pas de
     *                    restriction)
     * @param table       table sur laquelle la requete doit etre effectuee
     * @param condition   conditions a utiliser pour la requete (" " si pas de condition)
     * @return retourne une requete en langage SQL a partir des infos donnees en
     * entree
     */
    public static String construitSelect(String restriction, String table, String condition) {
        if (!condition.equals(" "))
            return ("SELECT " + restriction + " FROM " + table + " WHERE " + condition);
        else
            return ("SELECT " + restriction + " FROM " + table);
    }

    /**
     * Methode permettant de construire des requetes SQL de type INSERT
     *
     * @param table   table sur laquelle la requete doit etre effectuee
     * @param valeurs valeurs a affecter aux champs de la table (ATTENTION : il faut les
     *                mettre dans l'ordre des champs de la table)
     * @return retourne une requete en langage SQL a partir des informations donnes
     * en entree
     */
    public static String construitInsert(String table, String valeurs) {
        Vector retour = new Vector();
        try (Connection connection = connectionBDD()) {
            if (connection == null) {
                GestionMessage.message(2, "GestionBDD", "Connexion indisponible pour construitInsert");
            } else {
                // Recuperation des noms de colonnes de la table cible
                try (Statement statement = connection.createStatement();
                     ResultSet resultats = statement.executeQuery("SELECT * FROM " + table)) {
                    ResultSetMetaData meta = resultats.getMetaData();
                    int taille = meta.getColumnCount();
                    for (int i = 1; i <= taille; i++)
                        retour.add(meta.getColumnName(i));
                }
            }
        } catch (SQLException e) {
            GestionMessage.message(2, "GestionBDD", "Erreur lors de l'acces a la base de donnees pour un insert");
        }
        // creation d'une String pour construire la requete
        String col = "";
        int taille = retour.size();
        // creation de la chaine a inserer dans la requete
        for (int i = 0; i < taille; i++) {
            col += (String) retour.get(i);
            // pour ne pas ajouter une virgule en trop
            if (i < taille - 1)
                col += ",";
        }
        return ("INSERT INTO " + table + " (" + col + ") VALUES (" + valeurs + ")");
    }

    /**
     * Methode permettant de construire des requetes SQL de type UPDATE
     *
     * @param table     table sur laquelle la requete doit etre effectuee
     * @param valeurs   valeurs a modifier dans les champs de la table (ATTENTION : il
     *                  faut les mettre le nom des champs avant la modification de la
     *                  table)
     * @param condition conditions a utiliser pour la requete (" " si pas de condition)
     * @return retourne une requete en langage SQL a partir des informations donnes
     * en entree
     */
    public static String construitUpdate(String table, String valeurs, String condition) {
        if (!condition.equals(" "))
            return ("UPDATE " + table + " SET " + valeurs + " WHERE " + condition);
        else
            return ("UPDATE " + table + " SET " + valeurs);
    }

    /**
     * Methode permettant d'inserer un enregistrement dans la base de donnees en
     * suivant le schema de la base.
     * Utilise un PreparedStatement pour prevenir les injections SQL.
     *
     * @param enr enregistrement a ajouter a la base
     * @return true si l'insertion a reussi, false sinon
     */
    public static Boolean insertEnregistrement(Enregistrement enr) {
        // Construction du SQL avec placeholders pour 20 liens maximum
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(Agent.CONFIG.getTableBDD());
        sql.append(" (KEYWORDS");
        for (int i = 1; i <= 20; i++) {
            sql.append(",URL").append(i)
               .append(",TITLE").append(i)
               .append(",DESC").append(i)
               .append(",RANK").append(i)
               .append(",SELECT").append(i);
        }
        sql.append(",TIMEQUERY) VALUES (?");
        for (int i = 0; i < 20; i++) {
            sql.append(",?,?,?,?,?");
        }
        sql.append(",?)");

        Connection connection = connectionBDD();
        if (connection == null) {
            GestionMessage.message(2, "GestionBDD", "Impossible d'obtenir une connexion pour l'insertion");
            return false;
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, enr.getKeywords());
            Vector liens = enr.getLiens();
            // Remplissage des 20 emplacements de liens (null si absent)
            for (int i = 0; i < 20; i++) {
                int base = 2 + i * 5;
                if (i < liens.size()) {
                    Lien lien = (Lien) liens.get(i);
                    ps.setString(base,     lien.getUrl());
                    ps.setString(base + 1, lien.getTitre());
                    ps.setString(base + 2, lien.getDesc());
                    ps.setInt   (base + 3, i + 1);
                    ps.setInt   (base + 4, 0);
                } else {
                    ps.setNull(base,     Types.VARCHAR);
                    ps.setNull(base + 1, Types.VARCHAR);
                    ps.setNull(base + 2, Types.VARCHAR);
                    ps.setNull(base + 3, Types.INTEGER);
                    ps.setNull(base + 4, Types.INTEGER);
                }
            }
            // Parametre 102 : timestamp pour la gestion de la duree de vie
            ps.setString(102, String.valueOf(System.currentTimeMillis()));
            ps.executeUpdate();
            if (Agent.CONFIG.getTypeBDD().equals("HSQL")) {
                shutdownHSQL(connection);
            }
            GestionMessage.message(0, "GestionBDD", "Enregistrement insere avec succes");
            return true;
        } catch (SQLException e) {
            GestionMessage.message(2, "GestionBDD", "Erreur lors de l'insertion : " + e.getMessage());
            return false;
        } finally {
            try { connection.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Methode permettant d'updater le nombre de clics d'une url dans les
     * enregistrements non encore fermes.
     * Utilise des PreparedStatement pour prevenir les injections SQL.
     *
     * @param url url cliquee a mettre a jour
     * @return true si la mise a jour a reussi, false sinon
     */
    public static Boolean updateURL(String url) {
        // Etape 1 : recherche des UIDs contenant cette URL via PreparedStatement
        StringBuilder findUidSql = new StringBuilder("SELECT UID FROM ");
        findUidSql.append(Agent.CONFIG.getTableBDD()).append(" WHERE ");
        for (int i = 1; i < 20; i++) {
            findUidSql.append("URL").append(i).append("=? OR ");
        }
        findUidSql.append("URL20=?");

        Connection connection = connectionBDD();
        if (connection == null) return false;

        try {
            // Collecte des UIDs correspondants
            List<Integer> uids = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(findUidSql.toString())) {
                for (int i = 1; i <= 20; i++) {
                    ps.setString(i, url);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        uids.add(rs.getInt("UID"));
                    }
                }
            }

            if (uids.isEmpty()) return true;

            // Etape 2 : recuperation des details des enregistrements trouves
            StringBuilder selectDetailSql = new StringBuilder(
                    "SELECT UID,URL1,URL2,URL3,URL4,URL5,URL6,URL7,URL8,URL9,URL10,"
                  + "URL11,URL12,URL13,URL14,URL15,URL16,URL17,URL18,URL19,URL20,TIMEQUERY FROM ");
            selectDetailSql.append(Agent.CONFIG.getTableBDD()).append(" WHERE ");
            for (int i = 0; i < uids.size(); i++) {
                if (i > 0) selectDetailSql.append(" OR ");
                selectDetailSql.append("UID=?");
            }

            try (PreparedStatement ps2 = connection.prepareStatement(selectDetailSql.toString())) {
                for (int i = 0; i < uids.size(); i++) {
                    ps2.setInt(i + 1, uids.get(i));
                }
                try (ResultSet rs = ps2.executeQuery()) {
                    while (rs.next()) {
                        String dateStr = rs.getString("TIMEQUERY");
                        long date2 = Double.valueOf(dateStr).longValue();
                        // Mise a jour uniquement des enregistrements ouverts (< 30 minutes)
                        if (System.currentTimeMillis() - (date2 + 1800000) < 0) {
                            int uid = rs.getInt("UID");
                            for (int j = 1; j <= 20; j++) {
                                String urlInRecord = rs.getString("URL" + j);
                                if (urlInRecord != null && urlInRecord.equals(url)) {
                                    // Etape 3 : increment du compteur de clics
                                    String updateSql = "UPDATE " + Agent.CONFIG.getTableBDD()
                                            + " SET SELECT" + j + "=SELECT" + j + "+1 WHERE UID=?";
                                    try (PreparedStatement updatePs = connection.prepareStatement(updateSql)) {
                                        updatePs.setInt(1, uid);
                                        updatePs.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (Agent.CONFIG.getTypeBDD().equals("HSQL")) {
                shutdownHSQL(connection);
            }
            return true;
        } catch (SQLException e) {
            GestionMessage.message(2, "GestionBDD", "Erreur lors de la mise a jour de l'URL : " + e.getMessage());
            return false;
        } finally {
            try { connection.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Methode permettant d'arreter la base HSQL
     *
     * @param connection Connection : La connection
     */
    public static void shutdownHSQL(Connection connection) {
        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SHUTDOWN");
            }
        } catch (Exception exception) {
            GestionMessage.message(2, "GestionBDD", "Erreur lors de l'arret de la base de donnees");
        }
    }

    /**
     * Methode de test de connectivite de la base de donnees, ne fait que verifier
     * que la base de donnees repond
     *
     * @return retourne true si le serveur MySQL repond, false sinon
     */
    public static boolean testBDD() {
        try (Connection conn = GestionBDD.connectionBDD()) {
            return conn != null;
        } catch (SQLException e) {
            GestionMessage.message(2, "GestionBDD", "Erreur lors du test de connexion : " + e.getMessage());
            return false;
        }
    }
}
