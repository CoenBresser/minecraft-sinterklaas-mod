package nl.sinterklaas.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nl.sinterklaas.ModEntiteiten;
import nl.sinterklaas.core.Namen;

/** Alles wat alleen de client nodig heeft: hoe Sint en de Pieten getekend worden. */
@Mod.EventBusSubscriber(modid = Namen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SinterklaasClient {
	@SubscribeEvent
	public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntiteiten.PIET.get(), PietRenderer::new);
		event.registerEntityRenderer(ModEntiteiten.SINT.get(), SintRenderer::new);
	}

	private SinterklaasClient() {}
}
