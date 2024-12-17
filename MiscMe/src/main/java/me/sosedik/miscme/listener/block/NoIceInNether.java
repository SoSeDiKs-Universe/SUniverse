package me.sosedik.miscme.listener.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Regular ice "extinguishes" in ultra warm dimensions like Nether
 */
@NullMarked
public class NoIceInNether implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if (!block.getWorld().isUltraWarm()) return;
		// Packed ice is tickable and will melt on its own with time (Kiterino option)
		// Blue ice is exempt
		if (block.getType() != Material.ICE) return;

		block.setType(Material.AIR);
		block.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);

		Location loc = block.getLocation().center();
		World world = loc.getWorld();
		world.spawnParticle(Particle.LARGE_SMOKE, loc, 10, 0.2, 0.2, 0.2, 0.01);
		world.spawnParticle(Particle.FALLING_WATER, loc, 15, 0.2, 0.2, 0.2);
		world.spawnParticle(Particle.CLOUD, loc, 5, 0.1, 0.1, 0.1, 0.01);
	}

}
