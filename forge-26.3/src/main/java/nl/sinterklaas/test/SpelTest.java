package nl.sinterklaas.test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import nl.sinterklaas.KistLoot;
import nl.sinterklaas.ModBlokken;
import nl.sinterklaas.ModEntiteiten;
import nl.sinterklaas.ModItems;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.blok.BordBlok;
import nl.sinterklaas.blok.PakjeBlok;
import nl.sinterklaas.blok.SchoenBlok;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.entiteit.Piet;
import nl.sinterklaas.entiteit.Sint;
import nl.sinterklaas.entiteit.SinterklaasFiguur;
import nl.sinterklaas.item.PepernootItem;
import nl.sinterklaas.item.StafItem;

/**
 * Speelt de spelregels na op een echte Forge-server: ./gradlew :forge-26.3:runGameTestServer
 *
 * Deze klasse zit niet in de mod-jar en wordt alleen geladen als speltests aan staan.
 * Bij elke test hier hoort een bestand in data/sinterklaas/test_instance/ (welke testdatum, hoe lang);
 * die bestanden maakt tools/maak_assets.py. De testdatums zelf staan in data/sinterklaas/test_environment/.
 */
public final class SpelTest {
	private static final DeferredRegister<Consumer<GameTestHelper>> TESTS = DeferredRegister.create(Registries.TEST_FUNCTION, Namen.MOD_ID);
	static {
		test("pakje_blijft_dicht_op_vier_december", SpelTest::pakjeBlijftDichtOpVierDecember);
		test("schoenpakje_mag_altijd_open", SpelTest::schoenpakjeMagAltijdOpen);
		test("pakje_gaat_open_op_vijf_december", SpelTest::pakjeGaatOpenOpVijfDecember);
		test("schoen_met_wortel_geeft_pakje", SpelTest::schoenMetWortelGeeftPakje);
		test("schoen_zonder_wortel_geeft_briefje", SpelTest::schoenZonderWortelGeeftBriefje);
		test("bord_met_pepernoten", SpelTest::bordMetPepernoten);
		test("pepernoot_geeft_half_hartje", SpelTest::pepernootGeeftHalfHartje);
		test("uit_elk_ei_komt_de_goede_piet", SpelTest::uitElkEiKomtDeGoedePiet);
		test("piet_laat_iets_vallen", SpelTest::pietLaatIetsVallen);
		test("staf_roept_piet", SpelTest::stafRoeptPiet);
		test("sint_draagt_mijter_en_staf", SpelTest::sintDraagtMijterEnStaf);
		test("op_vijf_december_blijft_iedereen", SpelTest::opVijfDecemberBlijftIedereen);
		test("op_zes_december_vertrekt_iedereen", SpelTest::opZesDecemberVertrektIedereen);
		test("buiten_het_seizoen_spawnt_niemand_vanzelf", SpelTest::buitenHetSeizoenSpawntNiemandVanzelf);
		test("loot_en_recepten_zijn_geladen", SpelTest::lootEnReceptenZijnGeladen);
		test("soms_zit_er_een_pakje_in_een_kist", SpelTest::somsZitErEenPakjeInEenKist);
		test("afbreken_geeft_de_inhoud_terug", SpelTest::afbrekenGeeftDeInhoudTerug);
		test("in_een_pakje_zit_meestal_lekkers", SpelTest::inEenPakjeZitMeestalLekkers);
	}

	private static void test(String naam, Consumer<GameTestHelper> test) {
		TESTS.register(naam, () -> test);
	}

	/** Aangeroepen door SinterklaasMod als speltests aan staan. */
	public static void registreer(BusGroup modBus) {
		TESTS.register(modBus);
	}

	private static final BlockPos VLOER = new BlockPos(2, 0, 2);
	private static final BlockPos PLEK = new BlockPos(2, 1, 2);

	// ------------------------------------------------------------ hulpjes

	private static void zetOpVloer(GameTestHelper test, Block blok) {
		test.setBlock(VLOER, Blocks.STONE);
		test.setBlock(PLEK, blok);
	}

	/** Een nepspeler die midden in de testruimte staat. */
	private static Player speler(GameTestHelper test) {
		Player speler = test.makeMockPlayer(GameType.SURVIVAL);
		Vec3 plek = test.absoluteVec(new Vec3(2.5, 1, 3.5));
		speler.setPos(plek.x, plek.y, plek.z);
		return speler;
	}

	/** Rechtsklik op het blok op PLEK, met wat de speler in zijn hoofdhand heeft. */
	private static void klik(GameTestHelper test, Player speler) {
		test.useBlock(PLEK, speler);
	}

	private static void ok(GameTestHelper test, boolean voorwaarde, String wat) {
		test.assertTrue(voorwaarde, wat);
	}

	private static int aantalInInventaris(Player speler, Item item) {
		return speler.getInventory().countItem(item);
	}

