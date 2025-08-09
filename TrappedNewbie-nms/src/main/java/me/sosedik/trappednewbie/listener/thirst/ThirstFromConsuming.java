package me.sosedik.trappednewbie.listener.thirst;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Thirst restores by consuming some items
 */
public class ThirstFromConsuming implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		ThirstyPlayer.of(event.getPlayer()).addThirst(ThirstData.of(event.getItem()));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onConsume(EntityLoadCrossbowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		ItemStack crossbow = event.getCrossbow();
		var thirstData = ThirstData.of(crossbow);
		if (thirstData.isDummy()) return;

		player.emitSound(Sound.ENTITY_GENERIC_DRINK, 1F, 1F);
		crossbow.damage(1, player);
		ThirstyPlayer.of(player).addThirst(thirstData);
	}

}
