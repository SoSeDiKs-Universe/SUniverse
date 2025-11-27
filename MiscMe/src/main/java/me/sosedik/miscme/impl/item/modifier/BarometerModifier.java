package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.packet.EntityDataPacketContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.function.Predicate;

import static me.sosedik.utilizer.api.message.Mini.combine;

/**
 * Controls barometer's display & lore
 */
@NullMarked
public class BarometerModifier extends ItemModifier {

	public static Predicate<Player> SHOULD_SHOW_TIME_AWARE_WEATHER = (player) -> false;

	private static final NamespacedKey CLEAR = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_clear"));
	private static final NamespacedKey RAIN = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_rain"));
	private static final NamespacedKey THUNDER = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_thunder"));
	private static final NamespacedKey SNOW = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_snow"));
	private static final NamespacedKey SNOW_THUNDER = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_snow_thunder"));
	private static final NamespacedKey SANDSTORM = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_sandstorm"));
	private static final NamespacedKey SANDSTORM_THUNDER = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_sandstorm_thunder"));
	private static final NamespacedKey END = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_end"));
	private static final NamespacedKey NETHER = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("barometer/barometer_nether"));
	public static final FontData CLEAR_DAY_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("sunny_day"));
	public static final FontData CLEAR_SUNSET_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("sunny_sunset"));
	public static final FontData CLEAR_NIGHT_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("sunny_night"));
	public static final FontData CLEAR_SUNRISE_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("sunny_sunrise"));
	public static final FontData RAIN_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("rain"));
	public static final FontData THUNDERSTORM_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("thunderstorm"));
	public static final FontData SNOW_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("snow"));
	public static final FontData SNOW_THUNDER_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("snow_thunder"));
	public static final FontData SANDSTORM_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("sandstorm"));
	public static final FontData SANDSTORM_THUNDER_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("sandstorm_thunder"));
	public static final FontData END_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("end"));
	public static final FontData NETHER_FONT = ResourceLib.requireFontData(MiscMe.miscMeKey("nether"));

	public BarometerModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != MiscMeItems.BAROMETER) return ModificationResult.PASS;

		contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, CLEAR);

		if (ItemUtil.shouldFreeze(contextBox.getContext())) return ModificationResult.OK;

		Entity target = null;
		if (contextBox.getContext().getRootContext() instanceof EntityEquipmentPacketContext context) {
			target = context.getEntity();
		} else if (contextBox.getContext().getRootContext() instanceof EntityDataPacketContext context) {
			target = context.getEntity();
		}
		if (target == null) {
			target = contextBox.getViewer();
			if (target == null)
				return ModificationResult.OK;
		}

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		boolean addLore = contextBox.getContext().getContextType().hasVisibleLore();

		World world = target.getWorld();
		if (world.getEnvironment() == World.Environment.NETHER) {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, NETHER);
			if (addLore) contextBox.addLore(combine(Component.space(), NETHER_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.nether"))));
			return ModificationResult.OK;
		}

		if (world.getEnvironment() == World.Environment.THE_END) {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, END);
			if (addLore) contextBox.addLore(combine(Component.space(), END_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.end"))));
			return ModificationResult.OK;
		}

		// Note: also has conditions in DynamicInventoryInfoGatherer and ItemRightClickMessages
		if (world.hasStorm()) {
			double temperature = target.getLocation().getBlock().getTemperature();
			boolean sandstorm = temperature > 0.95;
			boolean snow = temperature < 0.15;
			if (world.isThundering()) {
				if (sandstorm) {
					contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, SANDSTORM_THUNDER);
					if (addLore) contextBox.addLore(combine(Component.space(), SANDSTORM_THUNDER_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.sandstorm_thunder"))));
				} else if (snow) {
					contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, SNOW_THUNDER);
					if (addLore) contextBox.addLore(combine(Component.space(), SNOW_THUNDER_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.snow_thunder"))));
				} else {
					contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, THUNDER);
					if (addLore) contextBox.addLore(combine(Component.space(), THUNDERSTORM_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.thunder"))));
				}
			} else {
				if (sandstorm) {
					contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, SANDSTORM);
					if (addLore) contextBox.addLore(combine(Component.space(), SANDSTORM_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.sandstorm"))));
				} else if (snow) {
					contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, SNOW);
					if (addLore) contextBox.addLore(combine(Component.space(), SNOW_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.snow"))));
				} else {
					contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, RAIN);
					if (addLore) contextBox.addLore(combine(Component.space(), RAIN_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.rain"))));
				}
			}
			return ModificationResult.OK;
		}

		if (addLore) {
			Player viewer = contextBox.getViewer();
			if (viewer != null && SHOULD_SHOW_TIME_AWARE_WEATHER.test(viewer)) {
				long time = world.getTime();
				if (time >= 12000 && time < 13000)
					contextBox.addLore(combine(Component.space(), CLEAR_SUNSET_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.clear"))));
				else if (time >= 13000 && time < 23000)
					contextBox.addLore(combine(Component.space(), CLEAR_NIGHT_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.clear"))));
				else if (time >= 23000 || time < 300)
					contextBox.addLore(combine(Component.space(), CLEAR_SUNRISE_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.clear"))));
				else
					contextBox.addLore(combine(Component.space(), CLEAR_DAY_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.clear"))));
			} else {
				contextBox.addLore(combine(Component.space(), CLEAR_DAY_FONT.icon(), formatMessage(messenger.getMessage("item.barometer.weather.clear"))));
			}
		}

		return ModificationResult.OK;
	}

	private static Component formatMessage(Component text) {
		return text.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
	}

}
