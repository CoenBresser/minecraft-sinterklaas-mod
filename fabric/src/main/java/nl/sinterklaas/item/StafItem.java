package nl.sinterklaas.item;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nl.sinterklaas.core.StafRegels;
import nl.sinterklaas.entiteit.Piet;

/**
 * De staf van Sinterklaas. Rechtsklik: alle Pieten in de buurt komen naar je toe.
 * Hoe ver de staf reikt en hoe lang de Pieten komen, staat in StafRegels (core).
 */
public class StafItem extends Item {
	public StafItem(Item.Properties eigenschappen) {
		super(eigenschappen);
	}

	@Override
	public InteractionResult use(Level wereld, Player speler, InteractionHand hand) {
		if (wereld instanceof ServerLevel server) {
			int geroepen = roepPieten(server, speler);
			ItemStack staf = speler.getItemInHand(hand);
			speler.getCooldowns().addCooldown(staf, StafRegels.PAUZE);
			server.playSound(null, speler.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 0.6F, 1.6F);
			server.sendParticles(ParticleTypes.NOTE, speler.getX(), speler.getY() + 2.2, speler.getZ(), 5, 0.4, 0.2, 0.4, 0.0);
			speler.sendOverlayMessage(Component.translatable(
				geroepen > 0 ? "bericht.sinterklaas.staf_geroepen" : "bericht.sinterklaas.staf_niemand", geroepen));
		}
		return InteractionResult.SUCCESS;
	}

	/** Roept alle Pieten binnen bereik. Geeft terug hoeveel Pieten het hoorden. */
	public static int roepPieten(ServerLevel wereld, Player speler) {
		List<Piet> pieten = wereld.getEntitiesOfClass(Piet.class, speler.getBoundingBox().inflate(StafRegels.BEREIK),
			piet -> piet.isAlive() && StafRegels.hoortStaf(piet.distanceTo(speler)));
		for (Piet piet : pieten) piet.wordGeroepenDoor(speler);
		return pieten.size();
	}
}
