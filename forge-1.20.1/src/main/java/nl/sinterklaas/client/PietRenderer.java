package nl.sinterklaas.client;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.entiteit.Piet;

/**
 * Tekent Piet als een gewoon mensfiguur, net als Sint: het model en de animaties van een speler,
 * met per variant een eigen skin (textures/piet_&lt;variant&gt;.png, het gewone skin-formaat van 64x64).
 * De muts of baret staat op de hoedlaag van de skin.
 */
public class PietRenderer extends HumanoidMobRenderer<Piet, HumanoidModel<Piet>> {
	private static final Map<PietVariant, ResourceLocation> TEXTUREN = new EnumMap<>(PietVariant.class);
	static {
		for (PietVariant v : PietVariant.values()) {
			TEXTUREN.put(v, SinterklaasMod.id("textures/entity/piet/" + v.id() + ".png"));
		}
	}

	public PietRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(Piet piet) {
		return TEXTUREN.get(piet.variant());
	}
}
