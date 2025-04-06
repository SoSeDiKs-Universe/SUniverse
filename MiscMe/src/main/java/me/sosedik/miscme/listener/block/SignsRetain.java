package me.sosedik.miscme.listener.block;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Signs keep text, color and glowing state upon breaking.
 * <br>Saved text can be applied to signs in the world with RMB.
 * <br>Also adds a recipe to reset signs with nbt.
 */
@NullMarked
public class SignsRetain implements Listener {

	public SignsRetain() {
		addSignResetRecipe();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSignBreak(BlockDropItemEvent event) {
		if (!(event.getBlockState() instanceof Sign sign)) return;
		if (isEmptySign(sign)) return;

		ItemStack drop = null;
		for (Item item : event.getItems()) {
			var itemStack = item.getItemStack();
			if (!Tag.ALL_SIGNS.isTagged(itemStack.getType())) continue;
			drop = itemStack;
			break;
		}
		if (drop == null) return;
		if (!(drop.getItemMeta() instanceof BlockStateMeta meta)) return;

		meta.setBlockState(sign);
		drop.setItemMeta(meta);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignPlace(BlockPlaceEvent event) {
		applySign(event.getItemInHand(), event.getBlock(), true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getItem() == null) return;

		var player = event.getPlayer();
		if (player.isSneaking()) return;

		var block = event.getClickedBlock();
		assert block != null;
		if (applySign(event.getItem(), block, false)) {
			event.setCancelled(true);
			player.swingMainHand();
			block.emitSound(Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 1F, 1F);
		}
	}

	private boolean isEmptySign(Sign sign) {
		for (Side side : Side.values()) {
			if (sign.getSide(side).getColor() != DyeColor.BLACK) return false;
			if (sign.getSide(side).isGlowingText()) return false;

			List<Component> lines = sign.getSide(side).lines();
			for (Component line : lines) {
				if (!ChatUtil.getPlainText(line).isEmpty())
					return false;
			}
		}
		return true;
	}

	private boolean applySign(ItemStack item, Block block, boolean applyDecorations) {
		if (!Tag.ALL_SIGNS.isTagged(item.getType())) return false;
		if (!(block.getState() instanceof Sign sign)) return false;
		if (!(item.getItemMeta() instanceof BlockStateMeta meta)) return false;
		if (!(meta.getBlockState() instanceof Sign itemSign)) return false;
		if (applyDecorations && isEmptySign(itemSign)) return false;

		boolean changed = false;
		for (Side side : Side.values()) {
			if (applyDecorations) {
				sign.getSide(side).setColor(itemSign.getSide(side).getColor());
				sign.getSide(side).setGlowingText(itemSign.getSide(side).isGlowingText());
			}

			List<Component> lines = itemSign.getSide(side).lines();
			for (var i = 0; i < lines.size(); i++) {
				var lineBefore = sign.getSide(side).line(i);
				var lineAfter = lines.get(i);
				if (!lineBefore.equals(lineAfter)) {
					sign.getSide(side).line(i, lines.get(i));
					changed = true;
				}
			}
		}

		if (changed || applyDecorations) {
			sign.update();
			return true;
		}

		return false;
	}

	private void addSignResetRecipe() {
		List<Material> signs = new ArrayList<>(Tag.ALL_SIGNS.getValues()).stream().filter(Material::isItem).toList();
		new ShapelessCraft(ItemStack.of(Material.OAK_SIGN), MiscMe.miscmeKey("sign_reset"))
			.special()
			.withExemptLeftovers()
			.addIngredients(signs, item -> {
				if (!item.hasItemMeta()) return false;
				if (!(item.getItemMeta() instanceof BlockStateMeta meta)) return false;
				if (!(meta.getBlockState() instanceof Sign sign)) return false;
				return !isEmptySign(sign);
			})
			.withPreCheck(event -> {
				ItemStack ingredient = null;
				for (ItemStack item : event.getMatrix()) {
					if (ItemStack.isEmpty(item)) continue;
					if (!Tag.ALL_SIGNS.isTagged(item.getType())) continue;

					ingredient = item;
					break;
				}
				if (ingredient == null) {
					event.setResult(null);
					return;
				}
				ItemStack result = ingredient.clone();
				result.editMeta(BlockStateMeta.class, BlockStateMeta::clearBlockState);
				event.setResult(result);
			})
			.register();
	}

}
