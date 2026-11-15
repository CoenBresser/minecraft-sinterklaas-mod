package nl.sinterklaas.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.entiteit.Sint;

/**
 * Tekent Sint als een gewoon mensfiguur: hetzelfde model en dezelfde animaties als een speler,
 * met een eigen skin (textures/sint.png, het gewone skin-formaat van 64x64).
 * Zijn mijter en lange baard zijn het voorwerp 'mijter' dat hij op zijn hoofd draagt.
 */
public class SintRenderer extends HumanoidMobRenderer<Sint, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
	private static final Identifier TEXTUUR = SinterklaasMod.id("textures/entity/sint.png");

	public SintRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
	}

	@Override
	public HumanoidRenderState createRenderState() {
		return new HumanoidRenderState();
	}

	@Override
	public Identifier getTextureLocation(HumanoidRenderState staat) {
		return TEXTUUR;
	}
}
