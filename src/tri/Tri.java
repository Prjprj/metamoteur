package tri;

/* --------
 * Tri.java
 * --------
 * 
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

/**
 * Titre       : classe publique Tri
 * Description : classe permettant de trier les resultats
 * 				 retournes par les autres agents et l'agent local.
 *  
 * @author  Jeremy FRECHARD
 * @author  Cécile GIRARD
 * @author  Aysel GUNES
 * @author  Pierre RAMOS
 * @version 1.0
 */

import agent.*;

import java.util.Vector;

public class Tri {

	/*
	 * Constante entiere representant le coefficient permettant de promouvoir
	 * l'enregistrement permute de l'agent local.
	 */
	final static int COEFF_PROMOTION_LOCALE = Agent.PromLocale;

	/*
	 * Constante entiere representant le coefficient de ponderation attribue pour
	 * l'enregistrement permute des autres agents.
	 */
	final static int COEFF_PONDERATION_AA = Agent.CoefPonderation;

	/*
	 * Variable de type Enregistrement representant l'enregistrement permute de
	 * l'agent local.
	 */
	private static Enregistrement enrLocal;

	/*
	 * Variable de type Enregistrement representant l'enregistrement trie selon les
	 * enregistrements permutes de l'agent local et des autres agents.
	 */
	private static Enregistrement enrMR_TriFinal;

	/**
	 * Retourne le contenu de la variable "enrLocal", enregistrement permute
	 * retourne par l'agent local.
	 * 
	 * @return Enregistrement
	 */
	public static Enregistrement getEnrLocal() {
		return enrLocal;
	}

	/**
	 * Retourne le contenu de la variable "enrMR_TriFinal", enregistrement trie en
	 * fonction des experiences de l'agent local et des autres agents.
	 * 
	 * @return Enregistrement : trie (celui retourne par le moteur de recherche).
	 */
	public static Enregistrement getEnrMR_TriFinal() {
		return enrMR_TriFinal;
	}

	/**
	 * Initialise la variable correspondant a l'enregistrement local (c'est-a-dire a
	 * celui resultant de la permutation des lien sretournes par le(s) moteurs(s) de
	 * recherche), a partir d'un enregistrement passe en parametre.
	 * 
	 * @param enr
	 *            Enregistrement : Un enregistrement correspondant a
	 *            l'enregistrement
	 */
	public static void initEnrLocal(Enregistrement enr) {
		enrLocal = enr;
	}

	/**
	 * Retourne un entier correspondant a l'index (l'indice dans le vecteur passe en
	 * parametre) du lien appartenant a l'enregistrement passe en parametre,
	 * similaire a celui passe en premier argument.
	 * 
	 * @param lienDonne
	 *            Lien : Un Lien.
	 * @param enr
	 *            Enregistrement : Un Enregistrement.
	 * @return int : Un entier correspondant a l'indice de ce lien dans
	 *         l'enregistrement.
	 */
	public static int indexLienSimilaireAA(Lien lienDonne, Enregistrement enr) {
		int index = -1;
		boolean test = false;
		Vector liens = enr.getLiens();
		for (int i = 0; (i < liens.size()) && (test == false); i++) {
			Lien lien = (Lien) liens.elementAt(i);
			if ((lienDonne.getUrl()).equals(lien.getUrl())) {
				index = i;
				test = true;
			}
		}
		return index;
	}

	/**
	 * Retourne un vecteur d'entiers dont les entiers correspondent aux indices des
	 * liens de l'enregistrement passe en parametre. En fait, chaque Lien a un
	 * similaire dans l'enregistrement local. Par consequent, l'entier stocke a
	 * l'indice "i" du vecteur retourne correspondra a l'indice du lien de
	 * l'enregistrement passe en parametre, similaire au lien "i" de
	 * l'enregistrement local.
	 * 
	 * @param enrAA
	 *            Enregistrement : Un Enregistrement appartenant aux Autres Agents.
	 * @return Vector : Un vecteur d'entiers contenant les indices des liens
	 *         similaires.
	 */
	public static Vector indexTriesLiensSimilairesAA(Enregistrement enrAA) {
		Vector resultat = new Vector();
		Vector liensAL = getEnrLocal().getLiens();
		for (int i = 0; i < liensAL.size(); i++) {
			Lien lienAL = (Lien) liensAL.elementAt(i);
			int index = indexLienSimilaireAA(lienAL, enrAA);
			resultat.addElement(new Integer(index));
		}
		return resultat;
	}

