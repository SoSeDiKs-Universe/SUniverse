package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

// MCCheck: 1.21.10, maybe a better way of checking?
@NullMarked
public class CrossbowWithFireworkAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLoad(EntityLoadCrossbowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (TrappedNewbieAdvancements.CROSSBOW_WITH_FIREWORK.isDone(player)) return;

		EquipmentSlot hand = event.getHand();
		TrappedNewbie.scheduler().sync(() -> {
			if (!player.isValid()) return;

			ItemStack item = player.getInventory().getItem(hand);
			if (item.getType() != Material.CROSSBOW) return;

			ChargedProjectiles data = item.getData(DataComponentTypes.CHARGED_PROJECTILES);
			if (data == null) return;

			for (ItemStack projectile : data.projectiles()) {
				if (projectile.getType() != Material.FIREWORK_ROCKET) continue;

				TrappedNewbieAdvancements.CROSSBOW_WITH_FIREWORK.awardAllCriteria(player);
				return;
			}
		}, 1L);
	}

}
