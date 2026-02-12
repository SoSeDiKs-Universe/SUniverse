package me.sosedik.trappednewbie.impl.task.tutorial;

import me.sosedik.requiem.api.event.player.PlayerStopGhostingEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.trappednewbie.api.task.Task;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FirstPossessionTask extends Task implements Listener {

	public FirstPossessionTask(String taskId, Player player) {
		super(taskId, player);
	}

	@Override
	public boolean canBeSkipped() {
		Player player = getPlayer();
		return !GhostyPlayer.isGhost(player) || TrappedNewbieTasks.TUTORIAL_TREE[TrappedNewbieTasks.TUTORIAL_TREE.length - 1].constructTask(player).canBeSkipped();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onGhosting(PlayerStopGhostingEvent event) {
		if (event.getPlayer() != getPlayer()) return;

		finish();
	}

}
