package nl.sinterklaas.entiteit;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import nl.sinterklaas.SinterklaasMod;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.core.StafRegels;

/**
 * Piet: een gewoon mensfiguur, net als Sint, en vriendelijk.
 * Hij valt niet aan en doet niet mee aan raids.
 * Er is één entiteit-type; welke Piet het is staat in de variant.
 */
public class Piet extends SinterklaasFiguur {
	private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(Piet.class, EntityDataSerializers.STRING);

	/** Wat een Piet af en toe laat vallen staat in data: loot_tables/gameplay/piet_cadeau.json */
	public static final ResourceLocation CADEAU = SinterklaasMod.id("gameplay/piet_cadeau");
	/** Tussen twee cadeautjes zit 10 tot 20 minuten. */
	private static final int CADEAU_MIN = 12000;
	private static final int CADEAU_EXTRA = 12000;

	/**
	 * Welke Piet er uit het spawn-ei komt dat op dit moment wordt gebruikt (zie PietEiItem);
	 * null bij een natuurlijke spawn of /summon. Alleen op de serverdraad gezet en gelezen.
	 */
	@Nullable
	public static PietVariant variantUitEi;

	private int cadeauWachttijd;
	/** Wie deze Piet met de staf heeft geroepen, en hoe lang hij nog komt. Wordt niet opgeslagen. */
	private UUID roeperId;
	private int roepTikkenOver;

	public Piet(EntityType<? extends Piet> type, Level wereld) {
		super(type, wereld);
		this.cadeauWachttijd = CADEAU_MIN + this.random.nextInt(CADEAU_EXTRA);
	}

	public PietVariant variant() {
		return PietVariant.vanId(this.entityData.get(VARIANT));
	}

	public void zetVariant(PietVariant variant) {
		this.entityData.set(VARIANT, variant.id());
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		// Geroepen met de staf gaat voor rondwandelen, maar niet voor schrikken (paniek heeft voorrang 1).
		this.goalSelector.addGoal(2, new KomNaarRoeperDoel(this));
	}

	/** De staf heeft geklonken: kom naar deze speler toe. */
	public void wordGeroepenDoor(Player speler) {
		this.roeperId = speler.getUUID();
		this.roepTikkenOver = StafRegels.ROEP_TIKKEN;
	}

	/** De speler die deze Piet nu roept, of null als niemand hem roept (of de speler weg is). */
	@Nullable
	public Player roeper() {
		if (this.roeperId == null || this.roepTikkenOver <= 0) return null;
		Player speler = this.level().getPlayerByUUID(this.roeperId);
		return speler != null && speler.isAlive() ? speler : null;
	}

	public int roepTikkenOver() {
		return this.roepTikkenOver;
	}

	@Override
	protected String spreker() {
		return variant().id();
	}

	@Override
	protected Component getTypeName() {
		return Component.translatable("entity.sinterklaas.piet." + variant().id());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(VARIANT, PietVariant.ROETVEEG.id());
	}

	/** Bij natuurlijke spawn en /summon: een willekeurige Piet. Uit een spawn-ei komt de Piet van dat ei. */
	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor wereld, DifficultyInstance moeilijkheid, MobSpawnType reden,
			@Nullable SpawnGroupData groep, @Nullable CompoundTag gegevens) {
		zetVariant(variantUitEi != null ? variantUitEi : PietVariant.willekeurig(wereld.getRandom().nextInt()));
		return super.finalizeSpawn(wereld, moeilijkheid, reden, groep, gegevens);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.roepTikkenOver > 0 && --this.roepTikkenOver == 0) this.roeperId = null;
		if (this.level() instanceof ServerLevel wereld && this.isAlive() && --this.cadeauWachttijd <= 0) {
			this.cadeauWachttijd = CADEAU_MIN + this.random.nextInt(CADEAU_EXTRA);
			LootParams params = new LootParams.Builder(wereld)
				.withParameter(LootContextParams.ORIGIN, this.position())
				.withParameter(LootContextParams.THIS_ENTITY, this)
				.create(LootContextParamSets.GIFT);
			boolean ietsGevallen = false;
			for (ItemStack stapel : wereld.getServer().getLootData().getLootTable(CADEAU).getRandomItems(params)) {
				this.spawnAtLocation(stapel);
				ietsGevallen = true;
			}
			if (ietsGevallen) this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 0.8F);
		}
	}

	/** Voor tests: laat de Piet bij de volgende tik iets vallen. */
	public void laatNuIetsVallen() {
		this.cadeauWachttijd = 0;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag uit) {
		super.addAdditionalSaveData(uit);
		uit.putString("variant", variant().id());
		uit.putInt("cadeau_wachttijd", this.cadeauWachttijd);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag in) {
		super.readAdditionalSaveData(in);
		if (in.contains("variant", Tag.TAG_STRING)) zetVariant(PietVariant.vanId(in.getString("variant")));
		if (in.contains("cadeau_wachttijd", Tag.TAG_INT)) this.cadeauWachttijd = in.getInt("cadeau_wachttijd");
	}
}
