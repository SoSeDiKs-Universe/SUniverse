package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.miscme.listener.item.FireAspectIsFlintAndSteel;
import me.sosedik.miscme.listener.item.FlintAndSteelIgnitesEntities;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Grants advancement for igniting a Creeper via {@link FireAspectIsFlintAndSteel#mimicFlintAndSteel(LivingEntity, Entity, Action, Block, BlockFace, EquipmentSlot)}
 * and {@link FlintAndSteelIgnitesEntities}
 */
@NullMarked
public class IgniteACreeperCompatAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onIgnite(PlayerInteractEntityEvent event) {
		if (!event.isCancelled()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getRightClicked().getType() != EntityType.CREEPER) return;
		if (!(event.getRightClicked() instanceof Creeper creeper)) return;
		if (creeper.isIgnited()) return;
		if (creeper.getFireTicks() != FlintAndSteelIgnitesEntities.FIRE_DURATION_TICKS) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (creeper.getTarget() != player) return;

		ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
		if (item.getType() != Material.FLINT_AND_STEEL) {
			item = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
			if (item.getType() != Material.FLINT_AND_STEEL) return;
		}

		TrappedNewbieAdvancements.IGNITE_A_CREEPER.awardAllCriteria(player);
	}

}
