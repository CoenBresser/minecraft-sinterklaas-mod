package nl.sinterklaas;

import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.sinterklaas.blok.BordBlok;
import nl.sinterklaas.blok.PakjeBlok;
import nl.sinterklaas.blok.SchoenBlok;
import nl.sinterklaas.blok.SchoenBlokEntiteit;
import nl.sinterklaas.core.Namen;

public final class ModBlokken {
	private static final DeferredRegister<Block> BLOKKEN = DeferredRegister.create(ForgeRegistries.BLOCKS, Namen.MOD_ID);
	private static final DeferredRegister<BlockEntityType<?>> BLOK_ENTITEITEN =
		DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Namen.MOD_ID);

	/** Gewoon pakje: mag alleen open volgens de datumregel. */
	public static final RegistryObject<Block> PAKJE = registreer(Namen.PAKJE, p -> new PakjeBlok(p, false), ModBlokken::pakjeEigenschappen);
	/** Pakje uit een schoen: mag altijd open. */
	public static final RegistryObject<Block> SCHOENPAKJE = registreer(Namen.SCHOENPAKJE, p -> new PakjeBlok(p, true), ModBlokken::pakjeEigenschappen);

	public static final RegistryObject<Block> SCHOEN = registreer(Namen.SCHOEN, SchoenBlok::new,
		() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.3F).sound(SoundType.WOOD).noOcclusion());

	public static final RegistryObject<Block> BORD = registreer(Namen.BORD, BordBlok::new,
		() -> BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(0.2F).sound(SoundType.DECORATED_POT).noOcclusion());

	public static final RegistryObject<BlockEntityType<SchoenBlokEntiteit>> SCHOEN_ENTITEIT = BLOK_ENTITEITEN.register(Namen.SCHOEN,
		() -> new BlockEntityType<>(SchoenBlokEntiteit::new, Set.of(SCHOEN.get())));

	private static BlockBehaviour.Properties pakjeEigenschappen() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.2F).sound(SoundType.WOOL).noOcclusion();
	}

	private static RegistryObject<Block> registreer(String naam, Function<BlockBehaviour.Properties, Block> maker,
			java.util.function.Supplier<BlockBehaviour.Properties> eigenschappen) {
		return BLOKKEN.register(naam, () -> maker.apply(eigenschappen.get().setId(BLOKKEN.key(naam))));
	}

	static void registreer(BusGroup modBus) {
		BLOKKEN.register(modBus);
		BLOK_ENTITEITEN.register(modBus);
	}

	private ModBlokken() {}
}
