package nl.sinterklaas.test;

import java.time.LocalDate;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.AfterBatch;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import nl.sinterklaas.KistLoot;
import nl.sinterklaas.ModBlokken;
import nl.sinterklaas.ModEntiteiten;
import nl.sinterklaas.ModItems;
import nl.sinterklaas.PietenpakBonus;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.blok.BordBlok;
import nl.sinterklaas.blok.PakjeBlok;
import nl.sinterklaas.blok.SchoenBlok;
import nl.sinterklaas.core.Namen;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.core.PietenpakRegels;
import nl.sinterklaas.entiteit.Piet;
import nl.sinterklaas.entiteit.Sint;
import nl.sinterklaas.item.PepernootItem;
import nl.sinterklaas.item.StafItem;

/**
 * Speelt de spelregels na op een echte Forge-server: ./gradlew runGameTestServer
 * Elke groep ('batch') speelt op een eigen testdatum; de groepen draaien na elkaar.
 */
@GameTestHolder(Namen.MOD_ID)
@PrefixGameTestTemplate(false)
public class SpelTest {
	private static final String RUIMTE = "leeg";
	private static final BlockPos VLOER = new BlockPos(2, 1, 2);
	private static final BlockPos PLEK = new BlockPos(2, 2, 2);

	// ------------------------------------------------------------ testdatums

	@BeforeBatch(batch = "dec4")
	public static void voorDec4(ServerLevel wereld) {
		SinterklaasMod.KLOK.zetTestDatum(LocalDate.of(2026, 12, 4));
	}

	@BeforeBatch(batch = "dec5")
	public static void voorDec5(ServerLevel wereld) {
		SinterklaasMod.KLOK.zetTestDatum(LocalDate.of(2026, 12, 5));
	}

	@BeforeBatch(batch = "dec6")
	public static void voorDec6(ServerLevel wereld) {
		SinterklaasMod.KLOK.zetTestDatum(LocalDate.of(2026, 12, 6));
	}

	@AfterBatch(batch = "dec4")
	public static void naDec4(ServerLevel wereld) {
		SinterklaasMod.KLOK.reset();
	}

	@AfterBatch(batch = "dec5")
	public static void naDec5(ServerLevel wereld) {
		SinterklaasMod.KLOK.reset();
	}

	@AfterBatch(batch = "dec6")
	public static void naDec6(ServerLevel wereld) {
		SinterklaasMod.KLOK.reset();
	}

	// ------------------------------------------------------------ hulpjes

	private static void zetOpVloer(GameTestHelper test, Block blok) {
		test.setBlock(VLOER, Blocks.STONE);
		test.setBlock(PLEK, blok);
	}

	/** Een nepspeler die midden in de testruimte staat. */
	private static Player speler(GameTestHelper test) {
		Player speler = test.makeMockPlayer();
		Vec3 plek = test.absoluteVec(new Vec3(2.5, 2, 3.5));
		speler.setPos(plek.x, plek.y, plek.z);
		return speler;
	}

	/** Rechtsklik op het blok op PLEK, met wat de speler in zijn hoofdhand heeft. */
	private static void klik(GameTestHelper test, Player speler) {
		BlockPos plek = test.absolutePos(PLEK);
		BlockState staat = test.getLevel().getBlockState(plek);
		staat.use(test.getLevel(), speler, InteractionHand.MAIN_HAND,
			new BlockHitResult(Vec3.atCenterOf(plek), Direction.UP, plek, false));
	}

	private static void ok(GameTestHelper test, boolean voorwaarde, String wat) {
		if (!voorwaarde) test.fail(wat);
	}

	private static int aantalInInventaris(Player speler, Item item) {
		return speler.getInventory().countItem(item);
	}

