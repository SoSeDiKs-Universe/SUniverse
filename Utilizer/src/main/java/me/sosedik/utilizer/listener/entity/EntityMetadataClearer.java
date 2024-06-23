package me.sosedik.utilizer.listener.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import me.sosedik.utilizer.util.MetadataUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Clears entity metadata of entities
 */
public class EntityMetadataClearer implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDespawn(@NotNull EntityRemoveFromWorldEvent event) {
		MetadataUtil.clearMetadata(event.getEntity());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@NotNull PlayerQuitEvent event) {
		MetadataUtil.clearMetadata(event.getPlayer());
	}

}
