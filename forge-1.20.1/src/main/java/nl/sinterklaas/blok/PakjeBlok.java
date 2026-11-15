package nl.sinterklaas.blok;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.sinterklaas.ModItems;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.core.PakjesRegels;
import nl.sinterklaas.core.PraatRegels;

/**
 * Een pakje is een loot box. Rechtsklik: het deksel klapt open met een geluidje,
 * even later springt de inhoud eruit en verdwijnt het pakje.
 * Of het open mag, bepaalt PakjesRegels (core). De inhoud staat in data:
 * loot_tables/gameplay/pakje_inhoud.json
 */
public class PakjeBlok extends Block {
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final ResourceLocation INHOUD = SinterklaasMod.id("gameplay/pakje_inhoud");
	/** Zo lang staat het pakje open voordat de inhoud eruit komt (15 tikken = 0,75 seconde). */
	public static final int OPEN_TIKKEN = 15;

	private static final VoxelShape VORM = Shapes.or(
		Block.box(2, 0, 2, 14, 9, 14),
		Block.box(1, 9, 1, 15, 12, 15),
		Block.box(2, 12, 6.5, 14, 15, 9.5)
	);

	private final boolean uitSchoen;

	public PakjeBlok(BlockBehaviour.Properties eigenschappen, boolean uitSchoen) {
		super(eigenschappen);
		this.uitSchoen = uitSchoen;
		this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bouwer) {
		bouwer.add(OPEN);
	}

	@Override
	public VoxelShape getShape(BlockState staat, BlockGetter wereld, BlockPos plek, CollisionContext context) {
		return VORM;
	}

	@Override
	public InteractionResult use(BlockState staat, Level wereld, BlockPos plek, Player speler, InteractionHand hand, BlockHitResult raak) {
		if (!(wereld instanceof ServerLevel server)) return InteractionResult.SUCCESS;
		if (staat.getValue(OPEN)) return InteractionResult.CONSUME;

		if (!PakjesRegels.magOpenen(SinterklaasMod.KLOK.vandaag(), this.uitSchoen)) {
			speler.sendSystemMessage(Component.translatable("bericht.sinterklaas.pakje_te_vroeg"));
			server.playSound(null, plek, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.6F, 1.4F);
			return InteractionResult.CONSUME;
		}

		server.setBlock(plek, staat.setValue(OPEN, true), Block.UPDATE_ALL);
		server.playSound(null, plek, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.7F, 1.5F);
		server.sendParticles(ParticleTypes.HAPPY_VILLAGER, plek.getX() + 0.5, plek.getY() + 0.9, plek.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0.0);
		server.scheduleTick(plek, this, OPEN_TIKKEN);
		return InteractionResult.CONSUME;
	}

	@Override
	public void tick(BlockState staat, ServerLevel wereld, BlockPos plek, RandomSource toeval) {
		if (!staat.getValue(OPEN)) return;

		LootParams params = new LootParams.Builder(wereld)
			.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(plek))
			.create(LootContextParamSets.CHEST);
		List<ItemStack> inhoud = wereld.getServer().getLootData().getLootTable(INHOUD).getRandomItems(params);

		wereld.removeBlock(plek, false);
		for (ItemStack stapel : inhoud) {
			if (stapel.is(ModItems.GEDICHTJE.get())) {
				zegGedichtje(wereld, plek, toeval);   // een gedichtje komt in de chat, niet als voorwerp
			} else {
				Block.popResource(wereld, plek, stapel);
			}
		}
		wereld.playSound(null, plek, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.BLOCKS, 0.8F, 1.2F);
		wereld.sendParticles(ParticleTypes.FIREWORK, plek.getX() + 0.5, plek.getY() + 0.5, plek.getZ() + 0.5, 25, 0.25, 0.25, 0.25, 0.08);
	}

	/** Stuurt een willekeurig gedichtje naar iedereen in de buurt. */
	public static void zegGedichtje(ServerLevel wereld, BlockPos plek, RandomSource toeval) {
		String gedicht = PraatRegels.kies(SinterklaasMod.TEKSTEN.gedichtjes(), toeval.nextInt());
		if (gedicht == null) return;
		Component bericht = Component.translatable("bericht.sinterklaas.gedichtje", gedicht);
		Vec3 midden = Vec3.atCenterOf(plek);
		for (ServerPlayer speler : wereld.players()) {
			if (speler.position().distanceToSqr(midden) <= 16 * 16) speler.sendSystemMessage(bericht);
		}
	}
}
