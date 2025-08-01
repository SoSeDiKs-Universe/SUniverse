package me.sosedik.trappednewbie.api.task;

import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.event.PlayerMadeAdvancementEvent;
import me.sosedik.packetadvancements.api.event.TeamProgressionChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ObtainAdvancementTask extends Task implements Listener {

	private final IAdvancement advancement;
	private final @Nullable String criterion;

	public ObtainAdvancementTask(String taskId, IAdvancement advancement, Player player) {
		this(taskId, advancement, null, player);
	}

	public ObtainAdvancementTask(String taskId, IAdvancement advancement, @Nullable String criterion, Player player) {
		super(taskId, player);
		this.advancement = advancement;
		this.criterion = criterion;
	}

	@Override
	public boolean canBeSkipped() {
		return this.criterion == null ? this.advancement.isDone(getPlayer()) : this.advancement.hasCriteria(getPlayer(), this.criterion);
	}

	@EventHandler
	public void onAdvancementGet(TeamProgressionChangeEvent event) {
		if (this.criterion == null) return;
		if (getPlayer() != event.getProgresser()) return;
		if (this.advancement != event.getAdvancement()) return;
		if (!event.getProgress().hasCriteria(this.criterion)) return;

		finish();
	}

	@EventHandler
	public void onAdvancementGet(PlayerMadeAdvancementEvent event) {
		if (getPlayer() != event.getPlayer()) return;
		if (this.advancement != event.getAdvancement()) return;

		finish();
	}

}
