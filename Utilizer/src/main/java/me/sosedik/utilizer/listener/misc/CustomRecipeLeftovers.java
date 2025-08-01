package me.sosedik.utilizer.listener.misc;

import me.sosedik.utilizer.api.event.recipe.ItemCraftEvent;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Custom recipe handlers and leftovers.
 * Accounts for durability when calculating the craftable amount of the item.
 */
@NullMarked
public class CustomRecipeLeftovers implements Listener {

	private static final Map<NamespacedKey, @Nullable BiPredicate<RemainingItemEvent, ItemStack>> LEFTOVER_EXEMPTS = new HashMap<>();

	// This logic is intentionally applied to the vanilla crafts as well
	// in order to make custom crafts (like work stations) compatible
	// E.g. work station calls CraftItemEvent and expects matrix to be
	// empty with the result to be set to the player's cursor, but due to
	// the fake event vanilla does not process it
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraft(CraftItemEvent event) {
		if (!(event.getRecipe() instanceof Keyed keyed)) return;
		if (!(event.getWhoClicked() instanceof Player player)) return;

		ItemStack result = event.getInventory().getResult();
		if (result == null) return;

		event.setCancelled(true);
		ClickType clickType = event.getClick();
		if (clickType.isShiftClick()) {
			PlayerInventory inv = player.getInventory();
			int craftable = getCraftableAmount(event.getInventory(), keyed.getKey());
			int receptive = getReceptiveAmount(inv, result);

			if (craftable <= 0) return;
			if (receptive <= 0) return;

			if (craftable > receptive) craftable = receptive;
			for (int i = 0; i < craftable; i++) {
				ItemStack drop = generateResult(event, keyed);
				if (drop == null) {
					craftable = i;
					break; // Something cancelled crafts, don't try further and process only already crafted
				}
				InventoryUtil.addOrDrop(player, drop, true);
			}

			updateMatrix(event, keyed.getKey(), player, craftable);
		} else if (clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP) {
			result = generateResult(event, keyed);
			if (result == null) return;

			Item drop = player.dropItem(result);
			if (drop == null) return;

			updateMatrix(event, keyed.getKey(), player, 1);
		} else if (clickType == ClickType.SWAP_OFFHAND) {
			ItemStack current = player.getInventory().getItemInOffHand();
			if (current.getType() != Material.AIR) return;

			result = generateResult(event, keyed);
			if (result == null) return;

			player.getInventory().setItemInOffHand(result);
			updateMatrix(event, keyed.getKey(), player, 1);
		} else if (clickType == ClickType.NUMBER_KEY) {
			int slot = event.getHotbarButton();
			ItemStack current = player.getInventory().getItem(slot);
			if (!ItemStack.isEmpty(current)) return;

			result = generateResult(event, keyed);
			if (result == null) return;

			player.getInventory().setItem(slot, result);
			updateMatrix(event, keyed.getKey(), player, 1);
		} else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
			result = generateResult(event, keyed);
			if (result == null) return;

			ItemStack cursor = event.getCursor();
			if (ItemStack.isEmpty(cursor)) {
				player.setItemOnCursor(result);
				updateMatrix(event, keyed.getKey(), player, 1);
				return;
			}
			if (result.isSimilar(cursor)) {
				int amount = cursor.getAmount() + result.getAmount();
				if (amount > cursor.getMaxStackSize()) return;
				player.setItemOnCursor(cursor.asQuantity(amount));
				updateMatrix(event, keyed.getKey(), player, 1);
			}
		}
	}

	private void updateMatrix(CraftItemEvent event, NamespacedKey key, Player player, int amount) {
		CraftingInventory inv = event.getInventory();
		BiPredicate<RemainingItemEvent, ItemStack> exemptCheck = LEFTOVER_EXEMPTS.get(key);
		@UnknownNullability ItemStack[] matrix = inv.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			if (ItemStack.isEmpty(matrix[i])) continue;

			var itemEvent = new RemainingItemEvent(event, player, event.getRecipe(), key, matrix[i], amount);

			if (exemptCheck != null && exemptCheck.test(itemEvent, matrix[i])) {
				if (matrix[i].getAmount() == amount) {
					matrix[i] = null;
				} else {
					matrix[i].subtract(amount);
				}
				continue;
			}

			itemEvent.callEvent();
			if (itemEvent.getResult() != null) {
				matrix[i] = itemEvent.getResult();
				continue;
			}

			Material replacement = matrix[i].getType().getCraftingRemainingItem();
			if (replacement != null) {
				matrix[i] = ItemStack.of(replacement);
				continue;
			}

			if (matrix[i].getAmount() == amount) {
				matrix[i] = null;
			} else {
				matrix[i].subtract(amount);
			}
		}

		inv.setMatrix(matrix);
	}

	private @Nullable ItemStack generateResult(CraftItemEvent parentEvent, Keyed recipe) {
		var event = new ItemCraftEvent(parentEvent, recipe.getKey());
		event.callEvent();
		ItemStack result = event.getResult();
		return result == null ? null : result.clone();
	}

	private int getCraftableAmount(CraftingInventory inv, NamespacedKey recipeKey) {
		if (LEFTOVER_EXEMPTS.containsKey(recipeKey)) return 1;

		int amount = 64;

		for (ItemStack stack : inv.getMatrix()) {
			if (ItemStack.isEmpty(stack)) continue;

			int craftable = DurabilityUtil.hasDurability(stack) ? DurabilityUtil.getDurability(stack) : stack.getAmount();
			amount = Math.min(amount, craftable);
		}

		return amount == -1 ? 1 : amount;
	}

	private int getReceptiveAmount(PlayerInventory inv, ItemStack item) {
		int amount = 0;

		for (int slot = 0; slot < 36; slot++) {
			ItemStack stack = inv.getItem(slot);

			if (ItemStack.isEmpty(stack)) {
				amount += item.getMaxStackSize();
			} else if (item.getMaxStackSize() > stack.getAmount() && item.isSimilar(stack)) {
				amount += (item.getMaxStackSize() - stack.getAmount());
			}
		}

		return amount;
	}

	/**
	 * Adds an exemption leftover rule
	 *
	 * @param key recipe key
	 * @param check checks whether the item should be exempt from leaving a leftover
	 */
	public static void addExemptRule(NamespacedKey key, @Nullable BiPredicate<RemainingItemEvent, ItemStack> check) {
		LEFTOVER_EXEMPTS.computeIfAbsent(key, k -> check == null ? (event, item) -> true : check);
	}

}
