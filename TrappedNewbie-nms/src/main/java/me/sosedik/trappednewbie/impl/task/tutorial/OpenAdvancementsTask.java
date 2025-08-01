package me.sosedik.trappednewbie.impl.task.tutorial;

import me.sosedik.packetadvancements.api.event.AsyncPlayerOpenAdvancementTabEvent;
import me.sosedik.trappednewbie.api.task.ObtainAdvancementTask;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OpenAdvancementsTask extends ObtainAdvancementTask implements Listener {

	public OpenAdvancementsTask(String taskId, Player player) {
		super(taskId, TrappedNewbieAdvancements.REQUIEM_ROOT, "open", player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAdvScreenOpen(AsyncPlayerOpenAdvancementTabEvent event) {
		if (event.getPlayer() != getPlayer()) return;

		TrappedNewbieAdvancements.REQUIEM_ROOT.awardCriteria(event.getPlayer(), "open");
		finish();
	}

}
