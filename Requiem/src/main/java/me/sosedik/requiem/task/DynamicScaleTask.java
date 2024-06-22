package me.sosedik.requiem.task;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Dynamically scales player's size to prevent clipping camera into blocks
 */
// TODO not ideal, has hardcoded scales, but it works, for now
public class DynamicScaleTask extends BukkitRunnable {

	private static final PotionEffect INFINITE_BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, Integer.MAX_VALUE, false, false, false);

	private final Player player;
	private final LivingEntity entity;
	private double previousHeight;
	private double shrinkHeightDiff = 0.000001;

	public DynamicScaleTask(@NotNull Player player, @NotNull LivingEntity entity) {
		this.player = player;
		this.entity = entity;
		this.previousHeight = entity.getHeight();
		Requiem.scheduler().sync(this, 0L, 1L);
	}

	@Override
	public void run() {
		if (!player.isOnline() || PossessingPlayer.getPossessed(player) != entity) {
			cancel();
			return;
		}

		if (player.getEyeLocation().addY(0.01).getBlock().isSolid()) {
			player.addPotionEffect(INFINITE_BLINDNESS);
		} else {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		}

		Location loc = entity.getLocation();
		double entityHeight = entity.getHeight();
		if (shrinkHeightDiff == -1) {
			shrinkHeightDiff = previousHeight - entityHeight;
			if (shrinkHeightDiff == 0) shrinkHeightDiff = 0.000001;
		}
		double height = loc.clone().subtract(loc.toBlockLocation()).getY() + entityHeight + shrinkHeightDiff;
		if (loc.clone().addY(height + 0.5).getBlock().isSolid()) {
			if (shrinkHeightDiff != 0) return;

			double playerScale = 0.5;
			double entityScale = 1;
			double heightToRoof = 1D - MathUtil.getDecimalPart(height);
			if (heightToRoof < 0.15) {
				playerScale = 0.1;
				entityScale = 0.8;
			} else if (heightToRoof < 0.4) {
				playerScale = 0.2;
			}
			scale(player).setBaseValue(playerScale);
			scale(entity).setBaseValue(entityScale);
			previousHeight = entityHeight;
			shrinkHeightDiff = -1;
		} else {
			if (shrinkHeightDiff == 0) return;

			scale(player).setBaseValue(0.5);
			scale(entity).setBaseValue(1);
			shrinkHeightDiff = 0;
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		player.removePotionEffect(PotionEffectType.BLINDNESS);
		scale(player).setBaseValue(1);
		scale(entity).setBaseValue(1);
	}

	private static @NotNull AttributeInstance scale(@NotNull LivingEntity entity) {
		return Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SCALE));
	}

}
