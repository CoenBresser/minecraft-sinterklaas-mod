package nl.sinterklaas.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.CameraType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import nl.sinterklaas.ModBlokken;
import nl.sinterklaas.ModEntiteiten;
import nl.sinterklaas.ModItems;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.blok.BordBlok;
import nl.sinterklaas.blok.PakjeBlok;
import nl.sinterklaas.blok.SchoenBlok;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.entiteit.Piet;
import nl.sinterklaas.entiteit.Sint;

/**
 * Automatische proef in een echte client. Controleert de spelregels van fase 2 in het spel zelf
 * en maakt screenshots. Elke geslaagde stap komt in het log als "PROEF OK: ...".
 */
public class ProefTest implements FabricClientGameTest {
	private static final int Y = 149;
	private final List<String> geslaagd = new ArrayList<>();

	private void ok(boolean voorwaarde, String wat) {
		if (!voorwaarde) throw new AssertionError("PROEF MISLUKT: " + wat);
		geslaagd.add(wat);
		SinterklaasMod.LOG.info("PROEF OK: {}", wat);
	}

	private static ServerPlayer speler(MinecraftServer s) {
		return s.getPlayerList().getPlayers().get(0);
	}

	private static BlockHitResult raak(BlockPos plek) {
		return new BlockHitResult(Vec3.atCenterOf(plek), Direction.UP, plek, false);
	}

	/** Rechtsklik met lege hand op een blok, aan de serverkant. */
	private static void klik(TestServerContext server, BlockPos plek) {
		server.runOnServer(s -> {
			ServerLevel w = s.overworld();
			w.getBlockState(plek).useWithoutItem(w, speler(s), raak(plek));
		});
	}

	private static BlockState staat(TestServerContext server, BlockPos plek) {
		return server.computeOnServer(s -> s.overworld().getBlockState(plek));
	}

	private static int voorwerpenBij(TestServerContext server, BlockPos plek) {
		return server.computeOnServer(s -> s.overworld().getEntitiesOfClass(ItemEntity.class, new AABB(plek).inflate(3)).size());
	}

	@Override
	public void runTest(ClientGameTestContext context) {
		// De proef draait in het Nederlands, zodat de screenshots de Nederlandse namen laten zien.
		var herladen = context.computeOnClient(mc -> {
			mc.options.languageCode = "nl_nl";
			mc.getLanguageManager().setSelected("nl_nl");
			return mc.reloadResourcePacks();
		});
		context.waitFor(mc -> herladen.isDone(), 1200);
		try (TestSingleplayerContext spel = context.worldBuilder().create()) {
			TestServerContext server = spel.getServer();
			server.runCommand("gamemode creative @p");
			server.runCommand("time set noon");
			server.runCommand("weather clear");
			server.runCommand("fill -9 148 -4 9 148 12 minecraft:smooth_stone");
			server.runCommand("tp @p 0.5 149 0.5 0 29");
			spel.getConnection().waitForChunksRender();

			// Snel één onderdeel bekijken tijdens het bouwen: PROEF_ALLEEN=staf ./gradlew :fabric:runClientGameTest
			if (System.getenv("PROEF_ALLEEN") != null && System.getenv("PROEF_ALLEEN").startsWith("staf")) {
				server.runOnServer(s -> ModEntiteiten.SINT.spawn(s.overworld(), new BlockPos(6, Y, 7), EntitySpawnReason.COMMAND).setNoAi(true));
				server.runCommand("tp @e[type=sinterklaas:sint] 6.5 149 7.5 180 0");
				staf(context, server);
				return;
			}

			datumregels(context, server);
			schoen(context, server);
			bord(server);
			pietenEnSint(context, server);
			staf(context, server);
			lootEnRecepten(server);
			mijter(context, server);
			seizoen(context, server);

			server.runCommand("sinterklaas datum reset");
			SinterklaasMod.LOG.info("PROEF KLAAR: {} stappen geslaagd", geslaagd.size());
		}
	}

