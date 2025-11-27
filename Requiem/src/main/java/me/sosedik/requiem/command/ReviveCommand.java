package me.sosedik.requiem.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Healing players
 */
@NullMarked
@Permission("requiem.command.revive")
public class ReviveCommand {

	@Command("revive [player]")
	public void onCommand(
		CommandSourceStack stack,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "silent") boolean silent
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		Requiem.scheduler().sync(() -> {
			boolean stateChanged = false;
			if (PossessingPlayer.isPossessing(target)) {
				stateChanged = true;
				PossessingPlayer.stopPossessing(target);
			} else if (GhostyPlayer.isGhost(target)) {
				stateChanged = true;
				GhostyPlayer.clearGhost(target);
			}

			if (!stateChanged) {
				if (silent) return;

				Messenger.messenger(stack.getSender()).sendMessage("command.revive.target_alive", raw("player", target.displayName()));
				return;
			}

			if (!silent) Messenger.messenger(target).sendMessage("command.revive");
			if (stack.getSender() != target)
				Messenger.messenger(stack.getSender()).sendMessage("command.revive.other", raw("player", target.displayName()));
		});
	}

}
