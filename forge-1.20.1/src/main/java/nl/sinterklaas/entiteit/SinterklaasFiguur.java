package nl.sinterklaas.entiteit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.core.PraatRegels;
import nl.sinterklaas.core.SeizoenRegels;

/**
 * Gedeeld gedrag van Sint en de Pieten: vriendelijk rondlopen en praten.
 * Ze zeggen af en toe vanzelf iets (met een pauze ertussen) en reageren op een rechtsklik.
 */
public abstract class SinterklaasFiguur extends PathfinderMob {
	private int praatWachttijd;
	private long laatsteKlik = Long.MIN_VALUE / 2;
	/** Bij welk Sinterklaasseizoen (jaar) deze figuur hoort; 0 = nog niet bepaald. */
	private int seizoensJaar;
	/** Zo vaak kijken we of het al 6 december is (100 tikken = 5 seconden). */
	private static final int KIJK_ELKE = 100;

	protected SinterklaasFiguur(EntityType<? extends SinterklaasFiguur> type, Level wereld) {
		super(type, wereld);
		this.praatWachttijd = PraatRegels.spontanePauze(this.random.nextInt());
	}

	/** Onder welke naam de zinnen in teksten.json staan, bijvoorbeeld "sint" of "koek". */
	protected abstract String spreker();

	public static AttributeSupplier.Builder maakAttributen() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 20.0)
			.add(Attributes.MOVEMENT_SPEED, 0.3);
	}

	/**
	 * Overdag, op een gewone ondergrond, onder de open lucht.
	 * Vanzelf verschijnen kan alleen in het seizoen (15 november t/m 5 december), en steeds vaker
	 * naarmate 5 december dichterbij komt. Spawn-eieren en commando's komen hier niet langs.
	 */
	public static boolean magHierSpawnen(EntityType<? extends Mob> type, ServerLevelAccessor wereld, MobSpawnType reden,
			BlockPos plek, RandomSource toeval) {
		boolean vanzelf = reden == MobSpawnType.NATURAL || reden == MobSpawnType.CHUNK_GENERATION;
		if (vanzelf && toeval.nextDouble() >= SeizoenRegels.spawnKans(SinterklaasMod.KLOK.vandaag())) return false;
		return Mob.checkMobSpawnRules(type, wereld, reden, plek, toeval)
			&& wereld.getRawBrightness(plek, 0) > 8
			&& wereld.canSeeSky(plek);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double afstand) {
		return false;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.level() instanceof ServerLevel server && this.isAlive() && this.tickCount % KIJK_ELKE == 1) {
			if (this.seizoensJaar == 0) this.seizoensJaar = SeizoenRegels.seizoensJaar(SinterklaasMod.KLOK.vandaag());
			if (SeizoenRegels.moetVertrekken(this.seizoensJaar, SinterklaasMod.KLOK.vandaag())) {
				vertrek(server);
				return;
			}
		}
		if (this.level() instanceof ServerLevel wereld && this.isAlive() && --this.praatWachttijd <= 0) {
			this.praatWachttijd = PraatRegels.spontanePauze(this.random.nextInt());
			// Alleen praten als er iemand in de buurt is om het te horen.
			if (wereld.getNearestPlayer(this, PraatRegels.GEHOORSAFSTAND) != null) {
				Component bericht = maakBericht();
				if (bericht != null) {
					for (ServerPlayer speler : wereld.players()) {
						if (speler.distanceToSqr(this) <= PraatRegels.GEHOORSAFSTAND * PraatRegels.GEHOORSAFSTAND) {
							speler.sendSystemMessage(bericht);
						}
					}
				}
			}
		}
	}

	@Override
	protected InteractionResult mobInteract(Player speler, InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
		if (this.level() instanceof ServerLevel wereld) {
			long nu = wereld.getGameTime();
			if (nu - this.laatsteKlik >= PraatRegels.KLIK_PAUZE) {
				this.laatsteKlik = nu;
				Component bericht = maakBericht();
				if (bericht != null) speler.sendSystemMessage(bericht);
				this.getLookControl().setLookAt(speler);
			}
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide);
	}

	/** Na 5 december gaat iedereen terug naar Spanje: een wolkje en weg. */
	private void vertrek(ServerLevel wereld) {
		wereld.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1.0, this.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
		wereld.playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.6F, 0.6F);
		this.discard();
	}

	public int seizoensJaar() {
		return seizoensJaar;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag uit) {
		super.addAdditionalSaveData(uit);
		uit.putInt("seizoensjaar", this.seizoensJaar);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag in) {
		super.readAdditionalSaveData(in);
		this.seizoensJaar = in.getInt("seizoensjaar");
	}

	/** Een willekeurige zin van deze spreker, in chatvorm: "<Naam> zin". */
	private Component maakBericht() {
		String zin = PraatRegels.kies(SinterklaasMod.TEKSTEN.zinnen(spreker()), this.random.nextInt());
		return zin == null ? null : Component.translatable("chat.type.text", this.getDisplayName(), zin);
	}
}
