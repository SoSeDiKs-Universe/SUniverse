package me.sosedik.utilizer.listener.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hides unwanted command
 */
@NullMarked
public class CommandsHider implements Listener {

	private static final List<String> DISABLED_COMMANDS = new ArrayList<>();
	private static final List<String> HIDDEN_COMMANDS = new ArrayList<>();

	public CommandsHider(Plugin plugin) {
		loadData(plugin);
	}

	@EventHandler
	public void onCommandSend(PlayerCommandSendEvent event) {
		Player player = event.getPlayer();
		Iterator<String> iterator = event.getCommands().iterator();
		while (iterator.hasNext()) {
			String com = iterator.next();
			var command = Bukkit.getCommandMap().getCommand(com);
			if (command == null) continue;

			if (DISABLED_COMMANDS.contains(com) || (HIDDEN_COMMANDS.contains(com) && !player.isOp())) {
				iterator.remove();
			}
		}
	}

	private static void loadData(Plugin plugin) {
		DISABLED_COMMANDS.clear();
		HIDDEN_COMMANDS.clear();

		// Overwritten commands
		DISABLED_COMMANDS.add("minecraft:help");
		DISABLED_COMMANDS.add("bukkit:help");

		//Bukkit.getCommandMap().getKnownCommands().keySet().forEach(key -> Logger.info("Command: " + key));

		var disabledCommands = plugin.getConfig().getConfigurationSection("disabled-commands");
		assert disabledCommands != null;
		disabledCommands.getKeys(false).forEach(k -> plugin.getConfig().getStringList("disabled-commands." + k).forEach(c -> {
			DISABLED_COMMANDS.add(k + ":" + c);
			DISABLED_COMMANDS.add(c);
		}));

		var hiddenCommands = plugin.getConfig().getConfigurationSection("hidden-commands");
		assert hiddenCommands != null;
		hiddenCommands.getKeys(false).forEach(k -> plugin.getConfig().getStringList("hidden-commands." + k).forEach(c -> {
			HIDDEN_COMMANDS.add(k + ":" + c);
			HIDDEN_COMMANDS.add(c);
		}));

		var deletedCommands = plugin.getConfig().getConfigurationSection("deleted-commands");
		assert deletedCommands != null;
		deletedCommands.getValues(false).keySet().forEach(k -> plugin.getConfig().getStringList("deleted-commands." + k).forEach(c -> {
			Bukkit.getCommandMap().getKnownCommands().remove(k + ":" + c);
			Bukkit.getCommandMap().getKnownCommands().remove(c);
		}));
	}

}
