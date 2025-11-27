package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import me.sosedik.utilizer.impl.item.modifier.CustomTotemOfUndyingModifier;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Time machine clock acts as totem of undying
 */
@NullMarked
public class TimeMachineClockIsTotemOfUndying implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getPlayer();
		ItemStack item = InventoryUtil.findItem(player, itemStack -> isUsableTimeMachine(player, itemStack));
		if (item == null) return;

		event.setCancelled(true);
		player.setCooldown(item, 60 * 60 * 20);
		CustomTotemOfUndyingModifier.playTotemEffect(player, item);
	}

	private boolean isUsableTimeMachine(Player player, @Nullable ItemStack item) {
		return AdvancementTrophies.isUsableTrophy(TrappedNewbieAdvancements.WALK_1000KM, player, item) && !player.hasCooldown(item);
	}

}
