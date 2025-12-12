package me.sosedik.utilizer;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.utilizer.api.command.FilteringCommandCaselessSuggestionProcessor;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@NullMarked
public class CommandManager {

	private static @UnknownNullability CommandManager commandManager;

	private final PaperCommandManager<CommandSourceStack> paperCommandManager;
	private final AnnotationParser<CommandSourceStack> annotationParser;

	private CommandManager(Plugin plugin) {
		paperCommandManager = PaperCommandManager.builder()
			.executionCoordinator(ExecutionCoordinator.asyncCoordinator())
			.buildOnEnable(plugin);

		paperCommandManager.suggestionProcessor(new FilteringCommandCaselessSuggestionProcessor<>());

		annotationParser = new AnnotationParser<>(paperCommandManager, CommandSourceStack.class, parserParameters -> CommandMeta.empty());
	}

	/**
	 * Registers commands from the provided classes
	 *
	 * @param plugin owning plugin instance
	 * @param commandClasses command classes
	 */
	public void registerCommands(Plugin plugin, Class<?> ... commandClasses) {
		CommandManager commandManager = commandManager();
		try {
			commands:
			for (Class<?> commandClass : commandClasses) {
				if (commandClass.getDeclaredConstructors().length != 1) {
					Utilizer.logger().warn("Couldn't register commands in {} for {} (must be exactly 1 constructor)", commandClass, plugin.getName());
					continue;
				}
				Constructor<?> constructor = commandClass.getDeclaredConstructors()[0];
				int paramCount = constructor.getParameterCount();
				if (paramCount == 0) {
					Object commandInstance = constructor.newInstance();
					commandManager.registerCommand(commandInstance);
				} else {
					Object[] initArgs = new Object[paramCount];
					for (int i = 0; i < paramCount; i++) {
						Class<?> parameterType = constructor.getParameterTypes()[i];
						if (Plugin.class.isAssignableFrom(parameterType)) {
							initArgs[i] = plugin;
						} else if (parameterType == CommandManager.class) {
							initArgs[i] = CommandManager.commandManager();
						} else {
							Utilizer.logger().warn("Couldn't register commands in {} for {} (unsupported constructor parameter: {})", commandClass, plugin.getName(), parameterType);
							continue commands;
						}
					}
					Object commandInstance = constructor.newInstance(initArgs);
					commandManager.registerCommand(commandInstance);
				}
			}
		} catch (SecurityException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			Utilizer.logger().error("Couldn't register commands for {}", plugin.getName(), e);
		}
	}

	/**
	 * Scan instance of {@link org.incendo.cloud.annotations.Command}-annotated type and attempt to
	 * compile it into {@link org.incendo.cloud.Command} instance.
	 *
	 * @param instance instance to scan
	 * @return a parsed command
	 */
	public <T> Collection<Command<CommandSourceStack>> registerCommand(T instance) {
		return annotationParser.parse(instance);
	}

	public PaperCommandManager<CommandSourceStack> manager() {
		return this.paperCommandManager;
	}

	static void init(Plugin plugin) {
		if (CommandManager.commandManager != null) return;
		CommandManager.commandManager = new CommandManager(plugin);
	}

	public static CommandManager commandManager() {
		return CommandManager.commandManager;
	}

}
