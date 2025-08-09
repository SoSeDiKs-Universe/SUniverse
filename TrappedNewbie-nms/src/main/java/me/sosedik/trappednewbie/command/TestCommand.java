package me.sosedik.trappednewbie.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;

/**
 * Just testing stuff
 */
@NullMarked
@Permission("trapped_newbie.commands.test")
public class TestCommand {

	@Command("test")
	public void onCommand(
		CommandSourceStack stack
	) {
		if (!(stack.getExecutor() instanceof Player player)) return;

		TrappedNewbie.scheduler().sync(() -> {
			// Stuff!
			player.getInventory().addItem(AdvancementTrophies.produceTrophy(TrappedNewbieAdvancements.SPRINT_250KM, player));
		});
	}

}
