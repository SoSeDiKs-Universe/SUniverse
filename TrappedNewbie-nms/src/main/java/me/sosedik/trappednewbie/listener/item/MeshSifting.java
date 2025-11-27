package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.util.ProbabilityCollection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Sifting items through meshes
 */
public class MeshSifting implements Listener {

	public static final ProbabilityCollection<Material> EXTRA_DROPS = new ProbabilityCollection<>();

	static {
		EXTRA_DROPS.add(TrappedNewbieItems.ROCK, 10);
		EXTRA_DROPS.add(TrappedNewbieItems.ANDESITE_ROCK, 3);
		EXTRA_DROPS.add(TrappedNewbieItems.DIORITE_ROCK, 3);
		EXTRA_DROPS.add(TrappedNewbieItems.GRANITE_ROCK, 3);
		EXTRA_DROPS.add(TrappedNewbieItems.BALL_OF_MUD, 20);
		EXTRA_DROPS.add(TrappedNewbieItems.FLAKED_FLINT, 10);
		EXTRA_DROPS.add(TrappedNewbieItems.ASH, 2);
		EXTRA_DROPS.add(Material.GUNPOWDER, 10);
		EXTRA_DROPS.add(Material.NAUTILUS_SHELL, 1);
		EXTRA_DROPS.add(Material.FEATHER, 7);
		EXTRA_DROPS.add(Material.CLAY_BALL, 8);
		EXTRA_DROPS.add(Material.STRING, 2);
		EXTRA_DROPS.add(Material.BOWL, 1);
		EXTRA_DROPS.add(Material.CHARCOAL, 4);
		EXTRA_DROPS.add(null, 65);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onUse(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (block.getType() != Material.GRAVEL) return;

		Player player = event.getPlayer();
		if (tryToUseMesh(player, block, EquipmentSlot.HAND) || tryToUseMesh(player, block, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToUseMesh(Player player, Block block, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != TrappedNewbieItems.GRASS_MESH) return false;
		if (player.hasCooldown(item)) return false;

		block.setType(Material.AIR);
		Location loc = block.getLocation().center();
		block.getWorld().dropItemNaturally(loc, ItemStack.of(Material.FLINT));

		Material extra = EXTRA_DROPS.get();
		if (extra != null) {
			block.getWorld().dropItemNaturally(loc, ItemStack.of(extra));
			TrappedNewbieAdvancements.TREASURE_HUNT.awardAllCriteria(player);
		}
		player.swingHand(hand);
		player.setCooldown(item.getType(), 15);
		block.emitSound(Sound.BLOCK_GRAVEL_BREAK, 1F, 1F);
		item.damage(1, player);
		return true;
	}

}
