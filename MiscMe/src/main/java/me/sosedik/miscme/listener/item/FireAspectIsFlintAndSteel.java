package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
		return mimicFlintAndSteel(player, entity, hand);
	}

	public static boolean mimicFlintAndSteel(Player player, Entity entity, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		ItemStack flintAndSteel = ItemStack.of(Material.FLINT_AND_STEEL);
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
		return mimicFlintAndSteel(player, null, action, block, blockFace, hand);
	}

	public static boolean mimicFlintAndSteelRightClick(LivingEntity livingEntity, EquipmentSlot hand) {
		Entity targetEntity = livingEntity.getTargetEntity(EntityUtil.PLAYER_REACH);
		if (targetEntity != null) return mimicFlintAndSteel(livingEntity, targetEntity, null, null, null, hand);

		RayTraceResult rayTraceResult = livingEntity.rayTraceBlocks(EntityUtil.PLAYER_REACH - 1D, FluidCollisionMode.ALWAYS);
		if (rayTraceResult == null) return false;

		Block block = rayTraceResult.getHitBlock();
		if (block == null) return false;

		BlockFace blockFace = rayTraceResult.getHitBlockFace();
		if (blockFace == null) return false;

		return mimicFlintAndSteel(livingEntity, null, Action.RIGHT_CLICK_BLOCK, block, blockFace, hand);
	}

	public static boolean mimicFlintAndSteel(LivingEntity livingEntity, @Nullable Entity targetEntity, @Nullable Action action, @Nullable Block block, @Nullable BlockFace blockFace, EquipmentSlot hand) {
		if (livingEntity.getEquipment() == null) return false;

		ItemStack item = livingEntity.getEquipment().getItem(hand);
		var flintAndSteel = ItemStack.of(Material.FLINT_AND_STEEL);
		livingEntity.getEquipment().setItem(hand, flintAndSteel);
		boolean actionApplied = false;
		if (livingEntity instanceof Player player) {
			if (action != null && block != null && blockFace != null) {
				var interactEvent1 = new PlayerInteractEvent(player, action, flintAndSteel, block, blockFace, hand, null);
				interactEvent1.callEvent();
				var interactEvent2 = new PlayerInteractEvent(player, action, player.getInventory().getItem(hand.getOppositeHand()), block, blockFace, hand.getOppositeHand(), null);
				interactEvent2.callEvent();
				actionApplied = interactEvent1.useItemInHand() == Event.Result.DENY || interactEvent2.useItemInHand() == Event.Result.DENY;
			}

			if (targetEntity != null) {
				var interactEntityEvent1 = new PlayerInteractEntityEvent(player, targetEntity, hand);
				interactEntityEvent1.callEvent();
				var interactEntityEvent2 = new PlayerInteractEntityEvent(player, targetEntity, hand.getOppositeHand());
				interactEntityEvent2.callEvent();
				if (!actionApplied) actionApplied = interactEntityEvent1.isCancelled() || interactEntityEvent2.isCancelled();
			}
		}
		flintAndSteel = livingEntity.getEquipment().getItem(hand);

		if (actionApplied) {
			int damage = DurabilityUtil.getDamage(flintAndSteel);
			if (damage < 0) damage = 1;
			item.damage(damage, livingEntity);
			livingEntity.getEquipment().setItem(hand, item);
			livingEntity.swingHand(hand);
			return true;
		}

		if (block != null && blockFace != null && !BurningProjectileCreatesFire.createFireOrIgnite(block, blockFace, livingEntity, BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)) {
			livingEntity.getEquipment().setItem(hand, item);
			return false;
		}

		if (block != null) {
			block.emitSound(Sound.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1F, (float) Math.random() * 0.4F + 0.8F);
			item.damage(1, livingEntity);
		}
		livingEntity.getEquipment().setItem(hand, item);
		livingEntity.swingHand(hand);

		return true;
	}

}
