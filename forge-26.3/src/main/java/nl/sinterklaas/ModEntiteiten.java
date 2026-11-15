package nl.sinterklaas;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.entiteit.Piet;
import nl.sinterklaas.entiteit.Sint;
import nl.sinterklaas.entiteit.SinterklaasFiguur;

public final class ModEntiteiten {
	private static final DeferredRegister<EntityType<?>> ENTITEITEN = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Namen.MOD_ID);

	/** Pieten hebben de afmetingen van een pillager, Sint die van een speler. Eén Piet-type voor alle varianten. */
	public static final RegistryObject<EntityType<Piet>> PIET = registreer(Namen.PIET,
		() -> EntityType.Builder.of(Piet::new, MobCategory.CREATURE).sized(0.6F, 1.95F).clientTrackingRange(8));

	public static final RegistryObject<EntityType<Sint>> SINT = registreer(Namen.SINT,
		() -> EntityType.Builder.of(Sint::new, MobCategory.CREATURE).sized(0.6F, 1.8F).clientTrackingRange(10));

	private static <T extends Entity> RegistryObject<EntityType<T>> registreer(String naam,
			java.util.function.Supplier<EntityType.Builder<T>> bouwer) {
		return ENTITEITEN.register(naam, () -> bouwer.get().build(ENTITEITEN.key(naam)));
	}

	static void registreer(BusGroup modBus) {
		ENTITEITEN.register(modBus);
		EntityAttributeCreationEvent.BUS.addListener(ModEntiteiten::attributen);
		SpawnPlacementRegisterEvent.BUS.addListener(ModEntiteiten::spawnRegels);
	}

	private static void attributen(EntityAttributeCreationEvent event) {
		event.put(PIET.get(), SinterklaasFiguur.maakAttributen().build());
		event.put(SINT.get(), SinterklaasFiguur.maakAttributen().build());
	}

	/**
	 * Natuurlijke spawn: overdag, en alleen in het seizoen (15 november t/m 5 december, oplopend;
	 * zie SeizoenRegels). Spawn-eieren werken altijd.
	 * In welke biomen en hoe vaak staat in data: data/sinterklaas/forge/biome_modifier/dorpsbiomen.json
	 * (gewicht 8 voor Piet en 1 voor Sint; ter vergelijking: een schaap heeft gewicht 12).
	 */
	private static void spawnRegels(SpawnPlacementRegisterEvent event) {
		event.register(PIET.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
			SinterklaasFiguur::magHierSpawnen, SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(SINT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
			(type, wereld, reden, plek, toeval) ->
				SinterklaasFiguur.magHierSpawnen(type, wereld, reden, plek, toeval) && toeval.nextInt(4) == 0,
			SpawnPlacementRegisterEvent.Operation.REPLACE);
	}

	private ModEntiteiten() {}
}
