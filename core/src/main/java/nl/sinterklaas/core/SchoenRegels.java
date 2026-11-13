package nl.sinterklaas.core;

/**
 * Schoen zetten in Minecraft-tijd: zet je een schoen, dan ligt er de volgende ochtend een pakje.
 * Een Minecraft-dag duurt 24000 tikken en begint 's ochtends (tik 0).
 */
public final class SchoenRegels {
	public static final long TIKKEN_PER_DAG = 24000L;

	/** Het dagnummer dat bij een kloktijd hoort (dag 0 is de eerste dag van de wereld). */
	public static long dagNummer(long klokTikken) {
		return Math.floorDiv(klokTikken, TIKKEN_PER_DAG);
	}

	/**
	 * @param gezetOpDag dag waarop de schoen is gezet (of waarop het vorige pakje eruit is gehaald)
	 * @param dagNu      de dag van nu
	 * @param alGevuld   ligt er al een pakje in?
	 * @return true als er nu een pakje in moet: het is een latere dag en de schoen is nog leeg.
	 *         Daardoor komt er hooguit één pakje per nacht.
	 */
	public static boolean moetVullen(long gezetOpDag, long dagNu, boolean alGevuld) {
		return !alGevuld && dagNu > gezetOpDag;
	}

	private SchoenRegels() {}
}
