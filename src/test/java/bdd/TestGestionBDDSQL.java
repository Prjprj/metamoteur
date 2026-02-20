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
 * --LICENSE NOTICE--
 */

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires purs (sans accès base de données) pour les méthodes SQL
 * de construction de requêtes dans {@link GestionBDD} :
 * construitSelect et construitUpdate.
 *
 * Ces méthodes ne font aucune E/S et peuvent être testées en isolation totale.
 */
public class TestGestionBDDSQL {

    // =========================================================================
    // construitSelect
    // =========================================================================

    @Test
    public void construitSelect_sansCondition_requeteSansWhere() {
        String sql = GestionBDD.construitSelect("*", "BDD", " ");
        assertEquals("SELECT * FROM BDD", sql);
    }

    @Test
    public void construitSelect_avecCondition_requeteAvecWhere() {
        String sql = GestionBDD.construitSelect("*", "BDD", "UID='1'");
        assertEquals("SELECT * FROM BDD WHERE UID='1'", sql);
    }

    @Test
    public void construitSelect_restrictionColonnes() {
        String sql = GestionBDD.construitSelect("UID,KEYWORDS", "BDD", " ");
        assertTrue(sql.startsWith("SELECT UID,KEYWORDS FROM BDD"));
        assertFalse(sql.contains("WHERE"));
    }

    @Test
    public void construitSelect_conditionMultiple() {
        String sql = GestionBDD.construitSelect("*", "BDD", "UID='1' AND KEYWORDS='java'");
        assertTrue(sql.contains("WHERE UID='1' AND KEYWORDS='java'"));
    }

    @Test
    public void construitSelect_debutAvecSELECT() {
        String sql = GestionBDD.construitSelect("*", "BDD", " ");
        assertTrue(sql.startsWith("SELECT "));
    }

    // =========================================================================
    // construitUpdate
    // =========================================================================

    @Test
    public void construitUpdate_avecCondition_requeteAvecWhere() {
        String sql = GestionBDD.construitUpdate("BDD", "SELECT1=SELECT1+1", "UID='5'");
        assertEquals("UPDATE BDD SET SELECT1=SELECT1+1 WHERE UID='5'", sql);
    }

    @Test
    public void construitUpdate_sansCondition_requeteSansWhere() {
        String sql = GestionBDD.construitUpdate("BDD", "RANK1=1", " ");
        assertEquals("UPDATE BDD SET RANK1=1", sql);
    }

    @Test
    public void construitUpdate_debutAvecUPDATE() {
        String sql = GestionBDD.construitUpdate("BDD", "RANK1=0", " ");
        assertTrue(sql.startsWith("UPDATE "));
    }

    @Test
    public void construitUpdate_contientSET() {
        String sql = GestionBDD.construitUpdate("BDD", "RANK1=0", " ");
        assertTrue(sql.contains(" SET "));
    }

    @Test
    public void construitUpdate_nomTableCorrect() {
        String sql = GestionBDD.construitUpdate("MA_TABLE", "COL=1", " ");
        assertTrue(sql.contains("MA_TABLE"));
    }
}
