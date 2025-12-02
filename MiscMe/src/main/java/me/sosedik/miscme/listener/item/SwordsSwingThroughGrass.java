package me.sosedik.miscme.listener.item;

import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Swords (/items in general) swing through grass
 */
@NullMarked
public class SwordsSwingThroughGrass implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSwing(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		LivingEntity entity = getEntityThoughGrass(player);
		if (entity != null)
			player.attack(entity);
	}

	/**
	 * Gets the targeted entity, ignoring the grass on the way
	 *
	 * @param player player
	 * @return targeted entity
	 */
	public static @Nullable LivingEntity getEntityThoughGrass(Player player) {
		Block block = player.getTargetBlockExact(EntityUtil.PLAYER_REACH);
		if (block == null) return null;
		if (block.getType().isCollidable()) return null;

		if (block.getType().getHardness() > 0) {
			if (LocationUtil.isCube(block) && !Tag.ITEMS_ENCHANTABLE_WEAPON.isTagged(player.getInventory().getItemInMainHand().getType())) return null;
		}

		RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(),
				player.getEyeLocation().getDirection(), EntityUtil.PLAYER_REACH, entity -> entity != player);
		if (rayTraceResult == null) return null;

		return rayTraceResult.getHitEntity() instanceof LivingEntity entity ? entity : null;
	}

}
