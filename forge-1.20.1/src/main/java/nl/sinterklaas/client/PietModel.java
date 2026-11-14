package nl.sinterklaas.client;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import nl.sinterklaas.entiteit.Piet;

/**
 * Het model van de vanilla pillager (dezelfde onderdelen, uit ModelLayers.PILLAGER), met de loop- en
 * kijkbewegingen van een illager. Een eigen klasse, omdat het vanilla IllagerModel in 1.20.1 alleen
 * echte illagers accepteert en Piet er bewust geen is.
 */
public class PietModel extends HierarchicalModel<Piet> implements HeadedModel {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public PietModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		// De pillager verbergt zijn hoedlaag; Piet heeft daar juist zijn baret met veer.
		this.head.getChild("hat").visible = true;
		// De gekruiste armen horen bij andere illagers; Piet heeft zijn armen los.
		root.getChild("arms").visible = false;
		this.rightArm = root.getChild("right_arm");
		this.leftArm = root.getChild("left_arm");
		this.rightLeg = root.getChild("right_leg");
		this.leftLeg = root.getChild("left_leg");
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setupAnim(Piet piet, float loopPositie, float loopSnelheid, float leeftijd, float hoofdDraai, float hoofdKnik) {
		this.head.yRot = hoofdDraai * Mth.DEG_TO_RAD;
		this.head.xRot = hoofdKnik * Mth.DEG_TO_RAD;
		if (this.riding) {
			this.rightArm.xRot = -Mth.PI / 5F;
			this.leftArm.xRot = -Mth.PI / 5F;
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = Mth.PI / 10F;
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = -Mth.PI / 10F;
			this.leftLeg.zRot = -0.07853982F;
		} else {
			this.rightArm.xRot = Mth.cos(loopPositie * 0.6662F + Mth.PI) * 2.0F * loopSnelheid * 0.5F;
			this.leftArm.xRot = Mth.cos(loopPositie * 0.6662F) * 2.0F * loopSnelheid * 0.5F;
			this.rightLeg.xRot = Mth.cos(loopPositie * 0.6662F) * 1.4F * loopSnelheid * 0.5F;
			this.rightLeg.yRot = 0.0F;
			this.rightLeg.zRot = 0.0F;
			this.leftLeg.xRot = Mth.cos(loopPositie * 0.6662F + Mth.PI) * 1.4F * loopSnelheid * 0.5F;
			this.leftLeg.yRot = 0.0F;
			this.leftLeg.zRot = 0.0F;
		}
		this.rightArm.yRot = 0.0F;
		this.rightArm.zRot = 0.0F;
		this.leftArm.yRot = 0.0F;
		this.leftArm.zRot = 0.0F;
	}
}
