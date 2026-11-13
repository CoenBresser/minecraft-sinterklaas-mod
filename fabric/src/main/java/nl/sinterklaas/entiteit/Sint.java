package nl.sinterklaas.entiteit;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import nl.sinterklaas.core.Namen;

/** Sinterklaas: heeft de vorm van een dorpeling. */
public class Sint extends SinterklaasFiguur {
	public Sint(EntityType<? extends Sint> type, Level wereld) {
		super(type, wereld);
	}

	@Override
	protected String spreker() {
		return Namen.SINT;
	}
}
