package me.sosedik.requiem.task;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.requiem.Requiem;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Allows ghosts going through walls
 */
public class GoingThroughWallsTask extends BukkitRunnable {

	private static final float NOT_PASSABLE_THRESHOLD = Material.OBSIDIAN.getBlastResistance();
	private static final PotionEffect INFINITE_BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, Integer.MAX_VALUE, false, false, false);

	private final Player player;
	private final ItemStack item;
	private Location lastClearLoc;
	private int tries = 0;

	public GoingThroughWallsTask(@NotNull Player player, @NotNull ItemStack item) {
		this.player = player;
		this.item = item;
		this.lastClearLoc = player.getLocation();

		Requiem.scheduler().sync(this, 0L, 1L);
	}

	@Override
	public void run() {
		Location loc = player.getLocation();
		if (isEmpty(loc.getBlock()))  {
			if (isEmpty(loc.getBlock().getRelative(BlockFace.UP))) {
				if (!item.isSimilar(player.getActiveItem())) {
					cancel();
					restoreView();
					return;
				}
				lastClearLoc = loc;
				restoreView();
				goForward();
				return;
			}

			if (!item.isSimilar(player.getActiveItem())) {
				goBack();
				return;
			}

			goForward();
			return;
		}

		player.sendPotionEffectChange(player, INFINITE_BLINDNESS);

		if (!isExempt(loc.getBlock().getType()) && lastClearLoc.getWorld() == loc.getWorld() && lastClearLoc.distanceSquared(loc) > 49) {
			goBack();
			return;
		}

		if (!item.isSimilar(player.getActiveItem())) {
			goBack();
			return;
		}

		goForward();
	}

	private void restoreView() {
		player.sendPotionEffectChangeRemove(player, PotionEffectType.BLINDNESS);
	}

	private void goForward() {
		tries++;
		Location forward = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(tries > 10 ? (tries > 20 ? 0.3 : 0.2 ) : 0.1));
		if (!isUnpassable(forward.getBlock()))
			player.teleport(forward, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH, TeleportFlag.EntityState.RETAIN_PASSENGERS);
	}

	private void goBack() {
		cancel();
		restoreView();
		if (isEmpty(player.getEyeLocation().getBlock())) return;
		lastClearLoc.center(0.1);
		player.teleport(lastClearLoc, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		player.playSound(lastClearLoc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1F, 0F);
		player.clearActiveItem();
		player.setCooldown(Material.BOW, 10);
	}

	private boolean isEmpty(@NotNull Block block) {
		return !LocationUtil.isTrulySolid(player, block);
	}

	private boolean isExempt(@NotNull Material type) {
		return Tag.SAND.isTagged(type)
				|| type == Material.GRAVEL || type == Material.SUSPICIOUS_GRAVEL
				|| MaterialTags.GLASS.isTagged(type)
				|| MaterialTags.GLASS_PANES.isTagged(type)
				|| Tag.WALLS.isTagged(type);
	}

	private boolean isUnpassable(@NotNull Block block) {
		return block.getType().getBlastResistance() > NOT_PASSABLE_THRESHOLD;
	}

}
