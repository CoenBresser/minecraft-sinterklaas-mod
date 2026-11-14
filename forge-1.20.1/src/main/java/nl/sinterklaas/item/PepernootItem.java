package nl.sinterklaas.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Een pepernoot eet je als een koekje (1 hongerpoppetje) en hij geeft daarnaast meteen
 * een half hartje terug. Je mag hem altijd eten, ook met een volle maag.
 */
public class PepernootItem extends Item {
	/** Hoeveel gezondheid één pepernoot direct teruggeeft: 1 punt = een half hartje. */
	public static final float GENEZING = 1.0F;

	public PepernootItem(Item.Properties eigenschappen) {
		super(eigenschappen);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stapel, Level wereld, LivingEntity eter) {
		if (!wereld.isClientSide) eter.heal(GENEZING);
		return super.finishUsingItem(stapel, wereld, eter);
	}
}
