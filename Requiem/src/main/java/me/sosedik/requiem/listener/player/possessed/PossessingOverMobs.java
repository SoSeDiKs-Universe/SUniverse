package me.sosedik.requiem.listener.player.possessed;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/**
 * Taking control of other mobs
 */
@NullMarked
public class PossessingOverMobs implements Listener {

	private static final String SOULBOUND_ITEM_TAG = "entity_soulbound";
	private static final String EQUIPMENT_CHECKED_TAG = "equipment_checked";

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
		if (entity.hasRider()) return;

		Player player = event.getPlayer();
		if (!PossessingPlayer.isAllowedForCapture(player, entity)) return;
		if (PossessingPlayer.isPossessing(player)) return;
		if (!GhostyPlayer.isGhost(player)) return;
		if (player.getInventory().getItemInMainHand().getType() != Material.AIR) return;

		Runnable action = () -> {
			markSoulboundItems(entity);
			PossessingPlayer.migrateStatsToPlayer(player, entity);
		};
		if (!PossessingPlayer.startPossessing(player, entity, action)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSoulboundItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		ItemStack item = event.getItemDrop().getItemStack();
		if (item.getType() == Material.AIR) return;
		if (!NBT.get(item, nbt -> (boolean) nbt.hasTag(SOULBOUND_ITEM_TAG))) return;

		event.setCancelled(true);
		player.playSound(player, Sound.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1F, 1F);
	}

	private void markSoulboundItems(LivingEntity entity) {
		if (NBT.getPersistentData(entity, nbt -> nbt.hasTag(EQUIPMENT_CHECKED_TAG))) return;

		NBT.modifyPersistentData(entity, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(EQUIPMENT_CHECKED_TAG, true));
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return;

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!entity.canUseEquipmentSlot(slot)) continue;
			if (equipment.getDropChance(slot) > 0.1) continue;

			ItemStack item = equipment.getItem(slot);
			if (ItemStack.isEmpty(item)) continue;

			NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setBoolean(SOULBOUND_ITEM_TAG, true));
			equipment.setItem(slot, item);
		}
	}

}
