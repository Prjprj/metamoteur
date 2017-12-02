/**
 * Chrono.java
 *
 * Classe permettant de calculer et d'afficher 
 * le temps d'ex�cution d'une commande 
 * 
 * @author J�r�my Fr�chard et Mehdi Ben Moussa
 * @version 1.0 31/05/2006
 * 
 */

public class Chrono {
	
	static private long start ;	// Temps syst�me de d�part
	static private long stop ;	// Temps syst�me d'arr�t
	
	/**
	 * Commande de d�marrage du chronom�tre
	 */
	public static void start () {
		start = System.currentTimeMillis() ;
	}
	
	/**
	 * Commande d'arr�t du chronom�tre
	 */
	public static void stop () {
		stop = System.currentTimeMillis() ;
	}
	
	/**
	 * Affichage du Chronom�tre
	 */
	public static void AffichageChrono () {
		System.out.println("\ttemps de calcul : " + (time()) + " ms") ;
	}
	
	/**
	 * Donne le temps �coul� entre le d�part et l'arr�t de chronom�tre
	 *
	 * @return int le temps �coul� entre start() et stop()
	 */
	private static int time () {
		return (int) (stop - start) ;
	}
} // Fin Chrono.java