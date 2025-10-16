package me.sosedik.trappednewbie.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.TrappedNewbie;
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
		if (!(stack.getExecutor() instanceof Player player)) {
			// Console (probably), yay!
			return;
		}

		TrappedNewbie.scheduler().sync(() -> {
			// Stuff!
//			player.getWorld().spawn(player.getLocation(), Husk.class, e1 -> {
//				e1.setCanPickupItems(true);
//				e1.getEquipment().setHelmet(ItemStack.of(Material.JACK_O_LANTERN));
//			});
//			player.getInventory().addItem(AdvancementTrophies.produceTrophy(TrappedNewbieAdvancements.KILL_ALL_MOBS_WEARING_A_JACK_O_LANTERN, player));
//			player.getInventory().addItem(AdvancementTrophies.produceTrophy(TrappedNewbieAdvancements.USE_100_STACKS_OF_ENDER_PEARLS, player));
		});
	}

}
