package agent;

import java.util.Vector;

public class TestEnregistrement {

	public static void main(String args[]) {
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

		Vector resLiens = Enregistrement.promotionLiensCliques(liens);
		for (int i = 0; i < resLiens.size(); i++) {
			System.out.println(((Lien) resLiens.elementAt(i)).getUrl());
			System.out.print("     Rang :" + ((Lien) resLiens.elementAt(i)).getRang());
			System.out.println("    Score :" + ((Lien) resLiens.elementAt(i)).getScore());
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
