package me.sosedik.trappednewbie.impl.thirst;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import me.sosedik.trappednewbie.listener.thirst.ThirstOnJoinLeave;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ThirstyPlayer {

	public static final int MAX_THIRST = 20;
	public static final int MIN_THIRST = 0;

	private static final String THIRST_TAG = "thirst";
	private static final String SATURATION_TAG = "saturation";

	private final Player player;
	private int thirst;
	private float saturation;
	private @Nullable BukkitTask damageTask = null;

	public ThirstyPlayer(Player player, ReadableNBT data) {
		this.player = player;
		this.thirst = data.getOrDefault(THIRST_TAG, MAX_THIRST);
		this.saturation = data.getOrDefault(SATURATION_TAG, 0F);
	}

	public Player getPlayer() {
		return this.player;
	}

	public boolean canRun() {
		return getThirst() > 6;
	}

	public int getThirst() {
		return this.thirst;
	}

	public boolean hasFullThirst() {
		return getThirst() >= MAX_THIRST;
	}

	public void setThirst(int thirst) {
		this.thirst = Math.clamp(thirst, MIN_THIRST, MAX_THIRST);
		this.player.sendHealthUpdate();
		if (getThirst() <= 0)
			startThirstDamageTask();
	}

	public void addThirst(int thirst) {
		if (thirst > 0) {
			setThirst(getThirst() + thirst);
			return;
		}
		float leftover = thirst + this.saturation;
		if (leftover < 0) {
			this.saturation = 0F;
			setThirst(getThirst() + (int) leftover);
			return;
		}
		this.saturation = leftover;
	}

	public float getSaturation() {
		return this.saturation;
	}

	public void setSaturation(float saturation) {
		this.saturation = Math.clamp(saturation, MIN_THIRST, this.thirst);
	}

	public void addThirst(ThirstData thirstData) {
		if (thirstData.isDummy()) return;

		Player player = getPlayer();
		if (thirstData.drinkType() != null && thirstData.thirst() > 0) {
			TrappedNewbieAdvancements.FIRST_DRINK.awardAllCriteria(player);
//			if (thirstData.thirstChance() <= 0)
//				TrappedNewbieAdvancements.DRINK_PURE_WATER.awardAllCriteria(player);
		}

		if (thirstData.thirstChance() > 0 && Math.random() < thirstData.thirstChance())
			player.addPotionEffect(thirstData.thirstSource() == null ? new PotionEffect(TrappedNewbieEffects.THIRST, 15 * 20, 0) : thirstData.thirstSource().getEffect());

//		if (thirstData.cooled()) // TODO temperature
//			TemperaturedPlayer.of(player).addFlag(TempFlag.COLD_DRINK);

		setSaturation(getSaturation() + thirstData.saturation());
		addThirst(thirstData.thirst());
	}

	private void startThirstDamageTask() {
		if (this.damageTask != null && !this.damageTask.isCancelled()) return;

		this.damageTask = TrappedNewbie.scheduler().sync(task -> {
			if (getThirst() > 0) return true;
			if (this.player.isDead()) return true;
			if (!this.player.isOnline()) return true;

			this.player.damage(1, DamageSource.builder(TrappedNewbieDamageTypes.THIRST_DAMAGE).build());
			return false;
		}, 3 * 20L, 3 * 20L);
	}

	public ReadableNBT save() {
		var nbt = NBT.createNBTObject();
		nbt.setInteger(THIRST_TAG, this.thirst);
		nbt.setFloat(SATURATION_TAG, this.saturation);
		return nbt;
	}

	public static ThirstyPlayer of(Player player) {
		return ThirstOnJoinLeave.of(player);
	}

}
