package nl.sinterklaas.core;

import java.util.List;

/** Wanneer en wat Sint en de Pieten zeggen. Tijden in tikken (20 tikken = 1 seconde). */
public final class PraatRegels {
	/** Minimale en maximale pauze tussen twee spontane zinnen: 1 tot 3 minuten. */
	public static final int SPONTAAN_MIN = 1200;
	public static final int SPONTAAN_MAX = 3600;
	/** Na een rechtsklik even wachten voor de volgende zin: 2 seconden. */
	public static final int KLIK_PAUZE = 40;
	/** Alleen spontaan praten als er een speler binnen dit aantal blokken is. */
	public static final double GEHOORSAFSTAND = 12.0;

	/** Pauze tot de volgende spontane zin, bij een willekeurig getal. */
	public static int spontanePauze(int willekeurigGetal) {
		return SPONTAAN_MIN + Math.floorMod(willekeurigGetal, SPONTAAN_MAX - SPONTAAN_MIN + 1);
	}

	/** Kiest een zin uit de lijst. Geeft null bij een lege lijst. */
	public static String kies(List<String> zinnen, int willekeurigGetal) {
		if (zinnen == null || zinnen.isEmpty()) return null;
		return zinnen.get(Math.floorMod(willekeurigGetal, zinnen.size()));
	}

	private PraatRegels() {}
}
