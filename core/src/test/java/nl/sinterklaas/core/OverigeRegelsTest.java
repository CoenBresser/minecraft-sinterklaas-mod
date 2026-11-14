package nl.sinterklaas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class OverigeRegelsTest {
	@Test
	void schoenMetWortelKrijgtDeVolgendeOchtendEenPakje() {
		long gezet = SchoenRegels.dagNummer(18000);           // dag 0, 's avonds
		assertFalse(SchoenRegels.pietIsLangsGeweest(gezet, SchoenRegels.dagNummer(23999), SchoenInhoud.WORTEL)); // nog dezelfde nacht
		assertTrue(SchoenRegels.pietIsLangsGeweest(gezet, SchoenRegels.dagNummer(24000), SchoenInhoud.WORTEL));  // ochtend van dag 1
		assertEquals(SchoenInhoud.PAKJE, SchoenRegels.naDeNacht(SchoenInhoud.WORTEL));
	}

	@Test
	void schoenZonderWortelKrijgtEenBriefjeEnGeenPakje() {
		assertTrue(SchoenRegels.pietIsLangsGeweest(0, 1, SchoenInhoud.LEEG));
		assertEquals(SchoenInhoud.BRIEFJE, SchoenRegels.naDeNacht(SchoenInhoud.LEEG));
	}

	@Test
	void schoenKrijgtMaarEenKeerPerNachtIets() {
		assertFalse(SchoenRegels.pietIsLangsGeweest(0, 1, SchoenInhoud.PAKJE));   // er ligt al een pakje
		assertFalse(SchoenRegels.pietIsLangsGeweest(0, 5, SchoenInhoud.PAKJE));   // ook na dagen komt er geen tweede bij
		assertFalse(SchoenRegels.pietIsLangsGeweest(0, 5, SchoenInhoud.BRIEFJE)); // en bij een briefje ook niet
		assertEquals(SchoenInhoud.PAKJE, SchoenRegels.naDeNacht(SchoenInhoud.PAKJE));
		assertEquals(SchoenInhoud.BRIEFJE, SchoenRegels.naDeNacht(SchoenInhoud.BRIEFJE));
		// Leeggehaald op dag 1 met een nieuwe wortel: pas op dag 2 een nieuw pakje.
		assertFalse(SchoenRegels.pietIsLangsGeweest(1, 1, SchoenInhoud.WORTEL));
		assertTrue(SchoenRegels.pietIsLangsGeweest(1, 2, SchoenInhoud.WORTEL));
	}

	@Test
	void eenWortelKanAlleenInEenLegeSchoen() {
		assertTrue(SchoenRegels.magWortelErin(SchoenInhoud.LEEG));
		assertFalse(SchoenRegels.magWortelErin(SchoenInhoud.WORTEL));
		assertFalse(SchoenRegels.magWortelErin(SchoenInhoud.BRIEFJE));
		assertFalse(SchoenRegels.magWortelErin(SchoenInhoud.PAKJE));
	}

	@Test
	void hetBriefjeVanPietStaatInHetTekstbestand() {
		java.util.List<String> briefje = Teksten.laad().schoenZonderWortel();
		assertEquals(4, briefje.size());
		assertEquals("Een schoen zonder wortel voor het paard,", briefje.get(0));
		assertEquals("Dat is natuurlijk geen cadeautje waard.", briefje.get(1));
		assertEquals("Wat dacht je wel niet?", briefje.get(2));
		assertEquals("Piet", briefje.get(3));
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
