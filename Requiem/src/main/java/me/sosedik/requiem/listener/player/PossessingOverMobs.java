package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.GlowingUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Taking control of other mobs
 */
public class PossessingOverMobs implements Listener {

	private static final String SOULBOUND_ITEM_TAG = "entity_soulbound";
	private static final String EQUIPMENT_CHECKED_TAG = "equipment_checked";

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(@NotNull PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
		if (entity.hasRider()) return;

		Player player = event.getPlayer();
		if (!PossessingPlayer.isAllowedForCapture(player, entity)) return;
		if (PossessingPlayer.isPossessing(player)) return;
		if (!GhostyPlayer.isGhost(player)) return;
		if (player.getInventory().getItemInMainHand().getType() != Material.AIR) return;

		event.setCancelled(true);

		markSoulboundItems(entity);
		PossessingPlayer.startPossessing(player, entity);
//		migrateInvFromEntity(player, entity); // TODO
	}

	private void markSoulboundItems(@NotNull LivingEntity entity) {
		if (entity.getPersistentData(nbt -> nbt.hasTag(EQUIPMENT_CHECKED_TAG))) return;

		entity.modifyPersistentData(nbt -> nbt.setBoolean(EQUIPMENT_CHECKED_TAG, true));
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return;

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (equipment.getDropChance(slot) > 0.1) continue;

			ItemStack item = equipment.getItem(slot);
			if (ItemStack.isEmpty(item)) continue;

			item.modifyNbt(nbt -> nbt.setBoolean(SOULBOUND_ITEM_TAG, true));
			equipment.setItem(slot, item);
		}
	}

}
