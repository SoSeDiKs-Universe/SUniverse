package me.sosedik.trappednewbie.impl.hud;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.hud.SimpleHudRenderer;
import me.sosedik.trappednewbie.listener.misc.CustomHudRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.combine;

@NullMarked
public class HealthRenderer extends SimpleHudRenderer {

	private static final int HEART_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("heart/red_heart")).width() + 1;
	private static final int HUD_OFFSET = -91;
	private static final Component[] EMPTY_HEARTS = new Component[]{mapping("heart/empty_heart"), mapping("heart/empty_heart_down")};

	private static final Component[][] RED_HEARTS = hearts("red_heart");
	private static final Component[][] WITHERED_HEARTS = hearts("withered_heart");
	private static final Component[][] POISONED_HEARTS = hearts("poisoned_heart");
	private static final Component[][] GHOST_HEARTS = hearts("ghost_heart");
	private static final Component[][] COLD_HEARTS = hearts("cold_heart");
	private static final Component[][][] FIRE_HEARTS = new Component[][][]{
		hearts("fire_heart_1"),
		hearts("fire_heart_2"),
		hearts("fire_heart_3")
	};

	private static final int TICKS = 3;
	private static final int TICK_DURATION = 2;

	private final List<Component> healthDisplay = new ArrayList<>();
	private int priorHealth = -1;
	private int damageTick = 0;
	private int tickDuration = 0;
	private int fireHeartsTick = 0;

	public HealthRenderer(Player player) {
		super("health_hud", player);
	}

	@Override
	public @Nullable Component render() {
		if (this.player.getGameMode().isInvulnerable()) return null;

		int[] health = getHealth();
		int currentHealth = health[0];
		int maxHealth = health[1];
		if (maxHealth <= 0) return null;

		Component[][] hearts = getHeartsType();
		if (hearts == null) return null;

		if (this.priorHealth != -1 && this.priorHealth != currentHealth)
			forceDamageTick();

		this.priorHealth = currentHealth;

		this.healthDisplay.clear();

		// Display last half heart as full at full health if max health is uneven
		if (maxHealth % 2 == 1) {
			if (currentHealth == maxHealth)
				currentHealth++;
			maxHealth++;
		}

		boolean lowHp = currentHealth < maxHealth / 4;
		boolean tick = false;
		if (this.damageTick > 0) {
			if (this.tickDuration-- == 0) {
				this.damageTick--;
				this.tickDuration = TICK_DURATION;
			}
			if (this.damageTick % 2 == 1)
				tick = true;
		}

		// Full hearts
		for (int i = 0; i < currentHealth / 2; i++) this.healthDisplay.add(getHearts(hearts, tick, lowHp)[0]);

		// Half heart
		if (currentHealth % 2 == 1) {
			currentHealth++;
			this.healthDisplay.add(getHearts(hearts, tick, lowHp)[1]);
		}

		// Empty hearts
		for (int i = 0; i < (maxHealth - currentHealth) / 2; i++) this.healthDisplay.add(getHearts(hearts, tick, lowHp)[2]);

		Component combined = combine(SpacingUtil.getSpacing(-2), this.healthDisplay);
		return SpacingUtil.getOffset(HUD_OFFSET, (HEART_WIDTH - 2) * (maxHealth / 2) + 2, combined.shadowColor(ShadowColor.none()));
	}

	public void forceDamageTick() {
		this.damageTick = (TICKS * 2) - 1;
		this.tickDuration = TICK_DURATION;
	}

	private int[] getHealth() {
		double health;
		double maxHealth;
		if (this.player.getVehicle() instanceof LivingEntity vehicle && vehicle.getRider() == this.player && PossessingPlayer.isPossessing(this.player)) {
			health = vehicle.getHealth();
			maxHealth = vehicle.getMaxHealth();
		} else {
			health = this.player.getHealth();
			maxHealth = this.player.getMaxHealth();
		}
		if (maxHealth <= 20) return new int[]{(int) Math.min(Math.ceil(health), 20), (int) Math.ceil(maxHealth)};
		return new int[]{(int) Math.min(Math.ceil(health * 20 / maxHealth), 20), 20};
	}

	private Component @Nullable [][] getHeartsType() {
		if (GhostyPlayer.isGhost(this.player)) {
			return null;
		} else if (this.player.hasPotionEffect(PotionEffectType.WITHER)) {
			return WITHERED_HEARTS;
		} else if (false /* HYPOTHERMIA && !COLD_RESISTANCE */) {
			return COLD_HEARTS;
		} else if (false /* HYPERTHERMIA && !HEAT_RESISTANCE (&& !FIRE_RESISTANCE ?) */) {
			return getFireHearts();
		} else if (this.player.getFireTicks() > 0 && !this.player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
			return getFireHearts();
		} else if (this.player.hasPotionEffect(PotionEffectType.POISON)) {
			return POISONED_HEARTS;
		} else {
			return RED_HEARTS;
		}
	}

	private Component[][] getFireHearts() {
		if (fireHeartsTick < 2) fireHeartsTick++;
		else fireHeartsTick = 0;
		return FIRE_HEARTS[fireHeartsTick];
	}

	private Component[] getHearts(Component[][] hearts, boolean tick, boolean lowHp) {
		return tick ? getLowHp(hearts[2], hearts[3], lowHp) : getLowHp(hearts[0], hearts[1], lowHp);
	}

	private Component[] getLowHp(Component[] base, Component[] down, boolean lowHp) {
		if (!lowHp) return base;
		return Math.random() > 0.5 ? down : base;
	}

	private static Component[][] hearts(String mapping) {
		mapping = "heart/" + mapping;
		Component[][] hearts = new Component[4][];
		hearts[0] = hearts(mapping, "heart/half_heart", EMPTY_HEARTS[0]);
		hearts[1] = hearts(mapping + "_down", "heart/half_heart_down", EMPTY_HEARTS[1]);
		hearts[2] = ticked(hearts[0], "heart/heart_hurt_overlay");
		hearts[3] = ticked(hearts[1], "heart/heart_hurt_overlay_down");
		return hearts;
	}

	private static Component[] ticked(Component[] base, String overlay) {
		Component[] ticked = new Component[base.length];
		Component overlayTick = mapping(overlay);
		for (int i = 0; i < base.length; i++)
			ticked[i] = overlayIcon(base[i], overlayTick, ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey(overlay)).width());
		return ticked;
	}

	private static Component[] hearts(String full, String half, Component empty) {
		Component fullHeart = mapping(full);
		Component halfHeart = mapping(half);
		return new Component[]{fullHeart, overlayIcon(fullHeart, halfHeart/*SpacingUtil.getOffset(1, HEART_WIDTH, halfHeart)*/, HEART_WIDTH - 1), empty};
	}

	public static void forceDamageTick(Player player) {
		CustomHudRenderer.getRenderer(player, HealthRenderer.class).forceDamageTick();
	}

}
