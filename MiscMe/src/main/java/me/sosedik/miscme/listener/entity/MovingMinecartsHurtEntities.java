package me.sosedik.miscme.listener.entity;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.miscme.MiscMe;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Moving minecarts are not a joke! They hurt, and hurt a lot
 */
@NullMarked
public class MovingMinecartsHurtEntities implements Listener {

	/**
	 * Fall damage caused by the player opening a chest with the entity on its lid
	 */
	public static final DamageType MOVING_MINECART = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(MiscMe.miscMeKey("moving_minecart"));

	private static final List<UUID> IMMUNE = new ArrayList<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVehicleExit(VehicleExitEvent event) {
		if (!(event.getVehicle() instanceof Minecart)) return;

		UUID uuid = event.getExited().getUniqueId();
		IMMUNE.add(uuid);
		MiscMe.scheduler().sync(() -> IMMUNE.remove(uuid), 10L);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCollide(VehicleEntityCollisionEvent event) {
		if (!(event.getVehicle() instanceof Minecart minecart)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (IMMUNE.contains(entity.getUniqueId())) return;
		if (entity instanceof Slime) return; // Slimes bounce back instead
		if (entity instanceof Player player && player.getGameMode().isInvulnerable()) return;

		double speedMultiplier = minecart.getVelocity().lengthSquared() + entity.getVelocity().lengthSquared();
		if (speedMultiplier < 0.08) return;

		double damage = 2 + 15 * Math.sqrt(speedMultiplier);
		boolean damageReduction = (minecart.getType() == EntityType.MINECART && minecart.getPassengers().isEmpty())
				|| (minecart instanceof PoweredMinecart furnaceMinecart && furnaceMinecart.getFuel() <= 1);
		if (damageReduction) {
			damage /= 4;
			if (damage < 3) return;
		}

		var damageSourceBuilder = DamageSource.builder(MOVING_MINECART);
		if (!minecart.getPassengers().isEmpty()) damageSourceBuilder.withDirectEntity(minecart).withCausingEntity(minecart.getPassengers().getFirst());
		entity.damage(damage, damageSourceBuilder.build());
		entity.setVelocity(minecart.getVelocity());
		minecart.setVelocity(minecart.getVelocity().multiply(0.9));
		if (entity.isDead())
			event.setCancelled(true);
	}

}
