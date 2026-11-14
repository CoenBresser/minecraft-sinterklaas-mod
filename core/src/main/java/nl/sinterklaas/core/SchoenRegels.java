package nl.sinterklaas.core;

/**
 * Schoen zetten in Minecraft-tijd.
 * - Zet je een schoen met een wortel voor het paard, dan ligt er de volgende ochtend een pakje in.
 * - Zet je een schoen zomaar, zonder wortel, dan ligt er de volgende ochtend een briefje van Piet in.
 * - De wortel is daarna op: voor een nieuw pakje moet er een nieuwe wortel in.
 * Een Minecraft-dag duurt 24000 tikken en begint 's ochtends (tik 0).
 */
public final class SchoenRegels {
	public static final long TIKKEN_PER_DAG = 24000L;

	/** Het dagnummer dat bij een kloktijd hoort (dag 0 is de eerste dag van de wereld). */
	public static long dagNummer(long klokTikken) {
		return Math.floorDiv(klokTikken, TIKKEN_PER_DAG);
	}

	/** Wacht de schoen nog op de nacht? Dat is zo zolang er nog geen briefje of pakje in ligt. */
	public static boolean wachtOpDeNacht(SchoenInhoud inhoud) {
		return inhoud == SchoenInhoud.LEEG || inhoud == SchoenInhoud.WORTEL;
	}

	/**
	 * @param gezetOpDag dag waarop de schoen is gezet (of waarop hij voor het laatst is leeggehaald)
	 * @param dagNu      de dag van nu
	 * @return true als Piet vannacht langs is geweest: het is een latere dag en er ligt nog niets
	 *         van Piet in. Daardoor komt er hooguit één briefje of pakje per nacht.
	 */
	public static boolean pietIsLangsGeweest(long gezetOpDag, long dagNu, SchoenInhoud inhoud) {
		return wachtOpDeNacht(inhoud) && dagNu > gezetOpDag;
	}

	/** Wat Piet 's nachts in de schoen achterlaat: een pakje bij een wortel, anders een briefje. */
	public static SchoenInhoud naDeNacht(SchoenInhoud inhoud) {
		switch (inhoud) {
			case WORTEL: return SchoenInhoud.PAKJE;
			case LEEG: return SchoenInhoud.BRIEFJE;
			default: return inhoud;
		}
	}

	/** Mag er nu een wortel in? Alleen in een lege schoen. */
	public static boolean magWortelErin(SchoenInhoud inhoud) {
		return inhoud == SchoenInhoud.LEEG;
	}

	private SchoenRegels() {}
}
