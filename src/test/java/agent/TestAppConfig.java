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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour {@link AppConfig} et {@link AppConfigLoader}.
 *
 * AppConfig : vérifie le pattern Builder et les getters.
 * AppConfigLoader : vérifie que le chargement du fichier .conf ou un fichier absent
 *                   retourne bien un objet non nul avec des valeurs cohérentes.
 */
public class TestAppConfig {

    // =========================================================================
    // AppConfig.Builder – valeurs par défaut
    // =========================================================================

    @Test
    public void builderDefaut_portServeur8080() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(8080, config.getPortServeur());
    }

    @Test
    public void builderDefaut_nbThread5() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(5, config.getNbThread());
    }

    @Test
    public void builderDefaut_sortie3() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(3, config.getSortie());
    }

    @Test
    public void builderDefaut_debug3() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(3, config.getDebug());
    }

    @Test
    public void builderDefaut_promLocale2() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(2, config.getPromLocale());
    }

    @Test
    public void builderDefaut_coefPonderation1() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(1, config.getCoefPonderation());
    }

    @Test
    public void builderDefaut_coefSim07() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(0.7, config.getCoefSim(), 1e-9);
    }

    @Test
    public void builderDefaut_pageIndexHtml() {
        AppConfig config = AppConfig.builder().build();
        assertEquals("index.html", config.getPageIndex());
    }

    @Test
    public void builderDefaut_pathSlashWwwSlash() {
        AppConfig config = AppConfig.builder().build();
        assertEquals("/www/", config.getPath());
    }

    @Test
    public void builderDefaut_moteursNonNull() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getMoteurs());
    }

    @Test
    public void builderDefaut_contactsNonNull() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getContacts());
    }

    // =========================================================================
    // AppConfig.Builder – valeurs personnalisées
    // =========================================================================

    @Test
    public void builderCustom_portServeur() {
        AppConfig config = AppConfig.builder().portServeur(9090).build();
        assertEquals(9090, config.getPortServeur());
    }

    @Test
    public void builderCustom_hostBDD() {
        AppConfig config = AppConfig.builder().hostBDD("localhost").build();
        assertEquals("localhost", config.getHostBDD());
    }

    @Test
    public void builderCustom_userBDD() {
        AppConfig config = AppConfig.builder().userBDD("root").build();
        assertEquals("root", config.getUserBDD());
    }

    @Test
    public void builderCustom_passBDD() {
        AppConfig config = AppConfig.builder().passBDD("secret").build();
        assertEquals("secret", config.getPassBDD());
    }

    @Test
    public void builderCustom_baseBDD() {
        AppConfig config = AppConfig.builder().baseBDD("metamoteur").build();
        assertEquals("metamoteur", config.getBaseBDD());
    }

    @Test
    public void builderCustom_tableBDD() {
        AppConfig config = AppConfig.builder().tableBDD("BDD").build();
        assertEquals("BDD", config.getTableBDD());
    }

    @Test
    public void builderCustom_typeBDD() {
        AppConfig config = AppConfig.builder().typeBDD("HSQL").build();
        assertEquals("HSQL", config.getTypeBDD());
    }

    @Test
    public void builderCustom_nbThread() {
        AppConfig config = AppConfig.builder().nbThread(10).build();
        assertEquals(10, config.getNbThread());
    }

    @Test
    public void builderCustom_coefSim() {
        AppConfig config = AppConfig.builder().coefSim(0.5).build();
        assertEquals(0.5, config.getCoefSim(), 1e-9);
    }

    @Test
    public void builderCustom_fichierLog() {
        AppConfig config = AppConfig.builder().fichierLog("test.log").build();
        assertEquals("test.log", config.getFichierLog());
    }

    @Test
    public void builderCustom_moteurs() {
        AppConfig config = AppConfig.builder().moteurs(new String[]{"google", "bing"}).build();
        assertArrayEquals(new String[]{"google", "bing"}, config.getMoteurs());
    }

    @Test
    public void builderCustom_contacts() {
        AppConfig config = AppConfig.builder().contacts(new String[]{"agent1", "agent2"}).build();
        assertArrayEquals(new String[]{"agent1", "agent2"}, config.getContacts());
    }

    // =========================================================================
    // Copie défensive des tableaux
    // =========================================================================

    @Test
    public void getMoteurs_copieDefensive() {
        String[] moteurs = {"google"};
        AppConfig config = AppConfig.builder().moteurs(moteurs).build();
        String[] m1 = config.getMoteurs();
        String[] m2 = config.getMoteurs();
        assertNotSame(m1, m2); // instances différentes à chaque appel
    }

    @Test
    public void getContacts_copieDefensive() {
        String[] contacts = {"agent1"};
        AppConfig config = AppConfig.builder().contacts(contacts).build();
        assertNotSame(config.getContacts(), config.getContacts());
    }

    // =========================================================================
    // AppConfigLoader
    // =========================================================================

    @Test
    public void appConfigLoader_fichierConf_retourneConfigNonNull() {
        AppConfig config = AppConfigLoader.load(Agent.FichierConf);
        assertNotNull(config);
    }

    @Test
    public void appConfigLoader_fichierAbsent_retourneConfigAvecValeursParDefaut() {
        // Un fichier inexistant → les valeurs par défaut du Builder sont utilisées
        AppConfig config = AppConfigLoader.load("fichier_inexistant_xyz.conf");
        assertNotNull(config);
        assertEquals(8080, config.getPortServeur()); // valeur par défaut du Builder
    }
}
