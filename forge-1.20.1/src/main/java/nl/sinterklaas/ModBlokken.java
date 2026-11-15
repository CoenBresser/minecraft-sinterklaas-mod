package nl.sinterklaas;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
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
	public static final RegistryObject<Block> PAKJE = BLOKKEN.register(Namen.PAKJE, () -> new PakjeBlok(pakjeEigenschappen(), false));
	/** Pakje uit een schoen: mag altijd open. */
	public static final RegistryObject<Block> SCHOENPAKJE = BLOKKEN.register(Namen.SCHOENPAKJE, () -> new PakjeBlok(pakjeEigenschappen(), true));

	public static final RegistryObject<Block> SCHOEN = BLOKKEN.register(Namen.SCHOEN, () -> new SchoenBlok(
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.3F).sound(SoundType.WOOD).noOcclusion()));

	public static final RegistryObject<Block> BORD = BLOKKEN.register(Namen.BORD, () -> new BordBlok(
		BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(0.2F).sound(SoundType.DECORATED_POT).noOcclusion()));

	public static final RegistryObject<BlockEntityType<SchoenBlokEntiteit>> SCHOEN_ENTITEIT = BLOK_ENTITEITEN.register(Namen.SCHOEN,
		() -> BlockEntityType.Builder.of(SchoenBlokEntiteit::new, SCHOEN.get()).build(null));

	private static BlockBehaviour.Properties pakjeEigenschappen() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.2F).sound(SoundType.WOOL).noOcclusion();
	}

	static void registreer(IEventBus modBus) {
		BLOKKEN.register(modBus);
		BLOK_ENTITEITEN.register(modBus);
	}

	private ModBlokken() {}
}
