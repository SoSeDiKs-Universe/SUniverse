package me.sosedik.trappednewbie.listener.entity;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.MetadataUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Bucketable;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * Custom bucketable entities
 */
@NullMarked
public class CustomEntityBuckets implements Listener {

	private static final String DELAY_COOLDOWN = "bucketable_cd";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (!MetadataUtil.hasMetadata(event.getPlayer(), DELAY_COOLDOWN)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof CraftLivingEntity entity)) return;
		if (!(event.getPlayer() instanceof CraftPlayer player)) return;
		if (MetadataUtil.hasMetadata(player, DELAY_COOLDOWN)) return;

		if (tryToPickup(player, entity, InteractionHand.MAIN_HAND)
			|| tryToPickup(player, entity, InteractionHand.OFF_HAND)) {
			event.setCancelled(true);
			// Automatically places the bucket back without a delay
			MetadataUtil.setMetadata(player, DELAY_COOLDOWN, true);
			TrappedNewbie.scheduler().sync(() -> MetadataUtil.removeMetadata(player, DELAY_COOLDOWN), 1L);
		}
	}

	private boolean tryToPickup(CraftPlayer player, CraftLivingEntity entity, InteractionHand hand) {
		Optional<InteractionResult> result = Bucketable.bucketMobPickup(player.getHandle(), hand, entity.getHandle());
		result.ifPresent(r -> player.swingHand(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND));
		return result.isPresent();
	}

}
