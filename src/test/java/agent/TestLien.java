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

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour la classe {@link Lien}.
 * Couvre : constructeurs, getters, setters, estPairRang(),
 *          toString(), toSql(), toXml(), toXhtml().
 */
public class TestLien {

    @BeforeClass
    public static void setUp() {
        // toXml() peut appeler GestionMessage ; toXhtml() n'en a pas besoin
        Agent.CONFIG = AppConfigLoader.load(Agent.FichierConf);
    }

    // -------------------------------------------------------------------------
    // Constructeur par défaut
    // -------------------------------------------------------------------------

    @Test
    public void constructeurParDefaut_valeursVides() {
        Lien lien = new Lien();
        assertEquals("", lien.getUrl());
        assertEquals("", lien.getTitre());
        assertEquals("", lien.getDesc());
        assertEquals(0, lien.getRang());
        assertEquals(0, lien.getScore());
    }

    // -------------------------------------------------------------------------
    // Constructeur paramétré
    // -------------------------------------------------------------------------

    @Test
    public void constructeurParametre_valeursInitialisees() {
        Lien lien = new Lien("http://example.com", "Titre", "Description", 3, 42);
        assertEquals("http://example.com", lien.getUrl());
        assertEquals("Titre", lien.getTitre());
        assertEquals("Description", lien.getDesc());
        assertEquals(3, lien.getRang());
        assertEquals(42, lien.getScore());
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    @Test
    public void setUrl_modifieUrl() {
        Lien lien = new Lien();
        lien.setUrl("http://new.url");
        assertEquals("http://new.url", lien.getUrl());
    }

    @Test
    public void setTitre_modifieTitre() {
        Lien lien = new Lien();
        lien.setTitre("Nouveau titre");
        assertEquals("Nouveau titre", lien.getTitre());
    }

    @Test
    public void setDesc_modifieDesc() {
        Lien lien = new Lien();
        lien.setDesc("Nouvelle description");
        assertEquals("Nouvelle description", lien.getDesc());
    }

    @Test
    public void setRang_modifieRang() {
        Lien lien = new Lien();
        lien.setRang(5);
        assertEquals(5, lien.getRang());
    }

    @Test
    public void setScore_modifieScore() {
        Lien lien = new Lien();
        lien.setScore(100);
        assertEquals(100, lien.getScore());
    }

    // -------------------------------------------------------------------------
    // estPairRang()
    // -------------------------------------------------------------------------

    @Test
    public void estPairRang_rangPair_retourneTrue() {
        Lien lien = new Lien("url", "titre", "desc", 4, 0);
        assertTrue(lien.estPairRang());
    }

    @Test
    public void estPairRang_rangImpair_retourneFalse() {
        Lien lien = new Lien("url", "titre", "desc", 3, 0);
        assertFalse(lien.estPairRang());
    }

    @Test
    public void estPairRang_rangZero_retourneTrue() {
        Lien lien = new Lien("url", "titre", "desc", 0, 0);
        assertTrue(lien.estPairRang());
    }

    @Test
    public void estPairRang_rangUn_retourneFalse() {
        Lien lien = new Lien("url", "titre", "desc", 1, 0);
        assertFalse(lien.estPairRang());
    }

    // -------------------------------------------------------------------------
    // toString()
    // -------------------------------------------------------------------------

    @Test
    public void toString_contientUrlEtTitreEtDesc() {
        Lien lien = new Lien("http://example.com", "Mon titre", "Ma desc", 2, 0);
        String res = lien.toString();
        assertTrue(res.contains("http://example.com"));
        assertTrue(res.contains("Mon titre"));
        assertTrue(res.contains("Ma desc"));
    }

    // -------------------------------------------------------------------------
    // toSql()
    // -------------------------------------------------------------------------

    @Test
    public void toSql_contientUrlEtRangEtScore() {
        Lien lien = new Lien("http://example.com", "titre", "desc", 1, 5);
        String sql = lien.toSql();
        assertTrue(sql.contains("http://example.com"));
        assertTrue(sql.contains("1"));
        assertTrue(sql.contains("5"));
    }

    @Test
    public void toSql_echappeLesSingleQuotes() {
        Lien lien = new Lien("url", "titre avec ' apostrophe", "desc", 1, 0);
        String sql = lien.toSql();
        // la méthode toSql() remplace les apostrophes par un espace
        assertFalse(sql.contains("'apostrophe"));
    }

    // -------------------------------------------------------------------------
    // toXml()
    // -------------------------------------------------------------------------

    @Test
    public void toXml_contientBalisesLinkTitleUrlDesc() {
        Lien lien = new Lien("http://example.com", "Titre", "Description", 1, 0);
        String xml = lien.toXml();
        assertTrue(xml.contains("<link"));
        assertTrue(xml.contains("</link>"));
        assertTrue(xml.contains("<title>"));
        assertTrue(xml.contains("<url>"));
        assertTrue(xml.contains("<desc>"));
    }

    @Test
    public void toXml_contientRangEtScore() {
        Lien lien = new Lien("http://example.com", "Titre", "Description", 3, 7);
        String xml = lien.toXml();
        assertTrue(xml.contains("rank=\"3\""));
        assertTrue(xml.contains("score=\"7\""));
    }

    @Test
    public void toXml_echappeLAmpersand() {
        Lien lien = new Lien("http://example.com?a=1&b=2", "Titre", "Desc", 1, 0);
        String xml = lien.toXml();
        assertTrue(xml.contains("&amp;"));
    }

    // -------------------------------------------------------------------------
    // toXhtml()
    // -------------------------------------------------------------------------

    @Test
    public void toXhtml_rangPair_contientClassBlockDistinct() {
        Lien lien = new Lien("http://example.com", "Titre", "Description", 2, 0);
        String xhtml = lien.toXhtml();
        assertTrue(xhtml.contains("blockDistinct"));
    }

    @Test
    public void toXhtml_rangImpair_contientClassBlock() {
        Lien lien = new Lien("http://example.com", "Titre", "Description", 1, 0);
        String xhtml = lien.toXhtml();
        // doit contenir "block" mais pas "blockDistinct"
        assertTrue(xhtml.contains("class=\"block\""));
        assertFalse(xhtml.contains("blockDistinct"));
    }

    @Test
    public void toXhtml_scorePositif_contientImageEtoile() {
        Lien lien = new Lien("http://example.com", "Titre", "Description", 1, 5);
        String xhtml = lien.toXhtml();
        assertTrue(xhtml.contains("etoile.png"));
    }

    @Test
    public void toXhtml_scoreNul_sansImageEtoile() {
        Lien lien = new Lien("http://example.com", "Titre", "Description", 1, 0);
        String xhtml = lien.toXhtml();
        assertFalse(xhtml.contains("etoile.png"));
    }
}
