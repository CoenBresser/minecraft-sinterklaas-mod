package nl.sinterklaas.blok;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.sinterklaas.ModBlokken;

/** Onthoudt op welke Minecraft-dag de schoen is gezet (of voor het laatst is leeggehaald). */
public class SchoenBlokEntiteit extends BlockEntity {
	public static final long NOG_NIET_GEZET = -1L;

	private long gezetOpDag = NOG_NIET_GEZET;

	public SchoenBlokEntiteit(BlockPos plek, BlockState staat) {
		super(ModBlokken.SCHOEN_ENTITEIT.get(), plek, staat);
	}

	public long gezetOpDag() {
		return gezetOpDag;
	}

	public void zetGezetOpDag(long dag) {
		this.gezetOpDag = dag;
		this.setChanged();
	}

	@Override
	protected void saveAdditional(CompoundTag uit) {
		super.saveAdditional(uit);
		uit.putLong("gezet_op_dag", gezetOpDag);
	}

	@Override
	public void load(CompoundTag in) {
		super.load(in);
		this.gezetOpDag = in.contains("gezet_op_dag", Tag.TAG_LONG) ? in.getLong("gezet_op_dag") : NOG_NIET_GEZET;
	}
}
