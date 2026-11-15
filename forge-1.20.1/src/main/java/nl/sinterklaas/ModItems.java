package nl.sinterklaas;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.core.PietenpakRegels;
import nl.sinterklaas.item.GedichtjeItem;
import nl.sinterklaas.item.MijterItem;
import nl.sinterklaas.item.PepernootItem;
import nl.sinterklaas.item.PietEiItem;
import nl.sinterklaas.item.PietenpakMateriaal;
import nl.sinterklaas.item.StafItem;

public final class ModItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Namen.MOD_ID);
	private static final DeferredRegister<CreativeModeTab> TABBLADEN = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Namen.MOD_ID);

	// Blokken als voorwerp
	public static final RegistryObject<Item> PAKJE = blokItem(Namen.PAKJE, ModBlokken.PAKJE);
	public static final RegistryObject<Item> SCHOENPAKJE = blokItem(Namen.SCHOENPAKJE, ModBlokken.SCHOENPAKJE);
	public static final RegistryObject<Item> SCHOEN = blokItem(Namen.SCHOEN, ModBlokken.SCHOEN);
	public static final RegistryObject<Item> BORD = blokItem(Namen.BORD, ModBlokken.BORD);

	// Lekkers
	/** Zelfde voedingswaarde als een vanilla koekje, altijd eetbaar, en een half hartje erbij (zie PepernootItem). */
	public static final RegistryObject<Item> PEPERNOOT = ITEMS.register(Namen.PEPERNOOT, () -> new PepernootItem(
		new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).alwaysEat().build())));
	public static final RegistryObject<Item> CHOCOLADELETTER = ITEMS.register(Namen.CHOCOLADELETTER, () -> new Item(
		new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).alwaysEat().build())));
	public static final RegistryObject<Item> ZAKJE_PEPERNOTEN = ITEMS.register(Namen.ZAKJE_PEPERNOTEN,
		() -> new Item(new Item.Properties().stacksTo(16)));

	// Overig
	/** De mijter draag je op je hoofd; zie MijterItem. */
	public static final RegistryObject<Item> MIJTER = ITEMS.register(Namen.MIJTER, () -> new MijterItem(new Item.Properties().stacksTo(1)));
	/** De staf van Sint: rechtsklik roept de Pieten in de buurt. */
	public static final RegistryObject<Item> STAF = ITEMS.register(Namen.STAF, () -> new StafItem(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> LINT = ITEMS.register(Namen.LINT, () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> GEDICHTJE = ITEMS.register(Namen.GEDICHTJE,
		() -> new GedichtjeItem(new Item.Properties().stacksTo(16)));

	// Spawn-eieren: één per Piet-variant, plus Sint. De kleuren doen er niet toe: elk ei heeft een eigen plaatje.
	public static final Map<PietVariant, RegistryObject<Item>> PIET_EIEREN = new EnumMap<>(PietVariant.class);
	static {
		for (PietVariant variant : PietVariant.values()) {
			PIET_EIEREN.put(variant, ITEMS.register("piet_" + variant.id() + "_spawn_egg",
				() -> new PietEiItem(variant, new Item.Properties())));
		}
	}
	/**
	 * Het Pietenpak: voor elke Piet een muts, jasje, broek en schoenen in zijn eigen kleuren, even sterk als ijzer.
	 * De bonus voor het hele pak staat in PietenpakBonus.
	 */
	public static final Map<PietVariant, Map<PietenpakRegels.Deel, RegistryObject<Item>>> PIETENPAK = new EnumMap<>(PietVariant.class);
	static {
		for (PietVariant variant : PietVariant.values()) {
			PietenpakMateriaal pak = new PietenpakMateriaal(variant);
			Map<PietenpakRegels.Deel, RegistryObject<Item>> delen = new EnumMap<>(PietenpakRegels.Deel.class);
			for (PietenpakRegels.Deel deel : PietenpakRegels.Deel.values()) {
				ArmorItem.Type soort = switch (deel) {
					case MUTS -> ArmorItem.Type.HELMET;
					case JASJE -> ArmorItem.Type.CHESTPLATE;
					case BROEK -> ArmorItem.Type.LEGGINGS;
					case SCHOENEN -> ArmorItem.Type.BOOTS;
				};
				delen.put(deel, ITEMS.register(PietenpakRegels.naam(variant, deel), () -> new ArmorItem(pak, soort, new Item.Properties())));
			}
			PIETENPAK.put(variant, delen);
		}
	}
	public static final RegistryObject<Item> SINT_EI = ITEMS.register("sint_spawn_egg",
		() -> new ForgeSpawnEggItem(ModEntiteiten.SINT, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

	public static final RegistryObject<CreativeModeTab> TABBLAD = TABBLADEN.register("sinterklaas", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.sinterklaas"))
		.icon(() -> new ItemStack(PAKJE.get()))
		.displayItems((parameters, uitvoer) -> {
			for (Supplier<Item> item : java.util.List.<Supplier<Item>>of(PAKJE, SCHOENPAKJE, SCHOEN, BORD, PEPERNOOT, ZAKJE_PEPERNOTEN,
					CHOCOLADELETTER, MIJTER, STAF, LINT, GEDICHTJE, SINT_EI)) {
				uitvoer.accept(new ItemStack(item.get()));
			}
			for (RegistryObject<Item> ei : PIET_EIEREN.values()) uitvoer.accept(new ItemStack(ei.get()));
			for (Map<PietenpakRegels.Deel, RegistryObject<Item>> pak : PIETENPAK.values()) {
				for (RegistryObject<Item> deel : pak.values()) uitvoer.accept(new ItemStack(deel.get()));
			}
		})
		.build());

	private static RegistryObject<Item> blokItem(String naam, Supplier<Block> blok) {
		return ITEMS.register(naam, () -> new BlockItem(blok.get(), new Item.Properties()));
	}

	static void registreer(IEventBus modBus) {
		ITEMS.register(modBus);
		TABBLADEN.register(modBus);
	}

	private ModItems() {}
}
