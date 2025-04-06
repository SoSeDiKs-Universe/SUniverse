package me.sosedik.utilizer.listener.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import me.sosedik.utilizer.util.MetadataUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Clears entity metadata of entities
 */
@NullMarked
public class EntityMetadataClearer implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDespawn(EntityRemoveFromWorldEvent event) {
		MetadataUtil.clearMetadata(event.getEntity());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		MetadataUtil.clearMetadata(event.getPlayer());
	}

}
