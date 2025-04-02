package me.sosedik.utilizer.listener.item;

import me.sosedik.kiterino.event.entity.EntityLoadsProjectileEvent;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Allow using bow-based items without arrows
 */
@NullMarked
public class BowUsableWithoutArrows implements Listener {

	private static final ItemStack DUMMY_ARROW = new ItemStack(Material.ARROW);

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.OFF_HAND) return;
		if (event.getClickedBlock() == null) return;

		Player player = event.getPlayer();
		if (player.hasActiveItem()) return;

		if (UtilizerTags.NO_ARROW_USABLE.isTagged(player.getInventory().getItemInMainHand().getType()))
			player.startUsingItem(EquipmentSlot.HAND);
		else if (UtilizerTags.NO_ARROW_USABLE.isTagged(player.getInventory().getItemInOffHand().getType()))
			player.startUsingItem(EquipmentSlot.OFF_HAND);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAirInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() != Event.Result.DENY) return;
		if (event.getClickedBlock() != null) return;
		if (event.getHand() == null) return;

		Player player = event.getPlayer();
		if (!UtilizerTags.NO_ARROW_USABLE.isTagged(player.getInventory().getItem(event.getHand()).getType())) return;

		event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(EntityLoadsProjectileEvent event) {
		if (!event.getProjectile().isEmpty()) return;
		if (!UtilizerTags.NO_ARROW_USABLE.isTagged(event.getWeapon().getType())) return;

		event.setProjectile(DUMMY_ARROW);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onShoot(EntityShootBowEvent event) {
		ItemStack weapon = event.getBow();
		if (weapon == null) return;
		if (!UtilizerTags.NO_ARROW_USABLE.isTagged(weapon.getType())) return;

		event.setCancelled(true);
		if (event.getEntity() instanceof Player player) player.updateInventory();
	}

}
