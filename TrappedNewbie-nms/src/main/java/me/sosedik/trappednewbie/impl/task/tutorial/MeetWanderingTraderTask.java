package me.sosedik.trappednewbie.impl.task.tutorial;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.task.Task;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MeetWanderingTraderTask extends Task implements Listener {

	public MeetWanderingTraderTask(String taskId, Player player) {
		super(taskId, player);
	}

	@Override
	public boolean canBeSkipped() {
		return TrappedNewbieAdvancements.REQUIEM_ROOT.hasCriteria(getPlayer(), "interact");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof WanderingTrader)) return;

		Player player = event.getPlayer();
		if (player.getWorld() != TrappedNewbie.limboWorld()) return;
		if (TrappedNewbieAdvancements.REQUIEM_ROOT.hasCriteria(player, "interact")) return;

		TrappedNewbieAdvancements.REQUIEM_ROOT.awardCriteria(true, player, "interact");
		finish();
	}

}
