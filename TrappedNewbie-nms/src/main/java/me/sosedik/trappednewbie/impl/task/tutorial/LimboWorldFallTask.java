package me.sosedik.trappednewbie.impl.task.tutorial;

import me.sosedik.trappednewbie.api.task.ObtainAdvancementTask;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LimboWorldFallTask extends ObtainAdvancementTask {

	public LimboWorldFallTask(String taskId, Player player) {
		super(taskId, TrappedNewbieAdvancements.BRAVE_NEW_WORLD, player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAttack(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld() != Utilizer.limboWorld()) return;
		if (event.getTo().getWorld() == Utilizer.limboWorld()) return;

		TrappedNewbieAdvancements.BRAVE_NEW_WORLD.awardAllCriteria(event.getPlayer());
		finish();
	}

}
