package me.sosedik.miscme.listener.entity;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Sneak to reverse rotate item frame
 */
@NullMarked
public class ItemFrameReverseRotate implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFrameRotation(PlayerItemFrameChangeEvent event) {
		if (event.getAction() != PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE) return;

		ItemFrame itemFrame = event.getItemFrame();
		if (itemFrame.isFixed()) return;
		if (itemFrame.getItem().isEmpty()) return;
		if (!event.getPlayer().isSneaking()) return;

		event.setCancelled(true);
		itemFrame.setRotation(itemFrame.getRotation().rotateCounterClockwise());
		itemFrame.emitSound(Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1F, 1F);
	}

}
