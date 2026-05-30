package me.sosedik.miscme.listener.item;

import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

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
		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock == null) return;
		if (!NoSwordInstaBreak.shouldPreventBlockBreak(player.getInventory().getItemInMainHand(), clickedBlock)) return;

		LivingEntity entity = EntityUtil.getEntityThoughGrass(player, EquipmentSlot.HAND);
		if (entity != null)
			player.attack(entity);
	}

}
