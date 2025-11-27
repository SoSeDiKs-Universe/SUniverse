package me.sosedik.miscme.listener.entity;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Minecarts boost from slimes and slime blocks
 */
@NullMarked
public class MinecartSlimeBoost implements Listener {

	private static final Set<UUID> BOOSTED_COOLDOWN = new HashSet<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCollide(VehicleEntityCollisionEvent event) {
		if (!(event.getVehicle() instanceof Minecart minecart)) return;
		if (minecart.getPassengers().isEmpty()) return; // We want to allow picking up slimes into minecart
		if (!(event.getEntity() instanceof Slime slime)) return;
		if (slime.getSize() <= 1) return;

		boostMinecart(minecart, minecart.getVelocity());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCollide(VehicleBlockCollisionEvent event) {
		if (!(event.getVehicle() instanceof Minecart minecart)) return;
		if (event.getBlock().getType() != Material.SLIME_BLOCK) return;

		boostMinecart(minecart, event.getVelocity());
	}

	private void boostMinecart(Minecart minecart, Vector velocity) {
		UUID uuid = minecart.getUniqueId();
		if (BOOSTED_COOLDOWN.contains(uuid)) return;
		BOOSTED_COOLDOWN.add(uuid);
		MiscMe.scheduler().sync(() -> BOOSTED_COOLDOWN.remove(uuid), 3L);

		minecart.setVelocity(velocity.multiply(-1.2));
		minecart.emitSound(Sound.BLOCK_SLIME_BLOCK_HIT, 1F, 1F);
		minecart.getWorld().spawnParticle(Particle.ITEM_SLIME, minecart.getLocation(), 10, 1, 1, 1);
	}

}