	private void datumregels(ClientGameTestContext context, TestServerContext server) {
		BlockPos p = new BlockPos(0, Y, 2);

		// 4 december: dicht, met chatmelding (echte muisklik in de client)
		server.runCommand("sinterklaas datum 2026-12-04");
		server.runCommand("setblock 0 149 2 sinterklaas:pakje");
		context.waitTicks(10);
		context.getInput().pressMouse(1);
		context.waitTicks(30);
		ok(staat(server, p).is(ModBlokken.PAKJE) && !staat(server, p).getValue(PakjeBlok.OPEN),
			"4 december: pakje blijft dicht na rechtsklik");
		context.takeScreenshot("fase2-1-pakje-4-december-dicht");

		// 5 december: open, animatie, daarna weg met inhoud (echte muisklik)
		server.runCommand("sinterklaas datum 2026-12-05");
		context.waitTicks(5);
		context.getInput().pressMouse(1);
		context.waitTicks(6);
		ok(staat(server, p).is(ModBlokken.PAKJE) && staat(server, p).getValue(PakjeBlok.OPEN),
			"5 december: pakje klapt open na rechtsklik");
		context.takeScreenshot("fase2-2-pakje-5-december-open");
		context.waitTicks(PakjeBlok.OPEN_TIKKEN + 10);
		ok(staat(server, p).isAir(), "5 december: pakje verdwijnt na het openen");
		// In creative raapt de speler de inhoud direct op; daarom tellen we voorwerpen én inventaris.
		boolean inhoud = voorwerpenBij(server, p) > 0 || server.computeOnServer(s -> !speler(s).getInventory().isEmpty());
		ok(inhoud, "5 december: er komt inhoud uit het pakje");
		server.runCommand("clear @p");
		server.runCommand("kill @e[type=item]");

		// 23 december mag nog, 24 december (vlak voor kerst) niet meer, en ook niet in de zomer
		String[][] gevallen = {{"2026-12-23", "open"}, {"2026-12-24", "dicht"}, {"2027-07-01", "dicht"}, {"2027-12-05", "open"}};
		for (String[] geval : gevallen) {
			server.runCommand("sinterklaas datum " + geval[0]);
			server.runCommand("setblock 0 149 2 sinterklaas:pakje");
			klik(server, p);
			boolean open = staat(server, p).getValue(PakjeBlok.OPEN);
			ok(open == geval[1].equals("open"), geval[0] + ": pakje gaat " + (geval[1].equals("open") ? "open" : "niet open"));
			server.runCommand("setblock 0 149 2 air");
		}

		// Pakje uit een schoen mag altijd
		server.runCommand("sinterklaas datum 2026-07-01");
		server.runCommand("setblock 0 149 2 sinterklaas:schoenpakje");
		klik(server, p);
		ok(staat(server, p).getValue(PakjeBlok.OPEN), "1 juli: pakje uit een schoen gaat wel open");
		context.waitTicks(PakjeBlok.OPEN_TIKKEN + 10);
		ok(staat(server, p).isAir(), "pakje uit een schoen verdwijnt na het openen");

		// Ongeldige datum verandert niets; reset werkt
		server.runCommand("sinterklaas datum geen-datum");
		ok(SinterklaasMod.KLOK.vandaag().toString().equals("2026-07-01"), "ongeldige datum in het commando verandert de testdatum niet");
		server.runCommand("sinterklaas datum reset");
		ok(!SinterklaasMod.KLOK.heeftTestDatum(), "/sinterklaas datum reset zet de testdatum uit");
		server.runCommand("clear @p");
		server.runCommand("kill @e[type=item]");
	}

	private static SchoenBlok.Inhoud inhoud(TestServerContext server, BlockPos plek) {
		return staat(server, plek).getValue(SchoenBlok.INHOUD);
	}

	/** Rechtsklik op de schoen met een wortel in de hand, aan de serverkant. Geeft terug hoeveel wortels er over zijn. */
	private static int klikMetWortels(TestServerContext server, BlockPos plek, int aantal) {
		server.runCommand("item replace entity @p weapon.mainhand with minecraft:carrot " + aantal);
		int over = server.computeOnServer(s -> {
			ServerLevel w = s.overworld();
			ServerPlayer sp = speler(s);
			var uitkomst = w.getBlockState(plek).useItemOn(sp.getMainHandItem(), w, sp, InteractionHand.MAIN_HAND, raak(plek));
			if (uitkomst instanceof net.minecraft.world.InteractionResult.TryEmptyHandInteraction) {
				w.getBlockState(plek).useWithoutItem(w, sp, raak(plek));
			}
			return sp.getMainHandItem().getCount();
		});
		server.runCommand("item replace entity @p weapon.mainhand with air");
		return over;
	}

