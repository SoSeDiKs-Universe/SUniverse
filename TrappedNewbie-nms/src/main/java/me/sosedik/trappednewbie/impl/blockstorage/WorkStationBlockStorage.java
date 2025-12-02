package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieSoundKeys;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.util.NMesSUtil;
import me.sosedik.utilizer.api.event.player.PlayerPlaceItemEvent;
import me.sosedik.utilizer.api.storage.block.BlockDataStorageHolder;
import me.sosedik.utilizer.api.storage.block.ExtraDroppableBlockStorage;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.ItemUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

@NullMarked
public class WorkStationBlockStorage extends BlockDataStorageHolder implements ExtraDroppableBlockStorage {

	private static final String STORED_ITEMS_KEY = "items";
	private static final String STORED_TOOL_KEY = "stored_tool";
	private static final double[] OFFSETS = {0.5 - 0.31, 0.5, 0.5 + 0.31};
	private static final int[] WEST_SLOTS = {6, 3, 0, 7, 4, 1, 8, 5, 2};
	private static final int[] RESULTS_ORDER = {4, 5, 3, 1, 7, 0, 2, 6, 8};
	private static final Map.Entry<NamespacedKey, Integer> DUMMY_RECIPE = Map.entry(TrappedNewbie.trappedNewbieKey("dummy"), -1);
	private static final Random RANDOM = new Random();

	private final ItemDisplay display;
	private final @Nullable ItemStack[] displayItems;
	private @Nullable ItemStack storedTool;
	private final ItemDisplay[] displayStands = new ItemDisplay[9];
	private ItemDisplay hammerStand;
	private Map.Entry<NamespacedKey, Integer> currentRecipe = DUMMY_RECIPE;

