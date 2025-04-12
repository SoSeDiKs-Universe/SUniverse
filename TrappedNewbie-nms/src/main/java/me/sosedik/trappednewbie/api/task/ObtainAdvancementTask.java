package me.sosedik.trappednewbie.api.task;

import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.event.PlayerMadeAdvancementEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class ObtainAdvancementTask extends Task implements Listener {

	private final IAdvancement advancement;

	protected ObtainAdvancementTask(String taskId, IAdvancement advancement, Player player) {
		super(taskId, player);
		this.advancement = advancement;
	}

	@Override
	public boolean canBeSkipped() {
		return this.advancement.isDone(getPlayer());
	}

	@Override
	public void onStart() {
		if (this.advancement.isDone(getPlayer())) {
			finish();
			return;
		}
		super.onStart();
	}

	@EventHandler
	public void onAdvancementGet(PlayerMadeAdvancementEvent event) {
		if (getPlayer() != event.getPlayer()) return;
		if (this.advancement == event.getAdvancement())
			finish();
	}

}
