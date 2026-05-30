package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Fire aspect can be used as flint and steel
 */
@NullMarked
public class FireAspectIsFlintAndSteel implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (player.hasCooldown(Material.FLINT_AND_STEEL)) return;
		if (UtilizerTags.FLINT_AND_STEEL.isTagged(player.getInventory().getItem(EquipmentSlot.HAND).getType())) return;
		if (UtilizerTags.FLINT_AND_STEEL.isTagged(player.getInventory().getItem(EquipmentSlot.OFF_HAND).getType())) return;

		Vector position = event.getClickedPosition();
		Entity entity = event.getRightClicked();
		if (interactEntity(player, entity, position, EquipmentSlot.HAND)
			|| interactEntity(player, entity, position, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
		}
	}

	private boolean interactEntity(Player player, Entity entity, Vector position, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (ItemStack.isEmpty(item)) return false;
		if (!ItemUtil.hasFireAspect(item)) return false;
		return mimicFlintAndSteel(player, entity, position, hand);
	}

	public static boolean mimicFlintAndSteel(Player player, Entity entity, Vector position, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		ItemStack flintAndSteel = ItemStack.of(Material.FLINT_AND_STEEL);
		player.getInventory().setItem(hand, flintAndSteel);
		var interactEvent = new PlayerInteractAtEntityEvent(player, entity, position, hand);
		interactEvent.callEvent();

		flintAndSteel = player.getInventory().getItem(hand);
		int damage = DurabilityUtil.getDamage(flintAndSteel);
		if (damage < 0) damage = 1;

		item.damage(damage, player);
		player.getInventory().setItem(hand, item);

		if (damage != 0 || interactEvent.isCancelled()) {
			player.swingHand(hand);
			if (ItemUtil.hasFireAspectStored(item))
				player.setFireTicks(player.getFireTicks() + 60);
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

		Vector interactPos = event.getClickedPosition();
		Action action = event.getAction();
		BlockFace blockFace = event.getBlockFace();
		if (useItem(player, action, block, blockFace, interactPos, EquipmentSlot.HAND)
			|| useItem(player, action, block, blockFace, interactPos, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean useItem(Player player, Action action, Block block, BlockFace blockFace, @Nullable Vector interactPos, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (ItemStack.isEmpty(item)) return false;
		if (!ItemUtil.hasFireAspect(item)) return false;
		if (player.hasCooldown(item)) return false;
		return mimicFlintAndSteel(player, null, interactPos, action, block, blockFace, hand);
	}

	public static boolean mimicFlintAndSteelRightClick(LivingEntity livingEntity, EquipmentSlot hand) {
		RayTraceResult rayTraceResult = livingEntity.rayTraceEntities(EntityUtil.getEntityReachBlocks(livingEntity, hand));
		if (rayTraceResult != null) {
			Entity hitEntity = rayTraceResult.getHitEntity();
			if (hitEntity != null)
				return mimicFlintAndSteel(livingEntity, hitEntity, rayTraceResult.getHitPosition(), null, null, null, hand);
		}

		rayTraceResult = livingEntity.rayTraceBlocks(EntityUtil.getEntityReach(livingEntity, hand) - 1D, FluidCollisionMode.ALWAYS);
		if (rayTraceResult == null) return false;

		Block block = rayTraceResult.getHitBlock();
		if (block == null) return false;

		BlockFace blockFace = rayTraceResult.getHitBlockFace();
		if (blockFace == null) return false;

		return mimicFlintAndSteel(livingEntity, null, rayTraceResult.getHitPosition(), Action.RIGHT_CLICK_BLOCK, block, blockFace, hand);
	}

	public static boolean mimicFlintAndSteel(LivingEntity livingEntity, @Nullable Entity targetEntity, @Nullable Vector interactPos, @Nullable Action action, @Nullable Block block, @Nullable BlockFace blockFace, EquipmentSlot hand) {
		if (livingEntity.getEquipment() == null) return false;

		ItemStack item = livingEntity.getEquipment().getItem(hand);
		var flintAndSteel = ItemStack.of(Material.FLINT_AND_STEEL);
		livingEntity.getEquipment().setItem(hand, flintAndSteel);
		boolean actionApplied = false;
		if (livingEntity instanceof Player player) {
			if (action != null && block != null && blockFace != null) {
				var interactEvent1 = new PlayerInteractEvent(player, action, flintAndSteel, block, blockFace, hand, interactPos);
				interactEvent1.callEvent();
				var interactEvent2 = new PlayerInteractEvent(player, action, player.getInventory().getItem(hand.getOppositeHand()), block, blockFace, hand.getOppositeHand(), interactPos);
				interactEvent2.callEvent();
				actionApplied = interactEvent1.useItemInHand() == Event.Result.DENY || interactEvent2.useItemInHand() == Event.Result.DENY;
			}

			if (targetEntity != null && interactPos != null) {
				var interactEntityEvent1 = new PlayerInteractAtEntityEvent(player, targetEntity, interactPos, hand);
				interactEntityEvent1.callEvent();
				var interactEntityEvent2 = new PlayerInteractAtEntityEvent(player, targetEntity, interactPos, hand.getOppositeHand());
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
			if (ItemUtil.hasFireAspectStored(item))
				livingEntity.setFireTicks(livingEntity.getFireTicks() + 60);
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

		if (ItemUtil.hasFireAspectStored(item))
			livingEntity.setFireTicks(livingEntity.getFireTicks() + 60);

		return true;
	}

}
