package nl.sinterklaas;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.SinterklaasKlok;
import nl.sinterklaas.core.Teksten;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Startpunt van de mod op Fabric. De spelregels zelf staan in de core-module. */
public class SinterklaasMod implements ModInitializer {
	public static final Logger LOG = LoggerFactory.getLogger("Sinterklaas");

	/** De datum voor de pakjes: serverdatum in Nederlandse tijd, of een testdatum. */
	public static final SinterklaasKlok KLOK = new SinterklaasKlok();
	/** Praatzinnen en gedichtjes uit sinterklaas/teksten.json. */
	public static final Teksten TEKSTEN = Teksten.laad();

	public static Identifier id(String pad) {
		return Identifier.fromNamespaceAndPath(Namen.MOD_ID, pad);
	}

	@Override
	public void onInitialize() {
		ModEntiteiten.registreer();
		ModBlokken.registreer();
		ModItems.registreer();
		KistLoot.registreer();
		PietenpakBonus.registreer();
		CommandRegistrationCallback.EVENT.register((dispatcher, context, omgeving) -> SinterklaasCommando.registreer(dispatcher));
		// Een testdatum hoort bij één speelsessie: na het stoppen van de server geldt weer de echte datum.
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> KLOK.reset());
		LOG.info("Sinterklaas-mod geladen");
	}
}
