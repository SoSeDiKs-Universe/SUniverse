package me.sosedik.requiem.listener.item;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import me.sosedik.requiem.dataset.RequiemItems;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Some Necronomicon mechanics
 */
@NullMarked
public class SoulboundNecronomicon implements Listener {

	public static final String OWNER_TAG = "owner";
	public static final String DEATHS_TAG = "deaths_mark";
	public static final String USES_TAG = "uses";

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		event.getDrops().removeIf(item -> item.getType() == RequiemItems.NECRONOMICON);
	}

	public static ItemStack getNecronomicon(Player player) {
		var item = ItemStack.of(RequiemItems.NECRONOMICON);
		item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
		NBT.modify(item, nbt -> {
			nbt.setUUID(OWNER_TAG, player.getUniqueId());
			nbt.setInteger(DEATHS_TAG, player.getStatistic(Statistic.DEATHS));
		});
		return item;
	}

	public static boolean isValid(Player player, ItemStack item) {
		return NBT.get(item, nbt -> {
			if (!nbt.hasTag(OWNER_TAG)) return false;
			if (!nbt.hasTag(DEATHS_TAG)) return false;

			UUID uuid = nbt.getUUID(OWNER_TAG);
			if (!player.getUniqueId().equals(uuid)) return false;

			int deaths = player.getStatistic(Statistic.DEATHS);
			return nbt.getInteger(DEATHS_TAG) == deaths;
		});
	}

	public static int getUses(Player player, ItemStack item) {
		return NBT.get(item, nbt -> {
			if (!nbt.hasTag(OWNER_TAG)) return -1;
			if (!nbt.hasTag(DEATHS_TAG)) return -1;

			UUID uuid = nbt.getUUID(OWNER_TAG);
			if (!player.getUniqueId().equals(uuid)) return -1;

			return nbt.getOrDefault(USES_TAG, 0);
		});
	}

	public static void increaseUses(Player player, ItemStack item) {
		NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setInteger(USES_TAG, nbt.getOrDefault(USES_TAG, 0) + 1));
	}

}
