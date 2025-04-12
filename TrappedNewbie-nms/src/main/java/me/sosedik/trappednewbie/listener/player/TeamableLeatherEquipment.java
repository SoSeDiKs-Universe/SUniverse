package me.sosedik.trappednewbie.listener.player;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Wearing the same leather chestplate or helmet will cancel the dealt damage
 */
@NullMarked
public class TeamableLeatherEquipment implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDamage(PrePlayerAttackEntityEvent event) {
		if (!(event.getAttacked() instanceof Player damaged)) return;

		Player damager = event.getPlayer();
		if (checkEquipment(damaged, damager, EquipmentSlot.CHEST, Material.LEATHER_CHESTPLATE)
				|| checkEquipment(damaged, damager, EquipmentSlot.HEAD, Material.LEATHER_HELMET)) {
			event.setCancelled(true);
			damaged.emitSound(Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 0.7F, 1.5F);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDamage(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof Player damaged)) return;

		Projectile projectile = event.getEntity();
		if (!(projectile.getShooter() instanceof Player shooter)) return;

		if (checkEquipment(damaged, shooter, EquipmentSlot.CHEST, Material.LEATHER_CHESTPLATE)
				|| checkEquipment(damaged, shooter, EquipmentSlot.HEAD, Material.LEATHER_HELMET)) {
			event.setCancelled(true);
		}
	}

	private boolean checkEquipment(Player player1, Player player2, EquipmentSlot slot, Material type) {
		ItemStack item1 = getVisualItem(player1, slot);
		if (item1.getType() != type) return false;

		ItemStack item2 = getVisualItem(player2, slot);
		if (item2.getType() != type) return false;

		if (!item1.hasData(DataComponentTypes.DYED_COLOR)) return false;
		if (!item2.hasData(DataComponentTypes.DYED_COLOR)) return false;

		Color color1 = Objects.requireNonNull(item1.getData(DataComponentTypes.DYED_COLOR)).color();
		Color color2 = Objects.requireNonNull(item2.getData(DataComponentTypes.DYED_COLOR)).color();

		return color1.equals(color2);
	}

	private ItemStack getVisualItem(Player player, EquipmentSlot slot) {
		var visualArmor = VisualArmor.of(player);
		if (visualArmor.hasItem(slot)) return visualArmor.getItem(slot);

		return player.getInventory().getItem(slot);
	}

}
