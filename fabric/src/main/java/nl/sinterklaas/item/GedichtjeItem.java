package nl.sinterklaas.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import nl.sinterklaas.blok.PakjeBlok;

/**
 * Een gedichtje. Uit een pakje komt het direct in de chat; als voorwerp (bijvoorbeeld via /give)
 * lees je het voor met een rechtsklik.
 */
public class GedichtjeItem extends Item {
	public GedichtjeItem(Item.Properties eigenschappen) {
		super(eigenschappen);
	}

	@Override
	public InteractionResult use(Level wereld, Player speler, InteractionHand hand) {
		if (wereld instanceof ServerLevel server) {
			PakjeBlok.zegGedichtje(server, speler.blockPosition(), speler.getRandom());
			speler.getItemInHand(hand).consume(1, speler);
		}
		return InteractionResult.SUCCESS;
	}
}
