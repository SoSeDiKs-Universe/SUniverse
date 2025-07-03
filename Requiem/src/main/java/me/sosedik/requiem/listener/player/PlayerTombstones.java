package me.sosedik.requiem.listener.player;

import de.tr7zw.nbtapi.NBT;
import me.sosedik.requiem.api.event.player.PlayerTombstoneCreateEvent;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.dataset.RequiemTags;
import me.sosedik.requiem.impl.block.TombstoneBlockStorage;
import me.sosedik.utilizer.api.storage.block.BlockDataStorage;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.listener.BlockStorage;
import me.sosedik.utilizer.util.BiomeTags;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Players leave tombstones on death
 */
@NullMarked
public class PlayerTombstones implements Listener {

	private static final Map<PlayerDeathEvent, PlayerTombstoneCreateEvent> EVENT_CACHE = new HashMap<>();

	@EventHandler(ignoreCancelled = true)
	public void onDeathCache(PlayerDeathEvent event) {
		Block block = getDamageBlock(event.getPlayer());
		if (block.getType() == Material.VOID_AIR) return; // GG

		cacheTombstone(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		PlayerTombstoneCreateEvent tombstoneCreateEvent = EVENT_CACHE.remove(event);
		if (tombstoneCreateEvent == null) return;

		placeTombstone(tombstoneCreateEvent);
	}

	private void cacheTombstone(PlayerDeathEvent event) {
		var tombstoneCreateEvent = new PlayerTombstoneCreateEvent(event);
		if (event.shouldDropExperience()) tombstoneCreateEvent.addExp(event.getDroppedExp());

		Player player = event.getPlayer();
		List<ItemStack> drops = event.getDrops();
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			ItemStack item = player.getInventory().getItem(slot);
			if (ItemStack.isEmpty(item)) continue;
			if (!drops.remove(item)) continue;

			tombstoneCreateEvent.setItem(slot, item);
		}

		tombstoneCreateEvent.callEvent();
		EVENT_CACHE.put(event, tombstoneCreateEvent);
	}

