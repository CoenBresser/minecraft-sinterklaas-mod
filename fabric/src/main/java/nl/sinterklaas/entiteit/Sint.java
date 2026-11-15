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
 * Hij draagt altijd zijn mijter (daar zit ook zijn lange witte baard aan vast) en houdt zijn staf vast.
 */
public class Sint extends SinterklaasFiguur {
	public Sint(EntityType<? extends Sint> type, Level wereld) {
		super(type, wereld);
		// De mijter en de staf van Sint zelf vallen niet.
		this.setDropChance(EquipmentSlot.HEAD, 0.0F);
		this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
	}

	@Override
	protected String spreker() {
		return Namen.SINT;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		// Sint zonder mijter of staf bestaat niet: geef ze (weer), ook aan een Sint uit een oudere versie.
		if (this.level() instanceof ServerLevel && this.isAlive() && this.tickCount % 100 == 2) {
			if (!this.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MIJTER)) {
				this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.MIJTER));
			}
			if (!this.getItemBySlot(EquipmentSlot.MAINHAND).is(ModItems.STAF)) {
				this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.STAF));
			}
		}
	}
}
