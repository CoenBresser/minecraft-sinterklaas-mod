package nl.sinterklaas.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Alle praatzinnen en gedichtjes. Ze staan in één bestand: sinterklaas/teksten.json.
 * Wie een zin wil veranderen, hoeft alleen dat bestand aan te passen.
 */
public final class Teksten {
	public static final String BESTAND = "/sinterklaas/teksten.json";

	private final Map<String, List<String>> praat;
	private final List<String> gedichtjes;
	private final List<String> schoenZonderWortel;

	public Teksten(Map<String, List<String>> praat, List<String> gedichtjes, List<String> schoenZonderWortel) {
		this.praat = praat;
		this.gedichtjes = gedichtjes;
		this.schoenZonderWortel = schoenZonderWortel;
	}

	/** Het briefje van Piet in een schoen zonder wortel, regel voor regel. */
	public List<String> schoenZonderWortel() {
		return schoenZonderWortel;
	}

	/** De zinnen van één spreker: een Piet-variant-id of "sint". */
	public List<String> zinnen(String spreker) {
		return praat.getOrDefault(spreker, List.of());
	}

	public List<String> gedichtjes() {
		return gedichtjes;
	}

	/** Leest het meegeleverde tekstbestand. */
	public static Teksten laad() {
		try (InputStream in = Teksten.class.getResourceAsStream(BESTAND)) {
			if (in == null) throw new IllegalStateException("Tekstbestand ontbreekt: " + BESTAND);
			return lees(new InputStreamReader(in, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new IllegalStateException("Kan tekstbestand niet lezen: " + BESTAND, e);
		}
	}

	public static Teksten lees(Reader bron) {
		JsonObject wortel = new Gson().fromJson(bron, JsonObject.class);
		Map<String, List<String>> praat = new HashMap<>();
		JsonObject sprekers = wortel.getAsJsonObject("praat");
		if (sprekers != null) {
			for (Map.Entry<String, JsonElement> e : sprekers.entrySet()) {
				praat.put(e.getKey(), lijst(e.getValue().getAsJsonArray()));
			}
		}
		JsonArray gedichtjes = wortel.getAsJsonArray("gedichtjes");
		JsonArray briefje = wortel.getAsJsonArray("schoen_zonder_wortel");
		return new Teksten(praat, gedichtjes == null ? List.of() : lijst(gedichtjes), briefje == null ? List.of() : lijst(briefje));
	}

	private static List<String> lijst(JsonArray array) {
		List<String> uit = new ArrayList<>();
		for (JsonElement el : array) uit.add(el.getAsString());
		return List.copyOf(uit);
	}
}
