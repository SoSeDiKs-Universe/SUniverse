package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import me.sosedik.utilizer.listener.misc.SneakEmptyHandRightClickCatcher;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

/**
 * Players can drink rain, rain is good
 */
@NullMarked
public class RainIsDrinkable implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onRainDrink(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Interaction interaction)) return;

		Player player = event.getPlayer();
		if (!canDrink(player)) return;
		if (SneakEmptyHandRightClickCatcher.getInteraction(player) != interaction) return;

		event.setCancelled(true);
		DrinkableWater.triggerCooldown(player, 30);
		ThirstyPlayer.of(player).addThirst(2);
		player.swingMainHand();
		player.emitSound(Sound.ENTITY_GENERIC_DRINK, 0.5F, 1F);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSneak(PlayerToggleSneakEvent event) {
		if (!event.isSneaking()) return;

		Player player = event.getPlayer();
		TrappedNewbie.scheduler().sync(task -> {
			if (!player.isSneaking()) return true;
			if (!player.isOnline()) return true;

			Interaction interaction = SneakEmptyHandRightClickCatcher.getInteraction(player);
			if (interaction == null) return true;

			SneakEmptyHandRightClickCatcher.markInteractor(interaction, "drinkable_rain", !canDrink(player));

			return false;
		}, 1L, 1L);
	}

	private boolean canDrink(Player player) {
		if (!player.isSneaking()) return false;
		if (DrinkableWater.hasCooldown(player)) return false;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return false;

		Location loc = player.getLocation();
		if (loc.getPitch() > -30) return false;
		if (!LocationUtil.isRainingAt(loc, true)) return false;

		var thirstyPlayer = ThirstyPlayer.of(player);
		if (thirstyPlayer.hasFullThirst()) return false;

		return player.rayTraceBlocks(EntityUtil.PLAYER_REACH) == null;
	}

}