	private void placeTombstone(PlayerTombstoneCreateEvent tombstoneCreateEvent) {
		PlayerDeathEvent event = tombstoneCreateEvent.getParentEvent();
		Player player = event.getPlayer();
		Block block = getDamageBlock(player);
		if (block.getType() == Material.VOID_AIR) return; // Should not happen?

		Material type = null;
		BlockFace facing = player.getFacing();
		if (block.getType() == Material.CACTUS) {
			type = RequiemItems.CACTUS_SKELETON_TOMBSTONE;
			Block upperBlock = block.getRelative(BlockFace.UP);
			while (upperBlock.getType() == Material.CACTUS) {
				block = upperBlock;
				upperBlock = block.getRelative(BlockFace.UP);
			}
		} else if (block.getType() == Material.POINTED_DRIPSTONE) {
			type = RequiemItems.DRIPSTONE_SKELETON_TOMBSTONE;
			Block relativeBlock = block.getRelative(BlockFace.UP);
			while (relativeBlock.getType() == Material.POINTED_DRIPSTONE) {
				block = relativeBlock;
				relativeBlock = block.getRelative(BlockFace.UP);
			}
			relativeBlock = block.getRelative(BlockFace.DOWN);
			if (relativeBlock.getType() == Material.POINTED_DRIPSTONE) {
				block = relativeBlock;
			}
		} else if (block.getType() == Material.LAVA) {
			type = RequiemItems.MELTED_SKELETON_TOMBSTONE;
		} else if (block.getType() == Material.WATER || (!block.isSolid() && LocationUtil.isWatery(block))) {
			type = RequiemItems.SWIMMER_SKELETON_TOMBSTONE;
		} else if (block.getType() == Material.POWDER_SNOW) {
			type = RequiemItems.POWDER_SNOW_SKELETON_TOMBSTONE;
		} else {
			EntityDamageEvent lastDamage = player.getLastDamageCause();
			if (lastDamage != null)
				type = getByDamageCause(player, lastDamage);

			if (type == null) {
				Set<Material> allowed = new HashSet<>(Set.of(
					RequiemItems.BASIC_SKELETON_TOMBSTONE, RequiemItems.COMMON_SKELETON_TOMBSTONE,
					RequiemItems.HEADLESS_SKELETON_TOMBSTONE, RequiemItems.NEUTRALIZED_SKELETON_TOMBSTONE,
					RequiemItems.UNSAVED_SKELETON_TOMBSTONE
				));

				if (!event.getDrops().isEmpty()) {
					allowed.add(RequiemItems.LUCKY_SKELETON_TOMBSTONE);
				}

				Biome biome = block.getBiome();
				if (block.getWorld().getEnvironment() == World.Environment.NETHER) {
					allowed.add(RequiemItems.BURNT_SKELETON_TOMBSTONE);
					allowed.add(RequiemItems.FUNGUS_GATHERER_SKELETON_TOMBSTONE);
				}
				if (block.getWorld().getEnvironment() == World.Environment.THE_END) {
					if (biome != Biome.THE_END) {
						allowed.add(RequiemItems.CHORUS_SKELETON_TOMBSTONE);
						allowed.add(RequiemItems.CHORUS_TANGLED_SKELETON_TOMBSTONE);
					} else {
						allowed.add(RequiemItems.DRAGON_BURNT_SKELETON_TOMBSTONE);
					}
				}
				if (BiomeTags.EXTRA_WARM.contains(biome))
					allowed.add(RequiemItems.DUSTY_SKELETON_TOMBSTONE);
				else if (biome == Biome.CRIMSON_FOREST)
					allowed.add(RequiemItems.CRIMSON_TANGLED_SKELETON_TOMBSTONE);
				else if (biome == Biome.LUSH_CAVES)
					allowed.add(RequiemItems.VINES_TANGLED_SKELETON_TOMBSTONE);

				Material blockType = block.getType();
				if (Tag.SAND.isTagged(blockType)
						|| blockType == Material.GRAVEL
						|| blockType == Material.SUSPICIOUS_GRAVEL
						|| blockType == Material.ROOTED_DIRT
						|| blockType == Material.SOUL_SAND)
					allowed.add(RequiemItems.BURIED_SKELETON_TOMBSTONE);
				if (blockType == Material.MOSS_BLOCK
						|| blockType == Material.MOSSY_COBBLESTONE
						|| blockType == Material.MOSSY_COBBLESTONE_SLAB
						|| blockType == Material.MOSSY_COBBLESTONE_STAIRS
						|| blockType == Material.MOSSY_COBBLESTONE_WALL
						|| blockType == Material.MOSSY_STONE_BRICKS
						|| blockType == Material.MOSSY_STONE_BRICK_SLAB
						|| blockType == Material.MOSSY_STONE_BRICK_STAIRS
						|| blockType == Material.MOSSY_STONE_BRICK_WALL
						|| blockType == Material.INFESTED_MOSSY_STONE_BRICKS)
					allowed.add(RequiemItems.MOSSY_SKELETON_TOMBSTONE);
				if (blockType == Material.MYCELIUM
						|| biome == Biome.MUSHROOM_FIELDS
						|| biome == Biome.OLD_GROWTH_PINE_TAIGA
						|| biome == Biome.OLD_GROWTH_SPRUCE_TAIGA) {
					allowed.add(RequiemItems.MUSHROOMER_SKELETON_TOMBSTONE);
					allowed.add(RequiemItems.SHROOM_SKELETON_TOMBSTONE);
				}
				if (blockType == Material.SNOW_BLOCK || blockType == Material.SNOW || BiomeTags.SNOWY.contains(biome)) {
					allowed.add(RequiemItems.SNOW_SKELETON_TOMBSTONE);
					allowed.add(RequiemItems.WINTER_SKELETON_TOMBSTONE);
				}
				if (blockType == Material.COBWEB) {
					allowed.add(RequiemItems.SPIDER_VICTIM_SKELETON_TOMBSTONE);
					allowed.add(RequiemItems.WEBBED_SKELETON_TOMBSTONE);
				}

				if (lastDamage instanceof EntityDamageByEntityEvent damageEvent) {
					if (damageEvent.getDamager() instanceof Spider) {
						allowed.add(RequiemItems.SPIDER_VICTIM_SKELETON_TOMBSTONE);
						allowed.add(RequiemItems.WEBBED_SKELETON_TOMBSTONE);
					}
				}

				event.getDrops().forEach(drop -> {
					Material dropType = drop.getType();
					if (UtilizerTags.BOWS.isTagged(dropType)) {
						allowed.add(RequiemItems.BOW_SKELETON_TOMBSTONE);
						allowed.add(RequiemItems.SHOT_SKELETON_TOMBSTONE);
					} else if (Tag.ITEMS_SKULLS.isTagged(dropType))
						allowed.add(RequiemItems.CORRUPTED_SKELETON_TOMBSTONE);
					else if (dropType == Material.END_CRYSTAL)
						allowed.add(RequiemItems.CRYSTAL_SKELETON_TOMBSTONE);
					else if (dropType == Material.WOODEN_SWORD)
						allowed.add(RequiemItems.ROOKIE_SKELETON_TOMBSTONE);
					else if (dropType == Material.STONE_SWORD) {
						allowed.add(RequiemItems.HEADACHE_SKELETON_TOMBSTONE);
						allowed.add(RequiemItems.SLAIN_SKELETON_TOMBSTONE);
					} else if (dropType == Material.IRON_SWORD)
						allowed.add(RequiemItems.DUELIST_SKELETON_TOMBSTONE);
					else if (dropType == Material.GOLDEN_SWORD)
						allowed.add(RequiemItems.THIEF_SKELETON_TOMBSTONE);
				});

				if (player.getEyeLocation().getBlock().getLightLevel() <= EntityUtil.DARKNESS_LIGHT_LEVEL && block.getWorld().getHighestBlockYAt(block.getLocation(), HeightMap.MOTION_BLOCKING) > player.getLocation().getBlockY()) {
					allowed.add(RequiemItems.DUNGEON_CRAWLER_SKELETON_TOMBSTONE);
					allowed.add(RequiemItems.EXPLORER_SKELETON_TOMBSTONE);
				}

				type = MathUtil.getRandom(allowed);
			}

			if (!block.isEmpty() && !block.isReplaceable() && block.isSolid()) {
				Block upperBlock = block.getRelative(BlockFace.UP);
				if (upperBlock.isEmpty() || upperBlock.isReplaceable())
					block = upperBlock;
			}
		}

		if (type == RequiemItems.QUICKSAND_SKELETON_TOMBSTONE
				|| type == RequiemItems.POWDER_SNOW_SKELETON_TOMBSTONE
				|| type == RequiemItems.MELTED_SKELETON_TOMBSTONE) {
			Block tempBlock = block;
			Block relativeBlock = block.getRelative(BlockFace.UP);
			int offset = 0;
			while (!relativeBlock.isEmpty() && !RequiemTags.TOMBSTONES.isTagged(relativeBlock.getType()) && offset++ < 10) {
				block = relativeBlock;
				relativeBlock = block.getRelative(BlockFace.UP);
			}
			if (!block.isEmpty() && !RequiemTags.TOMBSTONES.isTagged(block.getType()))
				block = tempBlock;
		} else if (type == RequiemItems.SWIMMER_SKELETON_TOMBSTONE
				|| type == RequiemItems.TRIDENT_SKELETON_TOMBSTONE) {
			Block tempBlock = block;
			Block relativeBlock = block.getRelative(BlockFace.DOWN);
			while ((relativeBlock.getType() == Material.WATER || !relativeBlock.isSolid() && LocationUtil.isWatery(relativeBlock)) && !RequiemTags.TOMBSTONES.isTagged(relativeBlock.getType())) {
				block = relativeBlock;
				relativeBlock = block.getRelative(BlockFace.DOWN);
			}
			if ((block.isSolid() || !LocationUtil.isWatery(block)) && !RequiemTags.TOMBSTONES.isTagged(block.getType()))
				block = tempBlock;
		}

		BlockDataStorage oldStorage = BlockStorage.getByLoc(block);
		if (oldStorage != null && !(oldStorage instanceof TombstoneBlockStorage)) return;

		Component message = event.deathMessage();
		if (message == null) message = event.originalDeathMessage();
		if (message != null)
			tombstoneCreateEvent.getData().getOrCreateCompound(TombstoneBlockStorage.DEATH_MESSAGE_KEY).mergeCompound(NBT.parseNBT(JSONComponentSerializer.json().serialize(message)));

		int slot = -1;
		for (ItemStack item : event.getDrops()) {
			tombstoneCreateEvent.setItem(slot, item);
			slot--;
		}
		event.getDrops().clear();

		if (oldStorage instanceof TombstoneBlockStorage storage) {
			storage.addData(tombstoneCreateEvent.getData());
		} else {
			BlockStorage.saveInfo(block, TombstoneBlockStorage.construct(block, type, facing, tombstoneCreateEvent.getData()));
		}

		event.setDroppedExp(0);
	}

