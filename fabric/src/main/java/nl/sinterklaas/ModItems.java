package nl.sinterklaas;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.item.GedichtjeItem;
import nl.sinterklaas.item.PepernootItem;
import nl.sinterklaas.item.StafItem;

public final class ModItems {
	// Blokken als voorwerp
	public static final Item PAKJE = blokItem(Namen.PAKJE, ModBlokken.PAKJE);
	public static final Item SCHOENPAKJE = blokItem(Namen.SCHOENPAKJE, ModBlokken.SCHOENPAKJE);
	public static final Item SCHOEN = blokItem(Namen.SCHOEN, ModBlokken.SCHOEN);
	public static final Item BORD = blokItem(Namen.BORD, ModBlokken.BORD);

	// Lekkers
	/** Zelfde voedingswaarde als een vanilla koekje, altijd eetbaar, en een half hartje erbij (zie PepernootItem). */
	public static final Item PEPERNOOT = registreer(Namen.PEPERNOOT,
		p -> new PepernootItem(p.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).alwaysEdible().build())));
	public static final Item CHOCOLADELETTER = registreer(Namen.CHOCOLADELETTER,
		p -> new Item(p.stacksTo(16).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.5F).alwaysEdible().build())));
	public static final Item ZAKJE_PEPERNOTEN = registreer(Namen.ZAKJE_PEPERNOTEN, p -> new Item(p.stacksTo(16)));

	// Overig
	/**
	 * De mijter draag je op je hoofd (helm-slot). Hij beschermt net zo goed als een ijzeren helm en slijt niet.
	 * Er zit een lange witte baard aan vast: Sint draagt hem, en een speler met de mijter op krijgt ook een baard.
	 */
	public static final double MIJTER_BESCHERMING = 2.0;
	public static final Item MIJTER = registreer(Namen.MIJTER,
		p -> new Item(p.stacksTo(1)
			.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build())
			.attributes(ItemAttributeModifiers.builder()
				.add(Attributes.ARMOR,
					new AttributeModifier(SinterklaasMod.id("mijter"), MIJTER_BESCHERMING, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.HEAD)
				.build())));
	/** De staf van Sint: rechtsklik roept de Pieten in de buurt. */
	public static final Item STAF = registreer(Namen.STAF, p -> new StafItem(p.stacksTo(1)));
	public static final Item LINT = registreer(Namen.LINT, Item::new);
	public static final Item GEDICHTJE = registreer(Namen.GEDICHTJE, p -> new GedichtjeItem(p.stacksTo(16)));

	// Spawn-eieren: één per Piet-variant, plus Sint
	public static final Map<PietVariant, Item> PIET_EIEREN = new EnumMap<>(PietVariant.class);
	static {
		for (PietVariant variant : PietVariant.values()) {
			PIET_EIEREN.put(variant, registreer("piet_" + variant.id() + "_spawn_egg", p -> {
				CompoundTag gegevens = new CompoundTag();
				gegevens.putString("variant", variant.id());
				return new SpawnEggItem(p.spawnEgg(ModEntiteiten.PIET)
					.component(DataComponents.ENTITY_DATA, TypedEntityData.of(ModEntiteiten.PIET, gegevens)));
			}));
		}
	}
	public static final Item SINT_EI = registreer("sint_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(ModEntiteiten.SINT)));

	public static final CreativeModeTab TABBLAD = Registry.register(
		BuiltInRegistries.CREATIVE_MODE_TAB,
		SinterklaasMod.id("sinterklaas"),
		FabricCreativeModeTab.builder()
			.title(Component.translatable("itemGroup.sinterklaas"))
			.icon(() -> new ItemStack(PAKJE))
			.displayItems((parameters, uitvoer) -> {
				for (Item item : new Item[] {PAKJE, SCHOENPAKJE, SCHOEN, BORD, PEPERNOOT, ZAKJE_PEPERNOTEN,
						CHOCOLADELETTER, MIJTER, STAF, LINT, GEDICHTJE, SINT_EI}) {
					uitvoer.accept(new ItemStack(item));
				}
				for (Item ei : PIET_EIEREN.values()) uitvoer.accept(new ItemStack(ei));
			})
			.build()
	);

	private static Item blokItem(String naam, Block blok) {
		return registreer(naam, p -> new BlockItem(blok, p.useBlockDescriptionPrefix()));
	}

	private static Item registreer(String naam, Function<Item.Properties, Item> maker) {
		ResourceKey<Item> sleutel = ResourceKey.create(Registries.ITEM, SinterklaasMod.id(naam));
		return Registry.register(BuiltInRegistries.ITEM, sleutel, maker.apply(new Item.Properties().setId(sleutel)));
	}

	static void registreer() {}

	private ModItems() {}
}
