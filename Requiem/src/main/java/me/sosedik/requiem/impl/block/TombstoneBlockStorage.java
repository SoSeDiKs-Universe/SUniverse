package me.sosedik.requiem.impl.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.loot.LootContextKey;
import me.sosedik.requiem.api.event.player.TombstoneDestroyEvent;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.utilizer.api.storage.block.BlockDataStorageHolder;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.util.Transformation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

@NullMarked
public class TombstoneBlockStorage extends BlockDataStorageHolder {

	private static final String FACING_KEY = "facing";
	private static final String STORAGES_KEY = "storages";
	public static final String EXP_KEY = "exp";
	public static final String ITEMS_KEY = "items";
	public static final String DEATH_MESSAGE_KEY = "message";

	private static final Random RANDOM = new Random();

	private final ReadWriteNBT storedData;
	private final ItemDisplay display;
	
	public TombstoneBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.storedData = nbt;
		this.display = createDisplay();
	}

	public TombstoneBlockStorage(BlockPlaceEvent event, ReadWriteNBT nbt) {
		super(event.getBlockPlaced(), nbt);
		this.storedData = nbt;

		ItemStack item = event.getItemInHand();
		NBT.get(item, itemNbt -> {
			if (itemNbt.hasTag(DEATH_MESSAGE_KEY)) {
				ReadWriteNBT data = NBT.createNBTObject();
				data.getOrCreateCompound(STORAGES_KEY)
					.getOrCreateCompound("0")
					.getOrCreateCompound(DEATH_MESSAGE_KEY)
					.mergeCompound(itemNbt.getCompound(DEATH_MESSAGE_KEY));
				this.storedData.mergeCompound(data);
			}
		});

		BlockFace facing = event.getPlayer().getFacing().getOppositeFace();
		this.display = createDisplay(block, facing, getBlock().getType());

		updateBlockType();
	}

	public TombstoneBlockStorage(Block block, NamespacedKey storageId, ItemDisplay display, ReadWriteNBT storedData) {
		super(block, storageId);
		this.storedData = storedData;
		this.display = display;

		updateBlockType();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		updateBlockType();
	}

	private void updateBlockType() {
		Material type = getMatchingMaterial();
		if (type == null) return;

		if (type == RequiemItems.CACTUS_SKELETON_TOMBSTONE) {
			if (this.block.getType() != Material.CACTUS)
				this.block.setType(Material.CACTUS);
		} else if (type == RequiemItems.DRIPSTONE_SKELETON_TOMBSTONE) {
			if (this.block.getType() != Material.POINTED_DRIPSTONE) {
				this.block.setType(Material.POINTED_DRIPSTONE);
				Block upperBlock = this.block.getRelative(BlockFace.UP);
				if (upperBlock.isEmpty())
					upperBlock.setType(Material.POINTED_DRIPSTONE);
			}
		} else if (type == RequiemItems.SNOW_SKELETON_TOMBSTONE) {
			if (this.block.getType() != Material.SNOW)
				this.block.setType(Material.SNOW);
		} else if (type == RequiemItems.WEBBED_SKELETON_TOMBSTONE) {
			if (this.block.getType() != Material.COBWEB)
				this.block.setType(Material.COBWEB);
		} else if (type == RequiemItems.SWIMMER_SKELETON_TOMBSTONE) {
			Material blockType = this.block.getType();
			if (blockType != Material.SEAGRASS && blockType != Material.TALL_SEAGRASS && blockType != Material.SEA_PICKLE)
				this.block.setType(Material.SEAGRASS);
		} else if (this.block.getType().isAir()) {
			this.block.setType(type);
		}
	}

	@Override
	public void onMove(Location from, Location to) {
		super.onMove(from, to);
		this.display.teleportAsync(this.display.getLocation().set(to.x(), to.getBlockY() + 0.5, to.getBlockZ()));
	}

	@Override
	public void onBreak(BlockBreakEvent event) {
		super.onBreak(event);

		if (isPlayerTombstone() && event.getBlock().getType() != getMatchingMaterial()) // Drop loot, keep base block
			event.setCancelled(true);

		if (dropLoot(event.getPlayer()))
			event.setDropItems(false);
	}

	@Override
	public boolean onExplode(Event event) {
		super.onExplode(event);

		if (dropLoot(null)) {
			this.block.setType(Material.AIR);
			return true;
		}
		return false;
	}

	@Override
	public void onBurn(BlockBurnEvent event) {
		super.onBurn(event);

		dropLoot(null);
	}

	@Override
	public void onDestroy(BlockDestroyEvent event) {
		super.onDestroy(event);

		if (event.willDrop() && dropLoot(null))
			event.setWillDrop(false);
	}

	private boolean dropLoot(@Nullable Player player) {
		if (isPlayerTombstone()) {
			dropIfSilkTouch(player);
			dropStorage(player);
			return true;
		}

		Material type = getMatchingMaterial();
		if (type == null) return false;
		if (this.block.getType() == type) return false;

		LootTable lootTable = Bukkit.getLootTable(new NamespacedKey(getId().getNamespace(), "blocks/" + getId().getKey()));
		if (lootTable == null) return false;

		if (dropIfSilkTouch(player)) return true;

		LootContext lootContext = new LootContext.Builder(this.block.getWorld())
			.luck(player == null ? 0F : (float) Objects.requireNonNull(player.getAttribute(Attribute.LUCK)).getValue())
			.with(LootContextKey.BLOCK_DATA, this.block.getBlockData())
			.with(LootContextKey.ORIGIN, this.block.getLocation())
			.with(LootContextKey.TOOL, player == null ? ItemStack.empty() : player.getInventory().getItemInMainHand())
			.build();
		Location loc = this.block.getLocation().center();
		lootTable.populateLoot(RANDOM, lootContext).forEach(item -> this.block.getWorld().dropItemNaturally(loc, item));

		return true;
	}

	private boolean dropIfSilkTouch(@Nullable Player player) {
		if (player == null) return false;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (!item.hasEnchant(Enchantment.SILK_TOUCH)) return false;

		Material type = getMatchingMaterial();
		if (type == null) return false;

		Location loc = this.block.getLocation().center();
		item = new ItemStack(type);

		if (this.storedData.hasTag(STORAGES_KEY)) {
			ReadWriteNBT storage = this.storedData.getOrCreateCompound(STORAGES_KEY).getCompound("0");
			if (storage != null && storage.hasTag(DEATH_MESSAGE_KEY))
				NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.getOrCreateCompound(DEATH_MESSAGE_KEY).mergeCompound(storage.getCompound(DEATH_MESSAGE_KEY)));
		}

		this.block.getWorld().dropItemNaturally(loc, item);
		return true;
	}

	private void dropStorage(@Nullable Player player) {
		if (!this.storedData.hasTag(STORAGES_KEY)) return;

		ReadWriteNBT storageData = this.storedData.getOrCreateCompound(STORAGES_KEY);
		List<ReadWriteNBT> storages = new ArrayList<>();
		int storageNum = 0;
		while (storageData.hasTag(String.valueOf(storageNum))) {
			ReadWriteNBT storage = storageData.getCompound(String.valueOf(storageNum++));
			if (storage != null)
				storages.add(storage);
		}
		var event = new TombstoneDestroyEvent(this.block, player, storages);

		for (ReadWriteNBT storage : event.getStorages()) {
			if (storage.hasTag(EXP_KEY)) event.addExp(storage.getInteger(EXP_KEY));
			if (storage.hasTag(ITEMS_KEY)) {
				ReadWriteNBT itemsTag = storage.getCompound(ITEMS_KEY);
				if (itemsTag == null) continue;

				for (String key : itemsTag.getKeys()) {
					int slot;
					try {
						slot = Integer.parseInt(key);
					} catch (NumberFormatException ignored) {
						continue;
					}

					ItemStack item = itemsTag.getItemStack(key);
					if (ItemStack.isEmpty(item)) continue;

					if (player == null || slot < 0 || !ItemStack.isEmpty(player.getInventory().getItem(slot))) {
						event.getToDrop().add(item);
					} else {
						player.getInventory().setItem(slot, item);
					}
				}
			}
		}
		event.callEvent();
		Location loc = this.block.getLocation().center();
		int exp = event.getExp();
		if (exp > 0) {
			if (player == null) {
				this.block.getWorld().spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(exp));
			} else {
				player.giveExp(exp);
			}
		}
		if (player == null) {
			event.getToDrop().forEach(item -> this.block.getWorld().dropItemNaturally(loc, item));
		} else {
			event.getToDrop().forEach(item -> InventoryUtil.addOrDrop(player, item, false));
		}
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		this.display.remove();
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		nbt.mergeCompound(this.storedData);
		return nbt;
	}

	public boolean isPlayerTombstone() {
		return this.storedData.hasTag(STORAGES_KEY);
	}

	public void addData(ReadWriteNBT data) {
		int nextStorageNum = this.storedData.hasTag(STORAGES_KEY) ? this.storedData.getOrCreateCompound(STORAGES_KEY).getKeys().size() : 0;
		ReadWriteNBT storages = this.storedData.getOrCreateCompound(STORAGES_KEY);
		storages.getOrCreateCompound(String.valueOf(nextStorageNum)).mergeCompound(data);
	}

	private ItemDisplay createDisplay() {
		BlockFace facing = this.storedData.getOrNull(FACING_KEY, BlockFace.class);
		if (facing == null) {
			facing = MathUtil.getRandom(LocationUtil.SURROUNDING_BLOCKS_XZ);
			this.storedData.setEnum(FACING_KEY, facing);
		}
		return createDisplay(this.block, facing, getMatchingMaterial());
	}

	private static ItemDisplay createDisplay(Block block, BlockFace facing, @Nullable Material type) {
		return block.getWorld().spawn(block.getLocation().center().setDirection(facing.getDirection()), ItemDisplay.class, display -> {
			display.setItemStack(new ItemStack(type == null ? RequiemItems.BASIC_SKELETON_TOMBSTONE : type));
			display.setPersistent(false);
			Transformation transformation = display.getTransformation();
			transformation.getScale().set(0.99);
			display.setTransformation(transformation);
		});
	}

	public static TombstoneBlockStorage construct(Block block, Material type, BlockFace facing, @Nullable ReadWriteNBT storageData) {
		ReadWriteNBT nbt = NBT.createNBTObject();
		if (storageData != null) nbt.getOrCreateCompound(STORAGES_KEY).getOrCreateCompound("0").mergeCompound(storageData);
		return new TombstoneBlockStorage(block, type.getKey(), createDisplay(block, facing, type), nbt);
	}

}