	private void schoen(ClientGameTestContext context, TestServerContext server) {
		BlockPos p = new BlockPos(-2, Y, 2);
		server.runCommand("gamemode survival @p");

		// 1. Schoen zomaar gezet, zonder wortel: de volgende ochtend een briefje, geen pakje
		server.runCommand("setblock -2 149 2 sinterklaas:schoen[facing=south]");
		context.waitTicks(120);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.LEEG, "schoen zonder wortel is dezelfde dag nog leeg");
		long dagVoor = server.computeOnServer(s -> SchoenBlok.dagNu(s.overworld()));
		server.runCommand("time add 24000");
		ok(server.computeOnServer(s -> SchoenBlok.dagNu(s.overworld())) == dagVoor + 1, "de klok is een Minecraft-dag verder gezet");
		context.waitTicks(120);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.BRIEFJE, "schoen zonder wortel: de volgende ochtend ligt er een briefje in");
		server.runCommand("tp @p -1.5 149 0.3 0 35");
		context.waitTicks(15);
		context.takeScreenshot("fase4-1-briefje-in-de-schoen");

		// Een wortel kan er nu niet bij: eerst het briefje lezen (echte muisklik met lege hand)
		context.getInput().pressMouse(1);
		context.waitTicks(15);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.LEEG, "rechtsklik leest het briefje en de schoen is weer leeg");
		ok(server.computeOnServer(s -> speler(s).getInventory().countItem(ModItems.SCHOENPAKJE)) == 0,
			"zonder wortel krijg je geen pakje");
		context.takeScreenshot("fase4-2-gedicht-van-piet");
		context.waitTicks(120);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.LEEG, "dezelfde dag komt er geen nieuw briefje (eenmalig per nacht)");

		// 2. Wortel erin met een rechtsklik: de volgende ochtend een pakje
		ok(klikMetWortels(server, p, 3) == 2, "rechtsklik met een wortel kost precies één wortel");
		ok(inhoud(server, p) == SchoenBlok.Inhoud.WORTEL, "de wortel ligt in de schoen");
		ok(klikMetWortels(server, p, 3) == 3, "een tweede wortel past er niet bij");
		context.waitTicks(15);
		context.takeScreenshot("fase4-3-wortel-in-de-schoen");
		context.waitTicks(120);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.WORTEL, "met wortel: dezelfde dag gebeurt er nog niets");
		server.runCommand("time add 24000");
		context.waitTicks(120);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.PAKJE, "met wortel: de volgende ochtend ligt er een pakje in");
		context.takeScreenshot("fase4-4-pakje-in-de-schoen");
		context.waitTicks(120);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.PAKJE, "er komt geen tweede pakje bij zolang het eerste erin ligt");

		klik(server, p);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.LEEG, "rechtsklik haalt het pakje eruit; de wortel is op");
		ok(server.computeOnServer(s -> speler(s).getInventory().countItem(ModItems.SCHOENPAKJE)) == 1,
			"de speler krijgt precies één pakje-uit-de-schoen");

		// 3. Zonder nieuwe wortel komt er de nacht erna weer een briefje, geen pakje
		server.runCommand("time add 24000");
		context.waitTicks(120);
		ok(inhoud(server, p) == SchoenBlok.Inhoud.BRIEFJE, "zonder nieuwe wortel ligt er de nacht erna weer een briefje");

		// 4. Afbreken met een wortel erin geeft de schoen en de wortel terug
		server.runCommand("clear @p");
		server.runCommand("kill @e[type=item]");
		server.runCommand("setblock -2 149 2 sinterklaas:schoen[facing=south,inhoud=wortel]");
		server.runCommand("setblock -2 149 2 air destroy");
		context.waitTicks(5);
		List<String> gevallen = server.computeOnServer(s -> {
			List<String> namen = new ArrayList<>();
			for (ItemEntity e : s.overworld().getEntitiesOfClass(ItemEntity.class, new AABB(p).inflate(3))) {
				namen.add(BuiltInRegistries.ITEM.getKey(e.getItem().getItem()).getPath() + "x" + e.getItem().getCount());
			}
			java.util.Collections.sort(namen);
			return namen;
		});
		ok(gevallen.equals(List.of("carrotx1", "schoenx1")),
			"een schoen met wortel afbreken geeft de schoen en de wortel terug");

		server.runCommand("setblock -2 149 2 sinterklaas:schoen[facing=south,inhoud=pakje]");
		server.runCommand("time set noon");
		server.runCommand("gamemode creative @p");
		server.runCommand("tp @p 0.5 149 0.5 0 29");
		server.runCommand("clear @p");
	}

	private void bord(TestServerContext server) {
		BlockPos p = new BlockPos(2, Y, 2);
		server.runCommand("setblock 2 149 2 sinterklaas:bord");
		server.runCommand("gamemode survival @p");
		server.runCommand("item replace entity @p weapon.mainhand with sinterklaas:pepernoot 5");
		for (int i = 0; i < 3; i++) {
			server.runOnServer(s -> {
				ServerLevel w = s.overworld();
				ServerPlayer sp = speler(s);
				w.getBlockState(p).useItemOn(sp.getMainHandItem(), w, sp, InteractionHand.MAIN_HAND, raak(p));
			});
		}
		ok(staat(server, p).getValue(BordBlok.PEPERNOTEN) == 3, "drie pepernoten op het bord gelegd");
		ok(server.computeOnServer(s -> speler(s).getMainHandItem().getCount()) == 2, "de pepernoten gaan uit de hand (5 - 3 = 2)");
		server.runCommand("item replace entity @p weapon.mainhand with air");
		klik(server, p);
		ok(staat(server, p).getValue(BordBlok.PEPERNOTEN) == 2, "met lege hand pak je er één van het bord");
		ok(server.computeOnServer(s -> speler(s).getInventory().countItem(ModItems.PEPERNOOT)) == 1, "die pepernoot komt in de inventaris");
		server.runCommand("gamemode creative @p");
		server.runCommand("clear @p");
		server.runCommand("setblock 2 149 2 sinterklaas:bord[pepernoten=4]");
	}

	private void pietenEnSint(ClientGameTestContext context, TestServerContext server) {
		// Elk spawn-ei geeft zijn eigen Piet
		int x = -6;
		for (PietVariant variant : PietVariant.values()) {
			BlockPos plek = new BlockPos(x, Y, 7);
			PietVariant gekregen = server.computeOnServer(s -> {
				ItemStack ei = new ItemStack(ModItems.PIET_EIEREN.get(variant));
				Piet piet = ModEntiteiten.PIET.spawn(s.overworld(), ei, speler(s), plek, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
				piet.setNoAi(true);
				piet.snapTo(plek.getX() + 0.5, Y, plek.getZ() + 0.5, 180, 0);
				piet.setYHeadRot(180);
				piet.setYBodyRot(180);
				return piet.variant();
			});
			ok(gekregen == variant, "spawn-ei " + variant.id() + " geeft de " + variant.id() + "-Piet");
			x += 2;
		}
		BlockPos sintPlek = new BlockPos(6, Y, 7);
		boolean sintEr = server.computeOnServer(s -> {
			Sint sint = ModEntiteiten.SINT.spawn(s.overworld(), new ItemStack(ModItems.SINT_EI), speler(s), sintPlek,
				EntitySpawnReason.SPAWN_ITEM_USE, false, false);
			sint.setNoAi(true);
			sint.snapTo(6.5, Y, 7.5, 180, 0);
			sint.setYHeadRot(180);
			sint.setYBodyRot(180);
			return sint.isAlive();
		});
		ok(sintEr, "spawn-ei van Sint geeft Sint");
		context.waitTicks(10);
		ok(server.computeOnServer(s -> s.overworld().getEntitiesOfClass(Sint.class, new AABB(sintPlek).inflate(1)).get(0)
			.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(ModItems.MIJTER)), "Sint draagt zijn mijter met baard");

		// Spawnen met ei of commando werkt ook buiten het seizoen
		server.runCommand("sinterklaas datum 2026-07-01");
		int voor = server.computeOnServer(s -> s.overworld().getEntitiesOfClass(Piet.class, new AABB(-20, 140, -20, 20, 160, 20)).size());
		server.runCommand("summon sinterklaas:piet 8 149 -2 {NoAI:1b}");
		int na = server.computeOnServer(s -> s.overworld().getEntitiesOfClass(Piet.class, new AABB(-20, 140, -20, 20, 160, 20)).size());
		ok(na == voor + 1, "op 1 juli kun je met een commando gewoon een Piet spawnen");
		server.runCommand("kill @e[type=sinterklaas:piet,x=8,y=149,z=-2,distance=..1.5]");
		server.runCommand("sinterklaas datum reset");

		// Variant blijft bewaard bij opslaan en laden
		boolean bewaard = server.computeOnServer(s -> {
			ServerLevel w = s.overworld();
			Piet bron = w.getEntitiesOfClass(Piet.class, new AABB(new BlockPos(2, Y, 7)).inflate(0.4)).get(0);
			net.minecraft.world.level.storage.TagValueOutput uit =
				net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, w.registryAccess());
			bron.saveWithoutId(uit);
			return bron.variant() == PietVariant.KOEK && "koek".equals(uit.buildResult().getStringOr("variant", ""));
		});
		ok(bewaard, "de variant van een Piet wordt opgeslagen");

		// Natuurlijke spawn staat aan in dorpsbiomen
		boolean inBioom = server.computeOnServer(s -> {
			var bioom = s.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS).value();
			boolean piet = false, sint = false;
			for (var w : bioom.getMobSettings().getMobs(MobCategory.CREATURE).unwrap()) {
				if (w.value().type() == ModEntiteiten.PIET) piet = true;
				if (w.value().type() == ModEntiteiten.SINT) sint = true;
			}
			return piet && sint;
		});
		ok(inBioom, "Piet en Sint staan in de natuurlijke spawnlijst van de vlakte");

		// Piet laat af en toe iets vallen
		server.runCommand("kill @e[type=item]");
		server.runOnServer(s -> s.overworld().getEntitiesOfClass(Piet.class, new AABB(new BlockPos(-6, Y, 7)).inflate(0.4)).get(0).laatNuIetsVallen());
		context.waitTicks(10);
		ok(voorwerpenBij(server, new BlockPos(-6, Y, 7)) > 0, "een Piet laat iets vallen als zijn wachttijd om is");
		server.runCommand("kill @e[type=item]");

		server.runCommand("tp @p 0.5 149 -1.5 0 2");
		context.waitTicks(20);
		context.takeScreenshot("fase2-3-zes-pieten-en-sint");

		// Praten: echte rechtsklik op de Piet recht voor de speler
		server.runCommand("tp @p 0.5 149 4.6 0 0");
		context.waitTicks(15);
		context.getInput().pressMouse(1);
		context.waitTicks(15);
		server.runCommand("tp @p 6.5 149 4.6 0 0");
		context.waitTicks(15);
		context.getInput().pressMouse(1);
		context.waitTicks(15);
		context.takeScreenshot("fase2-4-praten-na-rechtsklik");

		// Sint van dichtbij: van voren en van opzij
		server.runCommand("tp @p 6.5 149 5.2 0 -5");
		context.waitTicks(15);
		context.takeScreenshot("fase5-1-sint-van-voren");
		server.runCommand("tp @p 9.0 149 6.5 68 -5");
		context.waitTicks(15);
		context.takeScreenshot("fase5-2-sint-van-opzij");

		server.runCommand("tp @p -0.5 149 -0.2 0 30");
		context.waitTicks(20);
		context.takeScreenshot("fase2-5-schoen-en-bord");
	}

	private void lootEnRecepten(TestServerContext server) {
		// Inhoud van een pakje: 5000 keer trekken uit de loot table en tellen
		Map<String, Integer> telling = server.computeOnServer(s -> {
			ServerLevel w = s.overworld();
			LootTable tabel = s.reloadableRegistries().getLootTable(PakjeBlok.INHOUD);
			LootParams params = new LootParams.Builder(w).withParameter(LootContextParams.ORIGIN, new Vec3(0, Y, 0)).create(LootContextParamSets.CHEST);
			Map<String, Integer> tel = new HashMap<>();
			for (int i = 0; i < 5000; i++) {
				for (ItemStack stapel : tabel.getRandomItems(params)) {
					tel.merge(BuiltInRegistries.ITEM.getKey(stapel.getItem()).getPath(), 1, Integer::sum);
				}
			}
			return tel;
		});
		SinterklaasMod.LOG.info("PROEF INFO: inhoud van 5000 pakjes: {}", new java.util.TreeMap<>(telling));
		int gewoon = telling.getOrDefault("pepernoot", 0) + telling.getOrDefault("cookie", 0);
		int eieren = telling.entrySet().stream().filter(e -> e.getKey().endsWith("_spawn_egg")).mapToInt(Map.Entry::getValue).sum();
		int zeldzaam = telling.getOrDefault("chocoladeletter", 0) + telling.getOrDefault("zakje_pepernoten", 0) + telling.getOrDefault("mijter", 0) + eieren;
		int gedichtjes = telling.getOrDefault("gedichtje", 0);
		ok(gewoon > 3500, "pakjes bevatten meestal pepernoten of snoep (" + gewoon + " van 5000)");
		ok(zeldzaam > 500 && zeldzaam < 1500, "eigen Sinterklaas-items zijn zeldzaam (" + zeldzaam + " van 5000)");
		ok(telling.containsKey("chocoladeletter") && telling.containsKey("mijter") && telling.containsKey("zakje_pepernoten") && eieren > 0,
			"chocoladeletter, mijter, zakje pepernoten en Piet-spawn-eieren komen alle voor");
		ok(gedichtjes > 50 && gedichtjes < 300, "een gedichtje is zeer zeldzaam (" + gedichtjes + " van 5000)");

		// Zeldzaam in kisten
		int inKisten = server.computeOnServer(s -> {
			ServerLevel w = s.overworld();
			LootTable tabel = s.reloadableRegistries().getLootTable(BuiltInLootTables.VILLAGE_PLAINS_HOUSE);
			LootParams params = new LootParams.Builder(w).withParameter(LootContextParams.ORIGIN, new Vec3(0, Y, 0)).create(LootContextParamSets.CHEST);
			int n = 0;
			for (int i = 0; i < 2000; i++) {
				for (ItemStack stapel : tabel.getRandomItems(params)) if (stapel.is(ModItems.PAKJE)) n++;
			}
			return n;
		});
		ok(inKisten > 100 && inKisten < 320, "in dorpskisten zit zeldzaam een pakje (" + inKisten + " van 2000 kisten)");

		// Recepten
		for (String recept : new String[] {"pakje", "lint", "pepernoot", "zakje_pepernoten", "pepernoten_uit_zakje", "chocoladeletter", "schoen", "bord", "staf"}) {
			boolean bestaat = server.computeOnServer(s ->
				s.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, SinterklaasMod.id(recept))).isPresent());
			ok(bestaat, "recept '" + recept + "' is geladen");
		}

		// Elk voorwerp heeft een Nederlandse naam en alle blokken bestaan
		for (Block blok : new Block[] {ModBlokken.PAKJE, ModBlokken.SCHOENPAKJE, ModBlokken.SCHOEN, ModBlokken.BORD}) {
			ok(Item.BY_BLOCK.containsKey(blok), "blok " + BuiltInRegistries.BLOCK.getKey(blok).getPath() + " heeft een voorwerp");
		}
	}

	private int aantalFiguren(TestServerContext server) {
		return server.computeOnServer(s -> s.overworld().getEntitiesOfClass(
			nl.sinterklaas.entiteit.SinterklaasFiguur.class, new AABB(-30, 130, -30, 30, 170, 30)).size());
	}

	/** Hoeveel van de 1000 pogingen tot vanzelf spawnen gaan door op deze datum? */
	private int natuurlijkeSpawns(TestServerContext server, String datum) {
		server.runCommand("sinterklaas datum " + datum);
		return server.computeOnServer(s -> {
			ServerLevel w = s.overworld();
			BlockPos plek = new BlockPos(0, Y, -2);
			int n = 0;
			for (int i = 0; i < 1000; i++) {
				if (nl.sinterklaas.entiteit.SinterklaasFiguur.magHierSpawnen(ModEntiteiten.PIET, w, EntitySpawnReason.NATURAL, plek, w.getRandom())) n++;
			}
			return n;
		});
	}

	private void seizoen(ClientGameTestContext context, TestServerContext server) {
		server.runCommand("time set noon");
		context.waitTicks(5);

		// Vanzelf spawnen: alleen 15 november t/m 5 december, en steeds meer richting 5 december
		ok(natuurlijkeSpawns(server, "2026-07-01") == 0, "1 juli: er spawnt vanzelf niets");
		ok(natuurlijkeSpawns(server, "2026-11-14") == 0, "14 november: er spawnt vanzelf nog niets");
		int start = natuurlijkeSpawns(server, "2026-11-15");
		int midden = natuurlijkeSpawns(server, "2026-11-25");
		int piek = natuurlijkeSpawns(server, "2026-12-05");
		SinterklaasMod.LOG.info("PROEF INFO: natuurlijke spawns per 1000 pogingen: 15 nov {}, 25 nov {}, 5 dec {}", start, midden, piek);
		ok(start > 130 && start < 270, "15 november: het seizoen begint voorzichtig (" + start + " van 1000)");
		ok(midden > 520 && midden < 680, "25 november: al duidelijk meer (" + midden + " van 1000)");
		ok(piek == 1000, "5 december: de piek (" + piek + " van 1000)");
		ok(natuurlijkeSpawns(server, "2026-12-06") == 0, "6 december: er spawnt vanzelf niets meer");
		ok(natuurlijkeSpawns(server, "2026-12-20") == 0, "20 december: er spawnt vanzelf niets meer");

		// Iedereen vertrekt op 6 december
		server.runCommand("sinterklaas datum 2026-12-05");
		context.waitTicks(120);
		int op5dec = aantalFiguren(server);
		ok(op5dec == 7, "5 december: de zes Pieten en Sint zijn er nog (" + op5dec + ")");
		server.runCommand("tp @p 0.5 149 -1.5 0 2");
		server.runCommand("sinterklaas datum 2026-12-06");
		context.waitTicks(120);
		ok(aantalFiguren(server) == 0, "6 december: iedereen is vertrokken, ook wie uit een spawn-ei kwam");
		context.takeScreenshot("fase3-1-op-6-december-is-iedereen-weg");

		// Een spawn-ei werkt daarna gewoon; die Piet blijft tot de volgende 6 december
		server.runOnServer(s -> {
			Piet piet = ModEntiteiten.PIET.spawn(s.overworld(), new ItemStack(ModItems.PIET_EIEREN.get(PietVariant.HOOFD)), speler(s),
				new BlockPos(0, Y, 4), EntitySpawnReason.SPAWN_ITEM_USE, false, false);
			piet.setNoAi(true);
		});
		context.waitTicks(120);
		ok(aantalFiguren(server) == 1, "6 december: een Piet uit een spawn-ei blijft gewoon staan");
		server.runCommand("sinterklaas datum 2027-07-01");
		context.waitTicks(120);
		ok(aantalFiguren(server) == 1, "1 juli het jaar erna: die Piet is er nog");
		server.runCommand("sinterklaas datum 2027-12-05");
		context.waitTicks(120);
		ok(aantalFiguren(server) == 1, "5 december het jaar erna: die Piet is er nog");
		server.runCommand("sinterklaas datum 2027-12-06");
		context.waitTicks(120);
		ok(aantalFiguren(server) == 0, "6 december het jaar erna: ook die Piet vertrekt");
		server.runCommand("sinterklaas datum reset");
	}

	private void staf(ClientGameTestContext context, TestServerContext server) {
		// Sint houdt zijn staf vast
		context.waitTicks(110);
		ok(server.computeOnServer(s -> s.overworld().getEntitiesOfClass(Sint.class, new AABB(new BlockPos(6, Y, 7)).inflate(1)).get(0)
			.getMainHandItem().is(ModItems.STAF)), "Sint houdt zijn staf vast");
		server.runCommand("tp @p 6.5 149 4.6 0 0");
		context.waitTicks(15);
		context.takeScreenshot("fase6-1-sint-met-staf");
		server.runCommand("tp @p 9.0 149 6.5 68 -5");
		context.waitTicks(15);
		context.takeScreenshot("fase6-1b-sint-met-staf-van-opzij");
		if ("stafmodel".equals(System.getenv("PROEF_ALLEEN"))) return;

		// Twee Pieten: één dichtbij genoeg om de staf te horen, één te ver weg
		server.runCommand("tp @p 0.5 149 -3.0 0 5");
		java.util.UUID dichtbij = server.computeOnServer(s ->
			ModEntiteiten.PIET.spawn(s.overworld(), new BlockPos(0, Y, 10), EntitySpawnReason.COMMAND).getUUID());
		java.util.UUID ver = server.computeOnServer(s -> {
			Piet piet = ModEntiteiten.PIET.spawn(s.overworld(), new BlockPos(40, Y, -3), EntitySpawnReason.COMMAND);
			piet.setNoAi(true);
			piet.setNoGravity(true);
			return piet.getUUID();
		});
		context.waitTicks(5);
		server.runCommand("item replace entity @p weapon.mainhand with sinterklaas:staf");
		context.waitTicks(5);
		double afstandVoor = server.computeOnServer(s -> (double) s.overworld().getEntity(dichtbij).distanceTo(speler(s)));

		// Echte rechtsklik met de staf in de hand
		context.getInput().pressMouse(1);
		context.waitTicks(5);
		ok(server.computeOnServer(s -> ((Piet) s.overworld().getEntity(dichtbij)).roeper() == speler(s)),
			"rechtsklik met de staf roept een Piet op " + Math.round(afstandVoor) + " blokken afstand");
		ok(server.computeOnServer(s -> ((Piet) s.overworld().getEntity(ver)).roeper() == null),
			"een Piet op 40 blokken afstand hoort de staf niet");
		ok(server.computeOnServer(s -> speler(s).getCooldowns().isOnCooldown(speler(s).getMainHandItem())),
			"na het roepen moet je even wachten voor je opnieuw kunt roepen");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK));
		context.waitTicks(200);
		double afstandNa = server.computeOnServer(s -> (double) s.overworld().getEntity(dichtbij).distanceTo(speler(s)));
		ok(afstandNa < 4.5, "de geroepen Piet is naar de speler toe gelopen (van " + Math.round(afstandVoor) + " naar "
			+ (Math.round(afstandNa * 10) / 10.0) + " blokken)");
		context.takeScreenshot("fase6-2-piet-komt-op-de-staf-af");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.FIRST_PERSON));
		context.waitTicks(120);
		ok(server.computeOnServer(s -> ((Piet) s.overworld().getEntity(dichtbij)).roeper() == null),
			"na 15 seconden gaat de Piet weer zijn eigen gang");
		server.runOnServer(s -> { s.overworld().getEntity(dichtbij).discard(); s.overworld().getEntity(ver).discard(); });
		server.runCommand("clear @p");
	}

	private void mijter(ClientGameTestContext context, TestServerContext server) {
		server.runCommand("item replace entity @p armor.head with sinterklaas:mijter");
		ok(server.computeOnServer(s -> speler(s).getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(ModItems.MIJTER)),
			"de mijter kan in het helm-slot");
		context.waitTicks(5);
		ok(server.computeOnServer(s -> speler(s).getArmorValue()) == 2, "de mijter beschermt als een helm (2 pantserpunten)");
		server.runCommand("item replace entity @p armor.head with air");
		context.waitTicks(5);
		ok(server.computeOnServer(s -> speler(s).getArmorValue()) == 0, "zonder mijter is de bescherming weer weg");
		server.runCommand("item replace entity @p armor.head with sinterklaas:mijter");
		String[] hotbar = {"pakje", "schoenpakje", "schoen", "bord", "pepernoot", "zakje_pepernoten", "chocoladeletter", "lint", "gedichtje"};
		for (int i = 0; i < hotbar.length; i++) {
			server.runCommand("item replace entity @p hotbar." + i + " with sinterklaas:" + hotbar[i]);
		}
		server.runCommand("tp @p 0.5 149 -2.5 0 5");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK));
		context.waitTicks(15);
		context.takeScreenshot("fase5-3-speler-met-mijter-van-achter");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
		context.waitTicks(20);
		context.takeScreenshot("fase2-6-mijter-en-voorwerpen");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.FIRST_PERSON));

		// Alle spawn-eieren in de hotbar
		server.runCommand("clear @p");
		int i = 0;
		for (PietVariant v : PietVariant.values()) {
			server.runCommand("item replace entity @p hotbar." + (i++) + " with sinterklaas:piet_" + v.id() + "_spawn_egg");
		}
		server.runCommand("item replace entity @p hotbar.6 with sinterklaas:sint_spawn_egg");
		server.runCommand("item replace entity @p hotbar.7 with sinterklaas:mijter");
		context.waitTicks(10);
		context.takeScreenshot("fase2-7-spawn-eieren");
	}
}
