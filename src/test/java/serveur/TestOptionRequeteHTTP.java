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

import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour {@link OptionRequeteHTTP}.
 * Couvre : constructeur par défaut, constructeur paramétré,
 *          getNom(), getValeur(), toString(), listeOptions().
 */
public class TestOptionRequeteHTTP {

    // -------------------------------------------------------------------------
    // Constructeur par défaut
    // -------------------------------------------------------------------------

    @Test
    public void constructeurParDefaut_nomVide() {
        OptionRequeteHTTP opt = new OptionRequeteHTTP();
        assertEquals("", opt.getNom());
    }

    @Test
    public void constructeurParDefaut_valeurVide() {
        OptionRequeteHTTP opt = new OptionRequeteHTTP();
        assertEquals("", opt.getValeur());
    }

    // -------------------------------------------------------------------------
    // Constructeur paramétré
    // -------------------------------------------------------------------------

    @Test
    public void constructeurParametre_nomCorrect() {
        OptionRequeteHTTP opt = new OptionRequeteHTTP("Accept", "text/html");
        assertEquals("Accept", opt.getNom());
    }

    @Test
    public void constructeurParametre_valeurCorrecte() {
        OptionRequeteHTTP opt = new OptionRequeteHTTP("Accept", "text/html");
        assertEquals("text/html", opt.getValeur());
    }

    // -------------------------------------------------------------------------
    // toString()
    // -------------------------------------------------------------------------

    @Test
    public void toString_contientNomEtValeur() {
        OptionRequeteHTTP opt = new OptionRequeteHTTP("User-Agent", "MetaMoteur/1.0");
        String s = opt.toString();
        assertTrue(s.contains("User-Agent"));
        assertTrue(s.contains("MetaMoteur/1.0"));
    }

    @Test
    public void toString_vide_retourneEspaceSeul() {
        OptionRequeteHTTP opt = new OptionRequeteHTTP();
        // "" + " " + "" donne " "
        assertEquals(" ", opt.toString());
    }

    // -------------------------------------------------------------------------
    // listeOptions()
    // -------------------------------------------------------------------------

    @Test
    public void listeOptions_nonNull() {
        Vector opts = OptionRequeteHTTP.listeOptions();
        assertNotNull(opts);
    }

    @Test
    public void listeOptions_contientAccept() {
        Vector opts = OptionRequeteHTTP.listeOptions();
        assertTrue(opts.contains("Accept"));
    }

    @Test
    public void listeOptions_contientContentType() {
        Vector opts = OptionRequeteHTTP.listeOptions();
        assertTrue(opts.contains("Content-Type"));
    }

    @Test
    public void listeOptions_contientHost() {
        Vector opts = OptionRequeteHTTP.listeOptions();
        assertTrue(opts.contains("Host"));
    }

    @Test
    public void listeOptions_contientConnection() {
        Vector opts = OptionRequeteHTTP.listeOptions();
        assertTrue(opts.contains("Connection"));
    }

    @Test
    public void listeOptions_tailleSuperieureAZero() {
        Vector opts = OptionRequeteHTTP.listeOptions();
        assertTrue(opts.size() > 0);
    }
}
