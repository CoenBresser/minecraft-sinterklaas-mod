package nl.sinterklaas;

import net.minecraft.resources.Identifier;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.SinterklaasKlok;
import nl.sinterklaas.core.Teksten;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Startpunt van de mod op Forge (Minecraft 26.3). De spelregels zelf staan in de core-module. */
@Mod(Namen.MOD_ID)
public final class SinterklaasMod {
	public static final Logger LOG = LoggerFactory.getLogger("Sinterklaas");

	/** De datum voor de pakjes: serverdatum in Nederlandse tijd, of een testdatum. */
	public static final SinterklaasKlok KLOK = new SinterklaasKlok();
	/** Praatzinnen en gedichtjes uit sinterklaas/teksten.json. */
	public static final Teksten TEKSTEN = Teksten.laad();

	public static Identifier id(String pad) {
		return Identifier.fromNamespaceAndPath(Namen.MOD_ID, pad);
	}

	public SinterklaasMod(FMLJavaModLoadingContext context) {
		BusGroup modBus = context.getModBusGroup();
		ModEntiteiten.registreer(modBus);
		ModBlokken.registreer(modBus);
		ModItems.registreer(modBus);

		KistLoot.registreer(modBus);
		RegisterCommandsEvent.BUS.addListener(e -> SinterklaasCommando.registreer(e.getDispatcher()));
		// Een testdatum hoort bij één speelsessie: na het stoppen van de server geldt weer de echte datum.
		ServerStoppedEvent.BUS.addListener(e -> KLOK.reset());
		if (ForgeGameTestHooks.isGametestEnabled()) laadSpelTests(modBus);
		LOG.info("Sinterklaas-mod geladen");
	}

	/** De speltests zitten niet in de mod-jar: alleen laden als ze er zijn (bij het ontwikkelen en in CI). */
	private static void laadSpelTests(BusGroup modBus) {
		try {
			Class.forName("nl.sinterklaas.test.SpelTest").getMethod("registreer", BusGroup.class).invoke(null, modBus);
		} catch (ClassNotFoundException geenTests) {
			// De gewone mod-jar: geen tests.
		} catch (ReflectiveOperationException fout) {
			throw new IllegalStateException("De speltests konden niet geladen worden", fout);
		}
	}
}
