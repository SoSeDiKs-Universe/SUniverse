package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.block.nms.ClayKilnBlock;
import me.sosedik.trappednewbie.util.NMesSUtil;
import me.sosedik.utilizer.api.event.player.PlayerPlaceItemEvent;
import me.sosedik.utilizer.api.storage.block.BlockDataStorageHolder;
import me.sosedik.utilizer.api.storage.block.ExtraDroppableBlockStorage;
import me.sosedik.utilizer.util.ItemUtil;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class ClayKilnBlockStorage extends BlockDataStorageHolder implements ExtraDroppableBlockStorage {

	private static final String STORED_ITEM_KEY = "item";
	private static final NamespacedKey DUMMY_RECIPE_KEY = TrappedNewbie.trappedNewbieKey("dummy");

	private @Nullable ItemStack storedItem;
	private int fireChecks = 0;
	private ItemDisplay mainDisplayFrame;
	private ItemDisplay displayStand;
	private NamespacedKey currentRecipe = DUMMY_RECIPE_KEY;
	private int taskId = -1;

	public ClayKilnBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
	}

	public ClayKilnBlockStorage(BlockPlaceEvent event, ReadWriteNBT nbt) {
		super(event.getBlockPlaced(), nbt);
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			event.setCancelled(true); // We don't want to place a block, if any
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getInteractionPoint() == null) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		event.setCancelled(true);
		if (!tryToPlaceOrTake(player, EquipmentSlot.HAND))
			tryToPlaceOrTake(player, EquipmentSlot.OFF_HAND);
	}

	private boolean tryToPlaceOrTake(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (this.storedItem == null) {
			if (item.isEmpty()) return false;
			if (!new PlayerPlaceItemEvent(player, item).callEvent()) return false;

			player.swingHand(hand);
			this.storedItem = item.asOne();
			item.subtract();
			fixupDisplay();
			return true;
		}

		if (item.isEmpty()) {
			player.swingHand(hand);
			player.getInventory().setItem(hand, this.storedItem);
			this.storedItem = null;
			fixupDisplay();
			return true;
		}

		if (item.getAmount() < item.getMaxStackSize() && item.isSimilar(this.storedItem)) {
			player.swingHand(hand);
			item.add();
			this.storedItem = null;
			fixupDisplay();
			return true;
		}

		return false;
	}

	private void fixupDisplay() {
		if (this.storedItem == null) {
			this.displayStand.setItemStack(null);
			return;
		}

		double rotation = switch (getFacing()) {
			case EAST -> 90;
			case WEST -> -90;
			case SOUTH -> 180;
			default -> 0;
		};

		Transformation transformation = this.displayStand.getTransformation();
		if (ItemUtil.shouldRenderAsBlock(this.storedItem)) {
			transformation.getTranslation().set(0F, 0.07F, 0F);
			transformation.getLeftRotation().set(new Quaternionf().rotationXYZ(0F, (float) Math.toRadians(180), (float) Math.toRadians(rotation)));
		} else {
			transformation.getTranslation().set(0F);
			transformation.getLeftRotation().set(new Quaternionf().rotationXYZ((float) Math.toRadians(90), (float) Math.toRadians(180), (float) Math.toRadians(rotation)));
		}
		this.displayStand.setTransformation(transformation);
		this.displayStand.setItemStack(this.storedItem);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		createEntities();
		spawnDisplay();
		checkFire();
	}

	private void checkFire() {
		if (isFired()) {
			checkRecipe();
			return;
		}

		this.taskId = TrappedNewbie.scheduler().sync(task -> {
			if (!isLoaded()) {
				this.taskId = -1;
				return true;
			}
			if (!LocationUtil.isFireSource(getBlock().getRelative(BlockFace.DOWN))) {
				this.fireChecks = 0;
				return false;
			}
			this.fireChecks++;
			if (this.fireChecks >= 8) {
				if (getBlock().getBlockData() instanceof ClayKilnBlock.ClayKiln clayKiln) {
					clayKiln.setBurned(true);
					getBlock().setBlockData(clayKiln);
				}
				this.taskId = -1;
				ItemStack displayItem = ItemStack.of(requireMatchingMaterial());
				displayItem.setBlockData(getBlock().getBlockData());
				this.mainDisplayFrame.setItemStack(displayItem);
				checkRecipe();
				return true;
			}
			return false;
		}, 20L, 20L).getTaskId();
	}

	private void checkRecipe() {
		if (this.storedItem == null) {
			this.fireChecks = 0;
			this.currentRecipe = DUMMY_RECIPE_KEY;
			TrappedNewbie.scheduler().sync(this::checkRecipe, 40L);
			return;
		}

		FurnaceRecipe recipe;
		if (Bukkit.getRecipe(this.currentRecipe) instanceof FurnaceRecipe furnaceRecipe && furnaceRecipe.getInputChoice().test(this.storedItem))
			recipe = furnaceRecipe;
		else
			recipe = NMesSUtil.getFurnaceRecipe(this.storedItem);

		if (recipe == null) {
			this.fireChecks = 0;
			this.currentRecipe = DUMMY_RECIPE_KEY;
			TrappedNewbie.scheduler().sync(this::checkRecipe, 40L);
			return;
		}

		if (!LocationUtil.isFireSource(getBlock().getRelative(BlockFace.DOWN))) {
			this.fireChecks = 0;
			this.currentRecipe = DUMMY_RECIPE_KEY;
			TrappedNewbie.scheduler().sync(this::checkRecipe, 40L);
			return;
		}

		getBlock().emitSound(Sound.BLOCK_CAMPFIRE_CRACKLE, 0.4F, 1F);
		getBlock().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, getBlock().getLocation().toCenterLocation(), 1, 0.01, 0.1, 0.01, 0.03);

		if (!this.currentRecipe.equals(recipe.getKey())) {
			this.fireChecks = 0;
			this.currentRecipe = recipe.getKey();
			TrappedNewbie.scheduler().sync(this::checkRecipe, 5L);
			return;
		}

		this.fireChecks++;
		if (this.fireChecks * 4 >= recipe.getCookingTime()) {
			this.fireChecks = 0;
			this.currentRecipe = DUMMY_RECIPE_KEY;
			this.storedItem = recipe.getResult();
			fixupDisplay();
			getBlock().emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.4F, 2F);
			TrappedNewbie.scheduler().sync(this::checkRecipe, 40L);
			return;
		}
		TrappedNewbie.scheduler().sync(this::checkRecipe, 5L);
	}

	private void createEntities() {
		Location loc = getBlock().getLocation();
		this.displayStand = spawnAt(loc.clone().center(0.16).shiftTowards(getFacing(), 0.2));
		Transformation transformation = this.displayStand.getTransformation();
		transformation.getScale().set(0.4F);
		this.displayStand.setTransformation(transformation);
	}

	private void spawnDisplay() {
		Location loc = getBlock().getLocation().toCenterLocation();
		this.mainDisplayFrame = loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
			display.setPersistent(false);
			display.setInvulnerable(true);
			display.setGravity(false);
			ItemStack displayItem = ItemStack.of(requireMatchingMaterial());
			displayItem.setBlockData(getBlock().getBlockData());
			display.setItemStack(displayItem);
			double rotation = switch (getFacing()) {
				case WEST -> -90;
				case EAST -> 90;
				case NORTH -> 180;
				default -> 0;
			};
			Transformation transformation = display.getTransformation();
			transformation.getLeftRotation().set(new Quaternionf().rotationY((float) Math.toRadians(rotation)));
			display.setTransformation(transformation);
		});
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

	@Override
	public List<ItemStack> getExtraDrops(Event event) {
		return ItemStack.isEmpty(this.storedItem) ? List.of() : List.of(this.storedItem);
	}

	@Override
	public void cleanUp() {
		this.mainDisplayFrame.remove();
		this.displayStand.remove();
		if (this.taskId != -1)
			TrappedNewbie.scheduler().cancelTask(this.taskId);
	}

	@Override
	public void onMove(Location from, Location to) {
		super.onMove(from, to);
		Location loc = getBlock().getLocation();
		this.mainDisplayFrame.teleport(loc.clone().toCenterLocation());
		this.displayStand.teleport(loc.clone().center(0.16).shiftTowards(getFacing(), 0.2));
		fixupDisplay();
	}

	public BlockFace getFacing() {
		return getBlock().getBlockData() instanceof ClayKilnBlock.ClayKiln clayKiln ? clayKiln.getFacing() : BlockFace.NORTH;
	}

	public boolean isFired() {
		return getBlock().getBlockData() instanceof ClayKilnBlock.ClayKiln clayKiln && clayKiln.isBurned();
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		if (this.storedItem != null) nbt.setItemStack(STORED_ITEM_KEY, this.storedItem);
		return nbt;
	}

}
