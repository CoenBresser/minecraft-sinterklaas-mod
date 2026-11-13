package nl.sinterklaas.core;

/** De zes Pieten. De id wordt gebruikt in opslag, textuurnamen en tekstbestanden. */
public enum PietVariant {
	ROETVEEG("roetveeg"),
	HOOFD("hoofd"),
	PAKJES("pakjes"),
	WEGWIJS("wegwijs"),
	KOEK("koek"),
	MALLE("malle");

	private final String id;

	PietVariant(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	/** Zoekt een variant op id; onbekende of lege id's geven de Roetveegpiet. */
	public static PietVariant vanId(String id) {
		for (PietVariant v : values()) {
			if (v.id.equals(id)) return v;
		}
		return ROETVEEG;
	}

	/** Kiest een variant bij een willekeurig getal (mag negatief zijn). */
	public static PietVariant willekeurig(int getal) {
		return values()[Math.floorMod(getal, values().length)];
	}
}
