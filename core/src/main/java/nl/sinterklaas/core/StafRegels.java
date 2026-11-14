package nl.sinterklaas.core;

/**
 * De staf van Sinterklaas: met een rechtsklik roep je de Pieten in de buurt naar je toe.
 * Afstanden in blokken, tijden in tikken (20 tikken = 1 seconde).
 */
public final class StafRegels {
	/** Pieten binnen deze afstand horen de staf. */
	public static final double BEREIK = 24.0;
	/** Zo lang blijft een Piet naar je toe komen: 15 seconden. */
	public static final int ROEP_TIKKEN = 300;
	/** Zo lang moet je wachten voor je opnieuw kunt roepen: 5 seconden. */
	public static final int PAUZE = 100;
	/** Op deze afstand is de Piet er en blijft hij staan. */
	public static final double DICHTBIJ = 3.0;

	/** Hoort een Piet op deze afstand de staf? */
	public static boolean hoortStaf(double afstand) {
		return afstand <= BEREIK;
	}

	/** Moet een geroepen Piet nog verder lopen? Ja, zolang hij tijd over heeft en nog niet dichtbij is. */
	public static boolean moetNogKomen(double afstand, int tikkenOver) {
		return tikkenOver > 0 && afstand > DICHTBIJ;
	}

	private StafRegels() {}
}