	/**
	 * Retourne un Enregistrement correspondant a l'enregistrement passe en
	 * parametre, a la seule difference que ses liens sont reclasser selon les
	 * indices retournes par la methode "indexTriesLiensSimilairesAA". Par
	 * consequent, le lien d'indice "i" de l'enregistrement passe en parametre sera
	 * le meme que celui de l'enregistrement local, mais avec un rang et score,
	 * biensur, differents.
	 * 
	 * @param enrAA
	 *            Enregistrement : Un Enregistrement appartenant aux Autres Agents.
	 * @return Enregistrement ! Un Enregistrement dont les liens sont reclasses.
	 */
	public static Enregistrement enrTrieLiensSimilairesAA(Enregistrement enrAA) {
		Vector liensTries = new Vector();
		Vector liensAA = enrAA.getLiens();
		Vector indexTries = indexTriesLiensSimilairesAA(enrAA);
		for (int i = 0; i < indexTries.size(); i++) {
			int index = ((Integer) indexTries.elementAt(i)).intValue();
			Lien lien = (Lien) liensAA.elementAt(index);
			liensTries.addElement(lien);
		}
		int id = enrAA.getId();
		String motsCles = enrAA.getKeywords();
		Enregistrement enrAATrie = new Enregistrement(id, motsCles, liensTries);
		return enrAATrie;
	}

	/**
	 * Retourne un vecteur Enregistrements dont les liens de chacun d'entre eux ont
	 * ete reclasses selon les indices retournes par la methode
	 * "indexTriesLiensSimilairesAA". Par consequent, le lien d'indice "i" des
	 * enregistrements passes en parametre seront le meme que celui de
	 * l'enregistrement local, mais avec un rang et score, biensur, differents.
	 * 
	 * @param ensEnrAA
	 *            Vector : vecteur contenant l'ensemble des enregistrements retourné
	 *            par le client.
	 * @return Vector : Un vecteur d'Enregistrement.
	 */
	public static Vector ensEnrTriesLiensSimilairesAA(Vector ensEnrAA) {
		Vector ensEnrAATries = new Vector();
		for (int i = 0; i < ensEnrAA.size(); i++) {
			Enregistrement enrAA = (Enregistrement) ensEnrAA.elementAt(i);
			Enregistrement enrAATrie = enrTrieLiensSimilairesAA(enrAA);
			ensEnrAATries.addElement(enrAATrie);
		}
		return ensEnrAATries;
	}

	/**
	 * Retourne un entier correspondant a la somme des rangs de tous les liens
	 * d'indice "i", passe en parametre, de tous les enregistrements, passe en
	 * parametre via un vecteur d'Enregistrements.
	 * 
	 * @param index
	 *            int : Un entier representant un indice.
	 * @param ensEnrAA
	 *            Vector : Un vecteur d'Enregistrements representant ceux retournes
	 *            par les Autres Agents.
	 * @return int : Un entier.
	 */
	public static int sommeRangLien(int index, Vector ensEnrAA) {
		int somme = 0;
		for (int i = 0; i < ensEnrAA.size(); i++) {
			Enregistrement enr = (Enregistrement) ensEnrAA.elementAt(i);
			Vector liens = enr.getLiens();
			Lien lienDeIndex = (Lien) liens.elementAt(index);
			int rang = lienDeIndex.getRang();
			somme = somme + rang;
		}
		return somme;
	}

