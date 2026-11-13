package nl.sinterklaas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SeizoenRegelsTest {
	private static LocalDate dag(int jaar, int maand, int dag) {
		return LocalDate.of(jaar, maand, dag);
	}

	@Test
	void buitenHetSeizoenSpawntErNiets() {
		assertEquals(0.0, SeizoenRegels.spawnKans(dag(2026, 7, 1)));
		assertEquals(0.0, SeizoenRegels.spawnKans(dag(2026, 11, 14)));
		assertEquals(0.0, SeizoenRegels.spawnKans(dag(2027, 1, 1)));
	}

	@Test
	void na5DecemberSpawntErNietsMeer() {
		assertEquals(0.0, SeizoenRegels.spawnKans(dag(2026, 12, 6)));
		assertEquals(0.0, SeizoenRegels.spawnKans(dag(2026, 12, 24)));
		assertEquals(0.0, SeizoenRegels.spawnKans(dag(2026, 12, 31)));
	}

	@Test
	void hetSeizoenBegintOp15NovemberEnPiektOp5December() {
		assertEquals(SeizoenRegels.KANS_BIJ_START, SeizoenRegels.spawnKans(dag(2026, 11, 15)), 1e-9);
		assertEquals(1.0, SeizoenRegels.spawnKans(dag(2026, 12, 5)), 1e-9);
	}

	@Test
	void dichterBij5DecemberSpawnenErSteedsMeer() {
		double vorige = 0.0;
		for (LocalDate d = dag(2026, 11, 15); !d.isAfter(dag(2026, 12, 5)); d = d.plusDays(1)) {
			double kans = SeizoenRegels.spawnKans(d);
			assertTrue(kans > vorige, "kans moet stijgen op " + d);
			vorige = kans;
		}
		assertEquals(0.6, SeizoenRegels.spawnKans(dag(2026, 11, 25)), 1e-9);   // precies halverwege
	}

	@Test
	void op6DecemberVertrektIedereenVanDatSeizoen() {
		int seizoen = SeizoenRegels.seizoensJaar(dag(2026, 11, 20));
		assertEquals(2026, seizoen);
		assertFalse(SeizoenRegels.moetVertrekken(seizoen, dag(2026, 11, 20)));
		assertFalse(SeizoenRegels.moetVertrekken(seizoen, dag(2026, 12, 5)));
		assertTrue(SeizoenRegels.moetVertrekken(seizoen, dag(2026, 12, 6)));
		assertTrue(SeizoenRegels.moetVertrekken(seizoen, dag(2027, 3, 1)));
	}

	@Test
	void eenPietUitEenEiInDeZomerBlijftTotDeVolgende6December() {
		int seizoen = SeizoenRegels.seizoensJaar(dag(2027, 7, 1));
		assertEquals(2027, seizoen);
		assertFalse(SeizoenRegels.moetVertrekken(seizoen, dag(2027, 7, 1)));
		assertFalse(SeizoenRegels.moetVertrekken(seizoen, dag(2027, 12, 5)));
		assertTrue(SeizoenRegels.moetVertrekken(seizoen, dag(2027, 12, 6)));
	}

	@Test
	void eenPietUitEenEiNa5DecemberHoortBijHetVolgendeJaar() {
		int seizoen = SeizoenRegels.seizoensJaar(dag(2026, 12, 6));
		assertEquals(2027, seizoen);
		assertFalse(SeizoenRegels.moetVertrekken(seizoen, dag(2026, 12, 6)));
		assertFalse(SeizoenRegels.moetVertrekken(seizoen, dag(2027, 6, 1)));
		assertTrue(SeizoenRegels.moetVertrekken(seizoen, dag(2027, 12, 6)));
	}

	@Test
	void op5DecemberZelfHoorJeNogBijDitJaar() {
		assertEquals(2026, SeizoenRegels.seizoensJaar(dag(2026, 12, 5)));
	}
}
