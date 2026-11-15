package nl.sinterklaas;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietenpakRegels;

/** Wie het hele Pietenpak aan heeft, loopt sneller en springt hoger (zie PietenpakRegels). */
public final class PietenpakBonus {
	static void registreer() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (!PietenpakRegels.controleNu(server.getTickCount())) return;
			for (ServerPlayer speler : server.getPlayerList().getPlayers()) {
				if (heeftHeelPak(speler)) geefBonus(speler);
			}
		});
	}

	public static boolean heeftHeelPak(LivingEntity wie) {
		return PietenpakRegels.heelPak(naam(wie, EquipmentSlot.HEAD), naam(wie, EquipmentSlot.CHEST),
			naam(wie, EquipmentSlot.LEGS), naam(wie, EquipmentSlot.FEET));
	}

	/** Snelheid en Sprongkracht, zonder belletjes, maar met een icoontje zodat je ziet dat het werkt. */
	public static void geefBonus(LivingEntity wie) {
		wie.addEffect(new MobEffectInstance(MobEffects.SPEED, PietenpakRegels.BONUS_TIKKEN, PietenpakRegels.BONUS_NIVEAU, true, false, true));
		wie.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, PietenpakRegels.BONUS_TIKKEN, PietenpakRegels.BONUS_NIVEAU, true, false, true));
	}

	/** De naam van het voorwerp op die plek, alleen als het uit deze mod komt. */
	private static String naam(LivingEntity wie, EquipmentSlot plek) {
		Identifier id = BuiltInRegistries.ITEM.getKey(wie.getItemBySlot(plek).getItem());
		return Namen.MOD_ID.equals(id.getNamespace()) ? id.getPath() : "";
	}

	private PietenpakBonus() {}
}
