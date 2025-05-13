package me.sosedik.trappednewbie.listener.item;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import net.kyori.adventure.sound.Sound;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Creating flaked flint from flint
 */
public class FlintToFlakedFlint implements Listener {

	private static final NamespacedKey SUCCESS_SOUND = ResourceLib.getSound(TrappedNewbie.trappedNewbieKey("item/craft_flake_success"));
	private static final NamespacedKey FAIL_SOUND = ResourceLib.getSound(TrappedNewbie.trappedNewbieKey("item/craft_flake_fail"));

	private final Map<UUID, Integer> usesMap = new HashMap<>();
	private final Set<UUID> onCooldown = new HashSet<>();

	@EventHandler(ignoreCancelled = true)
	public void onFlakedFlintCreation(PlayerArmSwingEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;

		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() != Material.FLINT) return;

		Block block = player.getTargetBlockExact(4, FluidCollisionMode.ALWAYS);
		if (block == null) return;
		if (!TrappedNewbieTags.HARDENED.isTagged(block.getType())) return;

		if (!this.onCooldown.add(player.getUniqueId())) return;

		TrappedNewbie.scheduler().sync(() -> this.onCooldown.remove(player.getUniqueId()), 10L);

		if (!this.usesMap.containsKey(player.getUniqueId())) {
			this.usesMap.put(player.getUniqueId(), 1);
			delayedMapClear(player, 1);
			return;
		}

		int currentUse = this.usesMap.get(player.getUniqueId()) + 1;
		delayedMapClear(player, currentUse);

		if (currentUse > 2 && Math.random() <= 0.5) {
			BlockFace face = player.getTargetBlockFace(4, FluidCollisionMode.ALWAYS);
			if (face == null) return;

			this.usesMap.remove(player.getUniqueId());
			item.subtract();
			player.emitSound(Sound.sound(SUCCESS_SOUND, Sound.Source.PLAYER, 1F, 0.9F + (float) Math.random() * 0.2F));
			block.getWorld().dropItemNaturally(block.getLocation().center(), new ItemStack(TrappedNewbieItems.FLAKED_FLINT, Math.random() < 0.25 ? 3 : 2));
		} else {
			this.usesMap.replace(player.getUniqueId(), currentUse);
			player.playSound(Sound.sound(FAIL_SOUND, Sound.Source.PLAYER, 1F, 0.9F + (float) Math.random() * 0.2F));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(PlayerTargetBlockEvent event) {
		if (!TrappedNewbieTags.HARDENED.isTagged(event.getBlock().getType())) return;
		if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.FLINT) return;

		event.setCancelled(true);
	}

	private void delayedMapClear(Player player, int currentUse) {
		TrappedNewbie.scheduler().async(() -> this.usesMap.remove(player.getUniqueId(), currentUse), 20 * 5L);
	}

}
