/**
 * Chrono.java
 *
 * Classe permettant de calculer et d'afficher 
 * le temps d'exécution d'une commande 
 * 
 * @author Jérémy Fréchard et Mehdi Ben Moussa
 * @version 1.0 31/05/2006
 * 
 */

public class Chrono {
	
	static private long start ;	// Temps système de départ
	static private long stop ;	// Temps système d'arrêt
	
	/**
	 * Commande de démarrage du chronomètre
	 */
	public static void start () {
		start = System.currentTimeMillis() ;
	}
	
	/**
	 * Commande d'arrêt du chronomètre
	 */
	public static void stop () {
		stop = System.currentTimeMillis() ;
	}
	
	/**
	 * Affichage du Chronomètre
	 */
	public static void AffichageChrono () {
		System.out.println("\ttemps de calcul : " + (time()) + " ms") ;
	}
	
	/**
	 * Donne le temps écoulé entre le départ et l'arrêt de chronomètre
	 *
	 * @return int le temps écoulé entre start() et stop()
	 */
	private static int time () {
		return (int) (stop - start) ;
	}
} // Fin Chrono.java