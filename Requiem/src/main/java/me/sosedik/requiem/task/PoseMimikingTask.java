package me.sosedik.requiem.task;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Possesed mimik owner's poses
 */
public class PoseMimikingTask extends BukkitRunnable {

	private final Player player;
	private final LivingEntity entity;

	public PoseMimikingTask(@NotNull Player player, @NotNull LivingEntity entity) {
		this.player = player;
		this.entity = entity;
		Requiem.scheduler().sync(this, 1L, 1L);
	}

	@Override
	public void run() {
		if (!player.isOnline() || PossessingPlayer.getPossessed(player) != entity) {
			cancel();
			return;
		}

		if (entity instanceof Fox fox) {
			fox.setCrouching(player.isSneaking());
			fox.setSleeping(player.isSleeping());
		} else if (entity instanceof Panda panda) {
			panda.setSitting(player.isSneaking());
		} else {
			entity.setSneaking(player.isSneaking());
		}
	}

	@Override
	public void cancel() {
		super.cancel();
	}

}
