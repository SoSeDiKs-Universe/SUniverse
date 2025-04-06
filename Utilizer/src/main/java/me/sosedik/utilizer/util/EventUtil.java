package me.sosedik.utilizer.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
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
			for (Class<?> listenerClass : listenerClasses) {
				if (listenerClass.getDeclaredConstructors().length != 1) {
					Utilizer.logger().warn("Couldn't register events in {} for {}: must have only 1 constructor", listenerClass, plugin.getName());
					continue;
				}
				if (!isValidListener(listenerClass)) {
					Utilizer.logger().warn("Couldn't register events in {} for {} (class does not implement supported listener)", listenerClass, plugin.getName());
					continue;
				}
				Object listener = null;
				Constructor<?> constructor = listenerClass.getDeclaredConstructors()[0];
				int paramCount = constructor.getParameterCount();
				if (paramCount == 0) {
					listener = constructor.newInstance();
				} else if (paramCount == 1) {
					if (Plugin.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
						listener = constructor.newInstance(plugin);
					}
				} else if (paramCount == 2) {
					Class<?>[] paramTypes = constructor.getParameterTypes();
					if (paramTypes[0] == Plugin.class && paramTypes[1] == FileConfiguration.class) {
						listener = constructor.newInstance(plugin, pluginConfig);
					}
				}
				if (listener != null) {
					if (Listener.class.isAssignableFrom(listenerClass)) {
						pluginManager.registerEvents((Listener) listener, plugin);
					}
					if (PacketListener.class.isAssignableFrom(listenerClass)) {
						PacketEvents.getAPI().getEventManager().registerListener((PacketListener) listener, PacketListenerPriority.NORMAL);
					}
					continue;
				}
				Utilizer.logger().warn("Couldn't register events in {} for {} (unsupported constructor)", listenerClass, plugin.getName());
			}
		} catch (SecurityException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			Utilizer.logger().error("Couldn't register listeners for {}", plugin.getName(), e);
		}
	}

	private static boolean isValidListener(Class<?> listenerClass) {
		return Listener.class.isAssignableFrom(listenerClass)
				|| PacketListener.class.isAssignableFrom(listenerClass);
	}

}
