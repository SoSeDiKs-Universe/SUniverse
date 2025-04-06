package me.sosedik.miscme.listener.item;

import com.destroystokyo.paper.MaterialTags;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Dyes can be applied onto blocks immersible
 */
// MCCheck: 1.21.4, new colored blocks
@NullMarked
public class ImmersiveDyes implements Listener {

	public static final double DYE_REDUCE_CHANCE = 0.08D;
	public static final Material CLEARING_MATERIAL = Material.PAPER;

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onDye(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Player player = event.getPlayer();
		if (tryToDye(player, block, EquipmentSlot.HAND)
			|| tryToDye(player, block, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToDye(Player player, Block block, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!isDyingItem(item) && item.getType() != Material.SLIME_BALL) return false;

		Material apply = getApplied(item.getType(), block);
		if (apply == null) return false;
		if (block.getType() == apply) return true; // No need to try second hand

		if (Tag.SHULKER_BOXES.isTagged(apply)) {
			if (!(block.getBlockData() instanceof Directional oldData)) return true;
			if (!(apply.createBlockData() instanceof Directional newData)) return true;
			if (!(block.getState() instanceof ShulkerBox oldState)) return true;
			newData.setFacing(oldData.getFacing());
			ItemStack[] contents = oldState.getInventory().getStorageContents();
			Component customName = oldState.customName();
			block.setBlockData(newData);
			if (block.getState() instanceof ShulkerBox newState) {
				newState.customName(customName);
				newState.getSnapshotInventory().setStorageContents(contents);
				newState.update();
			}
			return postEffect(player, hand, item, block);
		}

		if (Tag.BEDS.isTagged(apply)) {
			if (!(block.getBlockData() instanceof Bed oldData)) return true;
			if (!(apply.createBlockData() instanceof Bed newData)) return true;
			newData.setPart(oldData.getPart());
			newData.setFacing(oldData.getFacing());
			newData.setOccupied(oldData.isOccupied());
			block.setBlockData(newData, false);
			Block secondBlock = block.getRelative(oldData.getFacing());
			if (!(secondBlock.getBlockData() instanceof Bed))
				secondBlock = block.getRelative(oldData.getFacing().getOppositeFace());
			if (secondBlock.getBlockData() instanceof Bed secondOldData) {
				newData.setPart(secondOldData.getPart());
				newData.setFacing(secondOldData.getFacing());
				newData.setOccupied(secondOldData.isOccupied());
				secondBlock.setBlockData(newData, false);
			}
			return postEffect(player, hand, item, block);
		}

		if (Tag.ITEMS_BANNERS.isTagged(apply) || Tag.BANNERS.isTagged(apply)) {
			if (!(block.getState() instanceof Banner banner)) return true;
			DyeColor color = banner.getBaseColor();
			List<Pattern> patterns = banner.getPatterns();
			if (block.getBlockData() instanceof Rotatable oldData && apply.createBlockData() instanceof Rotatable newData) {
				newData.setRotation(oldData.getRotation());
				block.setBlockData(newData);
			} else if (block.getBlockData() instanceof Directional oldData && apply.createBlockData() instanceof Directional newData) {
				newData.setFacing(oldData.getFacing());
				block.setBlockData(newData);
			} else {
				return true;
			}
			if (block.getState() instanceof Banner newState) {
				newState.setBaseColor(color);
				newState.setPatterns(patterns);
				newState.update();
			}
			return postEffect(player, hand, item, block);
		}

		if (apply == Material.STICKY_PISTON || apply == Material.PISTON || apply == Material.PISTON_HEAD) {
			if (block.getBlockData() instanceof PistonHead pistonHead)
				block = block.getRelative(pistonHead.getFacing().getOppositeFace());
			if (block.getBlockData() instanceof Piston oldData && apply.createBlockData() instanceof Piston newData) {
				newData.setExtended(oldData.isExtended());
				newData.setFacing(oldData.getFacing());
				block.setBlockData(newData);
				if (apply == Material.PISTON)
					block.getWorld().dropItemNaturally(block.getLocation().center(), new ItemStack(Material.SLIME_BALL));
				if (oldData.isExtended()) {
					block = block.getRelative(oldData.getFacing());
					if (block.getBlockData() instanceof PistonHead head) {
						head.setType(apply == Material.PISTON ? TechnicalPiston.Type.NORMAL : TechnicalPiston.Type.STICKY);
						block.setBlockData(head);
					}
				}
				return postEffect(player, hand, item, block);
			} else {
				return false;
			}
		} else {
			block.setType(apply);
			return postEffect(player, hand, item, block);
		}
	}

	private boolean postEffect(Player player, EquipmentSlot hand, ItemStack item, Block block) {
		playEffect(player, hand, block.getLocation(), block.getBlockData());

		if (!player.getGameMode().isInvulnerable()) {
			if (item.getType() == Material.SLIME_BALL || Math.random() < DYE_REDUCE_CHANCE)
				item.subtract();
		}

		return true;
	}

	private @Nullable Material getApplied(Material itemType, Block block) {
		// Piston to slime piston
		if (itemType == Material.SLIME_BALL) {
			if (block.getType() == Material.PISTON) return Material.STICKY_PISTON;
			if (block.getBlockData() instanceof PistonHead pistonHead && pistonHead.getType() == TechnicalPiston.Type.NORMAL) return Material.STICKY_PISTON;
			return null;
		}

		// Removing dyes
		if (itemType == Material.PAPER) {
			if (MaterialTags.STAINED_GLASS.isTagged(block))
				return Material.GLASS;
			if (MaterialTags.STAINED_GLASS_PANES.isTagged(block))
				return Material.GLASS_PANE;
			if (MaterialTags.STAINED_TERRACOTTA.isTagged(block))
				return Material.TERRACOTTA;
			if (MaterialTags.SHULKER_BOXES.isTagged(block))
				return Material.SHULKER_BOX;
			if (Tag.CANDLES.isTagged(block.getType()))
				return Material.CANDLE;
			if (Tag.CANDLE_CAKES.isTagged(block.getType()))
				return Material.CANDLE_CAKE;
			if (block.getType() == Material.STICKY_PISTON)
				return Material.PISTON;
			if (block.getBlockData() instanceof PistonHead pistonHead && pistonHead.getType() == TechnicalPiston.Type.STICKY)
				return Material.PISTON;
			return null;
		}

		if (MaterialTags.GLASS.isTagged(block))
			return block.getType() == Material.TINTED_GLASS ? null : Material.getMaterial(itemType.name().replace("DYE", "STAINED_GLASS"));
		if (MaterialTags.GLASS_PANES.isTagged(block))
			return Material.getMaterial(itemType.name().replace("DYE", "STAINED_GLASS_PANE"));
		if (MaterialTags.SHULKER_BOXES.isTagged(block))
			return Material.getMaterial(itemType.name().replace("DYE", "SHULKER_BOX"));
		if (MaterialTags.CONCRETES.isTagged(block))
			return Material.getMaterial(itemType.name().replace("DYE", "CONCRETE"));
		if (MaterialTags.CONCRETE_POWDER.isTagged(block))
			return Material.getMaterial(itemType.name().replace("DYE", "CONCRETE_POWDER"));
		if (MaterialTags.GLAZED_TERRACOTTA.isTagged(block))
			return Material.getMaterial(itemType.name().replace("DYE", "GLAZED_TERRACOTTA"));
		if (MaterialTags.TERRACOTTA.isTagged(block))
			return Material.getMaterial(itemType.name().replace("DYE", "TERRACOTTA"));
		if (Tag.WOOL.isTagged(block.getType()))
			return Material.getMaterial(itemType.name().replace("DYE", "WOOL"));
		if (Tag.WOOL_CARPETS.isTagged(block.getType()))
			return Material.getMaterial(itemType.name().replace("DYE", "CARPET"));
		if (Tag.BEDS.isTagged(block.getType()))
			return Material.getMaterial(itemType.name().replace("DYE", "BED"));
		if (Tag.ITEMS_BANNERS.isTagged(block.getType()))
			return Material.getMaterial(itemType.name().replace("DYE", "BANNER"));
		if (Tag.BANNERS.isTagged(block.getType()))
			return Material.getMaterial(itemType.name().replace("DYE", "WALL_BANNER"));
		if (Tag.CANDLES.isTagged(block.getType()))
			return Material.getMaterial(itemType.name().replace("DYE", "CANDLE"));
		if (Tag.CANDLE_CAKES.isTagged(block.getType()))
			return Material.getMaterial(itemType.name().replace("DYE", "CANDLE_CAKE"));

		return null;
	}

	/**
	 * Checks whether this item as a dye or a cleaning item
	 *
	 * @param item item
	 * @return whether this item is a dying item
	 */
	public static boolean isDyingItem(ItemStack item) {
		Material type = item.getType();
		return MaterialTags.DYES.isTagged(type) || type == CLEARING_MATERIAL;
	}

	/**
	 * Plays dying effect, that is: swing hand, play sound, spawn particles.
	 *
	 * @param player player
	 * @param hand hand to swing
	 * @param loc location for particles
	 * @param effect particle data
	 */
	public static void playEffect(Player player, @Nullable EquipmentSlot hand, Location loc, @Nullable BlockData effect) {
		player.clearActiveItem(); // In case dye is usable
		player.emitSound(Sound.ENTITY_LEASH_KNOT_PLACE, 1F, 2F);
		if (hand != null) player.swingHand(hand);
		if (effect != null) loc.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, loc.center(0.5), 10, 0.4, 0.4, 0.4, 0, effect);
	}

	/**
	 * Gets the dye color from the dye item
	 *
	 * @param dye dye item stack
	 * @return dye color
	 */
	public static @Nullable DyeColor getDyeColor(ItemStack dye) {
		try {
			return DyeColor.valueOf(dye.getType().getKey().getKey().replace("_dye", "").toUpperCase(Locale.US));
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

}
