package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Toggle player's gravity
 */
@NullMarked
@Permission("essence.command.gravity")
public class GravityCommand {

	@Command("gravity [player] [state]")
	public void onCommand(
		CommandSourceStack stack,
		@Nullable @Argument(value = "player") Player player,
		@Nullable @Argument(value = "state") Boolean state,
		@Flag(value = "silent") boolean silent
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		Essence.scheduler().sync(() -> {
			boolean gravity = state == null ? !target.hasGravity() : state;
			target.setGravity(gravity);
			TagResolver choice = Formatter.booleanChoice("choice", gravity);
			if (!silent) Messenger.messenger(target).sendMessage("command.gravity", choice);
			if (stack.getSender() != target)
				Messenger.messenger(stack.getSender()).sendMessage("command.gravity.other", choice, raw("player", target.displayName()));
		});
	}

}
