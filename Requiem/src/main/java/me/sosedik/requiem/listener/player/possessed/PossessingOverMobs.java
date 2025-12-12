package me.sosedik.requiem.listener.player.possessed;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.listener.item.NotDroppableItems;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

	private static final String POSSESSED_ITEM_TAG = "entity_soulbound";

	static {
		NotDroppableItems.addRule(new NotDroppableItems.NotDroppableRule(
			(entity, item) -> {
				if (!(entity instanceof Player player)) return false;
				if (!PossessingPlayer.isPossessing(player)) return false;
				if (!NBT.get(item, nbt -> (boolean) nbt.hasTag(POSSESSED_ITEM_TAG))) return false;

				player.playSound(player, Sound.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1F, 1F);
				return true;
			})
			.withAllowedCrafts()
		);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
		if (entity.hasRider()) return;

		Player player = event.getPlayer();
		if (!PossessingPlayer.isAllowedForCapture(player, entity)) return;
		if (PossessingPlayer.isPossessing(player)) return;
		if (!GhostyPlayer.isGhost(player)) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		Runnable action = () -> {
			markPossessedItems(entity);
			PossessingPlayer.migrateStatsToPlayer(player, entity);
		};
		if (!PossessingPlayer.startPossessing(player, entity, action)) return;

		event.setCancelled(true);
	}

	private void markPossessedItems(LivingEntity entity) {
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return;

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!entity.canUseEquipmentSlot(slot)) continue;

			ItemStack item = equipment.getItem(slot);
			if (item.isEmpty()) continue;

			item.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
			item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
			if (equipment.getDropChance(slot) <= 0.1)
				NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setBoolean(POSSESSED_ITEM_TAG, true));
			equipment.setItem(slot, item);
		}
	}

	/**
	 * Checks whether this is a possessed soulbound item
	 *
	 * @param item item
	 * @return whether this is a possessed soulbound item
	 */
	public static boolean isPossessedSoulboundItem(ItemStack item) {
		return NBT.get(item, nbt -> (boolean) nbt.hasTag(POSSESSED_ITEM_TAG));
	}

}
