package me.sosedik.miscme.listener.entity;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Pat allowed mobs
 * MCCheck: 1.21.10, new baby mobs
 */
@NullMarked
public class MobPatting implements Listener {

	private static final Set<UUID> PATTED = new HashSet<>();
	private static final Random RANDOM = new Random();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPat(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Mob entity)) return;
		if (PATTED.contains(entity.getUniqueId())) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;
		if (!isPattable(player, entity)) return;
		if (player.getLocation().distanceSquared(entity.getLocation()) > 9) return;

		Sound ambientSound = entity.getAmbientSound();
		if (ambientSound == null) return;

		event.setCancelled(true);

		UUID uuid = entity.getUniqueId();
		PATTED.add(uuid);
		MiscMe.scheduler().async(() -> PATTED.remove(uuid), 5L);

		player.swingMainHand();
		entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation().addY(Math.max(0.2, entity.getHeight() - 0.2)), 1);
		entity.emitSound(ambientSound, 1F, 0.8F + (RANDOM.nextInt(5) / 10F));
	}

	private boolean isPattable(Player player, Mob entity) {
		if (entity.getTarget() != null) return false;

		return switch (entity) {
			case Wolf wolf -> wolf.isTamed() || !wolf.isAdult();
			case Cat cat -> cat.isTamed() || !cat.isAdult();
			case Fox fox -> {
				if (!fox.isAdult()) yield true;

				AnimalTamer tamer = fox.getFirstTrustedPlayer();
				if (tamer != null && tamer.getUniqueId().equals(player.getUniqueId())) yield true;

				tamer = fox.getSecondTrustedPlayer();
				yield tamer != null && tamer.getUniqueId().equals(player.getUniqueId());
			}
			case Animals ignored -> true;
			default -> !(entity instanceof Monster) && MoreBabyMobs.isNonVanillaBaby(entity);
		};
	}

}
