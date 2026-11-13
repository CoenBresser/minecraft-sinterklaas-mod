package nl.sinterklaas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class PakjesRegelsTest {
	private static LocalDate dag(int jaar, int maand, int dag) {
		return LocalDate.of(jaar, maand, dag);
	}

	@Test
	void op4DecemberBlijftHetPakjeDicht() {
		assertFalse(PakjesRegels.magOpenen(dag(2026, 12, 4), false));
	}

	@Test
	void op5DecemberMagHetPakjeOpen() {
		assertTrue(PakjesRegels.magOpenen(dag(2026, 12, 5), false));
	}

	@Test
	void tussen5En23DecemberMagHetOpen() {
		assertTrue(PakjesRegels.magOpenen(dag(2026, 12, 6), false));
		assertTrue(PakjesRegels.magOpenen(dag(2026, 12, 23), false));
	}

	@Test
	void vlakVoorKerstGaatHetWeerDicht() {
		assertFalse(PakjesRegels.magOpenen(dag(2026, 12, 24), false));
		assertFalse(PakjesRegels.magOpenen(dag(2026, 12, 25), false));
		assertFalse(PakjesRegels.magOpenen(dag(2026, 12, 31), false));
	}

	@Test
	void deRestVanHetJaarIsHetDicht() {
		assertFalse(PakjesRegels.magOpenen(dag(2027, 1, 1), false));
		assertFalse(PakjesRegels.magOpenen(dag(2027, 6, 5), false));
		assertFalse(PakjesRegels.magOpenen(dag(2026, 11, 13), false));
		assertFalse(PakjesRegels.magOpenen(dag(2026, 11, 5), false));
	}

	@Test
	void hetWerktElkJaarOpnieuw() {
		assertFalse(PakjesRegels.magOpenen(dag(2027, 12, 4), false));
		assertTrue(PakjesRegels.magOpenen(dag(2027, 12, 5), false));
	}

	@Test
	void eenPakjeUitEenSchoenMagAltijdOpen() {
		assertTrue(PakjesRegels.magOpenen(dag(2026, 12, 4), true));
		assertTrue(PakjesRegels.magOpenen(dag(2026, 7, 1), true));
		assertTrue(PakjesRegels.magOpenen(dag(2026, 12, 25), true));
	}

	// --- Tijdzone: de datum in Nederland telt, niet UTC en niet de tijdzone van de server. ---

	@Test
	void netNaMiddernachtInNederlandIsHetAl5DecemberOokAlIsHetInUtcNog4December() {
		// 4 december 23:30 UTC = 5 december 00:30 in Nederland (wintertijd, UTC+1)
		Clock klok = Clock.fixed(Instant.parse("2026-12-04T23:30:00Z"), ZoneOffset.UTC);
		SinterklaasKlok sintKlok = new SinterklaasKlok(klok);
		assertEquals(dag(2026, 12, 5), sintKlok.vandaag());
		assertTrue(PakjesRegels.magOpenen(sintKlok.vandaag(), false));
	}

	@Test
	void netVoorMiddernachtInNederlandIsHetNog4December() {
		// 4 december 22:30 UTC = 4 december 23:30 in Nederland
		Clock klok = Clock.fixed(Instant.parse("2026-12-04T22:30:00Z"), ZoneOffset.UTC);
		SinterklaasKlok sintKlok = new SinterklaasKlok(klok);
		assertEquals(dag(2026, 12, 4), sintKlok.vandaag());
		assertFalse(PakjesRegels.magOpenen(sintKlok.vandaag(), false));
	}

	@Test
	void eenServerInEenAndereTijdzoneGebruiktTochDeNederlandseDatum() {
		// Server staat in Los Angeles: daar is het 4 december 15:30, in Nederland al 5 december 00:30.
		Clock amerika = Clock.fixed(Instant.parse("2026-12-04T23:30:00Z"), ZoneId.of("America/Los_Angeles"));
		assertEquals(dag(2026, 12, 5), new SinterklaasKlok(amerika).vandaag());

		// Server staat in Tokio: daar is het al 5 december 07:30, in Nederland nog 4 december 23:30.
		Clock japan = Clock.fixed(Instant.parse("2026-12-04T22:30:00Z"), ZoneId.of("Asia/Tokyo"));
		assertEquals(dag(2026, 12, 4), new SinterklaasKlok(japan).vandaag());
	}

	@Test
	void ookVlakVoorKerstTeltDeNederlandseMiddernacht() {
		// 23 december 22:59 UTC = 23 december 23:59 in Nederland: mag nog net.
		Clock nogNet = Clock.fixed(Instant.parse("2026-12-23T22:59:00Z"), ZoneOffset.UTC);
		assertTrue(PakjesRegels.magOpenen(new SinterklaasKlok(nogNet).vandaag(), false));
		// 23 december 23:00 UTC = 24 december 00:00 in Nederland: dicht.
		Clock teLaat = Clock.fixed(Instant.parse("2026-12-23T23:00:00Z"), ZoneOffset.UTC);
		assertFalse(PakjesRegels.magOpenen(new SinterklaasKlok(teLaat).vandaag(), false));
	}

	// --- Testschakelaar ---

	@Test
	void deTestdatumGaatVoorDeEchteDatumEnResetHerstelt() {
		Clock klok = Clock.fixed(Instant.parse("2026-11-13T12:00:00Z"), ZoneOffset.UTC);
		SinterklaasKlok sintKlok = new SinterklaasKlok(klok);
		assertFalse(sintKlok.heeftTestDatum());
		assertFalse(PakjesRegels.magOpenen(sintKlok.vandaag(), false));

		sintKlok.zetTestDatum(dag(2026, 12, 5));
		assertTrue(sintKlok.heeftTestDatum());
		assertTrue(PakjesRegels.magOpenen(sintKlok.vandaag(), false));

		sintKlok.reset();
		assertEquals(dag(2026, 11, 13), sintKlok.vandaag());
	}

	@Test
	void datumLezen() {
		assertEquals(dag(2026, 12, 5), SinterklaasKlok.leesDatum("2026-12-05").orElseThrow());
		assertTrue(SinterklaasKlok.leesDatum("5 december").isEmpty());
		assertTrue(SinterklaasKlok.leesDatum("2026-13-40").isEmpty());
		assertTrue(SinterklaasKlok.leesDatum("").isEmpty());
	}
}
