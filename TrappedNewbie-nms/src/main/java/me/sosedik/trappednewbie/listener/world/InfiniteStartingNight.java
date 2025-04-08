package me.sosedik.trappednewbie.listener.world;

import me.sosedik.miscme.task.CustomDayCycleTask;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

/**
 * The very first night in resource world should be infinite
 */
@NullMarked
public class InfiniteStartingNight implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (shouldHaveDayCycle(player)) return;

		World world = player.getWorld();
		if (!isNormalResourceWorld(world)) return;

		CustomDayCycleTask.stopDayCycle(world);
		world.setFullTime(18_000);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldJoin(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (shouldHaveDayCycle(player)) return;

		World world = player.getWorld();
		if (!isNormalResourceWorld(world)) return;

		Bukkit.unloadWorld(world, true);
	}

	private boolean isNormalResourceWorld(World world) {
		if (world.getEnvironment() != World.Environment.NORMAL) return false;

		NamespacedKey worldKey = world.getKey();
		if (!worldKey.getNamespace().equals(TrappedNewbie.instance().getName().toLowerCase(Locale.US))) return false;
		return worldKey.getKey().startsWith("worlds-resources/");
	}

	private boolean shouldHaveDayCycle(Player player) { // TODO actually, should be world's owner?
		return false; // TODO
	}

}
