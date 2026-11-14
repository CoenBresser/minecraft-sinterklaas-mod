package nl.sinterklaas.client;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.entiteit.Piet;

/** Tekent Piet met het model van de vanilla pillager, met per variant een eigen textuur. */
public class PietRenderer extends MobRenderer<Piet, PietModel> {
	private static final Map<PietVariant, ResourceLocation> TEXTUREN = new EnumMap<>(PietVariant.class);
	static {
		for (PietVariant v : PietVariant.values()) {
			TEXTUREN.put(v, SinterklaasMod.id("textures/entity/piet/" + v.id() + ".png"));
		}
	}

	public PietRenderer(EntityRendererProvider.Context context) {
		super(context, new PietModel(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(Piet piet) {
		return TEXTUREN.get(piet.variant());
	}
}
