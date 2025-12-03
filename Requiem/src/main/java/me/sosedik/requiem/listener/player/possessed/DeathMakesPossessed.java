package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.api.event.player.PlayerResurrectEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.BiomeTags;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.Statistic;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Some deaths make the player an entity possessor
 * <ul>
 * <li>Drowning deep enough makes Drowned</li>
 * <li>Suffocating in sand makes Husk</li>
 * <li>Dying in Nether's lava makes Nether Skeleton</li>
 * <li>Getting eaten by Zombies in darkness makes Zombie</li>
 * </ul>
 */
@NullMarked
public class DeathMakesPossessed implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDrowning(PlayerDeathEvent event) {
		if (event.getDamageSource().getDamageType() != DamageType.DROWN) return;

		Player player = event.getPlayer();
		if (player.getWorld().getEnvironment() == World.Environment.THE_END) return;
		if (!BiomeTags.OCEAN.contains(player.getLocation().getBlock().getComputedBiome())) return;
		if (player.getLocation().getBlockY() > player.getWorld().getSeaLevel() - 10) return;

		event.setCancelled(true);
		migrateAndPosses(player, Drowned.class);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSuffocation(PlayerDeathEvent event) {
		if (event.getDamageSource().getDamageType() != DamageType.IN_WALL) return;

		Player player = event.getPlayer();
		if (!Tag.SAND.isTagged(player.getEyeLocation().getBlock().getType())) return;

		event.setCancelled(true);
		migrateAndPosses(player, Husk.class);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLavaDrowning(PlayerDeathEvent event) {
		if (event.getDamageSource().getDamageType() != DamageType.LAVA) return;

		Player player = event.getPlayer();
		if (player.getWorld().getEnvironment() != World.Environment.NETHER) return;

		event.setCancelled(true);
		migrateAndPosses(player, WitherSkeleton.class);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeathByMob(PlayerDeathEvent event) {
		if (!(event.getDamageSource().getDirectEntity() instanceof Mob damager)) return;

		Player player = event.getPlayer();
		EntityType entityType = damager.getType();
		switch (entityType) {
			case ZOMBIE, DROWNED -> {
				if (!EntityUtil.isInDarkness(player)) return;
				event.setCancelled(true);
				migrateAndPosses(player, Zombie.class);
			}
			case HUSK -> {
				if (!EntityUtil.isInDarkness(player)) return;
				event.setCancelled(true);
				migrateAndPosses(player, Husk.class);
			}
		}
	}

	private <T extends LivingEntity> void migrateAndPosses(Player player, Class<T> entityClass) {
		player.setExp(0);
		player.setLevel(3); // Decrease attrition

		player.setStatistic(Statistic.TIME_SINCE_DEATH, 0);
		LivingEntity possessed = player.getWorld().spawn(player.getLocation(), entityClass, entity -> {
			PossessingPlayer.migrateStatsToEntity(player, entity);
			PossessingPlayer.markResurrected(entity);
		});
		Runnable action = () -> EntityUtil.clearTargets(player);
		if (PossessingPlayer.startPossessing(player, possessed, action))
			new PlayerResurrectEvent(player, possessed).callEvent();
		else
			possessed.remove();
	}

}
