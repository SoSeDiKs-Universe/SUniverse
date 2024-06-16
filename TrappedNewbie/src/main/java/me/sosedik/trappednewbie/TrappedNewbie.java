package me.sosedik.trappednewbie;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.api.command.parser.PlayerWorldParser;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.trappednewbie.listener.player.FirstWorldJoin;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.util.EventUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.jetbrains.annotations.NotNull;

import static me.sosedik.trappednewbie.api.command.parser.PlayerWorldParser.playerWorldParser;

public final class TrappedNewbie extends JavaPlugin {

	private static TrappedNewbie instance;

	@Override
	public void onLoad() {
		TrappedNewbie.instance = this;

		registerCommands();
	}

	@Override
	public void onEnable() {
		EventUtil.registerListeners(this,
			// player
			PerPlayerWorlds.class,
			// world
			FirstWorldJoin.class
		);
	}

	private void registerCommands() {
		var commandManager = CommandManager.commandManager();
		commandManager.manager().parserRegistry().registerParser(playerWorldParser());
		BukkitBrigadierMapper<CommandSourceStack> mapper = new BukkitBrigadierMapper<>(
			getLogger(),
			commandManager.manager().brigadierManager()
		);
		mapper.mapSimpleNMS(new TypeToken<PlayerWorldParser<CommandSourceStack>>() {}, "nbt_path", true);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull TrappedNewbie instance() {
		return TrappedNewbie.instance;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static @NotNull ComponentLogger logger() {
		return instance().getComponentLogger();
	}

}
