package me.sosedik.trappednewbie.listener.item;

import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.miscme.listener.item.FireAspectIsFlintAndSteel;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;

/**
 * Firestriker release logic
 */
@NullMarked
public class FirestrikerFire implements Listener {

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

		LivingEntity entity = event.getEntity();
		RayTraceResult rayTraceResult = entity.rayTraceBlocks(EntityUtil.PLAYER_REACH - 1D, FluidCollisionMode.ALWAYS);
		if (rayTraceResult == null) return;

		Block block = rayTraceResult.getHitBlock();
		if (block == null) return;

		BlockFace blockFace = rayTraceResult.getHitBlockFace();
		if (blockFace == null) return;

		FireAspectIsFlintAndSteel.mimikFlintAndSteel(entity, Action.RIGHT_CLICK_BLOCK, block, blockFace, event.getHand());
	}

}
