package me.sosedik.moves.listener.movement;

import me.sosedik.moves.Moves;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * don't lose air when head is visually above water
 */
public class DontLoseAirOnTopOfWater implements Listener {

	private static final Set<UUID> ON_DELAY = new HashSet<>();

	@EventHandler(ignoreCancelled = true)
	public void onSwim(@NotNull EntityAirChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getAmount() > player.getRemainingAir()) return;
		if (!player.isSwimming()) return;

		Location loc = player.getEyeLocation();
		if (MathUtil.getDecimalPartAbs(loc.getY()) < 0.875) return;
		if (!LocationUtil.isTrulySolid(player, loc.getBlock().getRelative(BlockFace.UP))) return;

		if (!ON_DELAY.add(player.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		int air = Math.min(event.getAmount() + 30, player.getMaximumAir());
		event.setAmount(air);
		Moves.scheduler().sync(() -> ON_DELAY.remove(player.getUniqueId()), 10L);
	}

	@EventHandler(ignoreCancelled = true)
	public void onStand(@NotNull EntityAirChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getAmount() > player.getRemainingAir()) return;
		if (player.getPose() != Pose.STANDING) return;

		Location loc = player.getEyeLocation();
		if (!(loc.getBlock().getBlockData() instanceof Levelled levelled)) return;
		if (levelled.getLevel() == 0) return;
		if (levelled.getLevel() > 2) return;

		if (!ON_DELAY.add(player.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		int air = Math.min(event.getAmount() + 30, player.getMaximumAir());
		event.setAmount(air);
		Moves.scheduler().sync(() -> ON_DELAY.remove(player.getUniqueId()), 10L);
	}

}
