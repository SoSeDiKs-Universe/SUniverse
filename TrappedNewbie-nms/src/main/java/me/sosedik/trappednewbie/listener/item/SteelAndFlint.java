package me.sosedik.trappednewbie.listener.item;

import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Flint and Steel! (I mean, it's your fault, really)
 */
@NullMarked
public class SteelAndFlint implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof LivingEntity entity)) return;

		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return;

		ItemStack item = equipment.getItemInMainHand();
		if (item.getType() != TrappedNewbieItems.STEEL_AND_FLINT) {
			item = equipment.getItemInOffHand();
			if (item.getType() != TrappedNewbieItems.STEEL_AND_FLINT) return;
		}

		item.damage(1, entity);
		entity.emitSound(Sound.ITEM_FLINTANDSTEEL_USE, 1F, (float) Math.random() * 0.4F + 0.8F);
		entity.setFireTicks(entity.getFireTicks() + (5 * 20));
		BurningProjectileCreatesFire.tryToLit(entity.getLocation().getBlock());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;

		Player player = event.getPlayer();
		EquipmentSlot hand = EquipmentSlot.HAND;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() != TrappedNewbieItems.STEEL_AND_FLINT) {
			item = player.getInventory().getItemInOffHand();
			if (item.getType() != TrappedNewbieItems.STEEL_AND_FLINT) return;
			if (event.getClickedBlock() == null) return;

			hand = EquipmentSlot.OFF_HAND;
		}

		player.swingHand(hand);
		item.damage(1, player);
		player.emitSound(Sound.ITEM_FLINTANDSTEEL_USE, 1F, (float) Math.random() * 0.4F + 0.8F);
		player.setFireTicks(player.getFireTicks() + (5 * 20));
		BurningProjectileCreatesFire.tryToLit(player.getLocation().getBlock());
	}

}
