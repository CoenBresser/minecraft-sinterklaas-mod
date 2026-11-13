package nl.sinterklaas;

import java.util.Set;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

/**
 * Stopt zeldzaam een pakje in bestaande kisten. De kans zelf staat in data:
 * data/sinterklaas/loot_table/chests/kist_extra.json
 */
public final class KistLoot {
	public static final ResourceKey<LootTable> KIST_EXTRA =
		ResourceKey.create(Registries.LOOT_TABLE, SinterklaasMod.id("chests/kist_extra"));

	private static final Set<ResourceKey<LootTable>> KISTEN = Set.of(
		BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
		BuiltInLootTables.VILLAGE_DESERT_HOUSE,
		BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
		BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
		BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
		BuiltInLootTables.SIMPLE_DUNGEON,
		BuiltInLootTables.ABANDONED_MINESHAFT
	);

	static void registreer() {
		LootTableEvents.MODIFY.register((sleutel, tabel, bron, registers) -> {
			if (bron.isBuiltin() && KISTEN.contains(sleutel)) {
				tabel.withPool(LootPool.lootPool().add(NestedLootTable.lootTableReference(KIST_EXTRA)));
			}
		});
	}

	private KistLoot() {}
}
