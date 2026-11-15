package nl.sinterklaas.blok;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.sinterklaas.ModItems;

/** Een bord waar je tot vier pepernoten op kunt leggen. Rechtsklik met lege hand pakt er één af. */
public class BordBlok extends Block {
	public static final int MAX = 4;
	public static final IntegerProperty PEPERNOTEN = IntegerProperty.create("pepernoten", 0, MAX);

	private static final VoxelShape LEEG = Block.box(1, 0, 1, 15, 1, 15);
	private static final VoxelShape VOL = Block.box(1, 0, 1, 15, 3, 15);

	public BordBlok(BlockBehaviour.Properties eigenschappen) {
		super(eigenschappen);
		this.registerDefaultState(this.stateDefinition.any().setValue(PEPERNOTEN, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bouwer) {
		bouwer.add(PEPERNOTEN);
	}

	@Override
	public VoxelShape getShape(BlockState staat, BlockGetter wereld, BlockPos plek, CollisionContext context) {
		return staat.getValue(PEPERNOTEN) == 0 ? LEEG : VOL;
	}

	@Override
	public InteractionResult use(BlockState staat, Level wereld, BlockPos plek, Player speler, InteractionHand hand, BlockHitResult raak) {
		int aantal = staat.getValue(PEPERNOTEN);
		ItemStack stapel = speler.getItemInHand(hand);
		if (stapel.is(ModItems.PEPERNOOT.get()) && aantal < MAX) {
			if (wereld instanceof ServerLevel server) {
				server.setBlock(plek, staat.setValue(PEPERNOTEN, aantal + 1), Block.UPDATE_ALL);
				if (!speler.getAbilities().instabuild) stapel.shrink(1);
				server.playSound(null, plek, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.7F, 1.3F);
			}
			return InteractionResult.sidedSuccess(wereld.isClientSide);
		}
		// Eraf pakken gaat alleen met de hoofdhand, anders pak je er bij één klik twee.
		if (hand != InteractionHand.MAIN_HAND || aantal == 0) return InteractionResult.PASS;
		if (wereld instanceof ServerLevel server) {
			server.setBlock(plek, staat.setValue(PEPERNOTEN, aantal - 1), Block.UPDATE_ALL);
			ItemStack noot = new ItemStack(ModItems.PEPERNOOT.get());
			if (!speler.addItem(noot)) Block.popResource(server, plek, noot);
			server.playSound(null, plek, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.7F, 1.3F);
		}
		return InteractionResult.sidedSuccess(wereld.isClientSide);
	}
}
