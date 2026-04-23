package me.sosedik.requiem.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.CommandUtils;
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
		Player target = CommandUtils.getTargetPlayer(stack, player);
		if (target == null) return;

		Requiem.scheduler().sync(() -> {
			boolean stateChanged = false;
			if (PossessingPlayer.isPossessing(target)) {
				stateChanged = true;
				PossessingPlayer.stopPossessing(target, false);
			} else if (GhostyPlayer.isGhost(target)) {
				stateChanged = true;
				GhostyPlayer.clearGhost(target);
			}

			if (!stateChanged) {
				if (!silent)
					Messenger.messenger(stack.getSender()).sendMessage("command.revive.target_alive", raw("player", target.displayName()));
				return;
			}

			if (!silent)
				Messenger.messenger(target).sendMessage("command.revive");

			if (CommandUtils.isTargetingOther(stack, target))
				Messenger.messenger(stack.getSender()).sendMessage("command.revive.other", raw("player", target.displayName()));
		});
	}

}
