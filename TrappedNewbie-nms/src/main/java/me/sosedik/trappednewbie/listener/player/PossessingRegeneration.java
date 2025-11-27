package me.sosedik.trappednewbie.listener.player;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Temporary way to regenerate health as possessor
 */
@NullMarked
public class PossessingRegeneration implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		LivingEntity possessed = PossessingPlayer.getPossessed(player);
		if (possessed == null) return;

		possessed.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
	}

}
