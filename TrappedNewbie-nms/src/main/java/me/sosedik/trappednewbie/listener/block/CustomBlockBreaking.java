package me.sosedik.trappednewbie.listener.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.misc.BlockBreakTask;
import org.bukkit.Chunk;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Custom block breaking system
 */
@NullMarked
public class CustomBlockBreaking implements Listener {

	public static final AttributeModifier MODIFIER = new AttributeModifier(TrappedNewbie.trappedNewbieKey("no_block_breaking"), -10_000_000, AttributeModifier.Operation.ADD_NUMBER);

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		AttributeInstance attribute = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
		if (attribute == null) return;

		attribute.addTransientModifier(MODIFIER);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMine(BlockDamageEvent event) {
		new BlockBreakTask(event.getBlock(), event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMine(BlockDamageAbortEvent event) {
		BlockBreakTask.stopBreaks(event.getPlayer(), event.getBlock());
		event.getPlayer().completeUsingActiveItem();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		BlockBreakTask.clearBlock(event.getBlock());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDestroy(BlockDestroyEvent event) {
		BlockBreakTask.clearBlock(event.getBlock());
	}

	@EventHandler(ignoreCancelled = true)
	public void onFall(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof FallingBlock)
			BlockBreakTask.clearBlock(event.getBlock());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRetract(BlockPistonRetractEvent event) {
		event.getBlocks().forEach(BlockBreakTask::clearBlock);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExtend(BlockPistonExtendEvent event) {
		event.getBlocks().forEach(BlockBreakTask::clearBlock);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onExplode(BlockExplodeEvent event) {
		event.blockList().forEach(BlockBreakTask::clearBlock);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onExplode(EntityExplodeEvent event) {
		event.blockList().forEach(BlockBreakTask::clearBlock);
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		BlockBreakTask.clearChunk(event.getChunk());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		for (Chunk chunk : event.getWorld().getLoadedChunks())
			BlockBreakTask.clearChunk(chunk);
	}

}
