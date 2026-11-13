package nl.sinterklaas.core;

import java.time.LocalDate;
import java.time.Month;

/**
 * Wanneer mag een pakje open?
 * - Een pakje uit een schoen mag altijd open.
 * - Een gewoon pakje mag open van 5 december t/m 23 december ("dicht vlak voor kerst").
 *   Van 24 december t/m 4 december het jaar erna blijft het dicht.
 */
public final class PakjesRegels {
	/** Pakjesavond: vanaf deze dag in december mag het. */
	public static final int EERSTE_DAG = 5;
	/** Laatste dag in december waarop het nog mag. Wil je langer? Verhoog dit getal (maximaal 31). */
	public static final int LAATSTE_DAG = 23;

	public static boolean magOpenen(LocalDate vandaag, boolean uitSchoen) {
		if (uitSchoen) return true;
		return vandaag.getMonth() == Month.DECEMBER
			&& vandaag.getDayOfMonth() >= EERSTE_DAG
			&& vandaag.getDayOfMonth() <= LAATSTE_DAG;
	}

	private PakjesRegels() {}
}