	private @Nullable Material getByDamageCause(Player player, EntityDamageEvent event) {
		EntityDamageEvent.DamageCause cause = event.getCause();
		if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
			return RequiemItems.DEVASTATED_SKELETON_TOMBSTONE;
		if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
			Material blockType = player.getEyeLocation().getBlock().getType();
			return Tag.SAND.isTagged(blockType) || blockType == Material.GRAVEL || blockType == Material.SUSPICIOUS_GRAVEL ? RequiemItems.QUICKSAND_SKELETON_TOMBSTONE : null;
		}
		if (cause == EntityDamageEvent.DamageCause.FALL) {
			boolean waterBucket = player.getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET
					|| player.getInventory().getItemInOffHand().getType() == Material.WATER_BUCKET;
			Biome biome = player.getLocation().getBlock().getBiome();
			if (waterBucket)
				return BiomeTags.SNOWY.contains(biome) ? RequiemItems.FROZEN_WATERDROP_SKELETON_TOMBSTONE : RequiemItems.WATERDROP_SKELETON_TOMBSTONE;
			return Math.random() > 0.5 ? RequiemItems.SKELETON_FROM_THE_SKY_TOMBSTONE : RequiemItems.ACROBAT_SKELETON_TOMBSTONE;
		}
		if (event instanceof EntityDamageByEntityEvent damage) {
			switch (damage.getDamager()) {
				case AbstractArrow arrow -> {
					if (arrow instanceof Trident) return RequiemItems.TRIDENT_SKELETON_TOMBSTONE;
					List<Material> choices = new ArrayList<>(List.of(RequiemItems.HUNTED_SKELETON_TOMBSTONE, RequiemItems.ARROWS_SKELETON_TOMBSTONE));
					boolean bow = UtilizerTags.BOWS.isTagged(player.getInventory().getItemInMainHand().getType())
							|| UtilizerTags.BOWS.isTagged(player.getInventory().getItemInOffHand().getType());
					if (bow) choices.add(RequiemItems.SHOT_SKELETON_TOMBSTONE);
					return MathUtil.getRandom(choices);
				}
				case FallingBlock fallingBlock -> {
					return fallingBlock.getBlockData().getMaterial() == Material.POINTED_DRIPSTONE ? RequiemItems.STALACTITE_SKELETON_TOMBSTONE : null;
				}
				case EnderDragon enderDragon -> {
					return RequiemItems.DRAGON_BURNT_SKELETON_TOMBSTONE;
				}
				default -> {}
			}
			if (damage.getDamageSource().getCausingEntity() instanceof Blaze)
				return RequiemItems.PIERCED_SKELETON_TOMBSTONE;
			if (damage.getDamager() instanceof LivingEntity livingEntity) {
				if (livingEntity.getEquipment() == null) return null;
				Material stackType = livingEntity.getEquipment().getItemInMainHand().getType();
				if (stackType == Material.WOODEN_SWORD)
					return RequiemItems.ROOKIE_SKELETON_TOMBSTONE;
				if (stackType == Material.STONE_SWORD)
					return Math.random() > 0.5 ? RequiemItems.SLAIN_SKELETON_TOMBSTONE : RequiemItems.HEADACHE_SKELETON_TOMBSTONE;
				if (stackType == Material.IRON_SWORD)
					return RequiemItems.DUELIST_SKELETON_TOMBSTONE;
				if (stackType == Material.GOLDEN_SWORD)
					return RequiemItems.THIEF_SKELETON_TOMBSTONE;
			}
		}
		return null;
	}

	private Block getDamageBlock(Player player) {
		if (player.getLastDamageCause() instanceof EntityDamageByBlockEvent event) {
			Block block = event.getDamager();
			if (block != null)
				return block;
		}
		Block locBlock = player.getLocation().getBlock();
		if (locBlock.isEmpty()) {
			Block supportingBlock = player.getSupportingBlock();
			if (supportingBlock != null)
				return supportingBlock;
			Block highestBlock = locBlock.getWorld().getHighestBlockAt(locBlock.getLocation());
			if (highestBlock.getY() < locBlock.getY()) {
				locBlock = highestBlock;
			} else {
				do {
					locBlock = locBlock.getRelative(BlockFace.DOWN);
				} while (locBlock.isEmpty() && locBlock.getType() != Material.VOID_AIR);
			}
		}
		return locBlock;
	}

}
