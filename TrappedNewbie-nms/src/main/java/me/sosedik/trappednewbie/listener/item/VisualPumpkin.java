package me.sosedik.trappednewbie.listener.item;

import com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Visual pumpkin acts same as normal one
 */
public class VisualPumpkin implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onTarget(EndermanAttackPlayerEvent event) {
		Player player = event.getPlayer();
		var visualArmor = VisualArmor.of(player);
		if (!visualArmor.hasHelmet()) return;
		if (visualArmor.getHelmet().getType() != Material.CARVED_PUMPKIN) return;

		event.setCancelled(true);
	}

}
