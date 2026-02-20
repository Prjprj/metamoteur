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

import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour les méthodes statiques utilitaires de {@link Enregistrement}
 * précédemment commentées dans TestEnregistrement :
 * entierMaximum, entierMaximumLocal, idEntiersEgaux, triOrdreDecroissant.
 *
 * Également : getters/setters de l'instance, getUrls, getTitres, getDescs,
 *             getRangs, add, toString, toXml.
 */
public class TestEnregistrementStatiques {

    // =========================================================================
    // entierMaximum
    // =========================================================================

    @Test
    public void entierMaximum_vecteurNormal_retourneLeMax() {
        Vector v = buildVector(5, 4, 3, 5, 1, 0);
        assertEquals(5, Enregistrement.entierMaximum(v));
    }

    @Test
    public void entierMaximum_tousIdentiques_retourneValeur() {
        Vector v = buildVector(3, 3, 3);
        assertEquals(3, Enregistrement.entierMaximum(v));
    }

    @Test
    public void entierMaximum_vecteurUnElement_retourneElement() {
        Vector v = buildVector(7);
        assertEquals(7, Enregistrement.entierMaximum(v));
    }

    @Test
    public void entierMaximum_tousAZero_retourneZero() {
        Vector v = buildVector(0, 0, 0);
        assertEquals(0, Enregistrement.entierMaximum(v));
    }

    // =========================================================================
    // entierMaximumLocal
    // =========================================================================

    @Test
    public void entierMaximumLocal_limiteAuDessusDesValeurs_retourneMaxReel() {
        Vector v = buildVector(3, 5, 1, 4);
        // max strictement inférieur à 6 → attend 5
        assertEquals(5, Enregistrement.entierMaximumLocal(6, v));
    }

    @Test
    public void entierMaximumLocal_limiteEgaleAuMax_exclutLeMax() {
        Vector v = buildVector(3, 5, 1, 4);
        // max strictement inférieur à 5 → attend 4
        assertEquals(4, Enregistrement.entierMaximumLocal(5, v));
    }

    @Test
    public void entierMaximumLocal_limiteAuDessusDesTous_retourneLePlusPetit() {
        Vector v = buildVector(2, 1);
        assertEquals(1, Enregistrement.entierMaximumLocal(2, v));
    }

    @Test
    public void entierMaximumLocal_aucuneSousLaLimite_retourneMinusUn() {
        Vector v = buildVector(5, 5, 5);
        // aucune valeur < 5 → retourne -1 (valeur sentinelle)
        assertEquals(-1, Enregistrement.entierMaximumLocal(5, v));
    }

    // =========================================================================
    // idEntiersEgaux
    // =========================================================================

    @Test
    public void idEntiersEgaux_valeurPresentePlusieursOccurrences() {
        Vector v = buildVector(5, 4, 3, 5, 1, 0, 5);
        Vector res = Enregistrement.idEntiersEgaux(5, v);
        assertEquals(3, res.size());
        assertEquals(0, ((Integer) res.elementAt(0)).intValue());
        assertEquals(3, ((Integer) res.elementAt(1)).intValue());
        assertEquals(6, ((Integer) res.elementAt(2)).intValue());
    }

    @Test
    public void idEntiersEgaux_valeurAbsente_retourneVide() {
        Vector v = buildVector(1, 2, 3);
        Vector res = Enregistrement.idEntiersEgaux(9, v);
        assertTrue(res.isEmpty());
    }

    @Test
    public void idEntiersEgaux_valeurZero_retourneIndexCorrects() {
        Vector v = buildVector(0, 1, 0, 2, 0);
        Vector res = Enregistrement.idEntiersEgaux(0, v);
        assertEquals(3, res.size());
    }

    // =========================================================================
    // triOrdreDecroissant
    // =========================================================================

    @Test
    public void triOrdreDecroissant_vecteurSimple_ordreDecroissant() {
        // scores : index 0→100, 1→2, 2→30, 3→4, 4→4, 5→60, 6→0, 7→0, 8→0, 9→100
        Vector scores = buildVector(100, 2, 30, 4, 4, 60, 0, 0, 0, 100);
        Vector res = Enregistrement.triOrdreDecroissant(scores);
        // premier : index 0 ou 9 (score 100)
        int premier = ((Integer) res.elementAt(0)).intValue();
        int second  = ((Integer) res.elementAt(1)).intValue();
        // les deux éléments à 100 doivent venir en premier
        assertTrue(premier == 0 || premier == 9);
        assertTrue(second  == 0 || second  == 9);
        assertNotEquals(premier, second);
    }

    @Test
    public void triOrdreDecroissant_tousIdentiques_taillePréservée() {
        Vector v = buildVector(3, 3, 3, 3);
        Vector res = Enregistrement.triOrdreDecroissant(v);
        assertEquals(4, res.size());
    }

    @Test
    public void triOrdreDecroissant_avecZeros_zeroEnDernier() {
        Vector v = buildVector(0, 5, 0, 3);
        Vector res = Enregistrement.triOrdreDecroissant(v);
        // les indices à 0 (indices 0 et 2 dans v) doivent être en fin
        int avant_dernier = ((Integer) res.elementAt(res.size() - 2)).intValue();
        int dernier       = ((Integer) res.elementAt(res.size() - 1)).intValue();
        // avant_dernier et dernier doivent être 0 ou 2 (indices dans v qui avaient score 0)
        assertTrue(avant_dernier == 0 || avant_dernier == 2);
        assertTrue(dernier       == 0 || dernier       == 2);
    }