	public WorkStationBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.displayItems = nbt.hasTag(STORED_ITEMS_KEY) ? nbt.getItemStackArray(STORED_ITEMS_KEY) : new ItemStack[9];
		if (nbt.hasTag(STORED_TOOL_KEY)) this.storedTool = nbt.getItemStack(STORED_TOOL_KEY);
		this.display = createDisplay();
	}

	public WorkStationBlockStorage(BlockPlaceEvent event, ReadWriteNBT nbt) {
		super(event.getBlockPlaced(), nbt);
		this.displayItems = new ItemStack[9];
		this.display = createDisplay();
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			event.setCancelled(true); // We don't want to place a block, if any
			return;
		}

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			tryToCraft(event);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if (event.getBlockFace() != BlockFace.UP) {
			tryToPlaceAHammer(event);
			return;
		}

		tryToPlaceAnItem(event);
	}

	private void tryToPlaceAnItem(PlayerInteractEvent event) {
		Location loc = event.getInteractionPoint();
		if (loc == null) return;

		double x = MathUtil.getDecimalPart(loc.getX());
		double z = MathUtil.getDecimalPart(loc.getZ());
		boolean xp = x > 0;
		boolean zp = z > 0;
		x = Math.abs(x);
		z = Math.abs(z);

		int i = (x < 0.34) ? (xp ? 0 : 2) : (x > 0.65 ? (xp ? 2 : 0) : 1);
		int j = (z < 0.34) ? (zp ? 0 : 2) : (z > 0.65 ? (zp ? 2 : 0) : 1);
		int slot = getSlot(i + 3 * j);

		Player player = event.getPlayer();
		ItemStack currentItem = this.displayItems[slot];
		EquipmentSlot heldSlot = EquipmentSlot.HAND;
		ItemStack heldItem = player.getInventory().getItem(heldSlot);
		if (ItemStack.isEmpty(currentItem)) {
			if (heldItem.isEmpty() || TrappedNewbieTags.HAMMERS.isTagged(heldItem.getType())) {
				heldSlot = EquipmentSlot.OFF_HAND;
				heldItem = player.getInventory().getItem(heldSlot);
				if (heldItem.isEmpty()) return;
			}
			if (!new PlayerPlaceItemEvent(player, heldItem).callEvent()) return;

			event.setCancelled(true);
			currentItem = heldItem.asOne();
			this.displayItems[slot] = currentItem;
			fixAngle(slot, i, j);
			heldItem.subtract();
			player.swingHand(heldSlot);
			getBlock().emitSound(Sound.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.PLAYERS, 1F, 1F);
			return;
		} else {
			if (heldItem.isEmpty() && !(player.getInventory().getItemInOffHand().isSimilar(currentItem))) {
				event.setCancelled(true);
				player.getInventory().setItemInMainHand(currentItem);
				this.displayItems[slot] = null;
				fixAngle(slot, i, j);
				player.swingMainHand();
				getBlock().emitSound(Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 1F, 1F);
				return;
			} else if (heldItem.getAmount() < heldItem.getMaxStackSize() && heldItem.isSimilar(currentItem)) {
				event.setCancelled(true);
				heldItem.add();
				this.displayItems[slot] = null;
				fixAngle(slot, i, j);
				player.swingMainHand();
				getBlock().emitSound(Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 1F, 1F);
				return;
			} else {
				heldItem = player.getInventory().getItemInOffHand();
				if (heldItem.getAmount() < heldItem.getMaxStackSize() && heldItem.isSimilar(currentItem)) {
					event.setCancelled(true);
					heldItem.add();
					this.displayItems[slot] = null;
					fixAngle(slot, i, j);
					player.swingOffHand();
					getBlock().emitSound(Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 1F, 1F);
					return;
				} else if (heldItem.isEmpty()) {
					event.setCancelled(true);
					player.getInventory().setItemInOffHand(currentItem);
					this.displayItems[slot] = null;
					fixAngle(slot, i, j);
					player.swingOffHand();
					getBlock().emitSound(Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 1F, 1F);
					return;
				}
			}
		}
		if (!player.isSneaking())
			event.setCancelled(true);
	}

	private void tryToPlaceAHammer(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getBlockFace() != getFacing() && !player.isSneaking()) return;

		ItemStack hand = player.getInventory().getItemInMainHand();
		if (ItemStack.isEmpty(this.storedTool)) {
			if (!TrappedNewbieTags.HAMMERS.isTagged(hand.getType())) return;
			if (!new PlayerPlaceItemEvent(player, hand).callEvent()) return;

			player.swingMainHand();
			this.storedTool = hand.asOne();
			hand.subtract();
			fixAngle(10, 1, 1);
		} else {
			if (!hand.isEmpty()) return;

			player.swingMainHand();
			player.getInventory().setItemInMainHand(this.storedTool);
			this.storedTool = null;
			fixAngle(10, 1, 1);
		}
		event.setCancelled(true);
	}

	private BlockFace getFacing() {
		return getBlock().getBlockData() instanceof Directional directional ? directional.getFacing() : BlockFace.NORTH;
	}

	private void tryToCraft(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (!TrappedNewbieTags.HAMMERS.isTagged(item.getType())) return;

		event.setCancelled(true);
		if (DurabilityUtil.isBroken(item)) {
			getBlock().emitSound(net.kyori.adventure.sound.Sound.sound(TrappedNewbieSoundKeys.WOOD_CHOP_FAIL_SOUND, net.kyori.adventure.sound.Sound.Source.BLOCK, 1F, 1F));
			applyRandomRotations();
			return;
		}

		ItemStack[] matrix = new ItemStack[this.displayItems.length];
		for (int i = 0; i < matrix.length; i++) {
			ItemStack matrixItem = this.displayItems[i];
			matrix[i] = matrixItem == null ? ItemStack.empty() : matrixItem;
		}
		Recipe recipe = Bukkit.getCraftingRecipe(matrix, player.getWorld());
		if (!(recipe instanceof Keyed keyed)) {
			getBlock().emitSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.2F, 2F);
			applyRandomRotations();
			return;
		}

		NamespacedKey recipeKey = keyed.getKey();
		if (!player.hasDiscoveredRecipe(recipeKey)) {
			getBlock().emitSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.2F, 2F);
			applyRandomRotations();
			return;
		}

		Map.Entry<ItemStack, Supplier<CraftItemEvent>> craftingRecipe = NMesSUtil.findCraftingRecipe(player, matrix);
		if (craftingRecipe == null || craftingRecipe.getKey().getAmount() > 9) {
			getBlock().emitSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.2F, 2F);
			applyRandomRotations();
			return;
		}

		if (RANDOM.nextDouble() < 0.2)
			item.damage(1, player);

		getBlock().emitSound(Sound.BLOCK_WOOD_HIT, 1F, 1F);
		applyRandomRotations();
		if (!this.currentRecipe.getKey().equals(recipeKey)) {
			this.currentRecipe = Map.entry(recipeKey, 1);
			return;
		}

		int tries = this.currentRecipe.getValue() + 1;
		if (tries < 3) {
			this.currentRecipe = Map.entry(recipeKey, tries);
			return;
		}

		CraftItemEvent craftEvent = craftingRecipe.getValue().get();
		craftEvent.callEvent();

		ItemStack result = player.getItemOnCursor();
		player.setItemOnCursor(null);

		int matrixLeftovers = 0;
		for (ItemStack matrixItem : craftEvent.getInventory().getMatrix()) {
			if (!ItemStack.isEmpty(matrixItem))
				matrixLeftovers += matrixItem.getAmount();
		}
		if (result.getAmount() + matrixLeftovers > 9 || ItemStack.isEmpty(result)) {
			getBlock().emitSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.2F, 2F);
			applyRandomRotations();
			return;
		}

		this.currentRecipe = DUMMY_RECIPE;
		getBlock().emitSound(Sound.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.PLAYERS, 1F, 1F);
		for (int i = 0; i < this.displayItems.length; i++) {
			this.displayItems[i] = null;
			this.displayStands[i].setItemStack(null);
		}
		int slot = 0;
		for (ItemStack matrixItem : craftEvent.getInventory().getMatrix()) {
			if (!ItemStack.isEmpty(matrixItem)) {
				this.displayItems[slot] = matrixItem;
				this.displayStands[slot].setItemStack(matrixItem);
			}
			slot++;
		}
		slot = 0;
		while (result.getAmount() > 0) {
			int displaySlot = RESULTS_ORDER[slot];
			if (ItemStack.isEmpty(this.displayItems[displaySlot])) {
				this.displayItems[displaySlot] = result.asOne();
				result = result.subtract();
			}
			slot++;
		}
		applyRandomRotations();
	}

	private void applyRandomRotations() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int slot = getSlot(i + 3 * j);
				fixAngle(slot, i, j);
			}
		}
	}

	private int getSlot(int slot) {
		return switch (getFacing()) {
			case WEST -> WEST_SLOTS[slot];
			case EAST -> 8 - WEST_SLOTS[slot];
			case NORTH -> 8 - slot;
			default -> slot;
		};
	}

	private void fixAngle(int slot, int i, int j) {
		Location loc = getBlock().getLocation();
		BlockFace facing = getFacing();
		double rotation = switch (facing) {
			case EAST -> 90;
			case WEST -> -90;
			case SOUTH -> 180;
			default -> 0;
		};

		if (slot == 10) {
			this.hammerStand.teleport(loc.add(OFFSETS[i] + offset(), 0.39, OFFSETS[j] + offset()).shiftTowards(facing, 0.15));
			Transformation transformation = this.hammerStand.getTransformation();
			transformation.getLeftRotation().set(new Quaternionf().rotationXYZ((float) Math.toRadians(90), 0F, (float) Math.toRadians(rotation)));
			this.hammerStand.setTransformation(transformation);
			this.hammerStand.setItemStack(this.storedTool);
			return;
		}

		ItemDisplay display = this.displayStands[slot];
		ItemStack item = this.displayItems[slot];
		if (ItemStack.isEmpty(item)) {
			display.setItemStack(null);
			return;
		}

		display.setInterpolationDelay(0); // Badly named method; it's actually required to call every time for interpolation
		if (ItemUtil.shouldRenderAsBlock(item)) {
			Transformation transformation = display.getTransformation();
			transformation.getTranslation().set(offset(), 0.07F, offset());
			transformation.getLeftRotation().set(new Quaternionf().rotationXYZ(0F, (float) Math.toRadians(180), (float) Math.toRadians(rotation)));
			display.setTransformation(transformation);
		} else {
			Transformation transformation = display.getTransformation();
			transformation.getTranslation().set(offset(), 0F, offset());
			transformation.getLeftRotation().set(new Quaternionf().rotationXYZ((float) Math.toRadians(90), (float) Math.toRadians(180), (float) Math.toRadians(rotation)));
			display.setTransformation(transformation);
		}
		display.setItemStack(item);
	}

	private float offset() {
		return RANDOM.nextFloat(0.04F) - 0.02F;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		createEntities();
		applyRandomRotations();
	}

	@Override
	public List<ItemStack> getExtraDrops(Event event) {
		List<ItemStack> drops = new ArrayList<>();
		for (ItemStack item : this.displayItems) {
			if (!ItemStack.isEmpty(item))
				drops.add(item);
		}
		if (!ItemStack.isEmpty(this.storedTool))
			drops.add(this.storedTool);
		return drops;
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		this.display.remove();
		for (Entity displayStand : this.displayStands)
			displayStand.remove();
		this.hammerStand.remove();
	}

	@Override
	public void onMove(Location from, Location to) {
		super.onMove(from, to);
		Location loc = getBlock().getLocation();
		this.display.teleport(calcDisplayLocation());
		this.hammerStand.teleport(loc.clone().center(0.39));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int slot = getSlot(i + 3 * j);
				Location asLoc = loc.clone().add(OFFSETS[i], 0.95, OFFSETS[j]);
				this.displayStands[slot].teleport(asLoc);
				ItemStack item = this.displayItems[slot];
				if (item != null)
					fixAngle(slot, i, j);
			}
		}
		fixAngle(10, 1, 1);
	}

	private void createEntities() {
		Location loc = getBlock().getLocation();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int slot = getSlot(i + 3 * j);
				Location asLoc = loc.clone().add(OFFSETS[i], 0.95, OFFSETS[j]);
				this.displayStands[slot] = spawnAt(asLoc);
				Transformation transformation = this.displayStands[slot].getTransformation();
				transformation.getScale().set(0.3F);
				this.displayStands[slot].setTransformation(transformation);
				ItemStack item = this.displayItems[slot];
				if (item != null)
					fixAngle(slot, i, j);
			}
		}
		this.hammerStand = spawnAt(loc.clone().center(0.39).shiftTowards(getFacing(), 0.15));
		Transformation transformation = this.hammerStand.getTransformation();
		transformation.getScale().set(0.4F);
		this.hammerStand.setTransformation(transformation);
		fixAngle(10, 1, 1);
	}

	private ItemDisplay spawnAt(Location loc) {
		return loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
			display.setPersistent(false);
			display.setInvulnerable(true);
			display.setGravity(false);
			display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
			display.setInterpolationDuration(2);
			display.setInterpolationDelay(0);
		});
	}

	private ItemDisplay createDisplay() {
		Location loc = calcDisplayLocation();
		return loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
			display.setPersistent(false);
			display.setItemStack(ItemStack.of(requireMatchingMaterial()));
			Transformation transformation = display.getTransformation();
			double rotation = switch (getFacing()) {
				case WEST -> -90;
				case EAST -> 90;
				case NORTH -> 180;
				default -> 0;
			};
			transformation.getLeftRotation().rotationY((float) Math.toRadians(rotation));
			display.setTransformation(transformation);
		});
	}

	private Location calcDisplayLocation() {
		return getBlock().getLocation().center();
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		nbt.setItemStackArray(STORED_ITEMS_KEY, this.displayItems);
		if (this.storedTool != null) nbt.setItemStack(STORED_TOOL_KEY, this.storedTool);
		return nbt;
	}

}
