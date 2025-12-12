package me.sosedik.requiem.task;

import com.destroystokyo.paper.MaterialTags;
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
import org.jspecify.annotations.NullMarked;

/**
 * Allows ghosts going through walls
 */
@NullMarked
public class GoingThroughWallsTask extends BukkitRunnable {

	private static final float NOT_PASSABLE_THRESHOLD = Material.OBSIDIAN.getBlastResistance();
	private static final PotionEffect INFINITE_BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, Integer.MAX_VALUE, false, false, false);

	private final Player player;
	private final ItemStack item;
	private Location lastClearLoc;
	private int tries = 0;

	public GoingThroughWallsTask(Player player, ItemStack item) {
		this.player = player;
		this.item = item;
		this.lastClearLoc = player.getLocation();

		Requiem.scheduler().sync(this, 0L, 1L);
	}

	@Override
	public void run() {
		Location loc = this.player.getLocation();
		if (isEmpty(loc.getBlock()))  {
			if (isEmpty(loc.getBlock().getRelative(BlockFace.UP))) {
				if (!this.item.isSimilar(this.player.getActiveItem())) {
					cancel();
					restoreView();
					return;
				}
				this.lastClearLoc = loc;
				restoreView();
				goForward();
				return;
			}

			if (!this.item.isSimilar(this.player.getActiveItem())) {
				goBack();
				return;
			}

			goForward();
			return;
		}

		this.player.sendPotionEffectChange(this.player, INFINITE_BLINDNESS);

		if (!isExempt(loc.getBlock().getType()) && this.lastClearLoc.getWorld() == loc.getWorld() && this.lastClearLoc.distanceSquared(loc) > 49) {
			goBack();
			return;
		}

		if (!this.item.isSimilar(this.player.getActiveItem())) {
			goBack();
			return;
		}

		goForward();
	}

	private void restoreView() {
		this.player.sendPotionEffectChangeRemove(this.player, PotionEffectType.BLINDNESS);
	}

	private void goForward() {
		this.tries++;
		Location forward = this.player.getLocation().add(this.player.getLocation().getDirection().normalize().multiply(this.tries > 10 ? (this.tries > 20 ? 0.3 : 0.2 ) : 0.1));
		if (!isUnpassable(forward.getBlock()))
			LocationUtil.smartTeleport(this.player, forward, true);
	}

	private void goBack() {
		cancel();
		restoreView();
		if (isEmpty(this.player.getEyeLocation().getBlock())) return;
		this.lastClearLoc.center(0.1);
		LocationUtil.smartTeleport(this.player, this.lastClearLoc, false);
		this.player.playSound(this.lastClearLoc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1F, 0F);
		this.player.clearActiveItem();
		this.player.setCooldown(this.item, 10);
	}

	private boolean isEmpty(Block block) {
		return !LocationUtil.isTrulySolid(this.player, block);
	}

	// MCCheck: 1.21.11, new blocks
	private boolean isExempt(Material type) {
		return Tag.SAND.isTagged(type)
			|| type == Material.GRAVEL || type == Material.SUSPICIOUS_GRAVEL
			|| MaterialTags.GLASS.isTagged(type)
			|| MaterialTags.GLASS_PANES.isTagged(type)
			|| Tag.WALLS.isTagged(type);
	}

	private boolean isUnpassable(Block block) {
		return block.getType().getBlastResistance() > NOT_PASSABLE_THRESHOLD;
	}

}
