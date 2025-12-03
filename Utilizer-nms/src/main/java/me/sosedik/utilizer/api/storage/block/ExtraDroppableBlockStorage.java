package me.sosedik.utilizer.api.storage.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface ExtraDroppableBlockStorage extends BlockDataStorage {

	@Override
	default void onBreak(BlockBreakEvent event) {
		if (!dropsOnBreak()) return;

		dropItems(getExtraDrops(event));
	}

	@Override
	default void onBreak(BlockBreakBlockEvent event) {
		dropItems(getExtraDrops(event));
	}

	@Override
	default void onDrop(BlockDropItemEvent event) {
		getExtraDrops(event).forEach(event::addDrop);
	}

	@Override
	default void onBurn(BlockBurnEvent event) {
		if (dropOnBurn())
			dropItems(getExtraDrops(event));
	}

	@Override
	default boolean onExplode(Event event) {
		if (!dropOnExplosion()) return false;

		dropItems(getExtraDrops(event));
		return removeFromExplosion(event);
	}

	@Override
	default void onDestroy(BlockDestroyEvent event) {
		if (!event.willDrop()) return;

		dropItems(getExtraDrops(event));
	}

	default void dropItems(List<ItemStack> drops) {
		Location loc = getBlock().getLocation().center();
		drops.forEach(item -> loc.getWorld().dropItemNaturally(loc, item));
	}

	default boolean dropsOnBreak() {
		return false;
	}

	default boolean dropOnBurn() {
		return false;
	}

	default boolean dropOnExplosion() {
		return false;
	}

	default boolean removeFromExplosion(Event event) {
		return false;
	}

	List<ItemStack> getExtraDrops(Event event);

}
