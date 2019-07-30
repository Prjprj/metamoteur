package tri;

import java.util.Vector;
import agent.*;

public class TestTri {

	public static void main(String args[]) {

		Vector liensAL = new Vector();
		liensAL.addElement(new Lien("url1", "titre1", "desc1", 1, 10));
		liensAL.addElement(new Lien("url2", "titre2", "desc2", 2, 9));
		liensAL.addElement(new Lien("url3", "titre3", "desc3", 3, 8));
		liensAL.addElement(new Lien("url4", "titre4", "desc4", 4, 7));
		liensAL.addElement(new Lien("url5", "titre5", "desc5", 5, 6));
		liensAL.addElement(new Lien("url6", "titre6", "desc6", 6, 5));
		liensAL.addElement(new Lien("url7", "titre7", "desc7", 7, 4));
		liensAL.addElement(new Lien("url8", "titre8", "desc8", 8, 3));
		liensAL.addElement(new Lien("url9", "titre9", "desc9", 9, 2));
		liensAL.addElement(new Lien("url10", "titre10", "desc10", 10, 1));
		Enregistrement enrAL = new Enregistrement(1, "mots AL", liensAL);

		Vector liensAA1 = new Vector();
		liensAA1.addElement(new Lien("url10", "titre10", "desc10", 1, 10));
		liensAA1.addElement(new Lien("url9", "titre9", "desc9", 2, 9));
		liensAA1.addElement(new Lien("url8", "titre8", "desc8", 3, 8));
		liensAA1.addElement(new Lien("url7", "titre7", "desc7", 4, 7));
		liensAA1.addElement(new Lien("url6", "titre6", "desc6", 5, 6));
		liensAA1.addElement(new Lien("url5", "titre5", "desc5", 6, 5));
		liensAA1.addElement(new Lien("url4", "titre4", "desc4", 7, 4));
		liensAA1.addElement(new Lien("url3", "titre3", "desc3", 8, 3));
		liensAA1.addElement(new Lien("url2", "titre2", "desc2", 9, 2));
		liensAA1.addElement(new Lien("url1", "titre1", "desc1", 10, 1));
		Enregistrement enrAA1 = new Enregistrement(2, "mots AA1", liensAA1);

		Vector liensAA2 = new Vector();
		liensAA2.addElement(new Lien("url10", "titre10", "desc10", 10, 10));
		liensAA2.addElement(new Lien("url9", "titre9", "desc9", 9, 9));
		liensAA2.addElement(new Lien("url8", "titre8", "desc8", 8, 8));
		liensAA2.addElement(new Lien("url7", "titre7", "desc7", 7, 7));
		liensAA2.addElement(new Lien("url6", "titre6", "desc6", 6, 6));
		liensAA2.addElement(new Lien("url5", "titre5", "desc5", 5, 5));
		liensAA2.addElement(new Lien("url4", "titre4", "desc4", 4, 4));
		liensAA2.addElement(new Lien("url3", "titre3", "desc3", 3, 3));
		liensAA2.addElement(new Lien("url2", "titre2", "desc2", 2, 2));
		liensAA2.addElement(new Lien("url1", "titre1", "desc1", 1, 1));
		Enregistrement enrAA2 = new Enregistrement(3, "mots AA2", liensAA2);

		Vector ensEnrAA = new Vector();
		ensEnrAA.addElement(enrAA1);
		ensEnrAA.addElement(enrAA2);

		/*
		 * Tri.initEnrLocal(enrAL); Vector ensEnrTries =
		 * Tri.ensEnrTriesLiensSimilairesAA(ensEnrAA); Enregistrement enrTrie1 =
		 * (Enregistrement) ensEnrTries.elementAt(0); Vector liensTries1 =
		 * enrTrie1.getLiens(); Enregistrement enrTrie2 = (Enregistrement)
		 * ensEnrTries.elementAt(1); Vector liensTries2 = enrTrie2.getLiens();
		 * System.out.println("ID    : " + enrTrie1.getId() + "          " +
		 * enrTrie2.getId()); System.out.println("Mots  : " + enrTrie1.getKeywords() +
		 * "          " + enrTrie2.getKeywords()); for (int i=0; i<liensTries1.size();
		 * i++) { System.out.print("Lien " + i + " :  ");
		 * System.out.println(((Lien)liensTries1.elementAt(i)).getUrl());
		 * System.out.print("Enr1   Rang :" + ((Lien)
		 * liensTries1.elementAt(i)).getRang()); System.out.println("     Score :" +
		 * ((Lien) liensTries1.elementAt(i)).getScore());
		 * System.out.print("Enr2   Rang :" + ((Lien)
		 * liensTries2.elementAt(i)).getRang()); System.out.println("    Score :" +
		 * ((Lien) liensTries2.elementAt(i)).getScore());
		 * 
		 * }
		 * 
		 * //double moyenne = Tri.moyenneRangLien(0, ensEnrTries);
		 * //System.out.println("\nMoyenne des rangs du lien 0 : " + moyenne);
		 * 
		 * Vector moyenneRangs = Tri.moyenneRangsLiensAA(ensEnrTries); System.out.
		 * println("\nMoyenne du rang de chaque lien pour les autres agents : "); for
		 * (int i=0; i<moyenneRangs.size(); i++) { System.out.println("   Moyenne lien "
		 * + i + " : " + moyenneRangs.elementAt(i)); }
		 * 
		 * Vector moyenneTotale = Tri.moyenneRangsLiensTousAgents(ensEnrAA);
		 * System.out.println("\nMoyenne generale de tous les agents en tenant \n" +
		 * "compte de la promotion locale et du coeff de \n" +
		 * "ponderation pour les autres agents :"); for (int i=0; i<moyenneRangs.size();
		 * i++) { System.out.println("   Moyenne lien " + i + " : " +
		 * moyenneTotale.elementAt(i)); }
		 * 
		 * Enregistrement enr_trie = Tri.tri(moyenneTotale);
		 * System.out.println("\nID de l enregistrement trie: " + enr_trie.getId());
		 * System.out.println("Mots cles de l'enregistrement trie: " +
		 * enr_trie.getKeywords()); System.out.println("Liens permutes : " ); Vector
		 * liens = enr_trie.getLiens(); for (int i=0; i<liens.size(); i++) { Lien lien=
		 * (Lien) liens.elementAt(i); System.out.print("   " + lien.getUrl());
		 * System.out.print("   " + lien.getTitre()); System.out.print("   " +
		 * lien.getDesc()); System.out.println("   " + lien.getRang()); }
		 */

		Tri.lancementTri(enrAL, ensEnrAA);
		Enregistrement enr_trie = Tri.getEnrMR_TriFinal();
		System.out.println("\nID de l enregistrement trie: " + enr_trie.getId());
		System.out.println("Mots cles de l'enregistrement trie: " + enr_trie.getKeywords());
		System.out.println("Liens permutes : ");
		Vector liens = enr_trie.getLiens();
		for (int i = 0; i < liens.size(); i++) {
			Lien lien = (Lien) liens.elementAt(i);
			System.out.print("   " + lien.getUrl());
			System.out.print("   " + lien.getTitre());
			System.out.print("   " + lien.getDesc());
			System.out.print("     (Ancien Rang): " + lien.getRang());
			int j = i + 1;
			System.out.println("     (Nouveau Rang): " + j);

		}

	}
}
