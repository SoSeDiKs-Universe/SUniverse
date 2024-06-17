package me.sosedik.utilizer.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
	public static void registerListeners(@NotNull Plugin plugin, @NonNull Class<?> @NonNull ... listenerClasses) {
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		FileConfiguration pluginConfig = plugin.getConfig();
		try {
			for (Class<?> listenerClass : listenerClasses) {
				if (listenerClass.getDeclaredConstructors().length != 1) {
					Utilizer.logger().warn("Couldn't register events in {} for {}", listenerClass, plugin.getName());
					continue;
				}
				if (!Listener.class.isAssignableFrom(listenerClass)) {
					Utilizer.logger().warn("Couldn't register events in {} for {} (class does not implement Listener)", listenerClass, plugin.getName());
					continue;
				}
				Listener listener = null;
				Constructor<?> constructor = listenerClass.getDeclaredConstructors()[0];
				int paramCount = constructor.getParameterCount();
				if (paramCount == 0) {
					listener = (Listener) constructor.newInstance();
				} else if (paramCount == 1) {
					if (constructor.getParameterTypes()[0] == Plugin.class) {
						listener = (Listener) constructor.newInstance(plugin);
					}
				} else if (paramCount == 2) {
					Class<?>[] paramTypes = constructor.getParameterTypes();
					if (paramTypes[0] == Plugin.class && paramTypes[1] == FileConfiguration.class) {
						listener = (Listener) constructor.newInstance(plugin, pluginConfig);
					}
				}
				if (listener != null) {
					pluginManager.registerEvents(listener, plugin);
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

}
