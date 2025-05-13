package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.recipe.ChoppingBlockCrafting;
import me.sosedik.utilizer.api.storage.block.BlockDataStorageHolder;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.RecipeManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ChoppingBlockStorage extends BlockDataStorageHolder {

	private static final NamespacedKey SUCCESS_SOUND = ResourceLib.getSound(TrappedNewbie.trappedNewbieKey("block/wood_chop"));
	private static final NamespacedKey FAIL_SOUND = ResourceLib.getSound(TrappedNewbie.trappedNewbieKey("block/wood_chop_fail"));

	private static final String STORED_ITEM_KEY = "item";

	private final ItemDisplay display;
	private @Nullable ItemStack currentItem;
	private int chops = 0;

	public ChoppingBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		if (nbt.hasTag(STORED_ITEM_KEY))
			this.currentItem = nbt.getItemStack(STORED_ITEM_KEY);
		this.display = createDisplay();
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			// Chopping
			if (tryToChop(player)) {
				event.setCancelled(true);
				return;
			}
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			ItemStack result = RecipeManager.getResult(ChoppingBlockCrafting.class, new ItemStack[]{player.getInventory().getItemInOffHand()});
			if (result != null)
				event.setCancelled(true);
			return;
		}

		// Placing item
		if (this.currentItem == null) {
			if (tryToPlace(player, EquipmentSlot.HAND) || tryToPlace(player, EquipmentSlot.OFF_HAND))
				event.setCancelled(true);
			return;
		}

		// Obtaining item back
		if (tryToPickup(player, EquipmentSlot.HAND) || tryToPickup(player, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
			this.currentItem = null;
			this.display.setItemStack(new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
		}
	}

	private boolean tryToPlace(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (RecipeManager.getRecipe(ChoppingBlockCrafting.class, new ItemStack[]{item}) == null) return false;

		this.currentItem = item.asOne();
		this.display.setItemStack(this.currentItem);
		player.swingHand(hand);
		if (item.getType().isBlock())
			getBlock().emitSound(item.getType().createBlockData().getSoundGroup().getPlaceSound(), 0.5F, 1F);
		item.subtract();

		return true;
	}

	private boolean tryToPickup(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.isEmpty()) {
			player.getInventory().setItem(hand, this.currentItem);
			if (item.getType().isBlock())
				getBlock().emitSound(item.getType().createBlockData().getSoundGroup().getStepSound(), 0.5F, 1F);
			return true;
		}
		if (item.getAmount() < item.getMaxStackSize() && item.isSimilar(this.currentItem)) {
			item.add();
			player.swingHand(hand);
			if (item.getType().isBlock())
				getBlock().emitSound(item.getType().createBlockData().getSoundGroup().getStepSound(), 0.5F, 1F);
			return true;
		}
		return false;
	}

	private boolean tryToChop(Player player) {
		if (this.currentItem == null) return false;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (!Tag.ITEMS_AXES.isTagged(item.getType())) return false;
		if (player.hasCooldown(item)) return true;

		// Client swings offhand first if there's a block in it,
		// which causes the main hand with an axe to not swing at all.
		// The delay is needed to guarantee that the main hand will be swung.
		Material offHand = player.getInventory().getItemInOffHand().getType();
		long delay = (offHand != Material.AIR && offHand.isBlock()) ? 5L : 0L;
		TrappedNewbie.scheduler().sync(player::swingMainHand, delay);

		if (item.getType().isBlock())
			getBlock().emitSound(item.getType().createBlockData().getSoundGroup().getHitSound(), 0.5F, 1F);
		item.damage(1, player);
		if (DurabilityUtil.isBroken(item) || this.chops++ < 2) {
			Tag.ITEMS_AXES.getValues().forEach(axe -> player.setCooldown(axe, 10));
			TrappedNewbie.scheduler().sync(() -> getBlock().emitSound(Sound.sound(FAIL_SOUND, Sound.Source.BLOCK, 1F, 1F)), delay);
			return true;
		}

		Tag.ITEMS_AXES.getValues().forEach(axe -> player.setCooldown(axe, 20));
		this.chops = 0;
		TrappedNewbie.scheduler().sync(() -> getBlock().emitSound(Sound.sound(SUCCESS_SOUND, Sound.Source.BLOCK, 1F, 1F)), delay);
		if (this.currentItem.getType().isBlock())
			getBlock().getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, getBlock().getLocation().center(), 100, 0.3, 0.3, 0.3, 1, this.currentItem.getType().createBlockData());
		ItemStack result = RecipeManager.getResult(ChoppingBlockCrafting.class, new ItemStack[]{this.currentItem});
		if (result != null)
			getBlock().getWorld().dropItemNaturally(calcDisplayLocation(), result);
		this.currentItem = null;
		this.display.setItemStack(new ItemStack(TrappedNewbieItems.MATERIAL_AIR));

		return true;
	}

	@Override
	public void onLoad() {
		super.onLoad();
	}

	@Override
	public void onMove(Location from, Location to) {
		super.onMove(from, to);
		this.display.teleportAsync(calcDisplayLocation());
	}

	private ItemDisplay createDisplay() {
		Location loc = calcDisplayLocation();
		return loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
			display.setPersistent(false);
			display.setItemStack(this.currentItem == null ? new ItemStack(TrappedNewbieItems.MATERIAL_AIR) : this.currentItem);
			Transformation transformation = display.getTransformation();
			transformation.getTranslation().set(0F, 0.2F, 0F);
			transformation.getScale().set(0.4F);
			display.setTransformation(transformation);
		});
	}

	private Location calcDisplayLocation() {
		return getBlock().getLocation().center();
	}

	public boolean hasItemToChop() {
		return this.currentItem != null;
	}

	@Override
	public void cleanUp() {
		if (this.currentItem != null)
			getBlock().getWorld().dropItemNaturally(calcDisplayLocation(), this.currentItem);
		this.display.remove();
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		if (this.currentItem != null) nbt.setItemStack(STORED_ITEM_KEY, this.currentItem);
		return nbt;
	}

}
