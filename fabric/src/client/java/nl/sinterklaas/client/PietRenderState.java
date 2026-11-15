package nl.sinterklaas.client;

import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import nl.sinterklaas.core.PietVariant;

/** Wat de renderer van een Piet moet weten: alles van de pillager, plus welke Piet het is. */
public class PietRenderState extends IllagerRenderState {
	public PietVariant variant = PietVariant.ROETVEEG;
}
