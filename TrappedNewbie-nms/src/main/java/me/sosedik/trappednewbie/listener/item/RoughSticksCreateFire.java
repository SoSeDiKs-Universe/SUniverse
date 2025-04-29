package me.sosedik.trappednewbie.listener.item;

import me.sosedik.miscme.listener.player.FireExtinguishByHand;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Rough sticks can create fire
 */
@NullMarked
public class RoughSticksCreateFire implements Listener {

	private final Map<UUID, Integer> usesMap = new HashMap<>();
	private final Random random = new Random();

	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		boolean rightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;
		if (!rightClick && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;
		if (player.getInventory().getItemInOffHand().getType() != TrappedNewbieItems.ROUGH_STICK) return;

		if (!this.usesMap.containsKey(player.getUniqueId())) {
			if (!rightClick) {
				this.usesMap.put(player.getUniqueId(), 1);
				delayedMapClear(player, 1);
			}
			return;
		}

		int currentUse = this.usesMap.get(player.getUniqueId());
		if (rightClick) {
			if (currentUse % 2 == 0) return;

			player.swingOffHand();
		} else if (currentUse % 2 != 0) return;

		if (currentUse >= this.random.nextInt(3) + 4) {
			Block block = Objects.requireNonNull(event.getClickedBlock()).getRelative(BlockFace.UP);
			if (block.getType().isAir()) {
				this.usesMap.remove(player.getUniqueId());
				FireExtinguishByHand.addFireImmunity(player, 10);
				block.setType(Material.FIRE);
				player.getInventory().getItemInMainHand().subtract();
				player.getInventory().getItemInOffHand().subtract();
				block.emitSound(Sound.ITEM_FIRECHARGE_USE, 1F, 1F);
				TrappedNewbieAdvancements.MAKE_A_FIRE.awardAllCriteria(player);
			}
		} else {
			this.usesMap.replace(player.getUniqueId(), ++currentUse);
			delayedMapClear(player, currentUse);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(PlayerTargetBlockEvent event) {
		Player player = event.getPlayer();
		if (player.getInventory().getItemInMainHand().getType() != TrappedNewbieItems.ROUGH_STICK) return;
		if (player.getInventory().getItemInOffHand().getType() != TrappedNewbieItems.ROUGH_STICK) return;

		event.setCancelled(true);
	}

	private void delayedMapClear(Player player, int currentUse) {
		TrappedNewbie.scheduler().async(() -> this.usesMap.remove(player.getUniqueId(), currentUse), 20 * 5L);
	}

}
