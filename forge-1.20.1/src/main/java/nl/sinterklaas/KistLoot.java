package nl.sinterklaas;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * Stopt zeldzaam een pakje in bestaande kisten. De kans zelf staat in data:
 * data/sinterklaas/loot_tables/chests/kist_extra.json
 */
public final class KistLoot {
	public static final ResourceLocation KIST_EXTRA = SinterklaasMod.id("chests/kist_extra");

	private static final Set<ResourceLocation> KISTEN = Set.of(
		BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
		BuiltInLootTables.VILLAGE_DESERT_HOUSE,
		BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
		BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
		BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
		BuiltInLootTables.SIMPLE_DUNGEON,
		BuiltInLootTables.ABANDONED_MINESHAFT
	);

	static void bijLaden(LootTableLoadEvent event) {
		if (KISTEN.contains(event.getName())) {
			event.getTable().addPool(LootPool.lootPool().name("sinterklaas_pakje").add(LootTableReference.lootTableReference(KIST_EXTRA)).build());
		}
	}

	private KistLoot() {}
}
