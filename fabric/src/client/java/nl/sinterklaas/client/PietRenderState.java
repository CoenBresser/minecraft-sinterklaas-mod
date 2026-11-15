package nl.sinterklaas.client;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import nl.sinterklaas.core.PietVariant;

/** Wat de renderer van een Piet moet weten: alles van een mensfiguur, plus welke Piet het is. */
public class PietRenderState extends HumanoidRenderState {
	public PietVariant variant = PietVariant.ROETVEEG;
}
