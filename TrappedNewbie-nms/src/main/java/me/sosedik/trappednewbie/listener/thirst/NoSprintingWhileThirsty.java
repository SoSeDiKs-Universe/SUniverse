package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Players can't sprint while thirsty
 */
@NullMarked
public class NoSprintingWhileThirsty implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSprint(PlayerToggleSprintEvent event) {
		if (!event.isSprinting()) return;

		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;
		if (ThirstyPlayer.of(player).canRun()) return;

		event.setCancelled(true);
		player.sendHealthUpdate(); // Trigger fake hunger level
		player.setSprinting(false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 15, 2, false, false, false));
	}

}
