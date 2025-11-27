package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Teleport up X blocks
 */
@NullMarked
@Permission("essence.command.up")
public class UpCommand {

	@Command("up [amount] [player]")
	public void onCommand(
		CommandSourceStack stack,
		@Argument(value = "amount") @Default(value = "1") int amount,
		@Nullable @Argument(value = "player") Player player
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		Essence.scheduler().sync(() -> {
			World world = target.getWorld();
			Location loc = target.getLocation().toCenterLocation();
			int y = Math.clamp(amount + loc.getBlockY(), world.getMinHeight(), world.getMaxHeight());
			loc.setY(y);

			Block block = loc.getBlock().getRelative(BlockFace.DOWN);
			if (!block.isSolid()) {
				target.sendBlockChange(block.getLocation(), Material.GLASS.createBlockData());
			}

			LocationUtil.smartTeleport(target, loc, false);
		});
	}

}
