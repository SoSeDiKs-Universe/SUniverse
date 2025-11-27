package me.sosedik.trappednewbie.api.item;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a second (visual) armor slots
 */
@NullMarked
public class VisualArmor {

	private final Player player;
	private @Nullable ItemStack helmet;
	private @Nullable ItemStack chestplate;
	private @Nullable ItemStack leggings;
	private @Nullable ItemStack boots;
	private @Nullable ItemStack gloves;
	private boolean armorPreview;

	public VisualArmor(
		Player player,
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

	public boolean canUseVisualArmor() {
		return !GhostyPlayer.isGhost(this.player) && !PossessingPlayer.isPossessingSoft(this.player);
	}

	public boolean hasItem(EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> hasHelmet();
			case CHEST -> hasChestplate();
			case LEGS -> hasLeggings();
			case FEET -> hasBoots();
			case OFF_HAND -> hasGloves();
			default -> false;
		};
	}

	public ItemStack getItem(EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> getHelmet();
			case CHEST -> getChestplate();
			case LEGS -> getLeggings();
			case FEET -> getBoots();
			case OFF_HAND -> getGloves();
			default -> ItemStack.empty();
		};
	}

	public void setItem(EquipmentSlot slot, @Nullable ItemStack item) {
		switch (slot) {
			case HEAD -> setHelmet(item);
			case CHEST -> setChestplate(item);
			case LEGS -> setLeggings(item);
			case FEET -> setBoots(item);
			case OFF_HAND -> setGloves(item);
		}
	}

	public boolean hasHelmet() {
		return this.helmet != null;
	}

	public ItemStack getHelmet() {
		return this.helmet == null ? ItemStack.empty() : this.helmet;
	}

	public void setHelmet(@Nullable ItemStack helmet) {
		this.helmet = ItemStack.isEmpty(helmet) ? null : helmet;
		refreshVisuals(EquipmentSlot.HEAD);
	}

	public boolean hasChestplate() {
		return this.chestplate != null;
	}

	public ItemStack getChestplate() {
		return this.chestplate == null ? ItemStack.empty() : this.chestplate;
	}

	public void setChestplate(@Nullable ItemStack chestplate) {
		this.chestplate = ItemStack.isEmpty(chestplate) ? null : chestplate;
		refreshVisuals(EquipmentSlot.CHEST);
	}

	public boolean hasLeggings() {
		return this.leggings != null;
	}

	public ItemStack getLeggings() {
		return this.leggings == null ? ItemStack.empty() : this.leggings;
	}

	public void setLeggings(@Nullable ItemStack leggings) {
		this.leggings = ItemStack.isEmpty(leggings) ? null : leggings;
		refreshVisuals(EquipmentSlot.LEGS);
	}

	public boolean hasBoots() {
		return this.boots != null;
	}

	public ItemStack getBoots() {
		return this.boots == null ? ItemStack.empty() : this.boots;
	}

	public void setBoots(@Nullable ItemStack boots) {
		this.boots = ItemStack.isEmpty(boots) ? null : boots;
		refreshVisuals(EquipmentSlot.FEET);
	}

	public boolean hasGloves() {
		return this.gloves != null;
	}

	public boolean hasNonBrokenGloves() {
		return hasGloves() && this.gloves.getType() != TrappedNewbieItems.SCRAP;
	}

	public ItemStack getGloves() {
		return this.gloves == null ? ItemStack.empty() : this.gloves;
	}

	public void setGloves(@Nullable ItemStack gloves) {
		this.gloves = ItemStack.isEmpty(gloves) ? null : gloves;
	}

	public ItemStack[] getArmorContents() {
		return new ItemStack[] {getHelmet(), getChestplate(), getLeggings(), getBoots()};
	}

	public ItemStack[] getAllContents() {
		return new ItemStack[] {getHelmet(), getChestplate(), getLeggings(), getBoots(), getGloves()};
	}

	private void refreshVisuals(EquipmentSlot slot) {
		if (!slot.isArmor()) return;

		int invSlot = InventoryUtil.getSlot(this.player, slot);
		if (invSlot != -1) this.player.sendItem(invSlot, getItem(slot));
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			if (onlinePlayer != this.player && onlinePlayer.getWorld() == this.player.getWorld())
				onlinePlayer.sendEquipmentChange(this.player, slot, getItem(slot));
		});
	}

	public boolean isArmorPreview() {
		return this.armorPreview;
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
	public static VisualArmor of(Player player) {
		return VisualArmorLayer.getVisualArmor(player);
	}

}
