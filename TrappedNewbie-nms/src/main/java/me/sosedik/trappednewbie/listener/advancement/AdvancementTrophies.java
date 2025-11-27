package me.sosedik.trappednewbie.listener.advancement;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@NullMarked
public class AdvancementTrophies implements Listener {

	public static final String PLAYER_TAG = "achiever";
	public static final String TROPHY_TAG = "trophy_id";
	private static final Map<IAdvancement, ItemStack> TROPHIES = new HashMap<>();
	private static final Map<String, IAdvancement> TROPHY_TO_ADVANCEMENT = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onUse(PlayerInteractEvent event) {
		EquipmentSlot hand = event.getHand();
		if (hand == null) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		Player player = event.getPlayer();
		if (canUse(player, item)) return;

		event.setCancelled(true);
		player.dropItem(hand);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onArmor(EntityEquipmentChangedEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		event.getEquipmentChanges().forEach((slot, data) -> {
			ItemStack newItem = data.newItem();
			if (canUse(player, newItem)) return;

			player.getInventory().setItem(slot, ItemStack.empty());
			player.dropItem(newItem);
		});
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		ItemStack item = event.getItem().getItemStack();
		IAdvancement advancement = getAdvancement(item);
		if (advancement == null) return;
		if (InventoryUtil.findItem(player, itemStack -> isUsableTrophy(advancement, player, itemStack)) == null) return;

		event.setCancelled(true);
	}

	/**
	 * Adds a new trophy for the advancement
	 *
	 * @param trophyId trophy id
	 * @param advancement advancement
	 * @param item base item
	 */
	public static void addTrophy(String trophyId, IAdvancement advancement, ItemStack item) {
		NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString(TROPHY_TAG, trophyId));
		TROPHIES.put(advancement, item);
		TROPHY_TO_ADVANCEMENT.put(trophyId, advancement);
	}

	/**
	 * Checks whether tha player can use the item if it's a trophy
	 *
	 * @param player player
	 * @param item item
	 * @return whether the player can use the item
	 */
	public static boolean canUse(Player player, @Nullable ItemStack item) {
		return ItemStack.isEmpty(item) || !isTrophy(item) || player.getUniqueId().equals(getAchiever(item));
	}

	/**
	 * Gets the unlinked trophy item
	 *
	 * @param advancement advancement
	 * @return unlinked trophy item
	 */
	public static @Nullable ItemStack getBaseTrophy(IAdvancement advancement) {
		return TROPHIES.get(advancement);
	}

	/**
	 * Creates a trophy linked to the player
	 *
	 * @param advancement advancement
	 * @param player player
	 * @return linked trophy item
	 */
	public static @Nullable ItemStack produceTrophy(IAdvancement advancement, Player player) {
		ItemStack baseItem = getBaseTrophy(advancement);
		if (baseItem == null) return null;

		baseItem = baseItem.clone();
		NBT.modify(baseItem, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setUUID(PLAYER_TAG, player.getUniqueId()));

		return baseItem;
	}

	/**
	 * Checks whether this item is a trophy
	 *
	 * @param item item
	 * @return whether this item is a trophy
	 */
	public static boolean isTrophy(@Nullable ItemStack item) {
		return getTrophyId(item) != null;
	}

	/**
	 * Gets trophy id from the item
	 *
	 * @param item item
	 * @return trophy id
	 */
	public static @Nullable String getTrophyId(@Nullable ItemStack item) {
		if (ItemStack.isEmpty(item)) return null;
		return NBT.get(item, nbt -> {
			if (!nbt.hasTag(TROPHY_TAG)) return null;
			return nbt.getString(TROPHY_TAG);
		});
	}

	/**
	 * Gets the achieving player's UUID from the item
	 *
	 * @param item item
	 * @return uuid
	 */
	public static @Nullable UUID getAchiever(@Nullable ItemStack item) {
		if (ItemStack.isEmpty(item)) return null;
		return NBT.get(item, nbt -> {
			if (!nbt.hasTag(PLAYER_TAG)) return null;
			return nbt.getUUID(PLAYER_TAG);
		});
	}

	/**
	 * Gets advancement from the item
	 *
	 * @param item item
	 * @return advancement
	 */
	public static @Nullable IAdvancement getAdvancement(@Nullable ItemStack item) {
		String trophyId = getTrophyId(item);
		return trophyId == null ? null : TROPHY_TO_ADVANCEMENT.get(trophyId);
	}

	/**
	 * Checks whether the item is linked to the advancement
	 *
	 * @param advancement advancement
	 * @param item item
	 * @return whether the item is linked to the advancement
	 */
	public static boolean isTrophy(IAdvancement advancement, @Nullable ItemStack item) {
		return getAdvancement(item) == advancement;
	}

	/**
	 * Checks whether the player can use this trophy
	 *
	 * @param advancement advancement
	 * @param player player
	 * @param item item
	 * @return whether the player can use this trophy
	 */
	@Contract("_, _, null -> false")
	public static boolean isUsableTrophy(IAdvancement advancement, Player player, @Nullable ItemStack item) {
		return isTrophy(advancement, item) && canUse(player, item);
	}

}
