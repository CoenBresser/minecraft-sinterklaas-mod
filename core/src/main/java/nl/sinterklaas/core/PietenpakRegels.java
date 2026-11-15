package nl.sinterklaas.core;

/**
 * Het Pietenpak: een harnas van vier delen (muts, jasje, broek en schoenen), voor elke Piet in zijn
 * eigen kleuren. Het beschermt net zo goed als ijzer. Wie het hele pak aan heeft, is een echte Piet:
 * die loopt sneller en springt hoger (handig op het dak). De delen mogen van verschillende Pieten zijn.
 * Tijden in tikken (20 tikken = 1 seconde).
 */
public final class PietenpakRegels {
	/** De vier delen van het pak, van boven naar beneden. */
	public enum Deel {
		MUTS("muts"),
		JASJE("jasje"),
		BROEK("broek"),
		SCHOENEN("schoenen");

		private final String id;

		Deel(String id) {
			this.id = id;
		}

		public String id() {
			return id;
		}
	}

	/** Zo vaak kijkt het spel of je het hele pak aan hebt: elke seconde. */
	public static final int CONTROLE_TIKKEN = 20;
	/** Zo lang duurt de bonus na elke controle: 3 seconden, zodat hij niet knippert. */
	public static final int BONUS_TIKKEN = 60;
	/** Sterkte van de bonus: 0 is niveau I (Snelheid I en Sprongkracht I). */
	public static final int BONUS_NIVEAU = 0;

	/** De naam van een deel van het pak, bijvoorbeeld "pietenpak_roetveeg_muts". */
	public static String naam(PietVariant variant, Deel deel) {
		return "pietenpak_" + variant.id() + "_" + deel.id();
	}

	/** Is dit een deel van een Pietenpak (van welke Piet dan ook) dat op deze plek hoort? */
	public static boolean isDeel(String naam, Deel deel) {
		if (naam == null) return false;
		for (PietVariant variant : PietVariant.values()) {
			if (naam.equals(naam(variant, deel))) return true;
		}
		return false;
	}

	/**
	 * Heeft iemand het hele pak aan? De namen zijn de voorwerpen op het hoofd, de borst, de benen en
	 * de voeten (leeg of null als daar niets zit). Delen van verschillende Pieten mag.
	 */
	public static boolean heelPak(String hoofd, String borst, String benen, String voeten) {
		return isDeel(hoofd, Deel.MUTS) && isDeel(borst, Deel.JASJE)
			&& isDeel(benen, Deel.BROEK) && isDeel(voeten, Deel.SCHOENEN);
	}

	/** Moet het spel nu kijken of iemand het hele pak aan heeft? */
	public static boolean controleNu(long tik) {
		return tik % CONTROLE_TIKKEN == 0;
	}

	private PietenpakRegels() {}
}
