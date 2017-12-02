package tri;

import java.util.Vector;

import agent.Enregistrement;
import agent.Lien;

public class TestPermutations {

	/* Les tests unitaires ont ete ralises lors de l'implementation
	 * de chaque methodes.
	 * 
	 * Des affichages ont ete effectues directement dans les methodes
	 * de la classe Permutations.
	 * Ils ont ete enleves par la suite, apres la validation
	 * de chaque methode.
	 */	
	
	public static void main(String args[]) {
		Enregistrement enrMR = Enregistrement.recuperationEnregistrement(1);
		
		/*	
		Vector motsClesSim = Permutations.simMotsCles(enrMR.getKeywords()); 
		System.out.println(motsClesSim.size());
		
		Vector casSim = Permutations.simCasSources(enrMR, motsClesSim);
		System.out.println(casSim.size());
		
		Vector assRang = Permutations.associationRangCasSrc_MR(enrMR, casSim);
		System.out.println(assRang.size());
		
		Vector docsPermutes = Permutations.permutationDocuments(enrMR, assRang);
		System.out.println(docsPermutes.size());
		
		Enregistrement enre = Permutations.permutations(enrMR, docsPermutes);
		
		System.out.println(enre.getId());
		System.out.println(enre.getKeywords());
		*/
		
		Permutations.lancementPermutations(enrMR);
		Enregistrement enre = Permutations.getEnrMR_liensPermutes();
		System.out.println("ID de l enregistrement: " + enre.getId());
		System.out.println("Mots cles : " + enre.getKeywords());
		System.out.println("Liens permutes : \n" );
		Vector liens = enre.getLiens();
		for (int i=0; i<liens.size(); i++) {
			Lien lien= (Lien) liens.elementAt(i);
			System.out.println(lien.getUrl());
			System.out.println(lien.getTitre());
			System.out.println(lien.getDesc());
			System.out.println(lien.getRang() + "\n\n");
		}
		
		
	}
}
