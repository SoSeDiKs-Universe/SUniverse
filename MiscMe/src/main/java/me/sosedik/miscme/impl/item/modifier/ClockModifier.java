package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContextType;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.listener.player.PlayerOptions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Shows time in clock's lore
 */
@NullMarked
public class ClockModifier extends ItemModifier {

	private static final NamespacedKey STILL_CLOCK = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("clock_00"));
	private static final NamespacedKey CLOCK_MIMIK = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("clock"));

	public ClockModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != Material.CLOCK) return ModificationResult.PASS;

		if (shouldFreeze(contextBox)) {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, STILL_CLOCK);
			return ModificationResult.OK;
		}

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		boolean updated = false;
		World world = player.getWorld();
		// Properly render clock in custom overworlds
		if (world.getEnvironment() == World.Environment.NORMAL && world != Bukkit.getWorlds().getFirst() && !contextBox.getItem().isDataOverridden(DataComponentTypes.ITEM_MODEL)) {
			updated = true;
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, CLOCK_MIMIK);
		}

		if (!contextBox.getContextType().hasVisibleLore()) return updated ? ModificationResult.OK : ModificationResult.PASS;
		if (!(contextBox.getContext() instanceof SlottedItemModifierContext)) return updated ? ModificationResult.OK : ModificationResult.PASS;

		contextBox.addLore(formatTime(player, contextBox.getLocale()));

		return ModificationResult.OK;
	}

	public static boolean shouldFreeze(ItemContextBox contextBox) {
		ItemModifierContextType contextType = contextBox.getContextType();
		return contextType == ItemModifierContextType.RECIPE_BOOK
				|| contextType == ItemModifierContextType.RECIPE_GHOST
				|| contextType == ItemModifierContextType.MERCHANT_OFFER
				|| contextType == ItemModifierContextType.ADVANCEMENT;
	}

	public static Component formatTime(Player player, Locale locale) {
		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(locale));

		World world = player.getWorld();
		if (world.getEnvironment() == World.Environment.NETHER) return messenger.getMessage("item.clock.time.nether");
		if (world.getEnvironment() == World.Environment.THE_END) return messenger.getMessage("item.clock.time.the_end");

		long day = world.getFullTime() / 24_000;
		Component time = formatTime(world, messenger, player);

		long t = world.getTime();
		int h = (int) (t / 1000) + 6;
		if (h > 23) h -= 24;
		String clock = getClockEmoji(h);

		return messenger.getMessage("item.clock.time", raw("emoji", clock), raw("day", day), component("time", time));
	}

	public static Component formatTime(World world, Messenger messenger, Player player) {
		long t = world.getTime();
		int h = (int) (t / 1000) + 6;
		if (h > 23) h -= 24;
		int m = (int) ((60 * (t % 1000)) / 1000);
		String amPm;
		boolean useAmPm = PlayerOptions.isAmPm(player);
		if (useAmPm) {
			if (h > 12) {
				h -= 12;
				amPm = "PM";
			} else amPm = "AM";
		} else amPm = "";

		return messenger.getMessage(useAmPm ? "clock.time.ampm" : "clock.time",
			raw("hour", (!useAmPm && h < 10) ? "0" + h : String.valueOf(h)),
			raw("minute", m < 10 ? "0" + m : String.valueOf(m)),
			raw("am_pm", amPm)
		);
	}

	/**
	 * Gets the clock emoji form the hour
	 *
	 * @param h hour
	 * @return the clock emoji
	 * @throws IllegalArgumentException if hour is invalid
	 */
	public static String getClockEmoji(@Range(from = 0, to = 24) int h) {
		return switch (h) {
			case 0, 12, 24 -> "🕛";
			case 1, 13 -> "🕐";
			case 2, 14 -> "🕑";
			case 3, 15 -> "🕒";
			case 4, 16 -> "🕓";
			case 5, 17 -> "🕔";
			case 6, 18 -> "🕕";
			case 7, 19 -> "🕖";
			case 8, 20 -> "🕗";
			case 9, 21 -> "🕘";
			case 10, 22 -> "🕙";
			case 11, 23 -> "🕚";
			default -> throw new IllegalArgumentException("Hour must be between 0 and 23.");
		};
	}

}
