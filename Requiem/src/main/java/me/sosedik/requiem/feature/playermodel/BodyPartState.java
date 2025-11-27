package me.sosedik.requiem.feature.playermodel;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.resourcelib.api.font.FontData;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class BodyPartState {

	private static final String MAX_HEALTH_TAG = "max_health";
	private static final String HEALTH_TAG = "health";
	private static final String BLEEDING_TAG = "health";

	private final BodyPart bodyPart;
	private int maxHealth;
	private int health;
	private boolean bleeding;
	private int damageTicks = 0;

	public BodyPartState(BodyPart bodyPart, @Nullable ReadableNBT nbt) {
		if (nbt == null) nbt = NBT.createNBTObject();
		this.bodyPart = bodyPart;
		this.maxHealth = nbt.getOrDefault(MAX_HEALTH_TAG, bodyPart.getDefaultMaxHealth());
		this.health = nbt.getOrDefault(HEALTH_TAG, this.maxHealth);
		this.bleeding = nbt.getOrDefault(BLEEDING_TAG, false);
	}

	public BodyPart getBodyPart() {
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

	public FontData getFontData(boolean tab) {
		if (isBleeding())
			return this.bodyPart.getFontData(tab, BodyDamage.RED);

		double ratio = getHealth() / (double) getMaxHealth();
		if (ratio >= 0.95) this.bodyPart.getFontData(tab, BodyDamage.GREEN);
		if (ratio >= 0.85) this.bodyPart.getFontData(tab, BodyDamage.LIME);
		if (ratio >= 0.5) this.bodyPart.getFontData(tab, BodyDamage.YELLOW);
		if (ratio >= 0.35) this.bodyPart.getFontData(tab, BodyDamage.ORANGE);
		if (ratio <= 0) this.bodyPart.getFontData(tab, BodyDamage.BLACK);
		return this.bodyPart.getFontData(tab, BodyDamage.RED);
	}

	public FontData getOverlayData(boolean tab) {
		return getBodyPart().getFontData(tab, BodyDamage.OVERLAY);
	}

	public void tick(Player player) {
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

	public void save(ReadWriteNBT nbt) {
		nbt.setInteger(MAX_HEALTH_TAG, getMaxHealth());
		nbt.setInteger(HEALTH_TAG, getHealth());
		nbt.setBoolean(BLEEDING_TAG, isBleeding());
	}

}
