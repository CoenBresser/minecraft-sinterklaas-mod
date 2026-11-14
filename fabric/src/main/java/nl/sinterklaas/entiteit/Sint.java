package nl.sinterklaas.entiteit;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nl.sinterklaas.ModItems;
import nl.sinterklaas.core.Namen;

/**
 * Sinterklaas: een gewoon mensfiguur (zelfde vorm als een speler).
 * Hij draagt altijd zijn mijter; daar zit ook zijn lange witte baard aan vast.
 */
public class Sint extends SinterklaasFiguur {
	public Sint(EntityType<? extends Sint> type, Level wereld) {
		super(type, wereld);
		this.setDropChance(EquipmentSlot.HEAD, 0.0F);   // de mijter van Sint zelf valt niet
	}

	@Override
	protected String spreker() {
		return Namen.SINT;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		// Sint zonder mijter bestaat niet: zet hem (weer) op, ook bij een Sint uit een oudere versie.
		if (this.level() instanceof ServerLevel && this.isAlive() && this.tickCount % 100 == 2
				&& !this.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MIJTER)) {
			this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.MIJTER));
		}
	}
}