	/**
	 * Retourne un vecteur d'entiers contenant a son indice "i" la somme des rangs
	 * des liens d'indice "i" des enregistrements passes en parametre via un vecteur
	 * d'Enregistrements.
	 * 
	 * @param ensEnrAA
	 *            Vector : Un vecteur d'Enregistrements representant ceux retournes
	 *            par les Autres Agents.
	 * @return Vector : Un vecteur d'entiers contenant des sommes de rang de liens
	 *         retournes par les Autres Agents.
	 */
	public static Vector moyenneRangsLiensAA(Vector ensEnrAA) {
		int nbEnr = ensEnrAA.size();
		Vector resultat = new Vector();
		if (nbEnr > 0) {
			for (int i = 0; i < 20; i++) {
				int somme = sommeRangLien(i, ensEnrAA);
				resultat = Permutations.ajoutRangMoyen(somme, nbEnr, resultat);
			}
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur d'entiers correspondant aux rangs des liens de
	 * l'enregistrement local.
	 * 
	 * @return Vector : Un vecteur d'entiers.
	 */
	public static Vector getRangsAL() {
		Vector resultat = new Vector();
		Vector liensAL = getEnrLocal().getLiens();
		for (int i = 0; i < liensAL.size(); i++) {
			Lien lien = (Lien) liensAL.elementAt(i);
			resultat.addElement(new Integer(lien.getRang()));
		}
		return resultat;
	}

	/**
	 * Un vecteur d'entiers contenant a son indice "i" la somme des rangs des liens
	 * d'indice "i" des enregistrements passes en parametre, via un vecteur
	 * d'Enregistrements, et de l'enregistrement local. Les rangs des liens
	 * appartenant a l'enregistrement local sont promus par rapport a ceux des
	 * Autres Agents.
	 * 
	 * @param ensEnrAA
	 *            Vector : Un vecteur d'Enregistrements representant ceux retournes
	 *            par les Autres Agents.
	 * @return Vector : Un vecteur d'entiers contenant des sommes de rang de liens.
	 */
	public static Vector moyenneRangsLiensTousAgents(Vector ensEnrAA) {
		Vector moyenneRangsAA = moyenneRangsLiensAA(ensEnrAA);
		Vector rangsAL = getRangsAL();
		Vector resultat = new Vector();
		for (int i = 0; i < getRangsAL().size(); i++) {
			int promotionAL = COEFF_PROMOTION_LOCALE * ((Integer) rangsAL.elementAt(i)).intValue();
			int ponderationAA = COEFF_PONDERATION_AA * ((Integer) moyenneRangsAA.elementAt(i)).intValue();
			int somme = promotionAL + ponderationAA;
			int denominateur = COEFF_PROMOTION_LOCALE + COEFF_PONDERATION_AA;
			resultat = Permutations.ajoutRangMoyen(somme, denominateur, resultat);
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur d'entiers contenant des indices. A son indice "i" se
	 * trouve l'indice du lien ayant pour rang celui a l'indice "i" du vecteur
	 * d'entiers passe en parametre.
	 * 
	 * @param vecteurEntiers
	 *            int : Un vecteur d'entiers.
	 * @return Vector : Un vecteur d'entiers contenant des indices.
	 */
	public static Vector indexMoyenneRangsLiensTousAgentsTries(Vector vecteurEntiers) {
		Vector resultat = new Vector();
		for (int i = 1; i <= vecteurEntiers.size(); i++) {
			int index = vecteurEntiers.indexOf(new Integer(i));
			resultat.addElement(new Integer(index));
		}
		return resultat;
	}

	/**
	 * Retourne un Enregistrement correspondant au tri final: tri de
	 * l'enregistrement permute par l'agent local et les enregistrements permutes
	 * retournes par les Autres Agents. Il faut savoir que l'enregistrement local
	 * est promu par rapport a celui des Autres Agents dans la mesure ou l'on se
	 * base sur la theorie de la confiance plus en soi qu'aux autres. Les Autres
	 * Agents sont, la, pour nous donner leur experience a titre de conseil.
	 * 
	 * @param moyenneTotaleRangs
	 *            Vector : Un vecteur d'entiers contenant les rangs moyen de chaque
	 *            lien.
	 * @return Enregistrement
	 */
	public static Enregistrement tri(Vector moyenneTotaleRangs) {
		Vector indexMoyenneRangsTries = indexMoyenneRangsLiensTousAgentsTries(moyenneTotaleRangs);
		Vector liensAL = getEnrLocal().getLiens();
		Vector liensTries = new Vector();
		for (int i = 0; i < liensAL.size(); i++) {
			int index = ((Integer) indexMoyenneRangsTries.elementAt(i)).intValue();
			Lien lien = (Lien) liensAL.elementAt(index);
			liensTries.addElement(lien);
		}
		int id = getEnrLocal().getId();
		String motsCles = getEnrLocal().getKeywords();
		Enregistrement enr_trie = new Enregistrement(id, motsCles, liensTries);
		return enr_trie;
	}

	/**
	 * Methode permettant de lancer le tri en prenant en parametre l'enregistrement
	 * local (permutation de l'enregistrement retourne par le(s) moteur(s) de
	 * recherche par l'agent local) et un vecteur d'enregistrements correspondant
	 * aux enregistrements (permutes) par les Autres Agents.
	 * 
	 * @param enrLocal
	 *            Enregistrement. L'Enregistrement permute par l'agent local.
	 * @param ensEnrAA
	 *            Vector : Un vecteur d'enregistrements correspondant aux
	 *            enregistrements retournes par les Autres Agents.
	 */
	public static void lancementTri(Enregistrement enrLocal, Vector ensEnrAA) {
		GestionMessage.message(0, "Tri", "Lancement du tri.");
		initEnrLocal(enrLocal);
		if (ensEnrAA.size() > 0) {
			Vector moyenneTotaleRangs = moyenneRangsLiensTousAgents(ensEnrAA);
			enrMR_TriFinal = tri(moyenneTotaleRangs);
		} else {
			enrMR_TriFinal = enrLocal;
		}
	}

}
