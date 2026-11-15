package nl.sinterklaas.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * De mijter draag je op je hoofd (helm-slot). Hij beschermt net zo goed als een ijzeren helm en slijt niet.
 * Er zit een lange witte baard aan vast: Sint draagt hem, en een speler met de mijter op krijgt ook een baard.
 */
public class MijterItem extends Item implements Equipable {
	public static final double BESCHERMING = 2.0;
	private static final UUID BESCHERMING_ID = UUID.fromString("5d0c6f1e-7a0b-4c1d-9d5a-0512000000a1");

	private final Multimap<Attribute, AttributeModifier> opHetHoofd = ImmutableMultimap.of(Attributes.ARMOR,
		new AttributeModifier(BESCHERMING_ID, "Mijter", BESCHERMING, AttributeModifier.Operation.ADDITION));

	public MijterItem(Item.Properties eigenschappen) {
		super(eigenschappen);
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	/** Rechtsklik met de mijter in je hand: zet hem op (en wissel met wat je al op had). */
	@Override
	public InteractionResultHolder<ItemStack> use(Level wereld, Player speler, InteractionHand hand) {
		return this.swapWithEquipmentSlot(this, wereld, speler, hand);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.HEAD ? this.opHetHoofd : super.getDefaultAttributeModifiers(slot);
	}
}
