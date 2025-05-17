package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.DurabilityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Fire aspect can be used as flint and steel
 */
@NullMarked
public class FireAspectIsFlintAndSteel implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (player.hasCooldown(Material.FLINT_AND_STEEL)) return;
		if (UtilizerTags.FLINT_AND_STEEL.isTagged(player.getInventory().getItem(EquipmentSlot.HAND).getType())) return;
		if (UtilizerTags.FLINT_AND_STEEL.isTagged(player.getInventory().getItem(EquipmentSlot.OFF_HAND).getType())) return;

		Entity entity = event.getRightClicked();
		if (interactEntity(player, entity, EquipmentSlot.HAND)
				|| interactEntity(player, entity, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
		}
	}

	private boolean interactEntity(Player player, Entity entity, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (ItemStack.isEmpty(item)) return false;
		if (!item.hasEnchant(Enchantment.FIRE_ASPECT)) return false;
		return mimikFlintAndSteel(player, entity, hand);
	}

	public static boolean mimikFlintAndSteel(Player player, Entity entity, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);
		player.getInventory().setItem(hand, flintAndSteel);
		var interactEvent = new PlayerInteractEntityEvent(player, entity, hand);
		interactEvent.callEvent();

		flintAndSteel = player.getInventory().getItem(hand);
		int damage = DurabilityUtil.getDamage(flintAndSteel);
		if (damage < 0) damage = 1;

		item.damage(damage, player);
		player.getInventory().setItem(hand, item);

		if (damage != 0 || interactEvent.isCancelled()) {
			player.swingHand(hand);
			return true;
		}

		return false;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Player player = event.getPlayer();
		if (player.hasCooldown(Material.FLINT_AND_STEEL)) return;
		if (UtilizerTags.FLINT_AND_STEEL.isTagged(player.getInventory().getItem(EquipmentSlot.HAND).getType())) return;
		if (UtilizerTags.FLINT_AND_STEEL.isTagged(player.getInventory().getItem(EquipmentSlot.OFF_HAND).getType())) return;
		if (block.getType().isInteractable() && !(block.getBlockData() instanceof Lightable) && !player.isSneaking())
			return;

		Action action = event.getAction();
		BlockFace blockFace = event.getBlockFace();
		if (useItem(player, action, block, blockFace, EquipmentSlot.HAND)
				|| useItem(player, action, block, blockFace, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean useItem(Player player, Action action, Block block, BlockFace blockFace, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (ItemStack.isEmpty(item)) return false;
		if (!item.hasEnchant(Enchantment.FIRE_ASPECT)) return false;
		if (player.hasCooldown(item)) return false;
		return mimikFlintAndSteel(player, action, block, blockFace, hand);
	}

	public static boolean mimikFlintAndSteel(Player player, Action action, Block block, BlockFace blockFace, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);
		player.getInventory().setItem(hand, flintAndSteel);
		var interactEvent = new PlayerInteractEvent(player, action, flintAndSteel, block, blockFace, hand, null);
		interactEvent.callEvent();
		flintAndSteel = player.getInventory().getItem(hand);

		boolean actionApplied = interactEvent.useItemInHand() == Event.Result.DENY;
		if (actionApplied) {
			int damage = DurabilityUtil.getDamage(flintAndSteel);
			if (damage < 0) damage = 1;
			item.damage(damage, player);
			player.getInventory().setItem(hand, item);
			player.swingHand(hand);
			return true;
		}

		if (!BurningProjectileCreatesFire.createFireOrIgnite(block, blockFace, player, BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)) {
			player.getInventory().setItem(hand, item);
			return false;
		}

		block.emitSound(Sound.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1F, 1.2F);
		item.damage(1, player);
		player.getInventory().setItem(hand, item);
		player.swingHand(hand);

		return true;
	}

}