	private static List<ItemEntity> gevallen(GameTestHelper test) {
		return test.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(test.absolutePos(PLEK)).inflate(2.5));
	}

	// ------------------------------------------------------------ pakjes en de datum

	private static void pakjeBlijftDichtOpVierDecember(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.PAKJE.get());
		klik(test, speler(test));
		test.runAfterDelay(PakjeBlok.OPEN_TIKKEN + 5, () -> {
			test.assertBlockPresent(ModBlokken.PAKJE.get(), PLEK);
			test.assertBlockProperty(PLEK, PakjeBlok.OPEN, false);
			test.succeed();
		});
	}

	private static void schoenpakjeMagAltijdOpen(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.SCHOENPAKJE.get());
		klik(test, speler(test));
		test.assertBlockProperty(PLEK, PakjeBlok.OPEN, true);
		test.runAfterDelay(PakjeBlok.OPEN_TIKKEN + 5, () -> {
			test.assertBlockNotPresent(ModBlokken.SCHOENPAKJE.get(), PLEK);
			test.succeed();
		});
	}

	private static void pakjeGaatOpenOpVijfDecember(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.PAKJE.get());
		klik(test, speler(test));
		test.assertBlockProperty(PLEK, PakjeBlok.OPEN, true);
		test.runAfterDelay(PakjeBlok.OPEN_TIKKEN + 5, () -> {
			test.assertBlockNotPresent(ModBlokken.PAKJE.get(), PLEK);
			ok(test, !gevallen(test).isEmpty(), "er kwam niets uit het pakje");
			test.succeed();
		});
	}

	// ------------------------------------------------------------ schoen

	private static void schoenMetWortelGeeftPakje(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.SCHOEN.get());
		Player speler = speler(test);
		speler.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CARROT, 3));
		klik(test, speler);
		test.assertBlockProperty(PLEK, SchoenBlok.INHOUD, SchoenBlok.Inhoud.WORTEL);
		ok(test, speler.getMainHandItem().getCount() == 2, "de wortel is niet uit de hand gegaan");

		test.runAfterDelay(5, () -> volgendeOchtend(test));
		test.succeedWhen(() -> {
			test.assertBlockProperty(PLEK, SchoenBlok.INHOUD, SchoenBlok.Inhoud.PAKJE);
			// Lege hand: het pakje uit de schoen pakken.
			Player pakker = speler(test);
			klik(test, pakker);
			test.assertBlockProperty(PLEK, SchoenBlok.INHOUD, SchoenBlok.Inhoud.LEEG);
			ok(test, aantalInInventaris(pakker, ModItems.SCHOENPAKJE.get()) == 1, "geen schoenpakje gekregen");
		});
	}

	private static void schoenZonderWortelGeeftBriefje(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.SCHOEN.get());
		test.runAfterDelay(5, () -> volgendeOchtend(test));
		test.succeedWhen(() -> {
			test.assertBlockProperty(PLEK, SchoenBlok.INHOUD, SchoenBlok.Inhoud.BRIEFJE);
			klik(test, speler(test));
			test.assertBlockProperty(PLEK, SchoenBlok.INHOUD, SchoenBlok.Inhoud.LEEG);
		});
	}

	/** Zet de klok een Minecraft-dag verder, net als een nacht slapen. */
	private static void volgendeOchtend(GameTestHelper test) {
		MinecraftServer server = test.getLevel().getServer();
		server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "time add 24000");
	}

	// ------------------------------------------------------------ bord en pepernoten

	private static void bordMetPepernoten(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.BORD.get());
		Player speler = speler(test);
		speler.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.PEPERNOOT.get(), 6));
		for (int i = 0; i < BordBlok.MAX; i++) klik(test, speler);
		test.assertBlockProperty(PLEK, BordBlok.PEPERNOTEN, BordBlok.MAX);
		ok(test, speler.getMainHandItem().getCount() == 2, "er gingen niet precies vier pepernoten op het bord");

		Player legeHand = speler(test);
		klik(test, legeHand);
		test.assertBlockProperty(PLEK, BordBlok.PEPERNOTEN, BordBlok.MAX - 1);
		ok(test, aantalInInventaris(legeHand, ModItems.PEPERNOOT.get()) == 1, "geen pepernoot van het bord gekregen");
		test.succeed();
	}

	private static void pepernootGeeftHalfHartje(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Sint eter = test.spawn(ModEntiteiten.SINT.get(), PLEK);
		eter.setHealth(10.0F);
		ItemStack noot = new ItemStack(ModItems.PEPERNOOT.get());
		var eten = noot.get(DataComponents.FOOD);
		ok(test, eten != null && eten.canAlwaysEat(), "een pepernoot moet je altijd kunnen eten");
		noot.getItem().finishUsingItem(noot, test.getLevel(), eter);
		ok(test, eter.getHealth() == 10.0F + PepernootItem.GENEZING, "de pepernoot gaf geen half hartje");
		test.succeed();
	}

	// ------------------------------------------------------------ Pieten en Sint

	private static void uitElkEiKomtDeGoedePiet(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Player speler = speler(test);
		BlockPos vloer = test.absolutePos(VLOER);
		for (PietVariant variant : PietVariant.values()) {
			ItemStack ei = new ItemStack(ModItems.PIET_EIEREN.get(variant).get());
			speler.setItemInHand(InteractionHand.MAIN_HAND, ei);
			ei.useOn(new UseOnContext(speler, InteractionHand.MAIN_HAND,
				new BlockHitResult(Vec3.atCenterOf(vloer), Direction.UP, vloer, false)));
		}
		List<Piet> pieten = test.getLevel().getEntitiesOfClass(Piet.class, new AABB(vloer).inflate(2.0));
		ok(test, pieten.size() == PietVariant.values().length, "niet uit elk ei kwam een Piet: " + pieten.size());
		for (PietVariant variant : PietVariant.values()) {
			ok(test, pieten.stream().filter(p -> p.variant() == variant).count() == 1, "geen (of meer dan één) " + variant.id());
		}
		pieten.forEach(Piet::discard);
		test.succeed();
	}

	private static void pietLaatIetsVallen(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Piet piet = test.spawn(ModEntiteiten.PIET.get(), PLEK);
		piet.setNoAi(true);
		piet.laatNuIetsVallen();
		test.succeedWhen(() -> ok(test, !gevallen(test).isEmpty(), "de Piet liet niets vallen"));
	}

	private static void stafRoeptPiet(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Piet piet = test.spawn(ModEntiteiten.PIET.get(), PLEK);
		Player speler = speler(test);
		ok(test, piet.roepTikkenOver() == 0, "de Piet werd al geroepen");
		int geroepen = StafItem.roepPieten(test.getLevel(), speler);
		ok(test, geroepen >= 1, "de staf riep niemand");
		ok(test, piet.roepTikkenOver() > 0, "de Piet hoorde de staf niet");
		test.succeed();
	}

	private static void sintDraagtMijterEnStaf(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Sint sint = test.spawn(ModEntiteiten.SINT.get(), PLEK);
		sint.setNoAi(true);
		test.succeedWhen(() -> {
			ok(test, sint.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MIJTER.get()), "Sint heeft geen mijter op");
			ok(test, sint.getMainHandItem().is(ModItems.STAF.get()), "Sint heeft geen staf");
			ok(test, sint.getAttributeValue(Attributes.ARMOR) == ModItems.MIJTER_BESCHERMING, "de mijter beschermt niet als een ijzeren helm");
		});
	}

	// ------------------------------------------------------------ seizoen

	private static void opVijfDecemberBlijftIedereen(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Piet piet = test.spawn(ModEntiteiten.PIET.get(), PLEK);
		piet.setNoAi(true);
		test.runAfterDelay(220, () -> {
			ok(test, piet.isAlive(), "de Piet vertrok al op 5 december");
			test.succeed();
		});
	}

	private static void opZesDecemberVertrektIedereen(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		// Deze figuren horen bij het seizoen van 2026: ze kwamen een dag eerder.
		SinterklaasMod.KLOK.zetTestDatum(LocalDate.of(2026, 12, 5));
		Piet piet = test.spawn(ModEntiteiten.PIET.get(), PLEK);
		Sint sint = test.spawn(ModEntiteiten.SINT.get(), PLEK);
		piet.setNoAi(true);
		sint.setNoAi(true);
		test.runAfterDelay(5, () -> SinterklaasMod.KLOK.zetTestDatum(LocalDate.of(2026, 12, 6)));
		test.succeedWhen(() -> ok(test, piet.isRemoved() && sint.isRemoved(), "Sint en Piet zijn er nog op 6 december"));
	}

	private static void buitenHetSeizoenSpawntNiemandVanzelf(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.GRASS_BLOCK);
		BlockPos plek = test.absolutePos(PLEK);
		for (int i = 0; i < 200; i++) {
			ok(test, !SinterklaasFiguur.magHierSpawnen(ModEntiteiten.PIET.get(), test.getLevel(),
				EntitySpawnReason.NATURAL, plek, test.getLevel().getRandom()), "er spawnde vanzelf een Piet op 6 december");
		}
		test.succeed();
	}

	// ------------------------------------------------------------ data: loot en recepten

	private static void lootEnReceptenZijnGeladen(GameTestHelper test) {
		MinecraftServer server = test.getLevel().getServer();
		for (ResourceKey<LootTable> tabel : List.of(PakjeBlok.INHOUD, Piet.CADEAU, KistLoot.KIST_EXTRA)) {
			ok(test, server.reloadableRegistries().getLootTable(tabel) != LootTable.EMPTY, "loot-table ontbreekt: " + tabel.identifier());
		}
		for (String blok : List.of("pakje", "schoenpakje", "schoen", "bord")) {
			ResourceKey<LootTable> tabel = ResourceKey.create(Registries.LOOT_TABLE, SinterklaasMod.id("blocks/" + blok));
			ok(test, server.reloadableRegistries().getLootTable(tabel) != LootTable.EMPTY, "loot-table ontbreekt: " + tabel.identifier());
		}
		for (String recept : List.of("pepernoot", "lint", "pakje", "zakje_pepernoten", "pepernoten_uit_zakje",
				"chocoladeletter", "schoen", "bord", "staf")) {
			ok(test, server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, SinterklaasMod.id(recept))).isPresent(),
				"recept ontbreekt: " + recept);
		}
		test.succeed();
	}

	/** De kans is 1 op 10 per kist; in 300 kisten zit er dus (vrijwel) zeker een pakje. */
	private static void somsZitErEenPakjeInEenKist(GameTestHelper test) {
		MinecraftServer server = test.getLevel().getServer();
		for (ResourceKey<LootTable> kist : List.of(BuiltInLootTables.VILLAGE_PLAINS_HOUSE, BuiltInLootTables.SIMPLE_DUNGEON,
				BuiltInLootTables.ABANDONED_MINESHAFT)) {
			LootParams params = new LootParams.Builder(test.getLevel())
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(test.absolutePos(PLEK)))
				.create(LootContextParamSets.CHEST);
			int pakjes = 0;
			for (int i = 0; i < 300; i++) {
				for (ItemStack stapel : server.reloadableRegistries().getLootTable(kist).getRandomItems(params)) {
					if (stapel.is(ModItems.PAKJE.get())) pakjes++;
				}
			}
			ok(test, pakjes > 0, "nooit een pakje in kist " + kist.identifier());
			ok(test, pakjes < 100, "veel te vaak een pakje in kist " + kist.identifier() + ": " + pakjes);
		}
		test.succeed();
	}

	private static int aantalGevallen(GameTestHelper test, Item item) {
		return gevallen(test).stream().filter(e -> e.getItem().is(item)).mapToInt(e -> e.getItem().getCount()).sum();
	}

	/** Een schoen met een wortel geeft de schoen en de wortel terug (geen pakje); een bord geeft zijn pepernoten terug. */
	private static void afbrekenGeeftDeInhoudTerug(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		test.setBlock(PLEK, ModBlokken.SCHOEN.get().defaultBlockState().setValue(SchoenBlok.INHOUD, SchoenBlok.Inhoud.WORTEL));
		test.getLevel().destroyBlock(test.absolutePos(PLEK), true);
		ok(test, aantalGevallen(test, ModItems.SCHOEN.get()) == 1, "de schoen viel niet");
		ok(test, aantalGevallen(test, Items.CARROT) == 1, "de wortel uit de schoen viel niet");
		ok(test, aantalGevallen(test, ModItems.SCHOENPAKJE.get()) == 0, "er viel een pakje uit een schoen zonder pakje");

		test.setBlock(PLEK, ModBlokken.BORD.get().defaultBlockState().setValue(BordBlok.PEPERNOTEN, 3));
		test.getLevel().destroyBlock(test.absolutePos(PLEK), true);
		ok(test, aantalGevallen(test, ModItems.BORD.get()) == 1, "het bord viel niet");
		ok(test, aantalGevallen(test, ModItems.PEPERNOOT.get()) == 3, "niet precies de drie pepernoten van het bord vielen");
		test.succeed();
	}

	/** De inhoud van 200 pakjes: pepernoten komen met 4 tot 8 tegelijk, en een gedichtje is zeldzaam. */
	private static void inEenPakjeZitMeestalLekkers(GameTestHelper test) {
		MinecraftServer server = test.getLevel().getServer();
		LootParams params = new LootParams.Builder(test.getLevel())
			.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(test.absolutePos(PLEK)))
			.create(LootContextParamSets.CHEST);
		int gedichtjes = 0;
		for (int i = 0; i < 200; i++) {
			for (ItemStack stapel : server.reloadableRegistries().getLootTable(PakjeBlok.INHOUD).getRandomItems(params)) {
				if (stapel.is(ModItems.GEDICHTJE.get())) gedichtjes++;
				if (stapel.is(ModItems.PEPERNOOT.get())) {
					ok(test, stapel.getCount() >= 4 && stapel.getCount() <= 8, "verkeerd aantal pepernoten in een pakje: " + stapel.getCount());
				}
			}
		}
		ok(test, gedichtjes < 40, "veel te vaak een gedichtje: " + gedichtjes + " van de 200");
		test.succeed();
	}

	private SpelTest() {}
}
