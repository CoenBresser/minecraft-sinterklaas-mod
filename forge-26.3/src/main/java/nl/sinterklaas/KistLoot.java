package nl.sinterklaas;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Stopt zeldzaam een pakje in bestaande kisten (dorpshuizen, kerkers, mijnschachten).
 * Alles staat in data:
 * - in welke kisten: data/sinterklaas/loot_modifiers/kist_pakje.json
 * - de kans en wat erin komt: data/sinterklaas/loot_table/chests/kist_extra.json
 */
public final class KistLoot extends LootModifier {
	public static final ResourceKey<LootTable> KIST_EXTRA =
		ResourceKey.create(Registries.LOOT_TABLE, SinterklaasMod.id("chests/kist_extra"));

	public static final MapCodec<KistLoot> CODEC = RecordCodecBuilder.mapCodec(i -> codecStart(i)
		.and(LootTable.KEY_CODEC.fieldOf("loot_table").forGetter(k -> k.extra))
		.apply(i, KistLoot::new));

	private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> SOORTEN =
		DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, nl.sinterklaas.core.Namen.MOD_ID);
	static {
		SOORTEN.register("kist_pakje", () -> CODEC);
	}

	private final ResourceKey<LootTable> extra;

	public KistLoot(LootItemCondition[] voorwaarden, ResourceKey<LootTable> extra) {
		super(voorwaarden);
		this.extra = extra;
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(LootTable tabel, ObjectArrayList<ItemStack> inhoud, LootContext context) {
		context.getLevel().getServer().reloadableRegistries().getLootTable(this.extra).getRandomItemsRaw(context, inhoud::add);
		return inhoud;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}

	static void registreer(BusGroup modBus) {
		SOORTEN.register(modBus);
	}
}
