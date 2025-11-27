package me.sosedik.miscme.listener.entity;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Undead mobs should not get hurt by cacti
 */
@NullMarked
public class DeadLivingsIgnoreCactiAndSweetBushes implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onCollide(EntityInsideBlockEvent event) {
		if (!ignoreBlock(event.getBlock().getType())) return;
		if (!Tag.ENTITY_TYPES_UNDEAD.isTagged(event.getEntity().getType())) return;

		event.setCancelled(true);
	}

	private boolean ignoreBlock(Material type) {
		return type == Material.CACTUS
			|| type == Material.SWEET_BERRY_BUSH;
	}

}
