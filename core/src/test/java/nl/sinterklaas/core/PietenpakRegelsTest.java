package nl.sinterklaas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.sinterklaas.core.PietenpakRegels.Deel;
import org.junit.jupiter.api.Test;

class PietenpakRegelsTest {
	private static String roet(Deel deel) {
		return PietenpakRegels.naam(PietVariant.ROETVEEG, deel);
	}

	@Test
	void elkDeelHeeftEenEigenNaamPerPiet() {
		assertEquals("pietenpak_roetveeg_muts", roet(Deel.MUTS));
		assertEquals("pietenpak_malle_schoenen", PietenpakRegels.naam(PietVariant.MALLE, Deel.SCHOENEN));
	}

	@Test
	void hetHelePakGeeftDeBonus() {
		assertTrue(PietenpakRegels.heelPak(roet(Deel.MUTS), roet(Deel.JASJE), roet(Deel.BROEK), roet(Deel.SCHOENEN)));
	}

	@Test
	void delenVanVerschillendePietenMogen() {
		assertTrue(PietenpakRegels.heelPak(
			PietenpakRegels.naam(PietVariant.HOOFD, Deel.MUTS),
			PietenpakRegels.naam(PietVariant.PAKJES, Deel.JASJE),
			PietenpakRegels.naam(PietVariant.KOEK, Deel.BROEK),
			PietenpakRegels.naam(PietVariant.MALLE, Deel.SCHOENEN)));
	}

	@Test
	void eenDeelTeWeinigGeeftGeenBonus() {
		assertFalse(PietenpakRegels.heelPak(null, roet(Deel.JASJE), roet(Deel.BROEK), roet(Deel.SCHOENEN)));
		assertFalse(PietenpakRegels.heelPak("minecraft:iron_helmet", roet(Deel.JASJE), roet(Deel.BROEK), roet(Deel.SCHOENEN)));
		assertFalse(PietenpakRegels.heelPak(roet(Deel.MUTS), roet(Deel.JASJE), roet(Deel.BROEK), ""));
	}

	@Test
	void eenDeelOpDeVerkeerdePlekTeltNiet() {
		assertFalse(PietenpakRegels.heelPak(roet(Deel.JASJE), roet(Deel.MUTS), roet(Deel.BROEK), roet(Deel.SCHOENEN)));
	}

	@Test
	void deBonusKnippertNiet() {
		assertTrue(PietenpakRegels.BONUS_TIKKEN > PietenpakRegels.CONTROLE_TIKKEN);
		assertTrue(PietenpakRegels.controleNu(40));
		assertFalse(PietenpakRegels.controleNu(41));
	}
}
