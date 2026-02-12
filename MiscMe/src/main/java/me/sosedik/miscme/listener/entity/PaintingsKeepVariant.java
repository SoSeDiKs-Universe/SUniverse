package me.sosedik.miscme.listener.entity;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Paintings keep variant when broken with shears / silk touch
 */
@NullMarked
public class PaintingsKeepVariant implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBreak(HangingBreakByEntityEvent event) {
		if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) return;
		if (!(event.getEntity() instanceof Painting painting)) return;
		if (!(event.getRemover() instanceof LivingEntity living)) return;
		if (living instanceof Player player && player.getGameMode().isInvulnerable()) return;

		EntityEquipment equipment = living.getEquipment();
		if (equipment == null) return;
		if (!living.canUseEquipmentSlot(EquipmentSlot.HAND)) return;

		ItemStack item = equipment.getItemInMainHand();
		if (!canKeepVariant(item)) return;

		event.setCancelled(true);
		living.damageItemStack(EquipmentSlot.HAND, 1);
		painting.emitSound(Sound.ENTITY_PAINTING_BREAK, 1F, 1F);
		painting.remove();

		var dropItem = ItemStack.of(Material.PAINTING);
		dropItem.setData(DataComponentTypes.PAINTING_VARIANT, painting.getArt());
		painting.getWorld().dropItem(painting.getLocation(), dropItem, drop -> drop.setPickupDelay(10));
	}

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Painting painting)) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		painting.emitSound(Sound.ENTITY_PAINTING_BREAK, 1F, 1F);
		painting.remove();

		var item = ItemStack.of(Material.PAINTING);
		if (!painting.isPlacedWithoutVariant())
			item.setData(DataComponentTypes.PAINTING_VARIANT, painting.getArt());
		player.getInventory().setItemInMainHand(item);
		player.swingMainHand();
	}

	private boolean canKeepVariant(ItemStack item) {
		return UtilizerTags.SHEARS.isTagged(item.getType()) || item.hasEnchant(Enchantment.SILK_TOUCH);
	}

}