    // =========================================================================
    // Constructeurs Enregistrement & méthodes instance
    // =========================================================================

    @Test
    public void constructeurParDefaut_valeursInitiales() {
        Enregistrement e = new Enregistrement();
        assertEquals(0, e.getId());
        assertEquals("", e.getKeywords());
        assertNotNull(e.getLiens());
        assertTrue(e.getLiens().isEmpty());
    }

    @Test
    public void constructeurParametre_valeursInjectees() {
        Vector liens = new Vector();
        liens.add(new Lien("url1", "t1", "d1", 1, 0));
        Enregistrement e = new Enregistrement(42, "java", liens);
        assertEquals(42, e.getId());
        assertEquals("java", e.getKeywords());
        assertEquals(1, e.getLiens().size());
    }

    @Test
    public void constructeurCopie_cloneCorrect() {
        Vector liens = new Vector();
        liens.add(new Lien("url1", "t1", "d1", 1, 0));
        Enregistrement original = new Enregistrement(7, "test", liens);
        Enregistrement copie = new Enregistrement(original);
        assertEquals(original.getId(), copie.getId());
        assertEquals(original.getKeywords(), copie.getKeywords());
        assertEquals(original.getLiens().size(), copie.getLiens().size());
    }

    @Test
    public void setKeywords_modifieMotsCles() {
        Enregistrement e = new Enregistrement();
        e.setKeywords("nouveau");
        assertEquals("nouveau", e.getKeywords());
    }

    @Test
    public void setId_modifieId() {
        Enregistrement e = new Enregistrement();
        e.setId(99);
        assertEquals(99, e.getId());
    }

    @Test
    public void add_ajouteUnLien() {
        Enregistrement e = new Enregistrement();
        e.add(new Lien("url", "titre", "desc", 1, 0));
        assertEquals(1, e.getLiens().size());
    }

    @Test
    public void getUrls_retourneToutesLesUrls() {
        Vector liens = new Vector();
        liens.add(new Lien("http://a.com", "t1", "d1", 1, 0));
        liens.add(new Lien("http://b.com", "t2", "d2", 2, 0));
        Enregistrement e = new Enregistrement(1, "kw", liens);
        Vector urls = e.getUrls();
        assertEquals(2, urls.size());
        assertEquals("http://a.com", urls.elementAt(0));
        assertEquals("http://b.com", urls.elementAt(1));
    }

    @Test
    public void getTitres_retourneTousLesTitres() {
        Vector liens = new Vector();
        liens.add(new Lien("url1", "Titre A", "d1", 1, 0));
        liens.add(new Lien("url2", "Titre B", "d2", 2, 0));
        Enregistrement e = new Enregistrement(1, "kw", liens);
        Vector titres = e.getTitres();
        assertEquals("Titre A", titres.elementAt(0));
        assertEquals("Titre B", titres.elementAt(1));
    }

    @Test
    public void getDescs_retourneToutesLesDescs() {
        Vector liens = new Vector();
        liens.add(new Lien("url1", "t1", "Desc A", 1, 0));
        liens.add(new Lien("url2", "t2", "Desc B", 2, 0));
        Enregistrement e = new Enregistrement(1, "kw", liens);
        Vector descs = e.getDescs();
        assertEquals("Desc A", descs.elementAt(0));
        assertEquals("Desc B", descs.elementAt(1));
    }

    @Test
    public void getRangs_retourneTousLesRangs() {
        Vector liens = new Vector();
        liens.add(new Lien("url1", "t1", "d1", 3, 0));
        liens.add(new Lien("url2", "t2", "d2", 7, 0));
        Enregistrement e = new Enregistrement(1, "kw", liens);
        Vector rangs = e.getRangs();
        assertEquals(3, ((Integer) rangs.elementAt(0)).intValue());
        assertEquals(7, ((Integer) rangs.elementAt(1)).intValue());
    }

    @Test
    public void toXml_contientKeywordsEtBalisesSearch() {
        Agent.CONFIG = AppConfigLoader.load(Agent.FichierConf);
        Vector liens = new Vector();
        liens.add(new Lien("http://example.com", "Titre", "Desc", 1, 0));
        Enregistrement e = new Enregistrement(1, "test xml", liens);
        String xml = e.toXml();
        assertTrue(xml.contains("<search>"));
        assertTrue(xml.contains("<keywords>test xml</keywords>"));
        assertTrue(xml.contains("</search>"));
        assertTrue(xml.contains("<link"));
    }

    @Test
    public void toString_contientIdEtKeywords() {
        Vector liens = new Vector();
        Enregistrement e = new Enregistrement(5, "motsCles", liens);
        String s = e.toString();
        assertTrue(s.contains("5"));
        assertTrue(s.contains("motsCles"));
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Vector buildVector(int... values) {
        Vector v = new Vector();
        for (int val : values) {
            v.addElement(val);
        }
        return v;
    }
}
