package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Some deaths make the player an entity possessor
 * <ul>
 * <li>Drowning deep enough makes Drowned</li>
 * <li>Suffocating in sand makes Husk</li>
 * <li>Dying in Nether's lava makes Nether Skeleton</li>
 * <li>Getting eaten by Zombies in darkness makes Zombie</li>
 * </ul>
 */
public class DeathMakesPossessed implements Listener { // TODO other death cases

	@EventHandler(ignoreCancelled = true)
	public void onDeathByMob(@NotNull PlayerDeathEvent event) {
		Player player = event.getPlayer();
		if (!(player.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageEvent)) return;
		if (!(lastDamageEvent.getDamager() instanceof Mob damager)) return;

		EntityType entityType = damager.getType();
		LivingEntity possessed = switch (entityType) {
			case ZOMBIE, DROWNED -> {
				if (!EntityUtil.isInDarkness(player)) yield null;
				yield player.getWorld().spawn(player.getLocation(), Zombie.class, z -> PossessingPlayer.migrateStatsToEntity(player, z));
			}
			case HUSK -> {
				if (!EntityUtil.isInDarkness(player)) yield null;
				yield player.getWorld().spawn(player.getLocation(), Husk.class, z -> PossessingPlayer.migrateStatsToEntity(player, z));
			}
			default -> null;
		};
		if (possessed == null) return;

		event.setCancelled(true);
		EntityUtil.clearTargets(player);
		PossessingPlayer.startPossessing(player, possessed);
	}

}
