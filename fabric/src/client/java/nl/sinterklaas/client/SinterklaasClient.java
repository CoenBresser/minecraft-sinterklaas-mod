package nl.sinterklaas.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import nl.sinterklaas.ModEntiteiten;

public class SinterklaasClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntiteiten.PIET, PietRenderer::new);
		EntityRendererRegistry.register(ModEntiteiten.SINT, SintRenderer::new);
	}
}
