package nl.sinterklaas;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietenpakRegels;

/** Wie het hele Pietenpak aan heeft, loopt sneller en springt hoger (zie PietenpakRegels). */
public final class PietenpakBonus {
	static void bijTik(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
		if (!PietenpakRegels.controleNu(event.player.tickCount)) return;
		if (heeftHeelPak(event.player)) geefBonus(event.player);
	}

	public static boolean heeftHeelPak(LivingEntity wie) {
		return PietenpakRegels.heelPak(naam(wie, EquipmentSlot.HEAD), naam(wie, EquipmentSlot.CHEST),
			naam(wie, EquipmentSlot.LEGS), naam(wie, EquipmentSlot.FEET));
	}

	/** Snelheid en Sprongkracht, zonder belletjes, maar met een icoontje zodat je ziet dat het werkt. */
	public static void geefBonus(LivingEntity wie) {
		wie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, PietenpakRegels.BONUS_TIKKEN, PietenpakRegels.BONUS_NIVEAU, true, false, true));
		wie.addEffect(new MobEffectInstance(MobEffects.JUMP, PietenpakRegels.BONUS_TIKKEN, PietenpakRegels.BONUS_NIVEAU, true, false, true));
	}

	/** De naam van het voorwerp op die plek, alleen als het uit deze mod komt. */
	private static String naam(LivingEntity wie, EquipmentSlot plek) {
		ResourceLocation id = ForgeRegistries.ITEMS.getKey(wie.getItemBySlot(plek).getItem());
		return id != null && Namen.MOD_ID.equals(id.getNamespace()) ? id.getPath() : "";
	}

	private PietenpakBonus() {}
}
