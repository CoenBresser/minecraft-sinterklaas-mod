package nl.sinterklaas;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.item.GedichtjeItem;
import nl.sinterklaas.item.PepernootItem;
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
	public static final RegistryObject<Item> PEPERNOOT = registreer(Namen.PEPERNOOT,
		p -> new PepernootItem(p.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).alwaysEdible().build())));
	public static final RegistryObject<Item> CHOCOLADELETTER = registreer(Namen.CHOCOLADELETTER,
		p -> new Item(p.stacksTo(16).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.5F).alwaysEdible().build())));
	public static final RegistryObject<Item> ZAKJE_PEPERNOTEN = registreer(Namen.ZAKJE_PEPERNOTEN, p -> new Item(p.stacksTo(16)));

	// Overig
	/**
	 * De mijter draag je op je hoofd (helm-slot). Hij beschermt net zo goed als een ijzeren helm en slijt niet.
	 * Er zit een lange witte baard aan vast: Sint draagt hem, en een speler met de mijter op krijgt ook een baard.
	 */
	public static final double MIJTER_BESCHERMING = 2.0;
	public static final RegistryObject<Item> MIJTER = registreer(Namen.MIJTER,
		p -> new Item(p.stacksTo(1)
			.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build())
			.attributes(ItemAttributeModifiers.builder()
				.add(Attributes.ARMOR,
					new AttributeModifier(SinterklaasMod.id("mijter"), MIJTER_BESCHERMING, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.HEAD)
				.build())));
	/** De staf van Sint: rechtsklik roept de Pieten in de buurt. */
	public static final RegistryObject<Item> STAF = registreer(Namen.STAF, p -> new StafItem(p.stacksTo(1)));
	public static final RegistryObject<Item> LINT = registreer(Namen.LINT, Item::new);
	public static final RegistryObject<Item> GEDICHTJE = registreer(Namen.GEDICHTJE, p -> new GedichtjeItem(p.stacksTo(16)));

	// Spawn-eieren: één per Piet-variant, plus Sint
	public static final Map<PietVariant, RegistryObject<Item>> PIET_EIEREN = new EnumMap<>(PietVariant.class);
	static {
		for (PietVariant variant : PietVariant.values()) {
			PIET_EIEREN.put(variant, registreer("piet_" + variant.id() + "_spawn_egg", p -> {
				CompoundTag gegevens = new CompoundTag();
				gegevens.putString("variant", variant.id());
				return new SpawnEggItem(p.spawnEgg(ModEntiteiten.PIET.get())
					.component(DataComponents.ENTITY_DATA, TypedEntityData.of(ModEntiteiten.PIET.get(), gegevens)));
			}));
		}
	}
	public static final RegistryObject<Item> SINT_EI = registreer("sint_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(ModEntiteiten.SINT.get())));

	public static final RegistryObject<CreativeModeTab> TABBLAD = TABBLADEN.register("sinterklaas", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.sinterklaas"))
		.icon(() -> new ItemStack(PAKJE.get()))
		.displayItems((parameters, uitvoer) -> {
			for (Supplier<Item> item : java.util.List.<Supplier<Item>>of(PAKJE, SCHOENPAKJE, SCHOEN, BORD, PEPERNOOT, ZAKJE_PEPERNOTEN,
					CHOCOLADELETTER, MIJTER, STAF, LINT, GEDICHTJE, SINT_EI)) {
				uitvoer.accept(new ItemStack(item.get()));
			}
			for (RegistryObject<Item> ei : PIET_EIEREN.values()) uitvoer.accept(new ItemStack(ei.get()));
		})
		.build());

	private static RegistryObject<Item> blokItem(String naam, Supplier<Block> blok) {
		return registreer(naam, p -> new BlockItem(blok.get(), p.useBlockDescriptionPrefix()));
	}

	private static RegistryObject<Item> registreer(String naam, Function<Item.Properties, Item> maker) {
		return ITEMS.register(naam, () -> maker.apply(new Item.Properties().setId(ITEMS.key(naam))));
	}

	static void registreer(BusGroup modBus) {
		ITEMS.register(modBus);
		TABBLADEN.register(modBus);
	}

	private ModItems() {}
}
