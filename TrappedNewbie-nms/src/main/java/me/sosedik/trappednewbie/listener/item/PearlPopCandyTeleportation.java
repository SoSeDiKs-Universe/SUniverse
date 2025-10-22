package me.sosedik.trappednewbie.listener.item;

import com.destroystokyo.paper.ParticleBuilder;
import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;

/**
 * Pearl candy pops can teleport
 */
@NullMarked
public class PearlPopCandyTeleportation implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUse(PlayerItemConsumeEvent event) {
		tryToUse(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUse(EntityItemConsumeEvent event) {
		tryToUse(event);
	}

	public void tryToUse(ItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() != TrappedNewbieItems.PEARL_POP_CANDY) return;

		LivingEntity entity = event.getEntity();

		if (entity instanceof Player player)
			player.setCooldown(item, 40);

		Block block = null;
		RayTraceResult rayTraceResult = entity.rayTraceBlocks(10, FluidCollisionMode.NEVER);
		if (rayTraceResult != null && rayTraceResult.getHitBlock() != null && rayTraceResult.getHitBlockFace() != null) {
			block = rayTraceResult.getHitBlock();
			if (LocationUtil.isTrulySolid(entity, block.getRelative(BlockFace.UP)))
				block = block.getRelative(rayTraceResult.getHitBlockFace());
		}
		if (block == null)
			block = entity.getTargetBlock(null, 10);

		new ParticleBuilder(Particle.PORTAL)
			.location(entity.getLocation())
			.count(32)
			.spawn();
		entity.emitSound(Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1F, 1F);
		LocationUtil.smartTeleport(entity, block.getLocation().center(1).setDirection(entity.getLocation().getDirection()))
			.thenAccept((r) -> entity.emitSound(Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1F, 1F));
	}

}
