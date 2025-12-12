package me.sosedik.utilizer.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@NullMarked
public class EventUtil {

	private EventUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Registers events from the provided classes
	 *
	 * @param plugin owning plugin instance
	 * @param listenerClasses listener classes
	 */
	public static void registerListeners(Plugin plugin, Class<?> ... listenerClasses) {
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		FileConfiguration pluginConfig = plugin.getConfig();
		try {
			listeners:
			for (Class<?> listenerClass : listenerClasses) {
				if (listenerClass.getDeclaredConstructors().length != 1) {
					Utilizer.logger().warn("Couldn't register events in {} for {}: must have only 1 constructor", listenerClass, plugin.getName());
					continue;
				}
				Object listener;
				Constructor<?> constructor = listenerClass.getDeclaredConstructors()[0];
				int paramCount = constructor.getParameterCount();
				if (paramCount == 0) {
					listener = constructor.newInstance();
				} else {
					Object[] initArgs = new Object[paramCount];
					for (int i = 0; i < paramCount; i++) {
						Class<?> parameterType = constructor.getParameterTypes()[i];
						if (Plugin.class.isAssignableFrom(parameterType)) {
							initArgs[i] = plugin;
						} else if (parameterType == FileConfiguration.class) {
							initArgs[i] = pluginConfig;
						} else if (parameterType == CommandManager.class) {
							initArgs[i] = CommandManager.commandManager();
						} else {
							Utilizer.logger().warn("Couldn't register events in {} for {} (unsupported constructor parameter: {})", listenerClass, plugin.getName(), parameterType);
							continue listeners;
						}
					}
					listener = constructor.newInstance(initArgs);
				}
				if (Listener.class.isAssignableFrom(listenerClass))
					pluginManager.registerEvents((Listener) listener, plugin);
				if (PacketListener.class.isAssignableFrom(listenerClass))
					PacketEvents.getAPI().getEventManager().registerListener((PacketListener) listener, PacketListenerPriority.NORMAL);
			}
		} catch (SecurityException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			Utilizer.logger().error("Couldn't register listeners for {}", plugin.getName(), e);
		}
	}

}
