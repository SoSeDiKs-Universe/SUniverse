package me.sosedik.requiem.listener.player.possessed;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.requiem.api.event.player.PlayerPossessedCuredEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.utilizer.listener.item.NotDroppableItems;
import me.sosedik.utilizer.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
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

	private static final String POSSESSED_ITEM_TAG = "entity_soulbound";
	private static final String POSSESSED_ITEM_SLOT_TAG = "slot";
	private static final String POSSESSED_ITEM_DROP_CHANCE_TAG = "drop_chance";

	static {
		NotDroppableItems.addRule(new NotDroppableItems.NotDroppableRule(
			(entity, item) -> {
				if (!(entity instanceof Player player)) return false;
				if (!PossessingPlayer.isPossessing(player)) return false;
				if (!NBT.get(item, nbt -> (boolean) nbt.hasTag(POSSESSED_ITEM_TAG))) return false;

//				HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("drop.entity_soulbound"));
				HudMessenger.of(player).displayMessage(Component.text("Предмети, прив’язані до душі керованого тіла, не можна викинути"));
				player.playSound(player, Sound.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1F, 1F);
				return true;
			})
			.withAllowedCrafts()
		);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
		if (entity.hasRider()) return;

		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;
		if (!PossessingPlayer.isAllowedForCapture(player, entity)) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		Runnable action = () -> {
			markPossessedItems(entity);
			PossessingPlayer.migrateInventoryAndStatsToPlayer(player, entity);
		};
		if (!PossessingPlayer.startPossessing(player, entity, action)) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onCure(PlayerPossessedCuredEvent event) {
		unmarkPossessedItems(event.getPlayer());
	}

	private void markPossessedItems(LivingEntity entity) {
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return;

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!entity.canUseEquipmentSlot(slot)) continue;

			ItemStack item = equipment.getItem(slot);
			if (item.isEmpty()) continue;

			float dropChance = equipment.getDropChance(slot);
			if (dropChance <= 0.1) {
				NBT.modify(item,itemNbt -> {
					ReadWriteNBT nbt = itemNbt.getOrCreateCompound(POSSESSED_ITEM_TAG);
					nbt.setEnum(POSSESSED_ITEM_SLOT_TAG, slot);
					nbt.setFloat(POSSESSED_ITEM_DROP_CHANCE_TAG, dropChance);
				});
			}
			equipment.setDropChance(slot, 0F);
			equipment.setItem(slot, item);
		}
	}

	/**
	 * Clears possessed soulbound item markings
	 *
	 * @param item item to clear from
	 */
	public static void unmarkPossessedItem(ItemStack item) {
		NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.removeKey(POSSESSED_ITEM_TAG));
	}

	/**
	 * Clears possessed soulbound item markings
	 *
	 * @param entity entity to clear from
	 */
	public static void unmarkPossessedItems(LivingEntity entity) {
		InventoryUtil.modifyItems(entity, item -> {
			NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.removeKey(POSSESSED_ITEM_TAG));
			return item;
		});
	}

	/**
	 * Restores possessed soulbound item
	 *
	 * @param entity entity to clear from
	 * @param item item
	 * @return whether the item was restored
	 */
	public static boolean restorePossessedItem(LivingEntity entity, ItemStack item) {
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return false;

		return NBT.get(item, itemNbt -> {
			if (!itemNbt.hasTag(POSSESSED_ITEM_TAG, NBTType.NBTTagCompound)) return false;

			ReadableNBT nbt = itemNbt.getCompound(POSSESSED_ITEM_TAG);
			assert nbt != null;

			EquipmentSlot equipmentSlot = nbt.getOrNull(POSSESSED_ITEM_SLOT_TAG, EquipmentSlot.class);
			if (equipmentSlot == null) return false;

			if (!equipment.getItem(equipmentSlot).isEmpty()) return false;

			float dropChance = nbt.getOrDefault(POSSESSED_ITEM_DROP_CHANCE_TAG, 1F);

			equipment.setItem(equipmentSlot, item);
			equipment.setDropChance(equipmentSlot, dropChance);

			return true;
		});
	}

	/**
	 * Gets a drop chance of a possessed soulbound item.
	 * Will return {@code 1F} if not a possessed soulbound item.
	 *
	 * @param item item
	 * @param removeTag whether to remove soulbound tag
	 * @return drop chance of a possessed soulbound item
	 */
	public static float getPossessedSoulboundItemDropChance(ItemStack item, boolean removeTag) {
		return NBT.modify(item, itemNbt -> {
			if (!itemNbt.hasTag(POSSESSED_ITEM_TAG, NBTType.NBTTagCompound)) return 1F;

			ReadableNBT nbt = itemNbt.getCompound(POSSESSED_ITEM_TAG);
			if (removeTag)
				itemNbt.removeKey(POSSESSED_ITEM_TAG);
			assert nbt != null;
			return nbt.getOrDefault(POSSESSED_ITEM_DROP_CHANCE_TAG, 1F);
		});
	}

}
