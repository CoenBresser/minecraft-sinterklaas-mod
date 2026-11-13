package nl.sinterklaas.core;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 * Het Sinterklaasseizoen voor Sint en de Pieten.
 * - Vanzelf verschijnen ze alleen van 15 november t/m 5 december, en steeds vaker naarmate
 *   5 december dichterbij komt.
 * - Op 6 december vertrekt iedereen (ook wie uit een spawn-ei kwam).
 * - Spawn-eieren en /summon werken altijd. Wie na 5 december verschijnt, hoort bij het
 *   seizoen van het jaar erna en blijft dus tot de volgende 6 december.
 */
public final class SeizoenRegels {
	public static final int START_MAAND = 11;
	public static final int START_DAG = 15;
	/** Kans op de eerste dag van het seizoen; op 5 december is de kans 1 (de piek). */
	public static final double KANS_BIJ_START = 0.2;

	public static LocalDate start(int jaar) {
		return LocalDate.of(jaar, START_MAAND, START_DAG);
	}

	public static LocalDate pakjesavond(int jaar) {
		return LocalDate.of(jaar, Month.DECEMBER, PakjesRegels.EERSTE_DAG);
	}

	/**
	 * Hoe groot is de kans dat een natuurlijke spawn doorgaat? 0 = nooit, 1 = de piek.
	 * Buiten het seizoen 0; in het seizoen loopt de kans gelijkmatig op tot 1 op 5 december.
	 */
	public static double spawnKans(LocalDate vandaag) {
		LocalDate begin = start(vandaag.getYear());
		LocalDate eind = pakjesavond(vandaag.getYear());
		if (vandaag.isBefore(begin) || vandaag.isAfter(eind)) return 0.0;
		double voortgang = (double) ChronoUnit.DAYS.between(begin, vandaag) / ChronoUnit.DAYS.between(begin, eind);
		return KANS_BIJ_START + (1.0 - KANS_BIJ_START) * voortgang;
	}

	/** Bij welk seizoen hoort iemand die vandaag verschijnt? Het jaar van de eerstvolgende 5 december. */
	public static int seizoensJaar(LocalDate vandaag) {
		return vandaag.isAfter(pakjesavond(vandaag.getYear())) ? vandaag.getYear() + 1 : vandaag.getYear();
	}

	/** Moet iemand van dit seizoen vandaag vertrekken? Ja, zodra 5 december van zijn seizoen voorbij is. */
	public static boolean moetVertrekken(int seizoensJaar, LocalDate vandaag) {
		return vandaag.isAfter(pakjesavond(seizoensJaar));
	}

	private SeizoenRegels() {}
}
