package nl.sinterklaas.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.entiteit.Sint;

/** Tekent Sint met het model en de animaties van de vanilla dorpeling, met een eigen textuur. */
public class SintRenderer extends MobRenderer<Sint, VillagerRenderState, VillagerModel> {
	private static final Identifier TEXTUUR = SinterklaasMod.id("textures/entity/sint.png");

	public SintRenderer(EntityRendererProvider.Context context) {
		super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
		// De dorpeling heeft een brede platte hoedrand. Die hoort niet bij de mijter en zou een stuk
		// van de armtextuur laten zien, dus die verbergen we.
		this.model.root().getChild("head").getChild("hat").getChild("hat_rim").visible = false;
	}

	@Override
	public VillagerRenderState createRenderState() {
		return new VillagerRenderState();
	}

	@Override
	public Identifier getTextureLocation(VillagerRenderState staat) {
		return TEXTUUR;
	}
}
