package tri;

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

import agent.Lien;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour la classe {@link Document}.
 * Couvre : constructeur par défaut, constructeur paramétré,
 *          getIdDoc(), getLienDoc(), toString().
 */
public class TestDocument {

    // -------------------------------------------------------------------------
    // Constructeur par défaut
    // -------------------------------------------------------------------------

    @Test
    public void constructeurParDefaut_idNegatifUn() {
        Document doc = new Document();
        assertEquals(-1, doc.getIdDoc());
    }

    @Test
    public void constructeurParDefaut_lienNonNull() {
        Document doc = new Document();
        assertNotNull(doc.getLienDoc());
    }

    @Test
    public void constructeurParDefaut_lienVide() {
        Document doc = new Document();
        assertEquals("", doc.getLienDoc().getUrl());
        assertEquals("", doc.getLienDoc().getTitre());
        assertEquals("", doc.getLienDoc().getDesc());
    }

    // -------------------------------------------------------------------------
    // Constructeur paramétré
    // -------------------------------------------------------------------------

    @Test
    public void constructeurParametre_idCorrect() {
        Lien lien = new Lien("http://example.com", "Titre", "Desc", 1, 0);
        Document doc = new Document(3, lien);
        assertEquals(3, doc.getIdDoc());
    }

    @Test
    public void constructeurParametre_lienCorrect() {
        Lien lien = new Lien("http://example.com", "Titre Test", "Desc Test", 2, 5);
        Document doc = new Document(1, lien);
        assertSame(lien, doc.getLienDoc());
        assertEquals("http://example.com", doc.getLienDoc().getUrl());
        assertEquals("Titre Test", doc.getLienDoc().getTitre());
        assertEquals("Desc Test", doc.getLienDoc().getDesc());
        assertEquals(2, doc.getLienDoc().getRang());
        assertEquals(5, doc.getLienDoc().getScore());
    }

    // -------------------------------------------------------------------------
    // toString()
    // -------------------------------------------------------------------------

    @Test
    public void toString_contientId() {
        Lien lien = new Lien("url", "titre", "desc", 1, 0);
        Document doc = new Document(7, lien);
        assertTrue(doc.toString().contains("7"));
    }

    @Test
    public void toString_contientSautDeLigne() {
        Lien lien = new Lien("url", "titre", "desc", 1, 0);
        Document doc = new Document(1, lien);
        assertTrue(doc.toString().contains("\n"));
    }

    // -------------------------------------------------------------------------
    // Heritage de Lien - héritage vérifié
    // -------------------------------------------------------------------------

    @Test
    public void documentEstUnLien() {
        Lien lien = new Lien("url", "titre", "desc", 3, 0);
        Document doc = new Document(1, lien);
        assertTrue(doc instanceof Lien);
    }
}
