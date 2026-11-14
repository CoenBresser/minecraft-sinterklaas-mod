package nl.sinterklaas;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.SinterklaasKlok;
import nl.sinterklaas.core.Teksten;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Startpunt van de mod op Forge (Minecraft 1.20.1). De spelregels zelf staan in de core-module. */
@Mod(Namen.MOD_ID)
public class SinterklaasMod {
	public static final Logger LOG = LoggerFactory.getLogger("Sinterklaas");

	/** De datum voor de pakjes: serverdatum in Nederlandse tijd, of een testdatum. */
	public static final SinterklaasKlok KLOK = new SinterklaasKlok();
	/** Praatzinnen en gedichtjes uit sinterklaas/teksten.json. */
	public static final Teksten TEKSTEN = Teksten.laad();

	public static ResourceLocation id(String pad) {
		return new ResourceLocation(Namen.MOD_ID, pad);
	}

	public SinterklaasMod() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModBlokken.registreer(modBus);
		ModItems.registreer(modBus);
		ModEntiteiten.registreer(modBus);

		MinecraftForge.EVENT_BUS.addListener(KistLoot::bijLaden);
		MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> SinterklaasCommando.registreer(e.getDispatcher()));
		// Een testdatum hoort bij één speelsessie: na het stoppen van de server geldt weer de echte datum.
		MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> KLOK.reset());
		LOG.info("Sinterklaas-mod geladen");
	}
}
