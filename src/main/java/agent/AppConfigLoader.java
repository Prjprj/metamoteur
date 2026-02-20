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
 * --LICENSE NOTICE--
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.logging.Logger;

/**
 * Charge la configuration depuis un fichier {@code .conf} et construit
 * un {@link AppConfig} immuable.
 * <p>
 * Remplace la logique de lecture dispersée dans le constructeur de {@link Agent}.
 * Les valeurs manquantes ou invalides conservent les valeurs par défaut définies
 * dans {@link AppConfig.Builder}.
 *
 * @version 2.0
 * @see AppConfig
 */
public final class AppConfigLoader {

    private static final Logger LOG = Logger.getLogger(AppConfigLoader.class.getName());

    /** Classe utilitaire — non instanciable. */
    private AppConfigLoader() {}

    /**
     * Charge la configuration à partir du fichier spécifié.
     *
     * @param fichierConf chemin vers le fichier {@code .conf}
     * @return un {@link AppConfig} immuable construit depuis le fichier ;
     *         en cas d'erreur de lecture, les valeurs par défaut sont utilisées.
     */
    public static AppConfig load(String fichierConf) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(fichierConf)) {
            props.load(in);
        } catch (IOException e) {
            LOG.warning("Impossible de lire le fichier de configuration : "
                    + fichierConf + " - " + e.getMessage()
                    + " (les valeurs par defaut seront utilisees)");
        }

        AppConfig.Builder builder = AppConfig.builder();

        applyString(props, "HostBDD",         v -> builder.hostBDD(v));
        applyString(props, "UserBDD",         v -> builder.userBDD(v));
        applyString(props, "PassBDD",         v -> builder.passBDD(v));
        applyString(props, "BaseBDD",         v -> builder.baseBDD(v));
        applyString(props, "TableBDD",        v -> builder.tableBDD(v));
        applyString(props, "TypeBDD",         v -> builder.typeBDD(v));
        applyInt   (props, "Sortie",          v -> builder.sortie(v));
        applyInt   (props, "Debug",           v -> builder.debug(v));
        applyInt   (props, "PromLocale",      v -> builder.promLocale(v));
        applyInt   (props, "CoefPonderation", v -> builder.coefPonderation(v));
        applyDouble(props, "CoefSim",         v -> builder.coefSim(v));
        applyString(props, "FichierLog",      v -> builder.fichierLog(v));
        applyInt   (props, "PortServeur",     v -> builder.portServeur(v));
        applyInt   (props, "NbThread",        v -> builder.nbThread(v));
        applyString(props, "PageIndex",       v -> builder.pageIndex(v));
        applyString(props, "Path",            v -> builder.path(v));

        // Moteurs et Contacts : valeurs séparées par des espaces
        String moteursStr = getString(props, "Moteurs");
        if (moteursStr != null && !moteursStr.isEmpty()) {
            builder.moteurs(moteursStr.split(" "));
        }
        String contactsStr = getString(props, "Contacts");
        if (contactsStr != null && !contactsStr.isEmpty()) {
            builder.contacts(contactsStr.split(" "));
        }

        return builder.build();
    }

    // -------------------------------------------------------------------------
    // Helpers privés
    // -------------------------------------------------------------------------

    private static String getString(Properties props, String key) {
        String v = props.getProperty(key);
        return (v != null) ? v.trim() : null;
    }

    private static void applyString(Properties props, String key, Consumer<String> setter) {
        String v = getString(props, key);
        if (v != null && !v.isEmpty()) {
            setter.accept(v);
        }
    }

    private static void applyInt(Properties props, String key, IntConsumer setter) {
        String v = getString(props, key);
        if (v != null && !v.isEmpty()) {
            try {
                setter.accept(Integer.parseInt(v));
            } catch (NumberFormatException e) {
                LOG.warning("Valeur entiere invalide pour la propriete '" + key + "' : " + v);
            }
        }
    }

    private static void applyDouble(Properties props, String key, DoubleConsumer setter) {
        String v = getString(props, key);
        if (v != null && !v.isEmpty()) {
            try {
                setter.accept(Double.parseDouble(v));
            } catch (NumberFormatException e) {
                LOG.warning("Valeur decimale invalide pour la propriete '" + key + "' : " + v);
            }
        }
    }
}
