package me.sosedik.trappednewbie.listener.world;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Falling in personal void worlds
 */
@NullMarked
public class PersonalVoidFall implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFall(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getDamageSource().getDamageType() != DamageType.OUT_OF_WORLD) return;

		World world = player.getWorld();
		if (!TrappedNewbie.NAMESPACE.equals(world.key().namespace())) return;
		if (!world.key().value().startsWith("worlds-personal/")) return;

		event.setCancelled(true);
		World newWorld = PerPlayerWorlds.resolveWorld(player, World.Environment.NORMAL);
		LocationUtil.smartTeleport(player, player.getLocation().world(newWorld).y(320), false)
			.thenRun(() -> player.setFallDistance(0F));
	}

}
