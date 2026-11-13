package nl.sinterklaas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class OverigeRegelsTest {
	@Test
	void schoenWordtDeVolgendeOchtendGevuld() {
		long gezet = SchoenRegels.dagNummer(18000);           // dag 0, 's avonds
		assertFalse(SchoenRegels.moetVullen(gezet, SchoenRegels.dagNummer(23999), false)); // nog dezelfde nacht
		assertTrue(SchoenRegels.moetVullen(gezet, SchoenRegels.dagNummer(24000), false));  // ochtend van dag 1
	}

	@Test
	void schoenKrijgtMaarEenPakjePerNacht() {
		assertFalse(SchoenRegels.moetVullen(0, 1, true));  // er ligt al een pakje
		assertFalse(SchoenRegels.moetVullen(0, 5, true));  // ook na dagen wachten komt er geen tweede bij
		// Pakje eruit gehaald op dag 1: pas op dag 2 een nieuw pakje.
		assertFalse(SchoenRegels.moetVullen(1, 1, false));
		assertTrue(SchoenRegels.moetVullen(1, 2, false));
	}

	@Test
	void dagNummerTeltPer24000Tikken() {
		assertEquals(0, SchoenRegels.dagNummer(0));
		assertEquals(0, SchoenRegels.dagNummer(23999));
		assertEquals(1, SchoenRegels.dagNummer(24000));
		assertEquals(10, SchoenRegels.dagNummer(240001));
	}

	@Test
	void erZijnZesPietenEnOnbekendeIdGeeftRoetveeg() {
		assertEquals(6, PietVariant.values().length);
		assertEquals(PietVariant.KOEK, PietVariant.vanId("koek"));
		assertEquals(PietVariant.ROETVEEG, PietVariant.vanId("bestaat-niet"));
		assertEquals(PietVariant.ROETVEEG, PietVariant.vanId(null));
		assertEquals(PietVariant.MALLE, PietVariant.willekeurig(-1));
	}

	@Test
	void praatPauzeBlijftBinnenDeGrenzen() {
		for (int i = -5000; i < 5000; i += 7) {
			int pauze = PraatRegels.spontanePauze(i);
			assertTrue(pauze >= PraatRegels.SPONTAAN_MIN && pauze <= PraatRegels.SPONTAAN_MAX, "pauze " + pauze);
		}
	}

	@Test
	void zinKiezen() {
		assertEquals("b", PraatRegels.kies(List.of("a", "b", "c"), 4));
		assertEquals("c", PraatRegels.kies(List.of("a", "b", "c"), -1));
		assertNull(PraatRegels.kies(List.of(), 3));
	}

	@Test
	void tekstbestandHeeftMinstensDrieZinnenPerSpreker() {
		Teksten teksten = Teksten.laad();
		for (PietVariant v : PietVariant.values()) {
			assertTrue(teksten.zinnen(v.id()).size() >= 3, "te weinig zinnen voor " + v.id());
		}
		assertTrue(teksten.zinnen(Namen.SINT).size() >= 3, "te weinig zinnen voor sint");
		assertFalse(teksten.gedichtjes().isEmpty());
		assertTrue(teksten.zinnen("bestaat-niet").isEmpty());
	}
}
