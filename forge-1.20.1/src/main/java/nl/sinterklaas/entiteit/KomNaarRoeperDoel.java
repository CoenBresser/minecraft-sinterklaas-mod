package nl.sinterklaas.entiteit;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import nl.sinterklaas.core.StafRegels;

/** Een Piet die met de staf is geroepen, loopt naar de speler toe en blijft bij hem staan kijken. */
public class KomNaarRoeperDoel extends Goal {
	private final Piet piet;
	private Player roeper;

	public KomNaarRoeperDoel(Piet piet) {
		this.piet = piet;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		this.roeper = this.piet.roeper();
		return this.roeper != null;
	}

	@Override
	public boolean canContinueToUse() {
		return this.piet.roeper() == this.roeper && this.roeper != null;
	}

	@Override
	public void tick() {
		this.piet.getLookControl().setLookAt(this.roeper, 30.0F, 30.0F);
		if (StafRegels.moetNogKomen(this.piet.distanceTo(this.roeper), this.piet.roepTikkenOver())) {
			this.piet.getNavigation().moveTo(this.roeper, 1.3);
		} else {
			this.piet.getNavigation().stop();
		}
	}

	@Override
	public void stop() {
		this.roeper = null;
		this.piet.getNavigation().stop();
	}
}
