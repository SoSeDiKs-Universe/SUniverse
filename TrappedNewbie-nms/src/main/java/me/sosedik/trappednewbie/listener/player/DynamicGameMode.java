package me.sosedik.trappednewbie.listener.player;

import me.sosedik.trappednewbie.misc.GameModeSwitcherTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Because your survival is quite an adventure!
 */
@NullMarked
public class DynamicGameMode implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		new GameModeSwitcherTask(event.getPlayer());
	}

}
