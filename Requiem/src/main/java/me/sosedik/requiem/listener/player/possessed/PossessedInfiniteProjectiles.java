package me.sosedik.requiem.listener.player.possessed;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import me.sosedik.kiterino.event.entity.EntityLoadsProjectileEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.listener.item.BowUsableWithoutArrows;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Projectiles from some possessed should be infinite
 */
@NullMarked
public class PossessedInfiniteProjectiles implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onTridentLaunch(PlayerLaunchProjectileEvent event) {
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
	public void onArrowLaunch(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getProjectile() instanceof AbstractArrow projectile)) return;
		if (!Tag.ENTITY_TYPES_ARROWS.isTagged(projectile.getType())) return;
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
	public void onArrowLoad(EntityLoadsProjectileEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;
		if (!event.getProjectile().isEmpty()) return;

		LivingEntity riding = PossessingPlayer.getPossessed(player);
		if (riding == null) return;
		if (!hasInfiniteArrows(riding.getType(), event.getWeapon())) return;

		event.setProjectile(BowUsableWithoutArrows.DUMMY_ARROW);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.OFF_HAND) return;
		if (event.getClickedBlock() == null) return;

		Player player = event.getPlayer();
		if (player.hasActiveItem()) return;

		LivingEntity riding = PossessingPlayer.getPossessed(player);
		if (riding == null) return;

		if (hasInfiniteArrows(riding.getType(), player.getInventory().getItemInMainHand()))
			player.startUsingItem(EquipmentSlot.HAND);
		else if (hasInfiniteArrows(riding.getType(), player.getInventory().getItemInOffHand()))
			player.startUsingItem(EquipmentSlot.OFF_HAND);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDurabilityChange(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity riding = PossessingPlayer.getPossessed(player);
		if (riding == null) return;
		if (!hasInfiniteArrows(riding.getType(), event.getItem())) return;

		event.setCancelled(true);
	}

	private boolean hasInfiniteArrows(EntityType type, ItemStack bow) {
		if (Tag.ENTITY_TYPES_SKELETONS.isTagged(type)) return bow.getType() == Material.BOW;
		if (type == EntityType.PILLAGER) return bow.getType() == Material.CROSSBOW;
		return false;
	}

}
