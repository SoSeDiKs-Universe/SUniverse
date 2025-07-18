package me.sosedik.utilizer.util;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * General utilities around items
 */
// MCCheck: 1.21.8, item types
@NullMarked
public class ItemUtil {

	private ItemUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Tries to get items tag
	 *
	 * @param key key
	 * @return items tag
	 */
	public static Tag<Material> itemTag(NamespacedKey key) {
		return Objects.requireNonNull(Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class), () -> "Couldn't find item tag " + key);
	}

	/**
	 * Tries to get items tag
	 *
	 * @param key key
	 * @return items tag
	 */
	public static Tag<Material> blockTag(NamespacedKey key) {
		return Objects.requireNonNull(Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class), () -> "Couldn't find block tag " + key);
	}

	/**
	 * Checks whether the item is a water bottle
	 *
	 * @param item item
	 * @return whether the item is a water bottle
	 */
	public static boolean isWaterBottle(ItemStack item) {
		if (item.getType() != Material.POTION) return false;
		if (!item.hasData(DataComponentTypes.POTION_CONTENTS)) return false;

		PotionContents potionContents = item.getData(DataComponentTypes.POTION_CONTENTS);
		assert potionContents != null;
		return potionContents.potion() == PotionType.WATER;
	}

	/**
	 * Checks whether item is considered hot
	 *
	 * @param item item
	 * @return whether item is a light source
	 */
	public static boolean isHot(ItemStack item) {
		return UtilizerTags.HOT_ITEMS.isTagged(item.getType()) || isLitCampfire(item);
	}

	/**
	 * Checks whether item is a light source
	 *
	 * @param item item
	 * @return whether item is a light source
	 */
	public static boolean isLightSource(ItemStack item) {
		return UtilizerTags.LIGHT_SOURCES.isTagged(item.getType())
				|| item.hasEnchant(Enchantment.FIRE_ASPECT)
				|| isLitCampfire(item);
	}

	/**
	 * Checks whether item is a burning item
	 *
	 * @param item item
	 * @return whether item is a burning item
	 */
	public static boolean isBurningItem(ItemStack item) {
		return item.getType() == Material.TORCH
			|| item.getType() == Material.SOUL_TORCH
			|| isLitCampfire(item);
	}

	/**
	 * Checks whether this item is a lit campfire
	 *
	 * @param item item
	 * @return whether this item is a lit campfire
	 */
	public static boolean isLitCampfire(ItemStack item) {
		return Tag.CAMPFIRES.isTagged(item.getType())
				&& item.hasItemMeta()
				&& item.getItemMeta() instanceof BlockStateMeta meta
				&& meta.hasBlockState()
				&& meta.getBlockState().getBlockData() instanceof Campfire campfire
				&& campfire.isLit();
	}

	/**
	 * Checks whether the item is as melee weapon
	 *
	 * @param item item
	 * @return whether the item is as melee weapon
	 */
	public static boolean isMeleeWeapon(ItemStack item) {
		Material type = item.getType();
		if (Tag.ITEMS_SWORDS.isTagged(type)) return true;
		if (Tag.ITEMS_AXES.isTagged(type)) return true;

		return switch (type) {
			case TRIDENT, MACE -> true;
			default -> false;
		};
	}

	/**
	 * Created player head with a texture
	 *
	 * @param texture texture value
	 * @return textured head item
	 */
	public static ItemStack texturedHead(String texture) {
		var item = new ItemStack(Material.PLAYER_HEAD);
		PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
		playerProfile.setProperty(new ProfileProperty("textures", texture));
		item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(playerProfile));
		return item;
	}

	/**
	 * Calculates attribute value of the item for its primary slot.
	 * <p>Armor items will calculate the value for their respective armor slot,
	 * and for other items main hand slot will be used.
	 *
	 * @see #getAttributeValue(ItemStack, EquipmentSlot, Attribute)
	 * @param item item
	 * @param attribute attribute
	 * @param entity entity
	 * @return attribute value of the item
	 */
	public static double getAttributeValue(@Nullable ItemStack item, Attribute attribute, @Nullable LivingEntity entity) {
		if (ItemStack.isEmpty(item)) return 0D;

		Material type = item.getType();
		if (MaterialTags.HELMETS.isTagged(type)) return getAttributeValue(item, EquipmentSlot.HEAD, attribute, entity);
		if (MaterialTags.CHESTPLATES.isTagged(type)) return getAttributeValue(item, EquipmentSlot.CHEST, attribute, entity);
		if (MaterialTags.LEGGINGS.isTagged(type)) return getAttributeValue(item, EquipmentSlot.LEGS, attribute, entity);
		if (MaterialTags.BOOTS.isTagged(type)) return getAttributeValue(item, EquipmentSlot.FEET, attribute, entity);
		return getAttributeValue(item, EquipmentSlot.HAND, attribute, entity);
	}

	/**
	 * Calculates attribute value of the item for provided slot.
	 *
	 * @param item item
	 * @param slot slot
	 * @param attribute attribute
	 * @return attribute value of the item
	 */
	public static double getAttributeValue(@Nullable ItemStack item, EquipmentSlot slot, Attribute attribute) {
		return getAttributeValue(item, slot, attribute, null);
	}

	/**
	 * Calculates attribute value of the item for provided slot.
	 *
	 * @param item item
	 * @param slot slot
	 * @param attribute attribute
	 * @param entity entity
	 * @return attribute value of the item
	 */
	public static double getAttributeValue(@Nullable ItemStack item, EquipmentSlot slot, Attribute attribute, @Nullable LivingEntity entity) { // TODO This is really dumb, replace with API
		if (ItemStack.isEmpty(item)) return 0D;

		// Get vanilla default value
		Collection<AttributeModifier> defaultAttributeModifiers = item.getType().getDefaultAttributeModifiers(slot).get(attribute);
		double defaultValue = defaultAttributeModifiers.isEmpty() ? 0 : calculateAttributeValue(0, defaultAttributeModifiers);

		// Get entity modifiers
		double entityValue;
		if (entity != null) {
			AttributeInstance attr = entity.getAttribute(attribute);
			entityValue = attr == null ? 0 : attr.getBaseValue();
		} else {
			if (attribute == Attribute.ATTACK_DAMAGE)
				entityValue = 2;
			else if (attribute == Attribute.ATTACK_SPEED)
				entityValue = 4;
			else
				entityValue = 0;
		}

		// Calculate enchantment bonuses
		double enchantBonus = 0;
//		for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) // TODO enchant bonuses :F
//			enchantBonus += entry.getKey().getDamageIncrease(entry.getValue(), EntityCategory.NONE);

		double combined = defaultValue + entityValue + enchantBonus;

		// Calculate extra value from attributes
		Collection<AttributeModifier> extraAttributeModifiers = item.getAttributeModifiers(slot).get(attribute);
		double extraValue = extraAttributeModifiers.isEmpty() ? 0 : calculateAttributeValue(combined, extraAttributeModifiers);

		// Return final value
		return MathUtil.round(combined + extraValue, 2);
	}

	private static double calculateAttributeValue(double baseValue, Collection<AttributeModifier> attributeModifiers) {
		double x = baseValue;
		for (AttributeModifier attributeModifier : attributeModifiers) {
			AttributeModifier.Operation operation = attributeModifier.getOperation();
			if (operation == AttributeModifier.Operation.ADD_NUMBER)
				x += attributeModifier.getAmount();
		}
		double y = x;
		for (AttributeModifier attributeModifier : attributeModifiers) {
			AttributeModifier.Operation operation = attributeModifier.getOperation();
			if (operation == AttributeModifier.Operation.ADD_SCALAR)
				y += x * attributeModifier.getAmount();
		}
		for (AttributeModifier attributeModifier : attributeModifiers) {
			AttributeModifier.Operation operation = attributeModifier.getOperation();
			if (operation == AttributeModifier.Operation.MULTIPLY_SCALAR_1)
				y *= 1D + attributeModifier.getAmount();
		}
		return y;
	}

	/**
	 * Adds enchantment glint to the item
	 *
	 * @param item item type
	 * @return item with enchantment glint
	 */
	public static ItemStack glint(Material item) {
		return glint(new ItemStack(item));
	}

	/**
	 * Adds enchantment glint to the item
	 *
	 * @param item item
	 * @return item with enchantment glint
	 */
	public static ItemStack glint(ItemStack item) {
		if (ItemStack.isEmpty(item)) return item;

		item = item.clone();
		item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
		return item;
	}

	/**
	 * Checks whether the block item should render as a block
	 *
	 * @param item item
	 * @return whether the block item should render as a block
	 */
	public static boolean shouldRenderAsBlock(ItemStack item) {
		Material type = item.getType();
		return type.isBlock() && !UtilizerTags.FLAT_BLOCK_RENDER.isTagged(type);
	}

}
