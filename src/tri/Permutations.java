package tri;

/* -----------------
 * Permutations.java
 * -----------------
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
 * Titre       : classe publique Permutations implementant l'interface
 * 				 Constantes. 
 * Description : classe permettant de permuter les resultats
 * 				 retournes par le moteur de recherche.
 * 
 * @author  Jeremy FRECHARD
 * @author  Cécile GIRARD
 * @author  Aysel GUNES
 * @author  Pierre RAMOS
 * @version 1.0
 */

import java.util.Vector;
import agent.*;

public class Permutations {

	/*
	 * Constante reelle permettant de declarer le seuil de similarite.
	 */
	final static double SEUIL_SIMILARITE = Agent.CoefSim;

	/*
	 * Constante entiere permettant de representer un nombre de valeur negative.
	 */
	final static int NEGATIF = -1;

	/*
	 * Constante entiere permettant de representer un nombre de valeur nulle.
	 */
	final static int NUL = 0;

	/*
	 * Constante entiere permettant de representer un nombre de valeur positive.
	 */
	final static int POSITIF = 1;

	/*
	 * Un enregistrement representant l'enregsitrement retourne par le(s) moteur(s)
	 * de recherche, dont ses liens ont ete permutes.
	 */
	private static Enregistrement enrMR_liensPermutes;

	/**
	 * Retourne l'Enregistrement ensMR_liensPermutes.
	 * 
	 * @return Enregistrement
	 */
	public static Enregistrement getEnrMR_liensPermutes() {
		return enrMR_liensPermutes;
	}

	/**
	 * Retourne un vecteur d'enregistrements, contenant les enregistrements de la
	 * base de donnees. Appel a la methode "recuperationEnregistrements()" de la
	 * classe src.agent.Enregistrement.
	 * 
	 * @return Vector : Un vecteur d'enregistremnts.
	 */
	public static Vector initEnsEnregistrements() {
		return Enregistrement.recuperationEnregistrements();

	}

	/**
	 * Retourne un vecteur contenant les mots de la chaine de caracteres passe en
	 * parametre. Les mots seront de type String.
	 * 
	 * @param chaine
	 *            String : Une chaine de caracteres.
	 * @return Vector : Un vecteur contenant des mots de type String.
	 */
	public static Vector convertStringToVector(String chaine) {
		Vector v = new Vector();
		String mot;
		if (chaine.indexOf(" ") == -1) {
			v.add(chaine);
			return v;
		}
		int debut = 0;
		int fin = chaine.indexOf(" ", debut);

		while (debut != fin) {
			mot = chaine.substring(debut, fin);
			v.add(mot);
			debut = fin;
			fin = chaine.indexOf(" ", fin);
		}
		return v;
	}

	/**
	 * Recuperation des enregistrements de la base de donnees dont les mots cles
	 * sont similaires aux mots cles passes en parametre, c'est-a-dire ceux entres
	 * par l'utilisateur lors de sa requete. Les enregistrements retenus sont
	 * appeles des cas sources.
	 * 
	 * @param motsCles
	 *            String : Une chaine de caracteres(ceux entres par l'utilisateur).
	 * @return Vector : Un vecteur de cas sources.
	 */
	public static Vector simMotsCles(String motsCles) {
		Vector casSources = new Vector();
		Vector ensEnregistrements = initEnsEnregistrements();
		Vector vecteurMotsCles = convertStringToVector(motsCles);
		Vector vecteurMotsClesBDD;
		String motsClesBDD;
		Enregistrement enregist;
		boolean test;

		for (int i = 0; i < ensEnregistrements.size(); i++) {
			test = false;
			enregist = (Enregistrement) ensEnregistrements.elementAt(i);
			motsClesBDD = enregist.getKeywords();
			vecteurMotsClesBDD = convertStringToVector(motsClesBDD);
			for (int j = 0; (j < vecteurMotsCles.size()) && (test == false); j++) {
				for (int k = 0; (k < vecteurMotsClesBDD.size()) && (test == false); k++) {
					if (((String) vecteurMotsCles.elementAt(j))
							.equalsIgnoreCase((String) vecteurMotsClesBDD.elementAt(k))) {
						casSources.addElement(enregist);
						test = true;
					}
				}
			}
		}
		return casSources;
	}

