package me.sosedik.delightfulfarming.listener.item;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingEnchantments;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles Sweeping enchantment on hoes,
 * as well as allows for quick replanting
 */
// MCCheck: 1.21.11, new crops/seeds / grassy blocks
@NullMarked
public class SweepingHoes implements Listener {

	private static final Set<Location> BREAK_CACHE = new HashSet<>();
	private static final Map<Location, Player> TO_REPLANT = new HashMap<>();
	private static final Map<Material, Material> REPLANTABLE_CROP_TO_SEED = new HashMap<>();
	private static final Map<GrassType, Set<Material>> BREAKABLE = new HashMap<>();
	private static final Map<Material, Set<Material>> BREAKABLE_MAPPINGS = new HashMap<>();
	private static final Map<Material, Set<Material>> SWEEPABLE_TALL_MAPPINGS = new HashMap<>();

	static {
		addReplantableCrop(Material.WHEAT, Material.WHEAT_SEEDS);
		addReplantableCrop(Material.BEETROOTS, Material.BEETROOT_SEEDS);
		addReplantableCrop(Material.TORCHFLOWER_CROP, Material.TORCHFLOWER_SEEDS);
		addReplantableCrop(Material.PITCHER_CROP, Material.PITCHER_POD);
		addReplantableCrop(Material.CARROTS, Material.CARROT);
		addReplantableCrop(Material.POTATOES, Material.POTATO);
		addReplantableCrop(Material.NETHER_WART, Material.NETHER_WART);
		addReplantableCrop(Material.COCOA, Material.COCOA_BEANS);

		addTallSweepable(Material.SUGAR_CANE);
		addTallSweepable(Material.KELP_PLANT);

		addBreakable(GrassType.CROP, REPLANTABLE_CROP_TO_SEED.keySet());
		addBreakable(GrassType.PAMPOOKIN_CROP, List.of(
			Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM,
			Material.MELON_STEM, Material.ATTACHED_MELON_STEM
		));
		addBreakable(GrassType.GRASS, List.of(
			Material.SHORT_GRASS, Material.TALL_GRASS,
			Material.FERN, Material.LARGE_FERN,
			Material.DEAD_BUSH, Material.BUSH, Material.FIREFLY_BUSH,
			Material.SHORT_DRY_GRASS, Material.TALL_DRY_GRASS
		));
		addBreakable(GrassType.MUSHROOM, MaterialTags.MUSHROOMS.getValues());
		addBreakable(GrassType.MUSHROOM_BLOCk, MaterialTags.MUSHROOM_BLOCKS.getValues());
		addBreakable(GrassType.FLOWER, Tag.FLOWERS.getValues());
		addBreakable(GrassType.LEAF, List.of(Material.LEAF_LITTER));
		addBreakable(GrassType.PAMPOOKIN, List.of(
			Material.MELON, Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN
		));
		addBreakable(GrassType.ROOT, List.of(
			Material.CRIMSON_ROOTS, Material.WARPED_ROOTS, Material.NETHER_SPROUTS
		));

		Tag.LEAVES.getValues().forEach(SweepingHoes::addBreakable);
		addBreakable(Material.NETHER_WART);
		addBreakable(Material.COCOA);
		addBreakable(Material.BAMBOO);
		addBreakable(Material.SUGAR_CANE);
		addBreakable(Material.CACTUS);
		addBreakable(Material.CACTUS_FLOWER);
		addBreakable(Material.HAY_BLOCK);
		addBreakable(Material.SWEET_BERRY_BUSH, DelightfulFarmingItems.SWEET_BERRY_PIPS);
		addBreakable(Material.SEAGRASS, Material.TALL_SEAGRASS);
		addBreakable(Material.KELP, Material.KELP_PLANT);
		addBreakable(Material.SEA_PICKLE);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDrop(BlockDropItemEvent event) {
		Block block = event.getBlock();
		if (!block.isEmpty()) return;

		Player player = TO_REPLANT.remove(block.getLocation());
		if (player == null) return;

		BlockState blockState = event.getBlockState();
		if (!(blockState.getBlockData() instanceof Ageable blockData)) return;

		Material seedType = REPLANTABLE_CROP_TO_SEED.get(blockState.getType());
		if (seedType == null) return;

		boolean foundSeed = false;
		Iterator<Item> iterator = event.getItems().iterator();
		while (iterator.hasNext()) {
			Item next = iterator.next();
			ItemStack itemStack = next.getItemStack();
			if (itemStack.getType() != seedType) continue;

			foundSeed = true;
			if (itemStack.getAmount() > 1)
				itemStack.subtract();
			else
				iterator.remove();
			break;
		}

		if (!foundSeed) {
			PlayerInventory inventory = player.getInventory();
			foundSeed = checkSeedAndSubtract(inventory.getItemInOffHand(), seedType);
			if (!foundSeed) {
				for (int i = 0; i < 9; i++) {
					ItemStack item = inventory.getItem(InventorySlotHelper.FIRST_HOTBAR_SLOT + i);
					if (!ItemStack.isEmpty(item) && checkSeedAndSubtract(item, seedType)) {
						foundSeed = true;
						break;
					}
				}
				if (!foundSeed) return;
			}
		}

		blockData.setAge(0);
		block.setBlockData(blockData);
	}

	private boolean checkSeedAndSubtract(ItemStack item, Material seed) {
		if (item.getType() != seed) return false;

		item.subtract();
		return true;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Set<Material> tallBlocks = null;
		Player player = event.getPlayer();
		if (!isReplantable(block, true)) {
			tallBlocks = SWEEPABLE_TALL_MAPPINGS.get(block.getType());
			if (tallBlocks == null) return;
		}

		ItemStack item = player.getInventory().getItemInMainHand();
		if (!Tag.ITEMS_HOES.isTagged(item.getType())) return;

		int level = player.isSneaking() ? 0 : item.getEnchantLevel(DelightfulFarmingEnchantments.SWEEPING);
		int xzRadius = switch (level) {
			case 0 -> 0;
			case 1 -> 1;
			default -> 2;
		};

		List<Block> blocks = LocationUtil.getBlocksAround(block, xzRadius, 0);
		if (level > 1)
			blocks = MiscUtil.combineToList(blocks, LocationUtil.getBlocksAround(block.getRelative(BlockFace.UP), xzRadius, 0));

		boolean replanted = false;
		ServerLevel serverLevel = ((CraftWorld) block.getWorld()).getHandle();
		for (Block sideBlock : blocks) {
			item = player.getInventory().getItemInMainHand();
			if (!Tag.ITEMS_HOES.isTagged(item.getType())) break;

			if (tallBlocks != null) {
				Material sideBlockType = sideBlock.getType();
				if (sideBlockType == Material.KELP) sideBlockType = Material.KELP_PLANT;
				if (sideBlock.getRelative(BlockFace.DOWN).getType() != sideBlockType) continue;
			} else {
				if (!isReplantable(sideBlock, false)) continue;
			}

			Location sideBlockLoc = sideBlock.getLocation();
			BlockPos blockPosition = CraftLocation.toBlockPosition(sideBlockLoc);
			if (!((CraftPlayer) player).getHandle().mayInteract(serverLevel, blockPosition)) continue;

			TO_REPLANT.put(sideBlockLoc, player);
			BlockState blockState = sideBlock.getState();
			boolean wasSneaking = player.isSneaking();
			// Workaround to prevent extra behaviors from grass break
			// as sneaking usually stands for skipping a special action
			// Notably, prevents Sweeping enchantment from triggering
			player.setSneaking(true);
			boolean breakResult = player.breakBlock(sideBlock);
			player.setSneaking(wasSneaking);
			TO_REPLANT.remove(sideBlockLoc);
			if (!breakResult) continue;

			replanted = true;
			serverLevel.levelEvent(net.minecraft.world.level.block.LevelEvent.PARTICLES_DESTROY_BLOCK, blockPosition, net.minecraft.world.level.block.Block.getId(((CraftBlockState) blockState).getHandle()));
		}

		if (!replanted) return;

		player.swingMainHand();

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (BREAK_CACHE.contains(block.getLocation())) return;

		Set<Material> materials = BREAKABLE_MAPPINGS.get(block.getType());
		if (materials == null) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.isEmpty()) return;

		int level = item.getEnchantLevel(DelightfulFarmingEnchantments.SWEEPING);
		if (level == 0) return;

		int xzRadius = level == 1 ? 1 : 2;

		List<Block> blocks = LocationUtil.getBlocksAround(block, xzRadius, 0);
		if (level > 1)
			blocks = MiscUtil.combineToList(blocks, LocationUtil.getBlocksAround(block.getRelative(BlockFace.UP), xzRadius, 0));
		blocks.remove(block);

		boolean broken = false;
		ServerLevel serverLevel = ((CraftWorld) block.getWorld()).getHandle();
		for (Block sideBlock : blocks) {
			item = player.getInventory().getItemInMainHand();
			if (!item.hasEnchant(DelightfulFarmingEnchantments.SWEEPING)) break;

			if (!materials.contains(sideBlock.getType())) continue;

			Location sideBlockLoc = sideBlock.getLocation();
			BlockPos blockPosition = CraftLocation.toBlockPosition(sideBlockLoc);
			if (!((CraftPlayer) player).getHandle().mayInteract(serverLevel, blockPosition)) continue;

			BlockState blockState = sideBlock.getState();
			BREAK_CACHE.add(sideBlockLoc);
			boolean breakResult = player.breakBlock(sideBlock);
			BREAK_CACHE.remove(sideBlockLoc);
			if (!breakResult) continue;

			broken = true;
			serverLevel.levelEvent(net.minecraft.world.level.block.LevelEvent.PARTICLES_DESTROY_BLOCK, blockPosition, net.minecraft.world.level.block.Block.getId(((CraftBlockState) blockState).getHandle()));
		}

		if (!broken) return;

		player.swingMainHand();
	}

