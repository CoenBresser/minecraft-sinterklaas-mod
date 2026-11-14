package nl.sinterklaas.blok;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.sinterklaas.ModBlokken;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.core.SchoenInhoud;
import nl.sinterklaas.core.SchoenRegels;

/**
 * Schoen zetten.
 * - Rechtsklik met een wortel op een lege schoen: de wortel ligt klaar voor het paard.
 *   De volgende (Minecraft-)ochtend ligt er een pakje in.
 * - Zet je de schoen zomaar, zonder wortel, dan ligt er de volgende ochtend een briefje van Piet in.
 * - Rechtsklik met lege hand haalt het pakje of het briefje eruit. De wortel is dan op.
 * Wat er wanneer in komt, bepaalt SchoenRegels (core).
 */
public class SchoenBlok extends Block implements EntityBlock {
	/** De inhoud zoals Minecraft die in de blokstatus bewaart; hoort één-op-één bij SchoenInhoud uit core. */
	public enum Inhoud implements StringRepresentable {
		LEEG(SchoenInhoud.LEEG), WORTEL(SchoenInhoud.WORTEL), BRIEFJE(SchoenInhoud.BRIEFJE), PAKJE(SchoenInhoud.PAKJE);

		public final SchoenInhoud regel;

		Inhoud(SchoenInhoud regel) {
			this.regel = regel;
		}

		public static Inhoud van(SchoenInhoud regel) {
			for (Inhoud i : values()) if (i.regel == regel) return i;
			return LEEG;
		}

		@Override
		public String getSerializedName() {
			return regel.id();
		}
	}

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Inhoud> INHOUD = EnumProperty.create("inhoud", Inhoud.class);
	/** Zo vaak kijkt de schoen of het al ochtend is (100 tikken = 5 seconden). */
	private static final int KIJK_ELKE = 100;

	private static final VoxelShape VORM_NZ = Block.box(4.5, 0, 1.5, 11.5, 11, 14.5);
	private static final VoxelShape VORM_OW = Block.box(1.5, 0, 4.5, 14.5, 11, 11.5);

	public SchoenBlok(BlockBehaviour.Properties eigenschappen) {
		super(eigenschappen);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(INHOUD, Inhoud.LEEG));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bouwer) {
		bouwer.add(FACING, INHOUD);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		// De neus wijst van de speler af.
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	protected VoxelShape getShape(BlockState staat, BlockGetter wereld, BlockPos plek, CollisionContext context) {
		return staat.getValue(FACING).getAxis() == Direction.Axis.Z ? VORM_NZ : VORM_OW;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos plek, BlockState staat) {
		return new SchoenBlokEntiteit(plek, staat);
	}

	@Override
	protected void onPlace(BlockState staat, Level wereld, BlockPos plek, BlockState oud, boolean doorZuiger) {
		super.onPlace(staat, wereld, plek, oud, doorZuiger);
		if (!oud.is(this)) wereld.scheduleTick(plek, this, 1);
	}

	public static long dagNu(Level wereld) {
		return SchoenRegels.dagNummer(wereld.getOverworldClockTime());
	}

	@Override
	protected void tick(BlockState staat, ServerLevel wereld, BlockPos plek, RandomSource toeval) {
		if (wereld.getBlockEntity(plek) instanceof SchoenBlokEntiteit schoen) {
			long dag = dagNu(wereld);
			SchoenInhoud inhoud = staat.getValue(INHOUD).regel;
			if (schoen.gezetOpDag() == SchoenBlokEntiteit.NOG_NIET_GEZET) {
				schoen.zetGezetOpDag(dag);
			} else if (SchoenRegels.pietIsLangsGeweest(schoen.gezetOpDag(), dag, inhoud)) {
				wereld.setBlock(plek, staat.setValue(INHOUD, Inhoud.van(SchoenRegels.naDeNacht(inhoud))), Block.UPDATE_ALL);
			}
		}
		wereld.scheduleTick(plek, this, KIJK_ELKE);
	}

	/** Rechtsklik met een wortel: leg hem in de schoen voor het paard. */
	@Override
	protected InteractionResult useItemOn(ItemStack stapel, BlockState staat, Level wereld, BlockPos plek, Player speler,
			InteractionHand hand, BlockHitResult raak) {
		if (!stapel.is(Items.CARROT) || !SchoenRegels.magWortelErin(staat.getValue(INHOUD).regel)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (wereld instanceof ServerLevel server) {
			server.setBlock(plek, staat.setValue(INHOUD, Inhoud.WORTEL), Block.UPDATE_ALL);
			stapel.consume(1, speler);
			server.playSound(null, plek, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.7F, 1.1F);
			speler.sendOverlayMessage(Component.translatable("bericht.sinterklaas.schoen_wortel_erin"));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState staat, Level wereld, BlockPos plek, Player speler, BlockHitResult raak) {
		if (!(wereld instanceof ServerLevel server)) return InteractionResult.SUCCESS;
		switch (staat.getValue(INHOUD)) {
			case LEEG -> speler.sendOverlayMessage(Component.translatable("bericht.sinterklaas.schoen_leeg"));
			case WORTEL -> speler.sendOverlayMessage(Component.translatable("bericht.sinterklaas.schoen_wortel_wacht"));
			case BRIEFJE -> {
				// Het briefje van Piet: regel voor regel in de chat.
				for (String regel : SinterklaasMod.TEKSTEN.schoenZonderWortel()) {
					speler.sendSystemMessage(Component.literal(regel));
				}
				leegMaken(server, plek, staat);
				server.playSound(null, plek, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 0.8F, 1.0F);
			}
			case PAKJE -> {
				leegMaken(server, plek, staat);
				ItemStack pakje = new ItemStack(ModBlokken.SCHOENPAKJE);
				if (!speler.addItem(pakje)) Block.popResource(server, plek.above(), pakje);
				server.playSound(null, plek, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
				speler.sendOverlayMessage(Component.translatable("bericht.sinterklaas.schoen_gevuld"));
			}
		}
		return InteractionResult.SUCCESS;
	}

	/** De schoen is weer leeg (de wortel is op) en wacht op de volgende nacht. */
	private static void leegMaken(ServerLevel wereld, BlockPos plek, BlockState staat) {
		wereld.setBlock(plek, staat.setValue(INHOUD, Inhoud.LEEG), Block.UPDATE_ALL);
		if (wereld.getBlockEntity(plek) instanceof SchoenBlokEntiteit schoen) {
			schoen.zetGezetOpDag(dagNu(wereld));
		}
	}
}
