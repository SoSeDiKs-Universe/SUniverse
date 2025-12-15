package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.util.DurabilityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Shovels convert additional blocks on sneak + right click
 */
@NullMarked
public class ShovelsConvertAdditionalBlocks implements Listener {

	private static final Set<UUID> COOLDOWNS = new HashSet<>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (COOLDOWNS.contains(player.getUniqueId())) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Material converted = getConverted(block);
		if (converted == null) return;

		if (tryToConvert(player, EquipmentSlot.HAND)
			|| tryToConvert(player, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
			block.getLocation().center(1).getNearbyLivingEntities(0.25, 0.1, 0.25)
				.forEach(entity -> {
					Vector velocity = entity.getVelocity();
					if (Math.abs(velocity.getY()) > 0.2) return;
					entity.setVelocity(velocity.setY(0.25));
				});
			block.setType(converted);
		}
	}

	private boolean tryToConvert(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!Tag.ITEMS_SHOVELS.isTagged(item.getType())) return false;
		if (player.hasCooldown(item)) return false;
		if (DurabilityUtil.isBroken(item)) return false;

		COOLDOWNS.add(player.getUniqueId());
		if (Math.random() < 0.1) item.damage(1, player);
		MiscMe.scheduler().sync(() -> COOLDOWNS.remove(player.getUniqueId()), 2);
		player.swingHand(hand);
		player.emitSound(Sound.BLOCK_GRASS_PLACE, 1F, 1F);

		return true;
	}

	private @Nullable Material getConverted(Block block) {
		return switch (block.getType()) {
			case FARMLAND, DIRT_PATH -> Material.DIRT;
			default -> null;
		};
	}

}
