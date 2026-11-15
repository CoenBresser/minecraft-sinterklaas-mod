package nl.sinterklaas;

import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import nl.sinterklaas.blok.BordBlok;
import nl.sinterklaas.blok.PakjeBlok;
import nl.sinterklaas.blok.SchoenBlok;
import nl.sinterklaas.blok.SchoenBlokEntiteit;
import nl.sinterklaas.core.Namen;

public final class ModBlokken {
	/** Gewoon pakje: mag alleen open volgens de datumregel. */
	public static final Block PAKJE = registreer(Namen.PAKJE, p -> new PakjeBlok(p, false), pakjeEigenschappen());
	/** Pakje uit een schoen: mag altijd open. */
	public static final Block SCHOENPAKJE = registreer(Namen.SCHOENPAKJE, p -> new PakjeBlok(p, true), pakjeEigenschappen());

	public static final Block SCHOEN = registreer(Namen.SCHOEN, SchoenBlok::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.3F).sound(SoundType.WOOD).noOcclusion());

	public static final Block BORD = registreer(Namen.BORD, BordBlok::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(0.2F).sound(SoundType.DECORATED_POT).noOcclusion());

	public static final BlockEntityType<SchoenBlokEntiteit> SCHOEN_ENTITEIT = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, SinterklaasMod.id(Namen.SCHOEN),
		new BlockEntityType<>(SchoenBlokEntiteit::new, Set.of(SCHOEN)));

	private static BlockBehaviour.Properties pakjeEigenschappen() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.2F).sound(SoundType.WOOL).noOcclusion();
	}

	private static Block registreer(String naam, Function<BlockBehaviour.Properties, Block> maker, BlockBehaviour.Properties eigenschappen) {
		ResourceKey<Block> sleutel = ResourceKey.create(Registries.BLOCK, SinterklaasMod.id(naam));
		return Registry.register(BuiltInRegistries.BLOCK, sleutel, maker.apply(eigenschappen.setId(sleutel)));
	}

	static void registreer() {}

	private ModBlokken() {}
}
