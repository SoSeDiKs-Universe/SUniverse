package me.sosedik.requiem.task;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

/**
 * Possesed mimic owner's poses
 */
// MCCheck: 1.21.11, new mobs with poses
@NullMarked
public class PoseMimicingTask extends BukkitRunnable {

	private final Player player;
	private final LivingEntity entity;

	public PoseMimicingTask(Player player, LivingEntity entity) {
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
