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

import static org.junit.Assert.*;

/**
 * Tests unitaires pour {@link OptionReponseHTTP}.
 * Couvre : constructeur par défaut, constructeur(nom,valeur),
 *          constructeur(nom) pour "Server" et "Date",
 *          construcServ(), construcDate(),
 *          getNom(), getValeur(), toString().
 */
public class TestOptionReponseHTTP {

    // -------------------------------------------------------------------------
    // Constructeur par défaut
    // -------------------------------------------------------------------------

    @Test
    public void constructeurParDefaut_nomVide() {
        OptionReponseHTTP opt = new OptionReponseHTTP();
        assertEquals("", opt.getNom());
    }

    @Test
    public void constructeurParDefaut_valeurVide() {
        OptionReponseHTTP opt = new OptionReponseHTTP();
        assertEquals("", opt.getValeur());
    }

    // -------------------------------------------------------------------------
    // Constructeur(nom, valeur)
    // -------------------------------------------------------------------------

    @Test
    public void constructeurNomValeur_nomCorrect() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Content-Type", "text/html");
        assertEquals("Content-Type", opt.getNom());
    }

    @Test
    public void constructeurNomValeur_valeurCorrecte() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Content-Type", "text/html");
        assertEquals("text/html", opt.getValeur());
    }

    // -------------------------------------------------------------------------
    // Constructeur(nom) avec "Server"
    // -------------------------------------------------------------------------

    @Test
    public void constructeurNom_server_nomEgalServer() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Server");
        assertEquals("Server", opt.getNom());
    }

    @Test
    public void constructeurNom_server_valeurNonVide() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Server");
        assertFalse(opt.getValeur().isEmpty());
    }

    @Test
    public void constructeurNom_server_valeurContientMetaMoteur() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Server");
        assertTrue(opt.getValeur().toLowerCase().contains("meta"));
    }

    // -------------------------------------------------------------------------
    // Constructeur(nom) avec "Date"
    // -------------------------------------------------------------------------

    @Test
    public void constructeurNom_date_nomEgalDate() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Date");
        assertEquals("Date", opt.getNom());
    }

    @Test
    public void constructeurNom_date_valeurNonVide() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Date");
        assertFalse(opt.getValeur().isEmpty());
    }

    // -------------------------------------------------------------------------
    // construcServ() / construcDate() appelés directement
    // -------------------------------------------------------------------------

    @Test
    public void construcServ_renseigneNomEtValeur() {
        OptionReponseHTTP opt = new OptionReponseHTTP();
        opt.construcServ();
        assertEquals("Server", opt.getNom());
        assertFalse(opt.getValeur().isEmpty());
    }

    @Test
    public void construcDate_renseigneNomEtValeur() {
        OptionReponseHTTP opt = new OptionReponseHTTP();
        opt.construcDate();
        assertEquals("Date", opt.getNom());
        assertFalse(opt.getValeur().isEmpty());
    }

    // -------------------------------------------------------------------------
    // toString()
    // -------------------------------------------------------------------------

    @Test
    public void toString_contientNomEtValeurSepares() {
        OptionReponseHTTP opt = new OptionReponseHTTP("Content-Length", "1024");
        String s = opt.toString();
        assertTrue(s.contains("Content-Length"));
        assertTrue(s.contains("1024"));
        // le séparateur est " : "
        assertTrue(s.contains(":"));
    }

    @Test
    public void toString_vide_retourneColonCentral() {
        OptionReponseHTTP opt = new OptionReponseHTTP();
        assertEquals(" : ", opt.toString());
    }
}
