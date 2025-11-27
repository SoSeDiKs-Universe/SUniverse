package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.util.EntityUtil;
import net.kyori.adventure.util.TriState;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;

/**
 * Interactions for chainmail bucket
 */
@NullMarked
public class ChainmailBucketInteractions implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (!event.getAction().isRightClick()) return;

		Player player = event.getPlayer();
		TriState result = tryToSpill(player, EquipmentSlot.HAND);
		if (result == TriState.NOT_SET) return;

		if (result != TriState.TRUE) {
			result = tryToSpill(player, EquipmentSlot.OFF_HAND);
			if (result != TriState.TRUE) return;
		}

		event.setCancelled(true);
	}

	private TriState tryToSpill(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != TrappedNewbieItems.CHAINMAIL_BUCKET) return TriState.FALSE;

		RayTraceResult rayTraceResult = player.rayTraceBlocks(EntityUtil.PLAYER_REACH, FluidCollisionMode.SOURCE_ONLY);
		if (rayTraceResult == null) return TriState.NOT_SET;

		Block block = rayTraceResult.getHitBlock();
		if (block == null) return TriState.NOT_SET;
		if (!block.isLiquid()) return TriState.NOT_SET;
		if (!(block.getBlockData() instanceof Levelled levelled)) return TriState.NOT_SET;
		if (levelled.getLevel() != 0) return TriState.NOT_SET;

		player.swingHand(hand);
		player.emitSound(block.getType() == Material.WATER ? Sound.ITEM_BUCKET_EMPTY : Sound.ITEM_BUCKET_EMPTY_LAVA, 1F, 1F);

		Material type = block.getType();
		block.setType(Material.AIR);

		Block blockTo = player.getLocation().getBlock();
		if (!blockTo.isReplaceable()) {
			blockTo = blockTo.getRelative(BlockFace.UP);
			if (!blockTo.isReplaceable())
				blockTo = block;
		}
		if (!blockTo.isEmpty() && !blockTo.isLiquid())
			blockTo.breakNaturally();
		blockTo.setType(type, false);
		if (blockTo.getBlockData() instanceof Levelled l) {
			l.setLevel(1);
			blockTo.setBlockData(l);
		}

		return TriState.TRUE;
	}

}
