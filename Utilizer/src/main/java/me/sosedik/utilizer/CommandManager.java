package me.sosedik.utilizer;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class CommandManager {

	private static CommandManager commandManager;

	private final PaperCommandManager<CommandSourceStack> paperCommandManager;
	private final AnnotationParser<CommandSourceStack> annotationParser;

	private CommandManager(@NonNull Plugin plugin) {
		paperCommandManager = PaperCommandManager.builder()
			.executionCoordinator(ExecutionCoordinator.asyncCoordinator())
			.buildOnEnable(plugin);

		annotationParser = new AnnotationParser<>(paperCommandManager, CommandSourceStack.class, parserParameters -> CommandMeta.empty());
	}

	/**
	 * Registers commands from the provided classes
	 *
	 * @param plugin owning plugin instance
	 * @param commandClasses command classes
	 */
	public void registerCommands(@NotNull Plugin plugin, @NonNull Class<?> @NonNull ... commandClasses) {
		CommandManager commandManager = commandManager();
		try {
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
					continue;
				} else if (paramCount == 1) {
					if (constructor.getParameterTypes()[0] == CommandManager.class) {
						constructor.newInstance(commandManager);
						continue;
					}
				} else if (paramCount == 2) {
					Class<?>[] paramTypes = constructor.getParameterTypes();
					if (paramTypes[0] == Plugin.class && paramTypes[1] == CommandManager.class) {
						constructor.newInstance(plugin, commandManager);
						continue;
					}
				}
				Utilizer.logger().warn("Couldn't register commands in {} for {} (unsupported constructor)", commandClass, plugin.getName());
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
	public <T> @NonNull Collection<@NonNull Command<@NonNull CommandSourceStack>> registerCommand(@NonNull T instance) {
		return annotationParser.parse(instance);
	}

	public @NonNull PaperCommandManager<@NonNull CommandSourceStack> manager() {
		return this.paperCommandManager;
	}

	static void init(@NonNull Plugin plugin) {
		if (CommandManager.commandManager != null) return;
		CommandManager.commandManager = new CommandManager(plugin);
	}

	public static @NonNull CommandManager commandManager() {
		return CommandManager.commandManager;
	}

}