	/**
	 * Recuperation des enregistrements parmi les cas sources trouves via la methode
	 * "simMotsCles" dont les url sont similaires a celle passee en parametre,
	 * c'est-a-dire l'une de celle renvoyee par le(s) moteur(s) de recherche.
	 * 
	 * @param urlMR
	 *            String : Une chaine de caractere representant une url (celle
	 *            retournee par le(s) moteurs de recherche).
	 * @paramCasSources Vector : Un vecteur de cas sources (enregistrements retenus
	 *                  par la methode "simMotsCles").
	 * @return Vector : Un vecteur de cas sources
	 */
	public static Vector simUrl(String urlMR, Vector casSources) {
		Vector resultat = new Vector();
		Vector liens;
		Lien lien;
		Enregistrement casSrc;
		boolean test;

		for (int i = 0; i < casSources.size(); i++) {
			test = false;
			casSrc = (Enregistrement) casSources.elementAt(i);
			liens = casSrc.getLiens();
			for (int j = 0; (j < liens.size()) && (test == false); j++) {
				lien = (Lien) liens.elementAt(j);
				if (urlMR.equals(lien.getUrl())) {
					resultat.addElement(casSrc);
					test = true;
				}
			}
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur contenant l'intersection des objets contenus dans les
	 * vecteurs passes en parametre.
	 * 
	 * @param v1
	 *            Vector : Un vecteur.
	 * @param v2
	 *            Vector : Un vecteur.
	 * @return Vector : Un vecteur contenant l'intersection des deux vecteurs passes
	 *         en parametre.
	 */
	public static Vector intersection(Vector v1, Vector v2) {
		Vector v = new Vector();
		Vector vReturn = new Vector();
		boolean test;
		int compteur;

		for (int i = 0; i < v1.size(); i++) {
			test = false;
			for (int j = 0; (j < v2.size()) && (test == false); j++) {
				if (((String) v1.elementAt(i)).equalsIgnoreCase((String) v2.elementAt(j))) {
					v.addElement(v1.elementAt(i));
					test = true;
				}
			}
		}
		/*
		 * Si certains objets ont ete ajoutes plusieurs fois dans le vecteur "v",
		 * suppression de ces objets.
		 */
		for (int i = 0; i < v.size(); i++) {
			compteur = 0;
			for (int j = i + 1; j < v.size(); j++) {
				if (!((String) v.elementAt(i)).equalsIgnoreCase((String) v.elementAt(j))) {
					compteur = compteur + 1;
				}
			}
			if (compteur == (v.size() - (i + 1))) {
				vReturn.addElement(v.elementAt(i));
			}
		}
		return vReturn;
	}

	/**
	 * Retourne un vecteur contenant l'union des objets contenus dans les vecteurs
	 * passes en parametre.
	 * 
	 * @param v1
	 *            Vector : Un vecteur.
	 * @param v2
	 *            Vector : Un vecteur.
	 * @return Vector : Un vecteur contenant l'union des deux vecteurs passes en
	 *         parametre.
	 */
	public static Vector union(Vector v1, Vector v2) {
		Vector v = new Vector();
		Vector vReturn = new Vector();
		int compteur;

		v = v1;
		for (int i = 0; i < v2.size(); i++) {
			v.addElement(v2.elementAt(i));
		}
		/*
		 * Si certains objets ont ete ajoutes plusieurs fois dans le vecteur "v",
		 * suppression de ces objets.
		 */
		for (int i = 0; i < v.size(); i++) {
			compteur = 0;
			for (int j = i + 1; j < v.size(); j++) {
				if (!((String) v.elementAt(i)).equalsIgnoreCase((String) v.elementAt(j))) {
					compteur = compteur + 1;
				}
			}
			if (compteur == (v.size() - (i + 1))) {
				vReturn.addElement(v.elementAt(i));
			}
		}
		return vReturn;
	}

	/**
	 * Retourne la valeur de la division entre le nombre d'elements contenus dans
	 * l'intersection et le nombre d'elements contenus dans l'union des deux
	 * vecteurs passes en parametre. Ce calcul fournit la similarite entre les deux
	 * vecteurs.
	 * 
	 * @param v1
	 *            Vector : Un vecteur de mots de type String.
	 * @param v2
	 *            Vector : Un vecteur de mots de type String.
	 * @return double : Un reel.
	 */
	public static double calculSimilarite(Vector v1, Vector v2) {
		Vector vUnion = union(v1, v2);
		Vector vIntersection = intersection(v1, v2);
		double similarite = 0.0;
		if (vUnion.size() != 0) {
			similarite = ((double) vIntersection.size()) / ((double) vUnion.size());
		}
		return similarite;
	}

	/**
	 * Recuperation des enregistrements parmi les cas sources trouves via la methode
	 * "simMotsCles" dont les descriptions sont similaires a celle passee en
	 * parametre, c'est-a-dire l'une de celle renvoyee par le(s) moteur(s) de
	 * recherche. La similarite s'evalue selon un seuil de similarite.
	 * 
	 * @param descMR
	 *            String : Une chaine de caractere representant une description
	 *            (celle retournee par le(s) moteurs de recherche).
	 * @paramCasSources Vector : Un vecteur de cas sources (enregistrements retenus
	 *                  par la methode "simMotsCles").
	 * @return Vector : Un vecteur de cas sources
	 */
	public static Vector simDescription(String descMR, Vector casSources) {
		Vector resultat = new Vector();
		Vector vecteurDescMR = convertStringToVector(descMR);
		Vector vecteurDescCasSrc;
		Vector liens;
		Lien lien;
		Enregistrement casSrc;
		double similarite;
		boolean test;

		for (int i = 0; i < casSources.size(); i++) {
			test = false;
			casSrc = (Enregistrement) casSources.elementAt(i);
			liens = casSrc.getLiens();
			for (int j = 0; (j < liens.size()) && (test == false); j++) {
				lien = (Lien) liens.elementAt(j);
				vecteurDescCasSrc = convertStringToVector(lien.getDesc());
				similarite = calculSimilarite(vecteurDescCasSrc, vecteurDescMR);

				/*
				 * Ajout dans la liste des Cas sources retenus a la seule condition que la
				 * variable "similarite" depasse ou soit egale au seuil de similarite.
				 */
				if (similarite >= SEUIL_SIMILARITE) {
					resultat.addElement(casSrc);
					test = true;
				}
			}
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur de vecteurs. Un sous vecteur, d'index "i", contient les
	 * cas sources de la base de donnees dont leurs informations est similaires aux
	 * informations de l'enregistrement du (des) moteur(s) de recherche donne en
	 * parametre. Au final, la taille du vecteur retourne devrait etre egale au
	 * nombre de liens contenu dans l'enregistrement retourne par le(s) moteur(s) de
	 * recherche (c'est-a-dire 20).
	 * 
	 * @param enrMR
	 *            Enregistrement : Un enregistrement representant les reponses
	 *            retournes par le(s) moteur(s) de recherche.
	 * @param casSrcBDD
	 *            Vector : Un vecteur de cas source retenu par la methode
	 *            "simMotsCles".
	 * @return Vector : Un vecteur de cas sources.
	 */
	public static Vector simCasSources(Enregistrement enrMR, Vector casSrcBDD) {
		Vector resultat = new Vector();
		Vector vecteurEnr = new Vector();
		Vector liensMR;
		Lien lienMR;
		String urlMR, descMR;

		liensMR = enrMR.getLiens();
		for (int i = 0; i < liensMR.size(); i++) {
			lienMR = (Lien) liensMR.elementAt(i);
			urlMR = lienMR.getUrl();
			descMR = lienMR.getDesc();
			vecteurEnr = new Vector();
			vecteurEnr = simUrl(urlMR, casSrcBDD);
			if (vecteurEnr.size() > 0) {
				resultat.addElement(vecteurEnr);
			} else {
				vecteurEnr = simDescription(descMR, casSrcBDD);
				if (vecteurEnr.size() > 0) {
					resultat.addElement(vecteurEnr);
				} else {
					resultat.addElement(vecteurEnr);
				}
			}
		}
		return resultat;
	}

	/**
	 * Retourne un reel representant la moyenne des rangs sommmes.
	 * 
	 * @param sommeRangs
	 *            int : Un entier representant les rangs sommes.
	 * @param nbRangs
	 *            int : Un entier representant le nombre d'entier (de rang) sommes.
	 * @return double : Un reel representant la moyenne des rangs sommes.
	 */
	public static double moyenneRang(int sommeRangs, int nbRangs) {
		if (nbRangs != 0) {
			double moyenne = (double) sommeRangs / (double) nbRangs;
			return moyenne;
		} else
			return 0.0;
	}

	/**
	 * Retourne un booleen: - Vrai si l'entier passe en parametre est present parmi
	 * les elements du vecteur passe en parametre, sauf le 0 qui peut etre plusieurs
	 * fois dans la mesure ou il represenre le fait qu'il n'y ait pas de documenr
	 * associe a un document du (des) moteur(s) de recherche. - Faux sinon.
	 * 
	 * @param entier
	 *            int : Un entier.
	 * @param v
	 *            Vector : Un vecteur contenant des entiers.
	 * @return boolean : Un booleen.
	 */
	public static boolean estPresent(int entier, Vector v) {
		boolean test = false;
		if (entier > 0) {
			for (int i = 0; i < v.size() && (test == false); i++) {
				if (v.elementAt(i).equals(new Integer(entier))) {
					test = true;
				}
			}
		}
		return test;
	}

	/**
	 * Retourne le premier entier disponible, c'est-à-dire le premier entier
	 * superieur a celui passe en parametre qui n'est pas present parmi les entiers
	 * qui se trouvent dans le vecteur d'entiers passe en parametre.
	 * 
	 * @param entier
	 *            int : Un entier.
	 * @param v
	 *            Vector : Un vecteur contenant des entiers.
	 * @return int : Un entier superieur a celui passe en parametre.
	 */
	public static int nbDisponibleSuperieur(int entier, Vector v) {
		int nbDisponible = -1;
		boolean present = true;
		if (entier < 20) {
			for (int i = 1; (i <= 20 - entier) && (present == true); i++) {
				present = estPresent(entier + i, v);
				if (present == false) {
					nbDisponible = entier + i;
				}
			}
		}
		return nbDisponible;
	}

	/**
	 * Retourne le premier entier disponible, c'est-à-dire le premier entier
	 * inferieur a celui passe en parametre qui n'est pas present parmi les entiers
	 * qui se trouvent dans le vecteur d'entiers passe en parametre.
	 * 
	 * @param entier
	 *            int : Un entier.
	 * @param v
	 *            Vector : Un vecteur contenant des entiers.
	 * @return int : Un entier inferieur a celui passe en parametre.
	 */
	public static int nbDisponibleInferieur(int entier, Vector v) {
		int nbDisponible = -1;
		boolean present = true;
		if (entier > 1) {
			for (int i = 1; (i <= entier - 1) && (present == true); i++) {
				present = estPresent(entier - i, v);
				if (present == false) {
					nbDisponible = entier - i;
				}
			}
		}
		return nbDisponible;
	}

	/**
	 * Retourne un vecteur d'entiers dont les entiers compris entre les variables
	 * "entier" et "nbDisponible", passes en parametre sont incremente d'une unite.
	 *
	 * @param rangPresent
	 *            int : Un entier representant le rang immediatement inferieur a
	 *            celui a inserer.
	 * @param nbDisponible
	 *            int : Un entier qui n'est pas present dans le vecteur d'entiers
	 *            passe en parametre.
	 * @param v
	 *            Vector : Un vecteur d'entiers.
	 * @return Vector : Un vecteur d'entiers.
	 */
	public static Vector incrementationRang(int rangPresent, int nbDisponible, Vector v) {
		Vector resultat = new Vector();

		for (int i = 0; i < v.size(); i++) {
			int nb = ((Integer) v.elementAt(i)).intValue();
			if ((nb > rangPresent) && (nb < nbDisponible)) {
				int addition = nb + 1;
				resultat.addElement(new Integer(addition));
			} else {
				resultat.addElement(v.elementAt(i));
			}
		}
		int rangAInserer = rangPresent + 1;
		resultat.addElement(new Integer(rangAInserer));
		return resultat;
	}

	/**
	 * Retourne un vecteur d'entiers dont les entiers compris entre les variables
	 * "entier" et "nbDisponible", passes en parametre sont decremente d'une unite.
	 * 
	 * @param rangPresent
	 *            int : Un entier representant le rang immediatement superieur a
	 *            celui a inserer.
	 * @param nbDisponible
	 *            int : Un entier qui n'est pas present dans le vecteur d'entiers
	 *            passe en parametre.
	 * @param v
	 *            Vector : Un vecteur d'entiers.
	 * @return Vector : Un vecteur d'entiers.
	 */
	public static Vector decrementationRang(int rangPresent, int nbDisponible, Vector v) {
		Vector resultat = new Vector();

		for (int i = 0; i < v.size(); i++) {
			int nb = ((Integer) v.elementAt(i)).intValue();
			if ((nb < rangPresent) && (nb > nbDisponible)) {
				int soustraction = nb - 1;
				resultat.addElement(new Integer(soustraction));
			} else {
				resultat.addElement(v.elementAt(i));
			}
		}
		int rangAInserer = rangPresent - 1;
		resultat.addElement(new Integer(rangAInserer));
		return resultat;
	}

	/**
	 * Retourne un vecteur d'entiers. Ajout du rang (en sachant qu'une moyenne a ete
	 * realisee pour la trouver) correspondant a un lien retournee par le(s)
	 * moteur(s) de recherche, dans le vecteur d'entiers (dans lequel chaque element
	 * a l'indice i correspond au rang de la reponse du (des) moteur(s) de recherche
	 * selon les enregistrements contenus dans la base de donnees de l'agent local).
	 * 
	 * @param sommeRangs
	 *            int : Un entier representant la somme de certains rangs.
	 * @param nbRangs
	 *            int : Un entier representant le nombre d'entiers, correspondant au
	 *            rang, qui ont ete sommes.
	 * @param v
	 *            Vector : Un vecteur d'entiers.
	 * @return Vector : Un vecteur d'entiers.
	 */
	public static Vector ajoutRangMoyen(int sommeRangs, int nbRangs, Vector v) {
		Vector resultat = new Vector();
		double rang = moyenneRang(sommeRangs, nbRangs);
		boolean present = estPresent((int) rang, v);
		if (present == true) {
			double chiffreApresVirgule = rang - (double) ((int) rang);
			if (chiffreApresVirgule > 0.5) {
				int nbDisponible = nbDisponibleSuperieur((int) rang, v);
				if (nbDisponible != -1) {
					resultat = incrementationRang((int) rang, nbDisponible, v);
				} else {
					resultat = decrementationRang((int) rang, nbDisponible, v);
				}
			} else {
				int nbDisponible = nbDisponibleInferieur((int) rang, v);
				if (nbDisponible != -1) {
					resultat = decrementationRang((int) rang, nbDisponible, v);
				} else {
					resultat = incrementationRang((int) rang, nbDisponible, v);
				}
			}
		} else {
			resultat = v;
			resultat.addElement(new Integer((int) rang));
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur d'entiers. Ajout du rang (en sachant que le minimum a ete
	 * choisi parmi tous les rangs des documents retenus pour "LE" document renvoye
	 * par le(s) moteur(s) d recherche). Dans le vecteur d'entiers, chaque element a
	 * l'indice i correspond au rang de la reponse du (des) moteur(s) de recherche
	 * selon les enregistrements contenus dans la base de donnees de l'agent local).
	 * 
	 * @param rangMin
	 *            int : Un entier representant le rang minimum trouve parmi les
	 *            documents retenus.
	 * @param v
	 *            Vector : Un vecteurs d'entiers.
	 * @return Vector : Un vecteur d'entiers.
	 */
	public static Vector ajoutRangMinimum(int rangMin, Vector v) {
		Vector resultat = new Vector();
		boolean present = estPresent(rangMin, v);
		if (present == true) {
			int nbDisponible = nbDisponibleSuperieur(rangMin, v);
			if (nbDisponible != -1) {
				resultat = incrementationRang(rangMin, nbDisponible, v);
			} else {
				nbDisponible = nbDisponibleInferieur(rangMin, v);
				if (nbDisponible != -1) {
					resultat = decrementationRang(rangMin, nbDisponible, v);
				}
			}
		} else {
			resultat = v;
			resultat.addElement(new Integer(rangMin));
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur d'entiers dans lequel chaque element a l'indice i
	 * correspond au rang de la reponse renvoyee par le(s) moteur(s) de recherche
	 * selon les enregistrements contenus dans la base de donnees de l'agent local.
	 * En fait, il y a une association du rang entre la reponse du (des) moteur(s)
	 * de recherche et les cas sources retenus par la methode "simCasSources".
	 * Plusieurs liens, contenus dans la base de donnees, peuvent correspondre a un
	 * seul lien (une seule reponse) du (des) moteur(s) de recherche. Par
	 * consequent, plusieurs rangs seront a notre disposition. Dans ce cas, nous
	 * effectuons une moyenne de ces rangs. Si deux rangs sont identiques dans le
	 * vecteurs a retourner, alors nous incrementons ou decrementons d'une unité la
	 * valeur des rangs compris entre ce rang et le premier entier inferieur ou
	 * superieur qui n'est pas present dans le vecteur a retourner.
	 * 
	 * Nous avons aussi realiser une methode qui prend la valeur minimale du rang si
	 * plusieurs liens de la base de donnees sont a notre disposition.
	 * 
	 * Cependant, pour une question de pertinence, nous avons opte pour la moyenne
	 * des rangs. Neanmoins, la methode permettant d'inserer le rang minimal a ete
	 * implemente et l'appel a cette derniere a ete, ici, mis en commentaire.
	 * 
	 * @param enrMR
	 *            Enregistrement L'enregistrement retourne par le(s) moteur(s) de
	 *            recherche.
	 * @param casSrcRetenus
	 *            Vector : Le vecteur de Cas Sources (enregistrements retenus par la
	 *            methode "SimCasSources").
	 * @return Vector : Un vecteur d'entiers dont la valeur comprise dans chaque
	 *         case "i" correspond au rang de la reponse "i" renvoyee par le(s)
	 *         moteur(s) de recherche.
	 */
	public static Vector associationRangCasSrc_MR(Enregistrement enrMR, Vector casSrcRetenus) {
		Vector resultat = new Vector();
		Vector sousResultatCasSrcRetenu;
		Vector vecteurSousDesc, vecteurDescMR;
		Vector liensMR, sousLiens;
		Lien lienMR, sousLien;
		String urlMR, descMR, sousUrl, sousDesc;
		Enregistrement sousEnr;
		int sommeRangs, compteur, sousRang;
		double similarite, rangMin, temp;
		boolean test;

		liensMR = enrMR.getLiens();
		for (int i = 0; i < liensMR.size(); i++) {
			lienMR = (Lien) liensMR.elementAt(i);
			urlMR = lienMR.getUrl();
			descMR = lienMR.getDesc();
			sommeRangs = 0;
			compteur = 0;
			rangMin = 0;
			temp = 0;

			/* Recuperation du sous vecteur contenu dans casSrcRetenus */
			sousResultatCasSrcRetenu = (Vector) casSrcRetenus.elementAt(i);
			for (int j = 0; j < sousResultatCasSrcRetenu.size(); j++) {
				test = false;
				sousEnr = (Enregistrement) sousResultatCasSrcRetenu.elementAt(j);
				sousLiens = sousEnr.getLiens();
				for (int k = 0; (k < sousLiens.size()) && (test == false); k++) {
					sousLien = (Lien) sousLiens.elementAt(k);
					sousUrl = sousLien.getUrl();
					sousRang = sousLien.getRang();
					if (urlMR.equals(sousUrl) == true) {
						sommeRangs = sommeRangs + sousRang;
						compteur = compteur + 1;

						/*
						 * Recherche du rang minimal parmi tous les documents des cas sources retenus
						 */
						temp = sousRang;
						if ((temp != 0) && (temp < rangMin)) {
							rangMin = temp;
						}
						test = true;
					}
				}
				if (test == false) {
					for (int k = 0; (k < sousLiens.size()) && (test == false); k++) {
						sousLien = (Lien) sousLiens.elementAt(k);
						sousDesc = sousLien.getDesc();
						sousRang = sousLien.getRang();
						vecteurDescMR = convertStringToVector(descMR);
						vecteurSousDesc = convertStringToVector(sousDesc);
						similarite = calculSimilarite(vecteurSousDesc, vecteurDescMR);
						if (similarite >= SEUIL_SIMILARITE) {
							sommeRangs = sommeRangs + sousRang;
							compteur = compteur + 1;

							/*
							 * Recherche du rang minimal parmi tous les documents des cas sources retenus
							 */
							temp = sousRang;
							if ((temp != 0) && (temp < rangMin)) {
								rangMin = temp;
							}
							test = true;
						}
					}
				}
			}
			/*
			 * - Enlever les commentaires devant l'appel a la methode "ajoutRangMinimum"
			 * pour l'utilisation de cette methode. Cette derniere permet de selectionner le
			 * rang minimum parmi les rangs de tous les documents homologues a celui
			 * retourne par le(s) moteur(s) de recherche.
			 * 
			 * - Dans ce cas, rajouter les commentaires devant la methode "ajoutRangMoyen",
			 * qui elle, permet de faire une moyenne de tous les rangs de chaque document
			 * homologue. Il nous parait plus pertinent d'exploiter tous les tous les
			 * resultats enregistres dans la base de donnees.
			 * 
			 * De toutes maniere, que ce soit le rang minimum ou moyen, chacun aura ses
			 * avantages et ses inconvenients.
			 */
			/*
			 * resultat = ajoutRangMinimum(rangMin, resultat);
			 */
			resultat = ajoutRangMoyen(sommeRangs, compteur, resultat);
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur de Documents a partir de l'enregistrement retourne par
	 * le(s) moteur(s) de recherche. Chaque document compris dans cet enregistrement
	 * est mis dans un type Document. Cette methode sera appelee lors du vote de la
	 * place de chaque document.
	 * 
	 * @param enrMR
	 *            Enregistrement : Un enregistrement retourne par le(s) moteur(s) de
	 *            recherche.
	 * @return Vector : Un vecteur de Documents.
	 */
	public static Vector initVectorDocuments(Enregistrement enrMR) {
		Vector documents = new Vector();
		Vector liens;
		Lien lien;
		Document doc;

		liens = enrMR.getLiens();
		for (int i = 0; i < liens.size(); i++) {
			lien = (Lien) liens.elementAt(i);
			doc = new Document(i, lien);
			documents.addElement(doc);
		}
		return documents;
	}

	/**
	 * Retourne le Document d'identifiant "id", passe en parametre, contenu dans le
	 * vecteur de Documents passe en parametre.
	 * 
	 * @param id
	 *            int : Un entier representant l'identifiant d'un document.
	 * @param vecteurDocs
	 *            Vector : Un vecteur de Documents.
	 * @return Document : Un Document d'identifiant "id".
	 */
	public static Document getDoc(int id, Vector vecteurDocs) {
		boolean test = false;
		Document doc = new Document();
		Document resultat = doc;
		for (int i = 0; (i < vecteurDocs.size()) && (test == false); i++) {
			doc = (Document) vecteurDocs.get(i);
			if (doc.getIdDoc() == id) {
				resultat = doc;
				test = true;
			}
		}
		return resultat;
	}

	/**
	 * Retourne la valeur du calcul du vote de la place d'un document (1) par
	 * rapport a un autre (2).
	 * 
	 * @param rang1_MR
	 *            int : Un entier representant le rang du document 1 dans les
	 *            reponses envoyees par le(s) moteur(s) de recherche.
	 * @param rang2_MR
	 *            int : Un entier representant le rang du document 2 dans les
	 *            reponses envoyees par le(s) moteur(s) de recherche.
	 * @param rang1_Associe
	 *            int : Un entier representant le rang du document 1 associe via les
	 *            documents de la base de donnees.
	 * @param rang2_Associe
	 *            int : Un entier representant le rang du document 2 associe via les
	 *            documents de la base de donnees.
	 * @return Un reel representant la valeur du calcul.
	 */
	public static double vote(int rang1_MR, int rang2_MR, int rang1_Associe, int rang2_Associe) {
		double soustraction_MR = (double) (rang1_MR - rang2_MR);
		double soustraction_Associe = (double) (rang1_Associe - rang2_Associe);
		double addition = soustraction_MR + soustraction_Associe;
		double resultat = addition / 2;
		return resultat;
	}

	/**
	 * Retourne un entier representant le type de la valeur du reel retourne par la
	 * methode "vote". Indique si cette valeur est: - negative, - nulle ou -
	 * positive.
	 * 
	 * @param vote
	 *            double : Un reel representant la valeur du vote retournee par la
	 *            methode "vote".
	 * @return int : Un entier significatif.
	 */
	public static int valeurVote(double vote) {
		int valeur;
		if (vote < 0.0)
			valeur = NEGATIF;
		else if (vote == 0.0)
			valeur = NUL;
		else
			valeur = POSITIF;
		return valeur;
	}

	/**
	 * Retourne l'indice d'un Document (passe en parametre) dans le vecteur de
	 * documents passe en parametre aussi.
	 * 
	 * @param doc
	 *            Document : Un Document.
	 * @param vecteurDocs
	 *            Vector : Un vecteur de Documents
	 * @return int : Un entier representant l'indice ou se trouve se document dans
	 *         le vecteur de documents passe en parametre.
	 */
	public static int indexDe(Document doc, Vector vecteurDocs) {
		int index = -1;
		boolean test = false;
		Document document;
		for (int i = 0; (i < vecteurDocs.size()) && (test == false); i++) {
			document = (Document) vecteurDocs.elementAt(i);
			if (document.equals(doc) == true) {
				index = i;
				test = true;
			}
		}
		return index;
	}

	/**
	 * Retourne un vecteur de Documents qui contient les memes elements que le
	 * vecteur v passe en parametre mais avec le document d1 inserer avant le
	 * document d2. Si le document d1 est deja avant le document d2 (meme s'il n'est
	 * pas immediatement au dessus de d2) alors, nous retournons le vecteur v passe
	 * en parametre.
	 * 
	 * @param d1
	 *            Document : Un document a inserer avant d2.
	 * @param d2
	 *            Document : Un document.
	 * @param vecteurDocs
	 *            Vector : Un vecteur de documents.
	 * @return Vector : Un vecteur de documents dans lequel d1 est insere avant d2.
	 */
	public static Vector insertionAvant(Document d1, Document d2, Vector vecteurDocs) {
		Vector resultat = new Vector();
		Document doc;
		int indexD1 = indexDe(d1, vecteurDocs);
		int indexD2 = indexDe(d2, vecteurDocs);

		if ((indexD1 != -1) && (indexD2 != -1)) {
			if (indexD1 < indexD2) {
				resultat = vecteurDocs;
			} else {
				for (int i = 0; i < indexD2; i++) {
					resultat.addElement(vecteurDocs.elementAt(i));
				}
				resultat.addElement(d1);
				for (int i = indexD2; i < vecteurDocs.size(); i++) {
					doc = (Document) vecteurDocs.elementAt(i);
					if (doc.equals(d1) == false) {
						resultat.addElement(doc);
					}
				}
			}
		} else {
			resultat = vecteurDocs;
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur de Documents qui contient les memes elements que le
	 * vecteur v passe en parametre mais avec le document d1 inserer immediatement
	 * apres le document d2.
	 * 
	 * @param d1
	 *            Document : Un document a inserer immediatement apres d2.
	 * @param d2
	 *            Document : Un document.
	 * @param vecteurDocs
	 *            Vector : Un vecteur de documents.
	 * @return Vector : Un vecteur de documents dans lequel d1 est insere
	 *         immediatement apres d2.
	 */
	public static Vector insertionApres(Document d1, Document d2, Vector vecteurDocs) {
		Vector resultat = new Vector();
		int indexD1 = indexDe(d1, vecteurDocs);
		int indexD2 = indexDe(d2, vecteurDocs);

		if (indexD1 != indexD2 - 1) {
			for (int i = 0; i < indexD1; i++) {
				resultat.addElement(vecteurDocs.elementAt(i));
			}
			for (int i = indexD1 + 1; i <= indexD2; i++) {
				resultat.addElement(vecteurDocs.elementAt(i));
			}
			resultat.addElement(vecteurDocs.elementAt(indexD1));
			for (int i = indexD2 + 1; i < vecteurDocs.size(); i++) {
				resultat.addElement(vecteurDocs.elementAt(i));
			}
		} else {
			resultat = vecteurDocs;
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur de documents ou la place du document d1 a ete instauree
	 * selon le document d2.
	 * 
	 * @param typeValeur
	 *            int : Un entier representant le type de valeur retournee par la
	 *            methode "vote".
	 * @param d1
	 *            Document : Un document a permute avec un autre.
	 * @param d2
	 *            Un document.
	 * @param rangsAssocies
	 *            Vecror : Un vecteur d'entiers representant, pour chaque indice i,
	 *            le rang qui est associe, par le biais de la base de donnees, a un
	 *            lien du (des) moteur(s) de recherche.
	 * @param vecteurDocs
	 *            Vector : Un vecteur de Documents.
	 * @return Vector : Un vecteur de Documents.
	 */
	public static Vector instaurationPlaceDocument(int typeValeur, Document d1, Document d2, Vector rangsAssocies,
			Vector vecteurDocs) {
		Vector resultat;
		if (typeValeur == NEGATIF) {
			resultat = insertionAvant(d1, d2, vecteurDocs);
		} else {
			if (typeValeur == NUL) {
				resultat = insertionApres(d1, d2, vecteurDocs);
			} else {
				resultat = vecteurDocs;
			}
		}
		return resultat;
	}

	/**
	 * Retourne un vecteur de Documents ou l'on a permuter le document d_i avec un
	 * autre.
	 * 
	 * @param d_i
	 *            Document : Un document parmi ceux retourne par le(s) moteur(s) de
	 *            recherche.
	 * @param d_k
	 *            Document : Un autre Document.
	 * @param rangsAssocies
	 *            Un vecteur d'entiers representant les rangs associes a chaque lien
	 *            retourne par le(s) moteur(s) de recherche.
	 * @param vecteurDocs
	 *            Vector : Un vecteur de Documents (contenant tous les liens
	 *            retournes par le(s) moteur(s) de recherche).
	 * @return Vector : Un vecteur de Document.
	 */
	public static Vector votePlaceDocument(Document d_i, Document d_k, Vector rangsAssocies, Vector vecteurDocs) {
		int id_i = d_i.getIdDoc();
		int id_k = d_k.getIdDoc();
		int rang_i_Associe = ((Integer) rangsAssocies.elementAt(id_i)).intValue();
		int rang_k_Associe = ((Integer) rangsAssocies.elementAt(id_k)).intValue();
		double valVote = vote(d_i.getRang(), d_k.getRang(), rang_i_Associe, rang_k_Associe);
		int typeValVote = valeurVote(valVote);
		Vector resultat = instaurationPlaceDocument(typeValVote, d_i, d_k, rangsAssocies, vecteurDocs);
		return resultat;
	}

	/**
	 * Retourne un vecteur de Documents representant la version finale de la
	 * permutation de tous les documents renvoyes par le(s) moteur(s) de recherche.
	 * 
	 * @param enrMR
	 *            Enregistrement : Un Enregistrement representant celui retourne par
	 *            le(s) moteur(s) de recherche.
	 * @param rangsAssocies
	 *            Vector : Un vecteur d'entiers representant les rangs associes a
	 *            chaque lien retourne par le(s) moteur(s) de recherche.
	 * @return Vector : Un vecteur de Documents permutes.
	 */
	public static Vector permutationDocuments(Enregistrement enrMR, Vector rangsAssocies) {
		Vector vecteurDocs = initVectorDocuments(enrMR);
		for (int i = 0; i < vecteurDocs.size() - 1; i++) {
			for (int k = i + 1; k < vecteurDocs.size(); k++) {
				vecteurDocs = votePlaceDocument(getDoc(i, vecteurDocs), getDoc(k, vecteurDocs), rangsAssocies,
						vecteurDocs);
			}
		}
		return vecteurDocs;
	}

	/**
	 * Retourne un Enregistrement representant l'enregistrement retourne par le(s)
	 * moteur(s) de recherche avec les liens permutes.
	 * 
	 * @param enrMR
	 *            Enregistrement : Un Enregistrement representant celui retourne par
	 *            le(s) moteur(s) de recherche.
	 * @param vecteurDocs
	 *            Vector : Un vecteur de Documents (contenant les liens retournes
	 *            par le(s) moteur(s) de recherche.
	 * @return Enregistrement : Un Enregistrement dont les liens ont ete permutes.
	 */
	public static Enregistrement permutations(Enregistrement enrMR, Vector vecteurDocs) {
		Document doc;
		Lien lien;
		Vector liens = new Vector();
		int id = enrMR.getId();
		String motsCles = enrMR.getKeywords();
		for (int i = 0; i < vecteurDocs.size(); i++) {
			doc = (Document) vecteurDocs.elementAt(i);
			lien = (Lien) doc.getLienDoc();
			liens.addElement(lien);
		}
		Enregistrement enrMR_liensPermutes = new Enregistrement(id, motsCles, liens);
		return enrMR_liensPermutes;
	}

	/**
	 * Methode permettant de lancer la permutation des liens de l'enregistrement
	 * retourne par le(s) moteur(s) de recherche.
	 * 
	 * @param enrMR
	 *            Enregistrement : Un enregistrement retourne par le(s) moteur(s) de
	 *            recherche.
	 */
	public static void lancementPermutations(Enregistrement enrMR) {
		GestionMessage.message(0, "Permutations", "Lancement des permutations des liens par l'agent local.");
		Vector motsClesSimilaires = simMotsCles(enrMR.getKeywords());
		Vector casSrcRetenus = simCasSources(enrMR, motsClesSimilaires);
		Vector rangsAssocies = associationRangCasSrc_MR(enrMR, casSrcRetenus);
		Vector docsPermutes = permutationDocuments(enrMR, rangsAssocies);
		enrMR_liensPermutes = permutations(enrMR, docsPermutes);
	}

}
