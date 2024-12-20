package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContextType;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VisualArmorModifier extends ItemModifier {

	public static final ItemModifierContextType VISUAL_ARMOR = ItemModifierContextType.context(ItemModifierContext.EMPTY.getClass()).withName().withLore().build();
	private static final NamespacedKey SADDLE_HEAD_MODEL = TrappedNewbie.trappedNewbieKey("saddle_head");

	public VisualArmorModifier(@NotNull NamespacedKey key) {
		super(key);
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		Player target;
		if (contextBox.getContext() instanceof EntityEquipmentPacketContext ctx && ctx.getEntity() instanceof Player other) {
			target = other;
		} else {
			target = contextBox.getViewer();
		}
		if (target == null) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		int slot = resolveSlot(target, contextBox.getContext());
		if (!isArmorSlot(target, slot)) return ModificationResult.PASS;

		var messenger = Messenger.messenger(player);
		EquipmentSlot equipmentSlot = InventoryUtil.getBySlot(slot);
		var visualArmor = VisualArmor.of(target);

		if (visualArmor.isArmorPreview()) {
			if (equipmentSlot == EquipmentSlot.OFF_HAND) {
				contextBox.setItem(
					visualArmor.hasGloves()
						? parseItem(player, contextBox.getLocale(), visualArmor.getGloves())
						: getEmpty(messenger, contextBox.getLocale(), player, target, EquipmentSlot.OFF_HAND, true)
				);
				return ModificationResult.RETURN;
			}
			ItemStack item = target.getInventory().getItem(equipmentSlot); // This item might've been sent by a plugin's packet, so we get the real item just in case
			if (ItemStack.isEmpty(item) || item.getType() == TrappedNewbieItems.MATERIAL_AIR) {
				contextBox.setItem(getEmpty(messenger, contextBox.getLocale(), player, target, equipmentSlot, true));
				return ModificationResult.RETURN;
			}
			contextBox.setItem(getLored(messenger, contextBox.getLocale(), player, target, parseItem(player, contextBox.getLocale(), item), equipmentSlot, true, true));
			return ModificationResult.RETURN;
		}

		if (!equipmentSlot.isArmor())
			return ModificationResult.PASS;

		contextBox.setItem(
			visualArmor.hasItem(equipmentSlot)
				? getLored(messenger, contextBox.getLocale(), player, target, parseItem(player, contextBox.getLocale(), visualArmor.getItem(equipmentSlot)), equipmentSlot, false, true)
				: getEmpty(messenger, contextBox.getLocale(), player, target, equipmentSlot, false)
		);

		return ModificationResult.RETURN;
	}

	@Override
	public boolean skipAir() {
		return false;
	}

	@Override
	public boolean skipContext(@NotNull ItemModifierContextType contextType) {
		return contextType == VISUAL_ARMOR;
	}

	private @NotNull ItemStack parseItem(@NotNull Player player, @NotNull Locale locale, @NotNull ItemStack item) {
		ItemStack newItem = modifyItem(new ItemContextBox(player, locale, VISUAL_ARMOR, ItemModifierContext.EMPTY, item));
		return newItem == null ? item.clone() : newItem;
	}

	private int resolveSlot(@NotNull Player player, @NotNull ItemModifierContext context) {
		if (context instanceof EntityEquipmentPacketContext ctx) return player.canUseEquipmentSlot(ctx.getSlot()) ? InventoryUtil.getSlot(player, ctx.getSlot()) : -1;
		if (context instanceof SlottedItemModifierContext ctx) return ctx.slot();
		return -1;
	}

	private @NotNull ItemStack getEmpty(@NotNull Messenger messenger, @NotNull Locale locale, @NotNull Player player, @NotNull Player target, @NotNull EquipmentSlot slot, boolean armorPreview) {
		ItemStack item = parseItem(player, locale, getOutline(messenger, slot, armorPreview));
		return getLored(messenger, locale, player, target, item, slot, armorPreview, false);
	}

	private @NotNull ItemStack getOutline(@NotNull Messenger messenger, @NotNull EquipmentSlot slot, boolean armorPreview) {
		ItemStack item = switch (slot) {
			case HEAD -> ItemStack.of(TrappedNewbieItems.HELMET_OUTLINE);
			case CHEST -> ItemStack.of(TrappedNewbieItems.CHESTPLATE_OUTLINE);
			case LEGS -> ItemStack.of(TrappedNewbieItems.LEGGINGS_OUTLINE);
			case FEET -> ItemStack.of(TrappedNewbieItems.BOOTS_OUTLINE);
			case OFF_HAND -> ItemStack.of(TrappedNewbieItems.GLOVES_OUTLINE);
			default -> throw new IllegalArgumentException("Unsupported equipment slot: " + slot.name());
		};
		String localeKey = "equipment." + slot.name().toLowerCase(Locale.ROOT) + "." + (armorPreview ? "armor" : "cosmetic");
		item.setData(DataComponentTypes.ITEM_NAME, messenger.getMessage(localeKey));
		return item;
	}

	private boolean isGhost(@NotNull Material type) { // TODO make tag?
		return type == TrappedNewbieItems.MATERIAL_AIR
			|| type == TrappedNewbieItems.HELMET_OUTLINE
			|| type == TrappedNewbieItems.CHESTPLATE_OUTLINE
			|| type == TrappedNewbieItems.LEGGINGS_OUTLINE
			|| type == TrappedNewbieItems.BOOTS_OUTLINE
			|| type == TrappedNewbieItems.GLOVES_OUTLINE;
	}

	private @NotNull ItemStack getLored(@NotNull Messenger messenger, @NotNull Locale locale, @NotNull Player player, @NotNull Player target, @NotNull ItemStack item, @NotNull EquipmentSlot slot, boolean armorPreview, boolean realItem) {
		if (ItemStack.isEmpty(item)) return item;

		// Show real armor if not wearing visual
		if (!armorPreview && !realItem) {
			ItemStack underwear = target.getInventory().getItem(slot);
			if (!ItemStack.isEmpty(underwear) && !isGhost(underwear.getType())) {
				return parseUnderwear(messenger, player, locale, underwear);
			}
		}

		// Show elytras when gliding
		if (!armorPreview && slot == EquipmentSlot.CHEST && player.isGliding() && item.getType() != Material.ELYTRA) {
			ItemStack underwear = player.getInventory().getItem(slot);
			if (underwear.getType() == Material.ELYTRA) {
				return parseUnderwear(messenger, player, locale, underwear);
			}
		}

		// Visual armor
		ItemMeta meta = item.getItemMeta();
		List<Component> lore = meta.hasLore() ? Objects.requireNonNull(meta.lore()) : new ArrayList<>();
		if (!armorPreview && realItem) lore.addFirst(messenger.getMessage("equipment.cosmetic"));
		if (slot.isArmor()) {
			if (lore.size() > 1 || (armorPreview && lore.size() == 1)) lore.add(Component.empty());
			lore.add(messenger.getMessage("equipment.layer_switch." + (armorPreview ? "underwear" : "cosmetic")).applyFallbackStyle(Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.withState(false))));
		}
		meta.lore(lore);

		// Using custom model for saddles on head
		if (!armorPreview && slot == EquipmentSlot.HEAD && item.getType() == Material.SADDLE) {
			meta.setItemModel(SADDLE_HEAD_MODEL);
		}

		item.setItemMeta(meta);
		return item;
	}

	private static @NotNull ItemStack parseUnderwear(@NotNull Messenger messenger, @NotNull Player player, @NotNull Locale locale, @NotNull ItemStack underwear) {
		ItemStack newUnderwear = modifyItem(player, locale, underwear);
		if (newUnderwear != null) underwear = newUnderwear;
		else underwear = underwear.clone();

		underwear.editMeta(meta -> {
			List<Component> lore = meta.hasLore() ? Objects.requireNonNull(meta.lore()) : new ArrayList<>();
			lore.addFirst(messenger.getMessage("equipment.underwear"));
			if (lore.size() > 1) lore.add(Component.empty());
			lore.add(messenger.getMessage("equipment.layer_switch.cosmetic").applyFallbackStyle(Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.withState(false))));
			meta.lore(lore);
		});
		return underwear;
	}

	public static boolean isArmorSlot(@NotNull Player player, int slot) {
		if (slot < 5) return false;
		if (slot > 8 && slot != InventorySlotHelper.OFF_HAND) return false;
		InventoryType inventoryType = player.getOpenInventory().getTopInventory().getType();
		return inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CRAFTING;
	}

}
