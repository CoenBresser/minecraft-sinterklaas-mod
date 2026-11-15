package nl.sinterklaas.entiteit;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import nl.sinterklaas.SinterklaasMod;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import nl.sinterklaas.core.PietVariant;
import nl.sinterklaas.core.StafRegels;
import org.jspecify.annotations.Nullable;

/**
 * Piet: een gewoon mensfiguur, net als Sint, en vriendelijk.
 * Hij valt niet aan en doet niet mee aan raids.
 * Er is één entiteit-type; welke Piet het is staat in de variant.
 */
public class Piet extends SinterklaasFiguur {
	private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(Piet.class, EntityDataSerializers.STRING);

	/** Wat een Piet af en toe laat vallen staat in data: loot_table/gameplay/piet_cadeau.json */
	public static final ResourceKey<LootTable> CADEAU =
		ResourceKey.create(Registries.LOOT_TABLE, SinterklaasMod.id("gameplay/piet_cadeau"));
	/** Tussen twee cadeautjes zit 10 tot 20 minuten. */
	private static final int CADEAU_MIN = 12000;
	private static final int CADEAU_EXTRA = 12000;

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
	public @Nullable Player roeper() {
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
	protected void defineSynchedData(SynchedEntityData.Builder data) {
		super.defineSynchedData(data);
		data.define(VARIANT, PietVariant.ROETVEEG.id());
	}

	/** Bij natuurlijke spawn en /summon: een willekeurige Piet. Een spawn-ei zet daarna zijn eigen variant. */
	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor wereld, DifficultyInstance moeilijkheid,
			EntitySpawnReason reden, @Nullable SpawnGroupData groep) {
		zetVariant(PietVariant.willekeurig(wereld.getRandom().nextInt()));
		return super.finalizeSpawn(wereld, moeilijkheid, reden, groep);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.roepTikkenOver > 0 && --this.roepTikkenOver == 0) this.roeperId = null;
		if (this.level() instanceof ServerLevel wereld && this.isAlive() && --this.cadeauWachttijd <= 0) {
			this.cadeauWachttijd = CADEAU_MIN + this.random.nextInt(CADEAU_EXTRA);
			if (this.dropFromGiftLootTable(wereld, CADEAU, this::spawnAtLocation)) {
				this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 0.8F);
			}
		}
	}

	/** Voor tests: laat de Piet bij de volgende tik iets vallen. */
	public void laatNuIetsVallen() {
		this.cadeauWachttijd = 0;
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput uit) {
		super.addAdditionalSaveData(uit);
		uit.putString("variant", variant().id());
		uit.putInt("cadeau_wachttijd", this.cadeauWachttijd);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput in) {
		super.readAdditionalSaveData(in);
		in.getString("variant").ifPresent(id -> zetVariant(PietVariant.vanId(id)));
		in.getInt("cadeau_wachttijd").ifPresent(t -> this.cadeauWachttijd = t);
	}
}
