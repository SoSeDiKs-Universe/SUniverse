package me.sosedik.miscme.listener.block;

import io.papermc.paper.loot.LootContextKey;
import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Bee;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * Bee nests fall if hit with a projectile
 */
@NullMarked
public class FallingBeeNests implements Listener {

	private static final Random RANDOM = new Random();
	private static final LootTable BEE_NEST_FALL_LOOT_TABLE = requireNonNull(Bukkit.getLootTable(MiscMe.miscMeKey("custom/bee_nest_fall")));

	@EventHandler(ignoreCancelled = true)
	public void onHit(ProjectileHitEvent event) {
		if (event.getHitBlock() == null) return;
		if (!(event.getEntity() instanceof AbstractArrow arrow)) return;

		Block block = event.getHitBlock();
		if (block.getType() != Material.BEE_NEST) return;

		block.getWorld().spawn(block.getLocation().center(), FallingBlock.class, fallingBlock -> {
			fallingBlock.setBlockState(block.getState());
			fallingBlock.setDropItem(false);
		});
		block.setType(Material.AIR);

		// Fixup arrow thinking it's stuck in a block
		MiscMe.scheduler().sync(() -> {
			if (arrow.isValid())
				arrow.startFalling();
		}, 1L);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFall(EntityChangeBlockEvent event) {
		if (event.getTo() != Material.BEE_NEST) return;
		if (!(event.getEntity() instanceof FallingBlock fallingBlock)) return;
		if (!(fallingBlock.getBlockState() instanceof Beehive beehive)) return;

		event.setCancelled(true);
		fallingBlock.remove();

		Location loc = fallingBlock.getLocation();
		beehive.releaseEntities(loc).forEach(bee -> {
			bee.setCannotEnterHiveTicks(20 * 20);
			bee.setAnger(40);
		});

		Player closestPlayer = (Player) LocationUtil.findClosestEntity(loc, 16, entity -> entity instanceof Player);
		if (closestPlayer != null)
			loc.getWorld().getNearbyEntitiesByType(Bee.class, loc, 50).forEach(bee -> bee.setTarget(closestPlayer));

		LootContext.Builder lootContext = new LootContext.Builder(loc.getWorld())
			.with(LootContextKey.BLOCK_DATA, beehive.getBlockData())
			.with(LootContextKey.ORIGIN, loc)
			.with(LootContextKey.TOOL, ItemStack.empty())
			.with(LootContextKey.THIS_ENTITY, fallingBlock);

		Collection<ItemStack> drops = BEE_NEST_FALL_LOOT_TABLE.populateLoot(RANDOM, lootContext.build());
		drops.forEach(drop -> loc.getWorld().dropItemNaturally(loc, drop));
	}

}
