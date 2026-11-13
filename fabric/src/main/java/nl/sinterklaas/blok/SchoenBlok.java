package nl.sinterklaas.blok;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.sinterklaas.ModBlokken;
import nl.sinterklaas.core.SchoenRegels;

/**
 * Schoen zetten: zet een schoen neer en de volgende (Minecraft-)ochtend ligt er een pakje in.
 * Rechtsklik om het pakje eruit te halen; de nacht daarna komt er weer één.
 * Wanneer er een pakje in moet, bepaalt SchoenRegels (core).
 */
public class SchoenBlok extends Block implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty GEVULD = BooleanProperty.create("gevuld");
	/** Zo vaak kijkt de schoen of het al ochtend is (100 tikken = 5 seconden). */
	private static final int KIJK_ELKE = 100;

	private static final VoxelShape VORM_NZ = Block.box(4.5, 0, 1.5, 11.5, 11, 14.5);
	private static final VoxelShape VORM_OW = Block.box(1.5, 0, 4.5, 14.5, 11, 11.5);

	public SchoenBlok(BlockBehaviour.Properties eigenschappen) {
		super(eigenschappen);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(GEVULD, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bouwer) {
		bouwer.add(FACING, GEVULD);
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
			if (schoen.gezetOpDag() == SchoenBlokEntiteit.NOG_NIET_GEZET) {
				schoen.zetGezetOpDag(dag);
			} else if (SchoenRegels.moetVullen(schoen.gezetOpDag(), dag, staat.getValue(GEVULD))) {
				wereld.setBlock(plek, staat.setValue(GEVULD, true), Block.UPDATE_ALL);
			}
		}
		wereld.scheduleTick(plek, this, KIJK_ELKE);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState staat, Level wereld, BlockPos plek, Player speler, BlockHitResult raak) {
		if (!(wereld instanceof ServerLevel server)) return InteractionResult.SUCCESS;
		if (!staat.getValue(GEVULD)) {
			speler.sendOverlayMessage(Component.translatable("bericht.sinterklaas.schoen_leeg"));
			return InteractionResult.SUCCESS;
		}
		server.setBlock(plek, staat.setValue(GEVULD, false), Block.UPDATE_ALL);
		if (server.getBlockEntity(plek) instanceof SchoenBlokEntiteit schoen) {
			schoen.zetGezetOpDag(dagNu(server));   // vannacht mag er weer één in
		}
		ItemStack pakje = new ItemStack(ModBlokken.SCHOENPAKJE);
		if (!speler.addItem(pakje)) Block.popResource(server, plek.above(), pakje);
		server.playSound(null, plek, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
		speler.sendOverlayMessage(Component.translatable("bericht.sinterklaas.schoen_gevuld"));
		return InteractionResult.SUCCESS;
	}
}
