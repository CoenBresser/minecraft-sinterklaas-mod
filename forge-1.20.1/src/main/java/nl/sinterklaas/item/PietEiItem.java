package nl.sinterklaas.item;

import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import nl.sinterklaas.ModEntiteiten;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.entiteit.Piet;

/**
 * Het spawn-ei van één bepaalde Piet. Alle Pieten zijn hetzelfde entiteit-type; het ei geeft door
 * welke variant eruit moet komen (zie Piet.finalizeSpawn). De kleuren doen er niet toe: elk ei heeft een eigen plaatje.
 */
public class PietEiItem extends ForgeSpawnEggItem {
	private final PietVariant variant;

	public PietEiItem(PietVariant variant, Item.Properties eigenschappen) {
		super(ModEntiteiten.PIET, 0xFFFFFF, 0xFFFFFF, eigenschappen);
		this.variant = variant;
	}

	public PietVariant variant() {
		return variant;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Piet.variantUitEi = this.variant;
		try {
			return super.useOn(context);
		} finally {
			Piet.variantUitEi = null;
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level wereld, Player speler, InteractionHand hand) {
		Piet.variantUitEi = this.variant;
		try {
			return super.use(wereld, speler, hand);
		} finally {
			Piet.variantUitEi = null;
		}
	}

	/** Ook uit een dispenser komt de goede Piet. */
	@Override
	protected DispenseItemBehavior createDispenseBehavior() {
		DispenseItemBehavior gewoon = super.createDispenseBehavior();
		return (BlockSource bron, ItemStack stapel) -> {
			Piet.variantUitEi = this.variant;
			try {
				return gewoon.dispense(bron, stapel);
			} finally {
				Piet.variantUitEi = null;
			}
		};
	}
}
