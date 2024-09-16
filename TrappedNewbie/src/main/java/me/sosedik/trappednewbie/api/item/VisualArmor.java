package me.sosedik.trappednewbie.api.item;

import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.util.Durability;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a second (visual) armor slots
 */
public class VisualArmor {

	private final Player player;
	private @Nullable ItemStack helmet;
	private @Nullable ItemStack chestplate;
	private @Nullable ItemStack leggings;
	private @Nullable ItemStack boots;
	private @Nullable ItemStack gloves;
	private boolean armorPreview;

	public VisualArmor(
			@NotNull Player player,
			@Nullable ItemStack helmet,
			@Nullable ItemStack chestplate,
			@Nullable ItemStack leggings,
			@Nullable ItemStack boots,
			@Nullable ItemStack gloves
	) {
		this.player = player;
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.gloves = gloves;
	}

	public boolean hasItem(@NotNull EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> hasHelmet();
			case CHEST -> hasChestplate();
			case LEGS -> hasLeggings();
			case FEET -> hasBoots();
			case OFF_HAND -> hasGloves();
			default -> false;
		};
	}

	public @NotNull ItemStack getItem(@NotNull EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> getHelmet();
			case CHEST -> getChestplate();
			case LEGS -> getLeggings();
			case FEET -> getBoots();
			case OFF_HAND -> getGloves();
			default -> new ItemStack(Material.AIR);
		};
	}

	public void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
		switch (slot) {
			case HEAD -> setHelmet(item);
			case CHEST -> setChestplate(item);
			case LEGS -> setLeggings(item);
			case FEET -> setBoots(item);
			case OFF_HAND -> setGloves(item);
		}
	}

	public boolean hasHelmet() {
		return helmet != null;
	}

	public @NotNull ItemStack getHelmet() {
		return helmet == null ? new ItemStack(Material.AIR) : helmet;
	}

	public void setHelmet(@Nullable ItemStack helmet) {
		this.helmet = ItemStack.isEmpty(helmet) ? null : helmet;
		refreshVisuals(EquipmentSlot.HEAD);
	}

	public boolean hasChestplate() {
		return chestplate != null;
	}

	public @NotNull ItemStack getChestplate() {
		return chestplate == null ? new ItemStack(Material.AIR) : chestplate;
	}

	public void setChestplate(@Nullable ItemStack chestplate) {
		this.chestplate = ItemStack.isEmpty(chestplate) ? null : chestplate;
		refreshVisuals(EquipmentSlot.CHEST);
	}

	public boolean hasLeggings() {
		return leggings != null;
	}

	public @NotNull ItemStack getLeggings() {
		return leggings == null ? new ItemStack(Material.AIR) : leggings;
	}

	public void setLeggings(@Nullable ItemStack leggings) {
		this.leggings = ItemStack.isEmpty(leggings) ? null : leggings;
		refreshVisuals(EquipmentSlot.LEGS);
	}

	public boolean hasBoots() {
		return boots != null;
	}

	public @NotNull ItemStack getBoots() {
		return boots == null ? new ItemStack(Material.AIR) : boots;
	}

	public void setBoots(@Nullable ItemStack boots) {
		this.boots = ItemStack.isEmpty(boots) ? null : boots;
		refreshVisuals(EquipmentSlot.FEET);
	}

	public boolean hasGloves() {
		return gloves != null;
	}

	public boolean hasNonBrokenGloves() {
		return hasGloves() && !Durability.isBroken(gloves);
	}

	public @NotNull ItemStack getGloves() {
		return gloves == null ? new ItemStack(Material.AIR) : gloves;
	}

	public void setGloves(@Nullable ItemStack gloves) {
		this.gloves = ItemStack.isEmpty(gloves) ? null : gloves;
	}

	private void refreshVisuals(@NotNull EquipmentSlot slot) {
		if (!slot.isArmor()) return;

		this.player.sendItem(InventoryUtil.getSlot(this.player, slot), getItem(slot));
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			if (onlinePlayer != player && onlinePlayer.getWorld() == player.getWorld())
				onlinePlayer.sendEquipmentChange(this.player, slot, getItem(slot));
		});
	}

	public boolean isArmorPreview() {
		return armorPreview;
	}

	public void toggleArmorPreview() {
		this.armorPreview = !this.armorPreview;
	}

	public void setArmorPreview(boolean armorPreview) {
		this.armorPreview = armorPreview;
	}

	/**
	 * Gets the player's visual armor
	 *
	 * @param player player
	 * @return visual armor
	 */
	public static @NotNull VisualArmor of(@NotNull Player player) {
		return VisualArmorLayer.getVisualArmor(player);
	}

}
