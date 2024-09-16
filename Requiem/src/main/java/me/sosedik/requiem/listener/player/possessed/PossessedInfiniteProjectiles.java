package me.sosedik.requiem.listener.player.possessed;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import me.sosedik.kiterino.event.player.PlayerLoadsProjectileEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Projectiles from some possessed should be infinite
 */
public class PossessedInfiniteProjectiles implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onTridentLaunch(@NotNull PlayerLaunchProjectileEvent event) {
		if (!(event.getProjectile() instanceof Trident trident)) return;

		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity riding = PossessingPlayer.getPossessed(player);
		if (riding == null) return;
		if (riding.getType() != EntityType.DROWNED) return;

		event.setShouldConsume(false);
		trident.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
	}

	@EventHandler(ignoreCancelled = true)
	public void onArrowLaunch(@NotNull EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getProjectile() instanceof AbstractArrow projectile)) return;
		if (projectile instanceof Trident) return;
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity riding = PossessingPlayer.getPossessed(player);
		if (riding == null) return;

		ItemStack item = event.getBow();
		if (item == null) return;
		if (!hasInfiniteArrows(riding.getType(), item)) return;

		event.setConsumeItem(false); // TODO not implemented
		projectile.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
	}

	@EventHandler
	public void onArrowLoad(@NotNull PlayerLoadsProjectileEvent event) {
		if (event.isFiringAllowed()) return;

		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity riding = PossessingPlayer.getPossessed(player);
		if (riding == null) return;
		if (!hasInfiniteArrows(riding.getType(), event.getWeapon())) return;

		event.setProjectile(null);
		event.setFiringAllowed(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDurabilityChange(@NotNull PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity riding = PossessingPlayer.getPossessed(player);
		if (riding == null) return;
		if (!hasInfiniteArrows(riding.getType(), event.getItem())) return;

		event.setDamage(0);
		event.setCancelled(true);
	}

	private boolean hasInfiniteArrows(@NotNull EntityType type, @NotNull ItemStack bow) {
		if (Tag.ENTITY_TYPES_SKELETONS.isTagged(type)) return bow.getType() == Material.BOW;
		if (type == EntityType.PILLAGER) return bow.getType() == Material.CROSSBOW;
		return false;
	}

}
