package nl.sinterklaas.core;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Zegt welke datum het "vandaag" is voor de Sinterklaas-regels.
 * Altijd de datum in Nederland (Europe/Amsterdam), ongeacht de tijdzone van de server.
 * Voor tests kan een op (beheerder) de datum overschrijven met een testdatum (commando /sinterklaas datum).
 */
public final class SinterklaasKlok {
	public static final ZoneId NEDERLAND = ZoneId.of("Europe/Amsterdam");

	private final Clock klok;
	private volatile LocalDate testDatum;

	/** Gebruikt de echte klok van de computer waar de server op draait. */
	public SinterklaasKlok() {
		this(Clock.systemUTC());
	}

	/** Voor tests: een eigen klok. De tijdzone van die klok doet er niet toe. */
	public SinterklaasKlok(Clock klok) {
		this.klok = klok;
	}

	public LocalDate vandaag() {
		LocalDate test = testDatum;
		return test != null ? test : LocalDate.now(klok.withZone(NEDERLAND));
	}

	public void zetTestDatum(LocalDate datum) {
		this.testDatum = datum;
	}

	public void reset() {
		this.testDatum = null;
	}

	public boolean heeftTestDatum() {
		return testDatum != null;
	}

	/** Leest "jjjj-mm-dd". Geeft leeg terug als de tekst geen geldige datum is. */
	public static Optional<LocalDate> leesDatum(String tekst) {
		try {
			return Optional.of(LocalDate.parse(tekst.trim()));
		} catch (DateTimeParseException | NullPointerException e) {
			return Optional.empty();
		}
	}
}
