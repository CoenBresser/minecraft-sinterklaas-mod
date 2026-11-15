package nl.sinterklaas.client;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.entiteit.Piet;

/** Tekent Piet met het model en de animaties van de vanilla pillager, met per variant een eigen textuur. */
public class PietRenderer extends MobRenderer<Piet, PietRenderState, IllagerModel<PietRenderState>> {
	private static final Map<PietVariant, Identifier> TEXTUREN = new EnumMap<>(PietVariant.class);
	static {
		for (PietVariant v : PietVariant.values()) {
			TEXTUREN.put(v, SinterklaasMod.id("textures/entity/piet/" + v.id() + ".png"));
		}
	}

	public PietRenderer(EntityRendererProvider.Context context) {
		super(context, new IllagerModel<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
		// De pillager verbergt zijn hoedlaag; Piet heeft daar juist zijn baret met veer.
		this.model.getHat().visible = true;
	}

	@Override
	public PietRenderState createRenderState() {
		return new PietRenderState();
	}

	@Override
	public void extractRenderState(Piet piet, PietRenderState staat, float deeltik) {
		super.extractRenderState(piet, staat, deeltik);
		staat.variant = piet.variant();
		staat.isRiding = piet.isPassenger();
		staat.mainArm = piet.getMainArm();
		staat.isAggressive = false;
	}

	@Override
	public Identifier getTextureLocation(PietRenderState staat) {
		return TEXTUREN.get(staat.variant);
	}
}
