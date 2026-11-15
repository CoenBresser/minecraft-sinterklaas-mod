package nl.sinterklaas;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.entiteit.Piet;
import nl.sinterklaas.entiteit.Sint;
import nl.sinterklaas.entiteit.SinterklaasFiguur;

public final class ModEntiteiten {
	/** Sint en de Pieten hebben de afmetingen van een speler. Eén Piet-type voor alle varianten. */
	public static final EntityType<Piet> PIET = registreer(Namen.PIET,
		EntityType.Builder.of(Piet::new, MobCategory.CREATURE).sized(0.6F, 1.8F).clientTrackingRange(8));

	public static final EntityType<Sint> SINT = registreer(Namen.SINT,
		EntityType.Builder.of(Sint::new, MobCategory.CREATURE).sized(0.6F, 1.8F).clientTrackingRange(10));

	private static <T extends net.minecraft.world.entity.Entity> EntityType<T> registreer(String naam, EntityType.Builder<T> bouwer) {
		ResourceKey<EntityType<?>> sleutel = ResourceKey.create(Registries.ENTITY_TYPE, SinterklaasMod.id(naam));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, sleutel, bouwer.build(sleutel));
	}

	static void registreer() {
		FabricDefaultAttributeRegistry.register(PIET, SinterklaasFiguur.maakAttributen());
		FabricDefaultAttributeRegistry.register(SINT, SinterklaasFiguur.maakAttributen());

		// Natuurlijke spawn: overdag, in de biomen waar ook dorpen staan, en alleen in het seizoen
		// (15 november t/m 5 december, oplopend; zie SeizoenRegels). Spawn-eieren werken altijd.
		SpawnPlacements.register(PIET, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
			SinterklaasFiguur::magHierSpawnen);
		SpawnPlacements.register(SINT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
			(type, wereld, reden, plek, toeval) ->
				SinterklaasFiguur.magHierSpawnen(type, wereld, reden, plek, toeval) && toeval.nextInt(4) == 0);

		var dorpsBiomen = BiomeSelectors.includeByKey(
			Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW, Biomes.SAVANNA,
			Biomes.DESERT, Biomes.TAIGA, Biomes.SNOWY_PLAINS);
		// gewicht, kleinste groep, grootste groep. Ter vergelijking: een schaap heeft gewicht 12.
		// Dit is de piek op 5 december; eerder in het seizoen gaat maar een deel van de spawns door.
		BiomeModifications.addSpawn(dorpsBiomen, MobCategory.CREATURE, PIET, 8, 1, 3);
		BiomeModifications.addSpawn(dorpsBiomen, MobCategory.CREATURE, SINT, 1, 1, 1);
	}

	private ModEntiteiten() {}
}
