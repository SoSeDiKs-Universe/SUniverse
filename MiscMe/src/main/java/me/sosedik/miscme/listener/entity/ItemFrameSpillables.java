package me.sosedik.miscme.listener.entity;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import me.sosedik.miscme.MiscMe;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Some items have extra behaviors when flipped in item frame
 */
@NullMarked
public class ItemFrameSpillables implements Listener {

	private static final Map<Material, SpillableItem> SPILLABLES = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFrameItemChange(PlayerItemFrameChangeEvent event) {
		if (event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE) return;

		ItemFrame itemFrame = event.getItemFrame();
		if (itemFrame.isFixed()) return;

		MiscMe.scheduler().sync(() -> handleFluid(itemFrame), 10L);
	}

	private void handleFluid(ItemFrame frame) {
		if (!frame.isValid()) return;
		if (frame.getRotation() != Rotation.FLIPPED) return;

		BlockFace attachedFace = frame.getAttachedFace();
		if (attachedFace == BlockFace.UP) return;
		if (attachedFace == BlockFace.DOWN) return;

		ItemStack item = frame.getItem();
		ItemStack remainder = spill(frame, item, frame.getLocation().getBlock().getRelative(BlockFace.DOWN));
		if (remainder == null) return;

		frame.setItem(remainder);
	}

	public static void addSpillable(Material type, SpillableItem fallable) {
		SPILLABLES.put(type, fallable);
	}

	/**
	 * Tries to spill an item
	 *
	 * @param item item to spill
	 * @param block block to spill onto
	 * @return null if not spilled, remainder otherwise
	 */
	public static @Nullable ItemStack spill(@Nullable Entity spiller, ItemStack item, Block block) {
		SpillableItem spillable = SPILLABLES.get(item.getType());
		if (spillable == null) return null;

		ItemStack remainder = spillable.onSpill(spiller, item, block);
		if (remainder == null && item.hasData(DataComponentTypes.USE_REMAINDER)) {
			UseRemainder data = item.getData(DataComponentTypes.USE_REMAINDER);
			assert data != null;
			remainder = data.transformInto();
		}

		return remainder == null ? ItemStack.empty() : remainder;
	}

	@FunctionalInterface
	public interface SpillableItem {

		/**
		 * Spills an item onto a block.
		 * <p>If {@code null} is returned, item's use/craft remainder will be used if available.
		 *
		 * @param item spilled item
		 * @param block block to spill onto
		 * @return item remainder
		 */
		@Nullable ItemStack onSpill(@Nullable Entity spiller, ItemStack item, Block block);

	}

}
