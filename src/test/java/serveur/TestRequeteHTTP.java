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
 * --LICENSE NOTICE--
 */

import agent.Agent;
import agent.AppConfigLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour {@link RequeteHTTP}.
 *
 * Couvre : détection du type GET / POST / INCONNU, extraction du fichier pointé,
 *          présence ou absence d'une suite de requête, getCorps(), getType(),
 *          getSuiteRequete(), isSuitePresente().
 */
public class TestRequeteHTTP {

    @BeforeClass
    public static void setUp() {
        // getFichierPointe() fait appel à Agent.CONFIG.getPageIndex()
        Agent.CONFIG = AppConfigLoader.load(Agent.FichierConf);
    }

    // =========================================================================
    // Type GET
    // =========================================================================

    @Test
    public void typeGet_detectionCorrecte() {
        RequeteHTTP req = new RequeteHTTP("GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertEquals(Constantes.GET, req.getType());
    }

    @Test
    public void typeGet_fichierPointe_indexHtml() {
        RequeteHTTP req = new RequeteHTTP("GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(req.getFichierPointe().contains("index.html"));
    }

    @Test
    public void typeGet_sansSuiteRequete_suiteNulle() {
        RequeteHTTP req = new RequeteHTTP("GET /aide.html HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertFalse(req.isSuitePresente());
        assertNull(req.getSuiteRequete());
    }

    @Test
    public void typeGet_avecParametres_suitePresente() {
        RequeteHTTP req = new RequeteHTTP("GET /search.html?q=java HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(req.isSuitePresente());
        assertNotNull(req.getSuiteRequete());
        assertEquals("q=java", req.getSuiteRequete());
    }

    @Test
    public void typeGet_corpsPreserve() {
        String corps = "GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        RequeteHTTP req = new RequeteHTTP(corps);
        assertEquals(corps, req.getCorps());
    }

    // =========================================================================
    // Type POST
    // =========================================================================

    @Test
    public void typePost_detectionCorrecte() {
        RequeteHTTP req = new RequeteHTTP(
                "POST /search.html HTTP/1.1\r\nHost: localhost\r\nContent-Length: 7\r\n\r\nq=java\r\n\r\n");
        assertEquals(Constantes.POST, req.getType());
    }

    @Test
    public void typePost_fichierPointe_searchHtml() {
        RequeteHTTP req = new RequeteHTTP(
                "POST /search.html HTTP/1.1\r\nHost: localhost\r\nContent-Length: 7\r\n\r\nq=java\r\n\r\n");
        assertTrue(req.getFichierPointe().contains("search.html"));
    }

    // =========================================================================
    // Type INCONNU
    // =========================================================================

    @Test
    public void typeInconnu_detectionCorrecte() {
        RequeteHTTP req = new RequeteHTTP("OPTIONS / HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertEquals(Constantes.INCONNU, req.getType());
    }

    // =========================================================================
    // Redirection vers index quand le fichier se termine par "/"
    // =========================================================================

    @Test
    public void typeGet_racine_retournePageIndex() {
        // La requête pointe sur "/" → getFichierPointe() doit ajouter le nom de la page d'index
        RequeteHTTP req = new RequeteHTTP("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");
        String fichier = req.getFichierPointe();
        // doit se terminer par le nom de la page d'accueil configurée (ex. "index.html")
        assertTrue(fichier.endsWith(Agent.CONFIG.getPageIndex()));
    }
}
