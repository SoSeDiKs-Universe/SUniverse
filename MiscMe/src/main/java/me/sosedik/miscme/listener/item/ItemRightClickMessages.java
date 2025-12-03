package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.miscme.impl.item.modifier.ClockModifier;
import me.sosedik.miscme.listener.player.PlayerSpeedTracker;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Some items show messages when right-clicked
 */
@NullMarked
public class ItemRightClickMessages implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		Player player = event.getPlayer();
		var messenger = Messenger.messenger(player);
		switch (item.getType()) {
			case Material m when Tag.ITEMS_COMPASSES.isTagged(m) -> {
				Location loc = player.getLocation();
				double x = MathUtil.round(loc.x(), 3);
				double z = MathUtil.round(loc.z(), 3);
				messenger.sendActionBar("item.compass.click", raw("x", x), raw("z", z));
				player.swingMainHand();
			}
			case CLOCK -> {
				World world = player.getWorld();
				if (world.getEnvironment() == World.Environment.NETHER) {
					messenger.sendActionBar("item.clock.time.nether");
				} else if (world.getEnvironment() == World.Environment.THE_END) {
					messenger.sendActionBar("item.clock.time.the_end");
				} else {
					long day = world.getFullTime() / 24_000;
					Component time = ClockModifier.formatTime(world.getTime(), messenger, player);
					messenger.sendActionBar("item.clock.click", raw("day", day), raw("time", time));
				}
				player.swingMainHand();
			}
			case Material m when m == MiscMeItems.DEPTH_METER -> {
				Location loc = player.getLocation();
				double y = MathUtil.round(loc.y(), 3);
				messenger.sendActionBar("item.depth_meter.click", raw("y", y));
				player.swingMainHand();
			}
			case Material m when m == MiscMeItems.LUNAR_CLOCK -> {
				World world = player.getWorld();
				if (world.getEnvironment() == World.Environment.NETHER) return;
				if (world.getEnvironment() == World.Environment.THE_END) return;

				messenger.sendActionBar("item.lunar_clock.phase." + world.getMoonPhase().name().toLowerCase(Locale.US));
				player.swingMainHand();
			}
			case Material m when m == MiscMeItems.SPEEDOMETER -> {
				double speed = PlayerSpeedTracker.getSpeed(player);
				messenger.sendActionBar("item.speedometer.speed.short", raw("speed", speed));
				player.swingMainHand();
			}
			case Material m when m == MiscMeItems.BAROMETER -> {
				String message = getWeatherMessageId(player);
				messenger.sendActionBar(message);
				player.swingMainHand();
			}
			case Material m when m == MiscMeItems.LUXMETER -> {
				Block block = player.getLocation().getBlock();
				messenger.sendActionBar("item.luxmeter.light_level.click",
					raw("level", block.getLightLevel()),
					raw("sky", block.getLightFromSky()),
					raw("block", block.getLightFromBlocks())
				);
				player.swingMainHand();
			}
			default -> {}
		}
	}

	private static String getWeatherMessageId(Player player) {
		World world = player.getWorld();
		if (world.getEnvironment() == World.Environment.NETHER) return "item.barometer.weather.nether";
		if (world.getEnvironment() == World.Environment.THE_END) return "item.barometer.weather.end";

		if (world.hasStorm()) {
			double temperature = player.getLocation().getBlock().getTemperature();
			boolean sandstorm = temperature > 0.95;
			boolean snow = temperature < 0.15;
			if (world.isThundering()) {
				if (sandstorm) return "item.barometer.weather.sandstorm_thunder";
				if (snow) return "item.barometer.weather.snow_thunder";
				return "item.barometer.weather.thunder";
			} else {
				if (sandstorm) return "item.barometer.weather.sandstorm";
				if (snow) return "item.barometer.weather.snow";
				return "item.barometer.weather.rain";
			}
		}
		return "item.barometer.weather.clear";
	}

}
