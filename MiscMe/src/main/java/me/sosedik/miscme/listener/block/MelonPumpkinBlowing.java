package me.sosedik.miscme.listener.block;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.loot.LootContextKey;
import me.sosedik.miscme.MiscMe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * Melons and pumpkins blow from projectiles
 */
@NullMarked
public class MelonPumpkinBlowing implements Listener {

	private static final Random RANDOM = new Random();
	private static final LootTable MELON_SMASH_LOOT_TABLE = requireNonNull(Bukkit.getLootTable(MiscMe.miscMeKey("custom/melon_smash")));
	private static final LootTable PUMPKIN_SMASH_LOOT_TABLE = requireNonNull(Bukkit.getLootTable(MiscMe.miscMeKey("custom/pumpkin_smash")));

	@EventHandler(ignoreCancelled = true)
	public void onHit(ProjectileHitEvent event) {
		if (event.getHitBlock() == null) return;
		if (!(event.getEntity() instanceof AbstractArrow arrow)) return;

		Block block = event.getHitBlock();
		LootTable lootTable = getLootTable(block);
		if (lootTable == null) return;

		BlockData blockData = block.getBlockData();
		Location loc = block.getLocation().center();
		ItemStack weapon = arrow.getWeapon();
		Entity source = arrow.getShooter() instanceof Entity entity ? entity : arrow;

		LootContext.Builder lootContext = new LootContext.Builder(loc.getWorld())
			.with(LootContextKey.BLOCK_DATA, blockData)
			.with(LootContextKey.ORIGIN, loc)
			.with(LootContextKey.TOOL, weapon == null ? ItemStack.empty() : weapon)
			.with(LootContextKey.THIS_ENTITY, source);

		block.setType(Material.AIR);

		Collection<ItemStack> drops = lootTable.populateLoot(RANDOM, lootContext.build());
		boolean glow = arrow instanceof SpectralArrow;
		drops.forEach(drop ->
			block.getWorld().dropItemNaturally(loc, drop, item -> {
				if (glow)
					item.setGlowing(true);
			}
		));

		var particles = new ParticleBuilder(Particle.BLOCK_CRUMBLE)
			.location(loc)
			.data(blockData);
		for (int i = 0; i < 450; i++) {
			particles
				.offset(Math.random(), Math.random(), Math.random())
				.spawn();
		}

		block.emitSound(block.getBlockSoundGroup().getBreakSound(), 1F, 1F);
		block.emitSound(Sound.ENTITY_ZOMBIE_STEP, 1F, 1F);

		// Fixup arrow thinking it's stuck in a block
		MiscMe.scheduler().sync(() -> {
			if (arrow.isValid())
				arrow.startFalling();
		}, 1L);
		event.setCancelled(true);
	}

	private @Nullable LootTable getLootTable(Block block) {
		return switch (block.getType()) {
			case MELON -> MELON_SMASH_LOOT_TABLE;
			case PUMPKIN -> PUMPKIN_SMASH_LOOT_TABLE;
			default -> null;
		};
	}

}
