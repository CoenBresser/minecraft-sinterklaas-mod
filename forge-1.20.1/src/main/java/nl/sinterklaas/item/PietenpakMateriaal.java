package nl.sinterklaas.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietVariant;

/**
 * Het materiaal van een Pietenpak: precies zo sterk als ijzer (en te repareren met ijzer), maar met
 * de plaatjes van de Piet: textures/models/armor/pietenpak_<piet>_layer_1.png en _layer_2.png.
 */
public class PietenpakMateriaal implements ArmorMaterial {
	private static final ArmorMaterial IJZER = ArmorMaterials.IRON;
	private final String naam;

	public PietenpakMateriaal(PietVariant variant) {
		this.naam = Namen.MOD_ID + ":pietenpak_" + variant.id();
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type soort) {
		return IJZER.getDurabilityForType(soort);
	}

	@Override
	public int getDefenseForType(ArmorItem.Type soort) {
		return IJZER.getDefenseForType(soort);
	}

	@Override
	public int getEnchantmentValue() {
		return IJZER.getEnchantmentValue();
	}

	@Override
	public SoundEvent getEquipSound() {
		return IJZER.getEquipSound();
	}

	@Override
	public Ingredient getRepairIngredient() {
		return IJZER.getRepairIngredient();
	}

	@Override
	public String getName() {
		return naam;
	}

	@Override
	public float getToughness() {
		return IJZER.getToughness();
	}

	@Override
	public float getKnockbackResistance() {
		return IJZER.getKnockbackResistance();
	}
}
