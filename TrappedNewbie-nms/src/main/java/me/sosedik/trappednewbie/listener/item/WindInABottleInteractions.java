package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.craft.CraftGlider;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.util.InventoryUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.AbstractWindCharge;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Wind in a bottle mechanics
 */
@NullMarked
public class WindInABottleInteractions implements Listener {

	private static final NamespacedKey BOTTLE_CONSUME_KEY = TrappedNewbie.trappedNewbieKey("wind_in_a_bottle_consume");

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		if (event.getRightClicked() instanceof AbstractWindCharge windCharge) {
			Player player = event.getPlayer();
			if (tryToFill(player, windCharge, EquipmentSlot.HAND)
				|| tryToFill(player, windCharge, EquipmentSlot.OFF_HAND))
				event.setCancelled(true);
			return;
		}

		Player player = event.getPlayer();
		if (tryToRelease(player, EquipmentSlot.HAND)
			|| tryToRelease(player, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToFill(Player player, AbstractWindCharge windCharge, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != Material.GLASS_BOTTLE) return false;

		addWind(player, windCharge, item, hand);
		return true;
	}

	private void addWind(Player player, Entity interacted, ItemStack item, EquipmentSlot hand) {
		interacted.remove();
		player.swingHand(hand);
		player.emitSound(Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1F, 1F);
		player.incrementStatistic(Statistic.USE_ITEM, Material.GLASS_BOTTLE);
		if (player instanceof CraftPlayer craftPlayer && interacted instanceof CraftEntity craftEntity)
			CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(craftPlayer.getHandle(), CraftItemStack.asNMSCopy(item), craftEntity.getHandle());
		item.subtract();
		InventoryUtil.replaceOrAdd(player, EquipmentSlot.HAND, ItemStack.of(TrappedNewbieItems.WIND_IN_A_BOTTLE));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;

		Player player = event.getPlayer();
		if (tryToRelease(player, EquipmentSlot.HAND)
			|| tryToRelease(player, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToRelease(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != TrappedNewbieItems.WIND_IN_A_BOTTLE) return false;

		player.swingHand(hand);

		if (player.getVehicle() instanceof CraftGlider craftGlider) {
			Vec3 vector = Vec3.directionFromRotation(craftGlider.getPitch() - 90, craftGlider.getYaw())
				.multiply(1, 0.6, 1)
				.normalize()
				.scale(0.8);
			craftGlider.getHandle().push(vector);
		} else if (player instanceof CraftPlayer craftPlayer) {
			Vec3 vector = craftPlayer.getHandle().getLookAngle()
				.multiply(1, 0.5, 1)
				.normalize()
				.reverse()
				.scale(0.8);
			craftPlayer.getHandle().push(vector);
			craftPlayer.getHandle().hurtMarked = true;
			craftPlayer.getHandle().checkFallDistanceAccumulation();
		}

		player.setCooldown(item, 20);

		player.getWorld().spawnParticle(Particle.GUST_EMITTER_SMALL, player.getLocation(), 10, 0.1, 0.1, 0.1, 0);
		player.emitSound(Sound.ENTITY_WIND_CHARGE_WIND_BURST, 0.5F, 0F);

		if (!player.getGameMode().isInvulnerable()) {
			item.subtract();

			var event = new RemainingItemEvent(null, player, null, BOTTLE_CONSUME_KEY, ItemStack.of(Material.GLASS_BOTTLE), 1);
			event.callEvent();
			ItemStack result = event.getResult();
			if (!ItemStack.isEmpty(result))
				InventoryUtil.replaceOrAdd(player, result);
		}

		return true;
	}

}
