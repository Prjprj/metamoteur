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

import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertTrue;

public class TestEnregistrement {
    @Test
    public void runTestEnregistrement() {
        /*
         * Vector v = Enregistrement.recuperationEnregistrements(); Enregistrement enr =
         * (Enregistrement) v.elementAt(1); Enregistrement enr =
         * Enregistrement.recuperationEnregistrement(2);
         * System.out.println(enr.getId()); System.out.println(enr.getKeywords());
         * Vector liens = enr.getLiens(); for (int i=0; i<liens.size(); i++) { Lien
         * lien= (Lien) liens.elementAt(i); System.out.println(lien.getUrl());
         * System.out.println(lien.getTitre()); System.out.println(lien.getDesc());
         * System.out.println(lien.getRang() + "\n\n"); }
         *
         * Vector v = new Vector(); v.addElement(new Integer(5)); v.addElement(new
         * Integer(4)); v.addElement(new Integer(3)); v.addElement(new Integer(5));
         * v.addElement(new Integer(1)); v.addElement(new Integer(0)); v.addElement(new
         * Integer(5)); v.addElement(new Integer(4)); v.addElement(new Integer(3));
         * v.addElement(new Integer(5)); v.addElement(new Integer(1)); v.addElement(new
         * Integer(0));
         *
         * int max = Enregistrement.entierMaximum(v); System.out.println("max: " + max);
         * int id = Enregistrement.idEntierMaximumLocal(1, v);
         * System.out.println("id du max: " + id);
         *
         * Vector res = Enregistrement.idEntiersEgaux(3, v);
         * System.out.println("Resultat :" + v.size()); System.out.println("Resultat :"
         * + res.size()); for (int i=0; i<res.size(); i++) {
         * System.out.println(res.elementAt(i)); }
         *
         * Vector res = Enregistrement.triOrdreDecroissant(v);
         * System.out.println("Resultat :" + v.size()); System.out.println("Resultat :"
         * + res.size()); for (int i=0; i<res.size(); i++) {
         * System.out.println(res.elementAt(i)); }
         */

        Vector liens = new Vector();
        liens.addElement(new Lien("url1", "titre1", "desc1", 1, 100));
        liens.addElement(new Lien("url2", "titre2", "desc2", 2, 2));
        liens.addElement(new Lien("url3", "titre3", "desc3", 3, 30));
        liens.addElement(new Lien("url4", "titre4", "desc4", 4, 4));
        liens.addElement(new Lien("url5", "titre5", "desc5", 5, 4));
        liens.addElement(new Lien("url6", "titre6", "desc6", 6, 60));
        liens.addElement(new Lien("url7", "titre7", "desc7", 7, 0));
        liens.addElement(new Lien("url8", "titre8", "desc8", 8, 0));
        liens.addElement(new Lien("url9", "titre9", "desc9", 9, 0));
        liens.addElement(new Lien("url10", "titre10", "desc10", 10, 100));

        int score[] = {100, 100, 60, 30, 4, 4, 2, 0, 0, 0};
        int rang[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Vector resLiens = Enregistrement.promotionLiensCliques(liens);
        for (int i = 0; i < resLiens.size(); i++) {
            assertTrue(((Lien) resLiens.elementAt(i)).getRang() == rang[i]);
            assertTrue(((Lien) resLiens.elementAt(i)).getScore() == score[i]);
			/*System.out.println(((Lien) resLiens.elementAt(i)).getUrl());
			System.out.print("     Rang :" + ((Lien) resLiens.elementAt(i)).getRang());
			System.out.println("    Score :" + ((Lien) resLiens.elementAt(i)).getScore());*/
        }

        /*
         * Agent agent = new Agent(); Enregistrement enr =
         * Enregistrement.recuperationEnregistrement(40); Vector resLiens =
         * enr.getLiens(); for (int i=0; i<resLiens.size(); i++) {
         * System.out.println("Lien " + i + " :    " +
         * ((Lien)resLiens.elementAt(i)).getUrl()); System.out.print("    Rang :" +
         * ((Lien) resLiens.elementAt(i)).getRang()); System.out.println("    Score :" +
         * ((Lien) resLiens.elementAt(i)).getScore()); }
         */

    }
}