	private boolean isReplantable(Block block, boolean initial) {
		Material blockType = block.getType();
		if (!REPLANTABLE_CROP_TO_SEED.containsKey(blockType)) return false;

		if (!initial && block.getBlockData() instanceof Ageable ageable)
			return ageable.getAge() == ageable.getMaximumAge();

		return true;
	}

	/**
	 * Adds a block that can be replanted with a crop
	 *
	 * @param crop crop block
	 * @param seed seed item
	 */
	public static void addReplantableCrop(Material crop, Material seed) {
		REPLANTABLE_CROP_TO_SEED.put(crop, seed);
	}

	/**
	 * Adds a tall sweepable block,
	 * should contain only insta-breakable ones
	 *
	 * @param block block type
	 */
	public static void addTallSweepable(Material block) {
		SWEEPABLE_TALL_MAPPINGS.put(block, Set.of(block));
	}

	/**
	 * Adds a block that can be broken in a radius
	 *
	 * @param grassType type of the block
	 * @param materials block materials
	 */
	public static void addBreakable(GrassType grassType, Collection<Material> materials) {
		Set<Material> stored = BREAKABLE.computeIfAbsent(grassType, k -> new HashSet<>());
		stored.addAll(materials);
		materials.forEach(material -> BREAKABLE_MAPPINGS.put(material, stored));
	}

	/**
	 * Adds blocks that can be broken in a radius
	 *
	 * @param materials block materials
	 */
	public static void addBreakable(Material... materials) {
		addBreakable(List.of(materials));
	}

	/**
	 * Adds blocks that can be broken in a radius
	 *
	 * @param materials block materials
	 */
	public static void addBreakable(Collection<Material> materials) {
		Set<Material> stored = Set.copyOf(materials);
		materials.forEach(material -> BREAKABLE_MAPPINGS.put(material, stored));
	}

	public enum GrassType {

		GRASS,
		CROP,
		PAMPOOKIN_CROP,
		MUSHROOM,
		MUSHROOM_BLOCk,
		FLOWER,
		LEAF,
		PAMPOOKIN,
		ROOT

	}

}
