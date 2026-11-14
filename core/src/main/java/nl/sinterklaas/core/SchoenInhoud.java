package nl.sinterklaas.core;

/** Wat er in een neergezette schoen zit. De id wordt gebruikt in de blokstatus en de modelnamen. */
public enum SchoenInhoud {
	/** Niets: wie zo zijn schoen zet, krijgt een briefje van Piet. */
	LEEG("leeg"),
	/** Een wortel voor het paard: dan komt er een cadeautje. */
	WORTEL("wortel"),
	/** Het briefje van Piet ("Een schoen zonder wortel voor het paard..."). */
	BRIEFJE("briefje"),
	/** Een pakje. */
	PAKJE("pakje");

	private final String id;

	SchoenInhoud(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}
}
