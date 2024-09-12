package me.sosedik.requiem.feature.playermodel;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.resourcelib.api.font.FontData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BodyPartState {

	private static final String MAX_HEALTH_TAG = "max_health";
	private static final String HEALTH_TAG = "health";
	private static final String BLEEDING_TAG = "health";

	private final BodyPart bodyPart;
	private int maxHealth;
	private int health;
	private boolean bleeding;
	private int damageTicks = 0;

	public BodyPartState(@NotNull BodyPart bodyPart, @Nullable ReadableNBT nbt) {
		if (nbt == null) nbt = NBT.createNBTObject();
		this.bodyPart = bodyPart;
		this.maxHealth = nbt.getOrDefault(MAX_HEALTH_TAG, bodyPart.getDefaultMaxHealth());
		this.health = nbt.getOrDefault(HEALTH_TAG, this.maxHealth);
		this.bleeding = nbt.getOrDefault(BLEEDING_TAG, false);
	}

	public @NotNull BodyPart getBodyPart() {
		return this.bodyPart;
	}

	public int getMaxHealth() {
		return this.maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getHealth() {
		return this.health;
	}

	public boolean isDead() {
		return getHealth() <= 0;
	}

	public void damage(int amount) {
		int preHealth = getHealth();
		setHealth(getHealth() - amount);
		if (getHealth() != preHealth)
			refreshDamageTicks();
	}

	public void heal(int amount) {
		int preHealth = getHealth();
		setHealth(getHealth() + amount);
		if (getHealth() != preHealth)
			refreshDamageTicks();
	}

	public void setHealth(int amount) {
		this.health = Math.clamp(amount, 0, getMaxHealth());
	}

	public boolean isBleeding() {
		return this.bleeding;
	}

	public void setBleeding(boolean bleeding) {
		this.bleeding = bleeding;
	}

	public boolean hasDamageTicks() {
		return this.damageTicks > 0;
	}

	public @NotNull FontData getFontData() {
		if (isBleeding())
			return getBodyPart().getFontData(BodyDamage.RED);

		double ratio = getHealth() / (double) getMaxHealth();
		if (ratio >= 0.95) getBodyPart().getFontData(BodyDamage.GREEN);
		if (ratio >= 0.85) getBodyPart().getFontData(BodyDamage.LIME);
		if (ratio >= 0.5) getBodyPart().getFontData(BodyDamage.YELLOW);
		if (ratio >= 0.35) getBodyPart().getFontData(BodyDamage.ORANGE);
		if (ratio <= 0) getBodyPart().getFontData(BodyDamage.BLACK);
		return getBodyPart().getFontData(BodyDamage.RED);
	}

	public @NotNull FontData getOverlayData() {
		return getBodyPart().getFontData(BodyDamage.OVERLAY);
	}

	public void tick(@NotNull Player player) {
		if (isDead()) {
			setHealth(getMaxHealth()); // TODO
			return;
		}

		if (hasDamageTicks())
			this.damageTicks--;

		if (isBleeding())
			refreshDamageTicks();
	}

	public void refreshDamageTicks() {
		this.damageTicks = 40;
	}

	public void save(@NotNull ReadWriteNBT nbt) {
		nbt.setInteger(MAX_HEALTH_TAG, getMaxHealth());
		nbt.setInteger(HEALTH_TAG, getHealth());
		nbt.setBoolean(BLEEDING_TAG, isBleeding());
	}

}
