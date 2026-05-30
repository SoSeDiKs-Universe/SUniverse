package me.sosedik.trappednewbie.listener.item;

import com.destroystokyo.paper.ParticleBuilder;
import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.EntityLoadsProjectileEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.miscme.listener.item.FireAspectIsFlintAndSteel;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * Firestriker release logic
 */
@NullMarked
public class FirestrikerFire implements Listener {

	@EventHandler
	public void onLoad(EntityLoadsProjectileEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		ItemStack item = event.getWeapon();
		if (item.getType() != TrappedNewbieItems.FIRESTRIKER) return;

		TrappedNewbie.scheduler().sync(_ -> {
			if (!player.isOnline()) return true;
			if (!item.equals(player.getActiveItem())) return true;

			double reach = EntityUtil.getEntityReach(player, player.getActiveItemHand());
			RayTraceResult rayTraceResult = player.rayTraceEntities((int) Math.floor(reach));
			if (rayTraceResult == null) {
				rayTraceResult = player.rayTraceBlocks(reach - 1D, FluidCollisionMode.ALWAYS);
				if (rayTraceResult == null) return false;
			}

			Vector hit = rayTraceResult.getHitPosition();
			new ParticleBuilder(Particle.SMOKE)
				.location(hit.toLocation(player.getWorld()))
				.spawn();

			return false;
		}, 1L, 1L);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(PlayerItemConsumeEvent event) {
		tryToUse(event);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(EntityItemConsumeEvent event) {
		tryToUse(event);
	}

	public void tryToUse(ItemConsumeEvent event) {
		if (!ItemStack.isType(event.getItem(), TrappedNewbieItems.FIRESTRIKER)) return;

		FireAspectIsFlintAndSteel.mimicFlintAndSteelRightClick(event.getEntity(), event.getHand());
	}

}
