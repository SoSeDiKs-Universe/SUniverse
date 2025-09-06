package me.sosedik.trappednewbie.listener.misc;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.miscme.impl.item.modifier.BarometerModifier;
import me.sosedik.miscme.impl.item.modifier.ClockModifier;
import me.sosedik.miscme.impl.item.modifier.LunarClockModifier;
import me.sosedik.miscme.listener.player.PlayerSpeedTracker;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.feature.ScoreboardRenderer;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.item.modifier.ItemOverlayToggleModifier;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import me.sosedik.utilizer.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Tracks player's inventory for items related to world state
 */
@NullMarked
public class DynamicInventoryInfoGatherer implements Listener {

	private static final Map<UUID, InventoryData> INVENTORY_DATAS = new HashMap<>();
	private static final FontData START_ONE = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/left"));
	private static final FontData CENTER_ONE = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/center"));
	private static final FontData END_ONE = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/right"));
	private static final FontData START_TWO = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/left_two"));
	private static final FontData CENTER_TWO = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/center_two"));
	private static final FontData END_TWO = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/right_two"));
	private static final FontData START_THREE = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/left_three"));
	private static final FontData CENTER_THREE = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/center_three"));
	private static final FontData END_THREE = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("sb_back/right_three"));
	private static final String OVERLAY_TAG = "overlay";

	public DynamicInventoryInfoGatherer() {
		TrappedNewbie.scheduler().sync(() -> {
			for (Player player : Bukkit.getOnlinePlayers())
				checkInventoryData(player);
		}, 20L, 20L);

		BarometerModifier.SHOULD_SHOW_TIME_AWARE_WEATHER = player -> getInventoryData(player).hasClock();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		checkInventoryData(player);
		addRenderData(player);
	}

	private void addRenderData(Player player) {
		ScoreboardRenderer scoreboardRenderer = ScoreboardRenderer.of(player);
		scoreboardRenderer.addProvider(TrappedNewbie.trappedNewbieKey("coordinates"), () -> {
			InventoryData inventoryData = getInventoryData(player);
			ReadWriteNBT playerData = PlayerDataStorage.getData(player);
			boolean showCompassOverlay = inventoryData.hasCompass() && playerData.resolveOrDefault("overlay.compass", true);
			boolean showDepthMeterOverlay = inventoryData.hasDepthMeter() && playerData.resolveOrDefault("overlay.depth_meter", true);
			if (!showCompassOverlay) {
				if (!showDepthMeterOverlay) return null;

				Location loc = player.getLocation();
				Component y = Component.text(loc.blockY());
				int width = SpacingUtil.getWidth(y);
				int extraWidth = SpacingUtil.getWidth(Component.text("Y: "));
				int totalWidth = width + extraWidth;

				List<Component> background = new ArrayList<>(totalWidth);
				for (int i = 1; i < totalWidth; i++)
					background.add(CENTER_ONE.icon());

				return List.of(
					SpacingUtil.getOffset(-1, 0, combined(
						START_ONE.offsetMapping(-START_ONE.width() - totalWidth),
						SpacingUtil.getOffset(-totalWidth, background.size() + 1, combine(SpacingUtil.getNegativePixel(), background)),
						SpacingUtil.getOffset(-totalWidth, totalWidth, combined(Component.text("Y: ", NamedTextColor.GREEN), y)),
						END_ONE.offsetMapping(-1)
					))
				);
			}

			Location loc = player.getLocation();
			Component x = Component.text(loc.blockX());
			Component y = Component.text(loc.blockY());
			Component z = Component.text(loc.blockZ());
			int xLength = SpacingUtil.getWidth(x);
			int yLength = showDepthMeterOverlay ? SpacingUtil.getWidth(y) : 0;
			int zLength = SpacingUtil.getWidth(z);
			int maxWidth = Math.max(xLength, Math.max(zLength, showDepthMeterOverlay ? yLength: 0));
			int extraWidth = SpacingUtil.getWidth(Component.text("Y: "));
			int totalWidth = maxWidth + extraWidth;

			List<Component> background = new ArrayList<>(totalWidth);
			for (int i = 1; i < totalWidth; i++)
				background.add(showDepthMeterOverlay ? CENTER_THREE.icon() : CENTER_TWO.icon());

			return showDepthMeterOverlay
				? List.of(
					SpacingUtil.getOffset(-1, 0, combined(
						START_THREE.offsetMapping(-START_THREE.width() - totalWidth),
						SpacingUtil.getOffset(-totalWidth, background.size() + 1, combine(SpacingUtil.getNegativePixel(), background)),
						SpacingUtil.getOffset(-totalWidth, totalWidth, combined(Component.text("X: ", NamedTextColor.RED), SpacingUtil.getSpacing(maxWidth - xLength), x)),
						END_THREE.offsetMapping(-1)
					)),
					SpacingUtil.getOffset(-totalWidth - 1, totalWidth, combined(Component.text("Y: ", NamedTextColor.GREEN), SpacingUtil.getSpacing(maxWidth - yLength), y)),
					SpacingUtil.getOffset(-totalWidth - 1, totalWidth, combined(Component.text("Z: ", NamedTextColor.BLUE), SpacingUtil.getSpacing(maxWidth - zLength), z))
				)
				: List.of(
					SpacingUtil.getOffset(-1, 0, combined(
						START_TWO.offsetMapping(-START_TWO.width() - totalWidth),
						SpacingUtil.getOffset(-totalWidth, background.size() + 1, combine(SpacingUtil.getNegativePixel(), background)),
						SpacingUtil.getOffset(-totalWidth, totalWidth, combined(Component.text("X: ", NamedTextColor.RED), SpacingUtil.getSpacing(maxWidth - xLength), x)),
						END_TWO.offsetMapping(-1)
					)),
					SpacingUtil.getOffset(-totalWidth - 1, totalWidth, combined(Component.text("Z: ", NamedTextColor.BLUE), SpacingUtil.getSpacing(maxWidth - zLength), z))
				);
		});
		scoreboardRenderer.addProvider(TrappedNewbie.trappedNewbieKey("clock"), () -> {
			InventoryData inventoryData = getInventoryData(player);
			ReadWriteNBT playerData = PlayerDataStorage.getData(player);
			boolean showClockOverlay = inventoryData.hasClock() && playerData.resolveOrDefault("overlay.clock", true);
			if (!showClockOverlay) return null;

			World world = player.getWorld();
			if (world.getEnvironment() == World.Environment.NETHER) return null;
			if (world.getEnvironment() == World.Environment.THE_END) return null;

			Component text = ClockModifier.formatTime(world.getTime(), Messenger.messenger(player), player);

			int textWidth = SpacingUtil.getWidth(text);
			text = SpacingUtil.getOffset(-textWidth, textWidth, text);

			int totalWidth = textWidth;

			List<Component> texts = new ArrayList<>();
			texts.add(text);

			boolean showLunarClockOverlay = inventoryData.hasLunarClock() && playerData.resolveOrDefault("overlay.lunar_clock", true);
			if (showLunarClockOverlay) {
				Component icon = Component.text(LunarClockModifier.getMoonEmoji(world.getMoonPhase()));
				texts.addFirst(SpacingUtil.getOffset(-totalWidth - 13, 10, icon));
				totalWidth += 13;
			}

			boolean showBarometerOverlay = inventoryData.hasBarometer() && playerData.resolveOrDefault("overlay.barometer", true);
			if (showBarometerOverlay) {
				FontData fontData;
				if (world.hasStorm()) {
					double temperature = player.getLocation().getBlock().getTemperature();
					boolean sandstorm = temperature > 0.95;
					boolean snow = temperature < 0.15;
					if (world.isThundering()) {
						if (sandstorm)
							fontData = BarometerModifier.SANDSTORM_THUNDER_FONT;
						else if (snow)
							fontData = BarometerModifier.SNOW_THUNDER_FONT;
						else
							fontData = BarometerModifier.THUNDERSTORM_FONT;
					} else {
						if (sandstorm)
							fontData = BarometerModifier.SANDSTORM_FONT;
						else if (snow)
							fontData = BarometerModifier.SNOW_FONT;
						else
							fontData = BarometerModifier.RAIN_FONT;
					}
				} else {
					long time = world.getTime();
					if (time >= 12000 && time < 13000)
						fontData = BarometerModifier.CLEAR_SUNSET_FONT;
					else if (time >= 13000 && time < 23000)
						fontData = BarometerModifier.CLEAR_NIGHT_FONT;
					else if (time >= 23000 || time < 300)
						fontData = BarometerModifier.CLEAR_SUNRISE_FONT;
					else
						fontData = BarometerModifier.CLEAR_DAY_FONT;
				}
				int offset = fontData.width() + 4;
				texts.addFirst(fontData.offsetMapping(-totalWidth - offset));
				totalWidth += offset;
			}

			if (texts.size() > 1)
				text = combined(texts);

			List<Component> background = new ArrayList<>(totalWidth);
			for (int i = 1; i < totalWidth; i++)
				background.add(CENTER_ONE.icon());

			return List.of(
				SpacingUtil.getOffset(-1, 0, combined(
					START_ONE.offsetMapping(-START_ONE.width() - totalWidth),
					SpacingUtil.getOffset(-totalWidth, background.size() + 1, combine(SpacingUtil.getNegativePixel(), background)),
					text,
					END_ONE.offsetMapping(-1)
				))
			);
		});
		scoreboardRenderer.addProvider(TrappedNewbie.trappedNewbieKey("speedometer"), () -> {
			InventoryData inventoryData = getInventoryData(player);
			ReadWriteNBT playerData = PlayerDataStorage.getData(player);
			boolean showSpeedometerOverlay = inventoryData.hasSpeedometer() && playerData.resolveOrDefault("overlay.speedometer", true);
			if (!showSpeedometerOverlay) return null;

			double speed = PlayerSpeedTracker.getSpeed(player);
			Component text = Messenger.messenger(player).getMessage("item.speedometer.speed.short", raw("speed", String.format("%.3f", speed)));

			int width = SpacingUtil.getWidth(text);

			List<Component> background = new ArrayList<>(width);
			for (int i = 1; i < width; i++)
				background.add(CENTER_ONE.icon());

			return List.of(
				SpacingUtil.getOffset(-1, 0, combined(
					START_ONE.offsetMapping(-START_ONE.width() - width),
					SpacingUtil.getOffset(-width, background.size() + 1, combine(SpacingUtil.getNegativePixel(), background)),
					SpacingUtil.getOffset(-width, width, text),
					END_ONE.offsetMapping(-1)
				))
			);
		});
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		ItemOverlayToggleModifier.ToggleableData overlayToggleable = ItemOverlayToggleModifier.getOverlayToggleable(item.getType());
		if (overlayToggleable == null) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (overlayToggleable.condition() != null && !overlayToggleable.condition().test(player)) return;

		ReadWriteNBT data = PlayerDataStorage.getData(player);
		data = data.getOrCreateCompound(OVERLAY_TAG);

		data.setBoolean(overlayToggleable.id(), !data.getOrDefault(overlayToggleable.id(), true));
	}

	@EventHandler
	public void onSave(PlayerDataSaveEvent event) {
		persistData(event.getPreData(), event.getData());
	}

	@EventHandler
	public void onLoad(PlayerDataLoadedEvent event) {
		persistData(event.getData(), event.getBackupData());
	}

	private void persistData(ReadWriteNBT preData, ReadWriteNBT data) {
		if (preData.hasTag(OVERLAY_TAG))
			data.getOrCreateCompound(OVERLAY_TAG).mergeCompound(preData.getOrCreateCompound(OVERLAY_TAG));
	}

	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent event) {
		checkInventoryData(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		INVENTORY_DATAS.remove(event.getPlayer().getUniqueId());
	}

	private void checkInventoryData(Player player) {
		InventoryData data = new InventoryData(player);
		INVENTORY_DATAS.put(player.getUniqueId(), data);
		if (data.shouldDisableReducedDebugInfo())
			DynamicReducedF3DebugInfo.disableReducedDebugInfo(player);
		else
			DynamicReducedF3DebugInfo.enableReducedDebugInfo(player);
	}

	public static InventoryData getInventoryData(Player player) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		return INVENTORY_DATAS.computeIfAbsent(player.getUniqueId(), k -> new InventoryData(player));
	}

	public static class InventoryData {

		private final boolean clock;
		private final boolean lunarClock;
		private final boolean compass;
		private final boolean depthMeter;
		private final boolean barometer;
		private final boolean speedometer;

		public InventoryData(Player player) {
			this.clock = InventoryUtil.findItem(player, item -> item.getType() == Material.CLOCK) != null;
			this.lunarClock = InventoryUtil.findItem(player, item -> item.getType() == MiscMeItems.LUNAR_CLOCK) != null;
			this.compass = InventoryUtil.findItem(player, item -> Tag.ITEMS_COMPASSES.isTagged(item.getType())) != null;
			this.depthMeter = InventoryUtil.findItem(player, item -> item.getType() == MiscMeItems.DEPTH_METER) != null;
			this.barometer = InventoryUtil.findItem(player, item -> item.getType() == MiscMeItems.BAROMETER) != null;
			this.speedometer = InventoryUtil.findItem(player, item -> item.getType() == MiscMeItems.SPEEDOMETER) != null;
		}

		public boolean hasClock() {
			return this.clock;
		}

		public boolean hasLunarClock() {
			return this.lunarClock;
		}

		public boolean hasCompass() {
			return this.compass;
		}

		public boolean hasDepthMeter() {
			return this.depthMeter;
		}

		public boolean hasBarometer() {
			return this.barometer;
		}

		public boolean hasSpeedometer() {
			return this.speedometer;
		}

		public boolean shouldDisableReducedDebugInfo() {
			return hasAllItems(); // No cheesing!
		}

		public boolean hasAllItems() {
			return hasClock() && hasLunarClock() && hasCompass() && hasDepthMeter() && hasBarometer() && hasSpeedometer();
		}

	}

}