	private static List<ItemEntity> gevallen(GameTestHelper test) {
		return test.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(test.absolutePos(PLEK)).inflate(2.5));
	}

	// ------------------------------------------------------------ pakjes en de datum

	@GameTest(template = RUIMTE, batch = "dec4")
	public static void pakjeBlijftDichtOpVierDecember(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.PAKJE.get());
		klik(test, speler(test));
		test.runAfterDelay(PakjeBlok.OPEN_TIKKEN + 5, () -> {
			test.assertBlockPresent(ModBlokken.PAKJE.get(), PLEK);
			test.assertBlockProperty(PLEK, PakjeBlok.OPEN, false);
			test.succeed();
		});
	}

	@GameTest(template = RUIMTE, batch = "dec4")
	public static void schoenpakjeMagAltijdOpen(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.SCHOENPAKJE.get());
		klik(test, speler(test));
		test.assertBlockProperty(PLEK, PakjeBlok.OPEN, true);
		test.runAfterDelay(PakjeBlok.OPEN_TIKKEN + 5, () -> {
			test.assertBlockNotPresent(ModBlokken.SCHOENPAKJE.get(), PLEK);
			test.succeed();
		});
	}

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void pakjeGaatOpenOpVijfDecember(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.PAKJE.get());
		klik(test, speler(test));
		test.assertBlockProperty(PLEK, PakjeBlok.OPEN, true);
		test.runAfterDelay(PakjeBlok.OPEN_TIKKEN + 5, () -> {
			test.assertBlockNotPresent(ModBlokken.PAKJE.get(), PLEK);
			// Er komt altijd iets uit (alleen het zeldzame gedichtje gaat naar de chat, maar dat zit er naast).
			ok(test, !gevallen(test).isEmpty(), "er kwam niets uit het pakje");
			test.succeed();
		});
	}

	// ------------------------------------------------------------ schoen

	@GameTest(template = RUIMTE, batch = "dec5", timeoutTicks = 300)
	public static void schoenMetWortelGeeftPakje(GameTestHelper test) {
		zetOpVloer(test, ModBlokken.SCHOEN.get());
		Player speler = speler(test);
		speler.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CARROT, 3));
		klik(test, speler);
		test.assertBlockProperty(PLEK, SchoenBlok.INHOUD, SchoenBlok.Inhoud.WORTEL);
		ok(test, speler.getMainHandItem().getCount() == 2, "de wortel is niet uit de hand gegaan");
		// Een tweede wortel past er niet bij.
		klik(test, speler);
		ok(test, speler.getMainHandItem().getCount() == 2, "er ging een tweede wortel in de schoen");

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

	@GameTest(template = RUIMTE, batch = "dec5", timeoutTicks = 300)
	public static void schoenZonderWortelGeeftBriefje(GameTestHelper test) {
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
		ServerLevel wereld = test.getLevel();
		wereld.setDayTime(wereld.getDayTime() + 24000L);
	}

	// ------------------------------------------------------------ bord en pepernoten

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void bordMetPepernoten(GameTestHelper test) {
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

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void pepernootGeeftHalfHartje(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Sint eter = test.spawn(ModEntiteiten.SINT.get(), PLEK);
		eter.setHealth(10.0F);
		ItemStack noot = new ItemStack(ModItems.PEPERNOOT.get());
		ok(test, noot.isEdible() && noot.getFoodProperties(eter).canAlwaysEat(), "een pepernoot moet je altijd kunnen eten");
		noot.getItem().finishUsingItem(noot, test.getLevel(), eter);
		ok(test, eter.getHealth() == 10.0F + PepernootItem.GENEZING, "de pepernoot gaf geen half hartje");
		test.succeed();
	}

	// ------------------------------------------------------------ Pieten en Sint

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void uitElkEiKomtDeGoedePiet(GameTestHelper test) {
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
		ok(test, Piet.variantUitEi == null, "de variant van het ei bleef hangen");
		pieten.forEach(Piet::discard);
		test.succeed();
	}

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void uitHetGewoneEiKomtEenWillekeurigePiet(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Player speler = speler(test);
		BlockPos vloer = test.absolutePos(VLOER);
		for (int n = 0; n < 30; n++) {
			ItemStack ei = new ItemStack(ModItems.PIET_EI.get());
			speler.setItemInHand(InteractionHand.MAIN_HAND, ei);
			ei.useOn(new UseOnContext(speler, InteractionHand.MAIN_HAND,
				new BlockHitResult(Vec3.atCenterOf(vloer), Direction.UP, vloer, false)));
		}
		List<Piet> pieten = test.getLevel().getEntitiesOfClass(Piet.class, new AABB(vloer).inflate(2.0));
		ok(test, pieten.size() == 30, "niet uit elk gewoon ei kwam een Piet: " + pieten.size());
		long soorten = pieten.stream().map(Piet::variant).distinct().count();
		ok(test, soorten >= 3, "het gewone Piet-ei gaf te weinig verschillende Pieten: " + soorten);
		pieten.forEach(Piet::discard);
		test.succeed();
	}

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void pietLaatIetsVallen(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Piet piet = test.spawn(ModEntiteiten.PIET.get(), PLEK);
		piet.setNoAi(true);
		piet.laatNuIetsVallen();
		test.succeedWhen(() -> ok(test, !gevallen(test).isEmpty(), "de Piet liet niets vallen"));
	}

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void stafRoeptPiet(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Piet piet = test.spawn(ModEntiteiten.PIET.get(), PLEK);
		Player speler = speler(test);
		ok(test, piet.roeper() == null, "de Piet werd al geroepen");
		int geroepen = StafItem.roepPieten(test.getLevel(), speler);
		ok(test, geroepen >= 1, "de staf riep niemand");
		ok(test, piet.roepTikkenOver() > 0, "de Piet hoorde de staf niet");
		test.succeed();
	}

	@GameTest(template = RUIMTE, batch = "dec5", timeoutTicks = 200)
	public static void sintDraagtMijterEnStaf(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Sint sint = test.spawn(ModEntiteiten.SINT.get(), PLEK);
		sint.setNoAi(true);
		test.succeedWhen(() -> {
			ok(test, sint.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MIJTER.get()), "Sint heeft geen mijter op");
			ok(test, sint.getMainHandItem().is(ModItems.STAF.get()), "Sint heeft geen staf");
			ok(test, sint.getAttributeValue(Attributes.ARMOR) == 2.0, "de mijter beschermt niet als een ijzeren helm");
		});
	}

	// ------------------------------------------------------------ seizoen

	@GameTest(template = RUIMTE, batch = "dec5", timeoutTicks = 250)
	public static void opVijfDecemberBlijftIedereen(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.STONE);
		Piet piet = test.spawn(ModEntiteiten.PIET.get(), PLEK);
		piet.setNoAi(true);
		test.runAfterDelay(220, () -> {
			ok(test, piet.isAlive(), "de Piet vertrok al op 5 december");
			test.succeed();
		});
	}

	@GameTest(template = RUIMTE, batch = "dec6", timeoutTicks = 250)
	public static void opZesDecemberVertrektIedereen(GameTestHelper test) {
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

	@GameTest(template = RUIMTE, batch = "dec6")
	public static void buitenHetSeizoenSpawntNiemandVanzelf(GameTestHelper test) {
		test.setBlock(VLOER, Blocks.GRASS_BLOCK);
		BlockPos plek = test.absolutePos(PLEK);
		for (int i = 0; i < 200; i++) {
			ok(test, !nl.sinterklaas.entiteit.SinterklaasFiguur.magHierSpawnen(ModEntiteiten.PIET.get(), test.getLevel(),
				MobSpawnType.NATURAL, plek, test.getLevel().getRandom()), "er spawnde vanzelf een Piet op 6 december");
		}
		test.succeed();
	}

	// ------------------------------------------------------------ data: loot en recepten

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void lootEnReceptenZijnGeladen(GameTestHelper test) {
		var server = test.getLevel().getServer();
		for (ResourceLocation tabel : List.of(PakjeBlok.INHOUD, Piet.CADEAU, KistLoot.KIST_EXTRA,
				SinterklaasMod.id("blocks/pakje"), SinterklaasMod.id("blocks/schoenpakje"),
				SinterklaasMod.id("blocks/schoen"), SinterklaasMod.id("blocks/bord"))) {
			ok(test, server.getLootData().getLootTable(tabel) != LootTable.EMPTY, "loot-table ontbreekt: " + tabel);
		}
		for (String recept : List.of("pepernoot", "lint", "pakje", "zakje_pepernoten", "pepernoten_uit_zakje",
				"chocoladeletter", "schoen", "bord", "staf")) {
			ok(test, server.getRecipeManager().byKey(SinterklaasMod.id(recept)).isPresent(), "recept ontbreekt: " + recept);
		}
		for (ResourceLocation kist : List.of(BuiltInLootTables.VILLAGE_PLAINS_HOUSE, BuiltInLootTables.SIMPLE_DUNGEON,
				BuiltInLootTables.ABANDONED_MINESHAFT)) {
			ok(test, server.getLootData().getLootTable(kist).getPool("sinterklaas_pakje") != null, "geen pakje in kist: " + kist);
		}
		test.succeed();
	}

	// ------------------------------------------------------------ Pietenpak

	@GameTest(template = RUIMTE, batch = "dec5")
	public static void pietenpakBeschermtAlsIJzerEnGeeftBonus(GameTestHelper test) {
		var server = test.getLevel().getServer();
		for (PietVariant v : PietVariant.values()) {
			for (PietenpakRegels.Deel deel : PietenpakRegels.Deel.values()) {
				String naam = PietenpakRegels.naam(v, deel);
				ok(test, ModItems.PIETENPAK.get(v).get(deel).get() instanceof ArmorItem, "geen harnas: " + naam);
				ok(test, server.getRecipeManager().byKey(SinterklaasMod.id(naam)).isPresent(), "recept ontbreekt: " + naam);
			}
		}
		test.setBlock(VLOER, Blocks.STONE);
		ArmorStand standaard = test.spawn(EntityType.ARMOR_STAND, PLEK);
		EquipmentSlot[] plekken = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		PietVariant[] pieten = {PietVariant.HOOFD, PietVariant.PAKJES, PietVariant.KOEK, PietVariant.MALLE};
		for (int i = 0; i < 4; i++) {
			standaard.setItemSlot(plekken[i], new ItemStack(ModItems.PIETENPAK.get(pieten[i]).get(PietenpakRegels.Deel.values()[i]).get()));
		}
		ok(test, PietenpakBonus.heeftHeelPak(standaard), "een pak van verschillende Pieten telt niet als heel pak");
		PietenpakBonus.geefBonus(standaard);
		ok(test, standaard.hasEffect(MobEffects.MOVEMENT_SPEED) && standaard.hasEffect(MobEffects.JUMP), "geen snelheid en sprongkracht");
		standaard.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		ok(test, !PietenpakBonus.heeftHeelPak(standaard), "met een ijzeren helm telt het toch als heel pak");
		test.succeedWhen(() -> ok(test, standaard.getAttributeValue(Attributes.ARMOR) == 15.0,
			"het Pietenpak beschermt niet als ijzer (" + standaard.getAttributeValue(Attributes.ARMOR) + ")"));
	}

	private static int aantalGevallen(GameTestHelper test, Item item) {
		return gevallen(test).stream().filter(e -> e.getItem().is(item)).mapToInt(e -> e.getItem().getCount()).sum();
	}

	/** Een schoen met een wortel geeft de schoen en de wortel terug (geen pakje); een bord geeft zijn pepernoten terug. */
	@GameTest(template = RUIMTE, batch = "dec5")
	public static void afbrekenGeeftDeInhoudTerug(GameTestHelper test) {
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

	/** De kans is 1 op 10 per kist; in 300 kisten zit er dus (vrijwel) zeker een pakje, maar lang niet in elke. */
	@GameTest(template = RUIMTE, batch = "dec5")
	public static void somsZitErEenPakjeInEenKist(GameTestHelper test) {
		var server = test.getLevel().getServer();
		LootParams params = new LootParams.Builder(test.getLevel())
			.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(test.absolutePos(PLEK)))
			.create(LootContextParamSets.CHEST);
		for (ResourceLocation kist : List.of(BuiltInLootTables.VILLAGE_PLAINS_HOUSE, BuiltInLootTables.SIMPLE_DUNGEON,
				BuiltInLootTables.ABANDONED_MINESHAFT)) {
			int pakjes = 0;
			for (int i = 0; i < 300; i++) {
				for (ItemStack stapel : server.getLootData().getLootTable(kist).getRandomItems(params)) {
					if (stapel.is(ModItems.PAKJE.get())) pakjes++;
				}
			}
			ok(test, pakjes > 0, "nooit een pakje in kist " + kist);
			ok(test, pakjes < 100, "veel te vaak een pakje in kist " + kist + ": " + pakjes);
		}
		test.succeed();
	}

	/** De inhoud van 200 pakjes: pepernoten komen met 4 tot 8 tegelijk, en een gedichtje is zeldzaam. */
	@GameTest(template = RUIMTE, batch = "dec5")
	public static void inEenPakjeZitMeestalLekkers(GameTestHelper test) {
		LootParams params = new LootParams.Builder(test.getLevel())
			.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(test.absolutePos(PLEK)))
			.create(LootContextParamSets.CHEST);
		int gedichtjes = 0;
		for (int i = 0; i < 200; i++) {
			for (ItemStack stapel : test.getLevel().getServer().getLootData().getLootTable(PakjeBlok.INHOUD).getRandomItems(params)) {
				if (stapel.is(ModItems.GEDICHTJE.get())) gedichtjes++;
				if (stapel.is(ModItems.PEPERNOOT.get())) {
					ok(test, stapel.getCount() >= 4 && stapel.getCount() <= 8, "verkeerd aantal pepernoten in een pakje: " + stapel.getCount());
				}
			}
		}
		ok(test, gedichtjes < 40, "veel te vaak een gedichtje: " + gedichtjes + " van de 200");
		test.succeed();
	}
}
