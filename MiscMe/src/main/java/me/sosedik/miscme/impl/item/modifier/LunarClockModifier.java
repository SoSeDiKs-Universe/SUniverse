package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.world.MoonPhase;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static me.sosedik.utilizer.api.message.Mini.combine;

/**
 * Controls lunar clock's display & lore
 */
@NullMarked
public class LunarClockModifier extends ItemModifier {

	private static final NamespacedKey STILL_LUNAR_CLOCK = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("lunar_clock_00"));
	private static final NamespacedKey NO_MOON_LUNAR_CLOCK = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("lunar_clock_04"));
	private static final NamespacedKey LUNAR_CLOCK = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("lunar_clock"));
	private static final Map<MoonPhase, Component> ICONS = new EnumMap<>(MoonPhase.class);

	static {
		for (MoonPhase moonPhase : MoonPhase.values()) {
			ICONS.put(moonPhase, Mini.asIcon(Component.text(getMoonEmoji(moonPhase))));
		}
	}

	public LunarClockModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != MiscMeItems.LUNAR_CLOCK) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null || ItemUtil.shouldFreeze(contextBox)) {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, STILL_LUNAR_CLOCK);
			return ModificationResult.OK;
		}

		World world = player.getWorld();
		if (world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, NO_MOON_LUNAR_CLOCK);
		} else {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, LUNAR_CLOCK);

			if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.OK;
			if (!(contextBox.getContext() instanceof SlottedItemModifierContext)) return ModificationResult.OK;

			var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
			MoonPhase moonPhase = world.getMoonPhase();
			Component icon = ICONS.get(moonPhase);
			Component name = messenger.getMessage("item.lunar_clock.phase." + moonPhase.name().toLowerCase(Locale.US)).colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
			contextBox.addLore(combine(Component.space(), icon, name));
		}

		return ModificationResult.OK;
	}

	public static String getMoonEmoji(MoonPhase moonPhase) {
		return switch (moonPhase) {
			case FULL_MOON -> "🌕";
			case WANING_GIBBOUS -> "🌔";
			case LAST_QUARTER -> "🌓";
			case WANING_CRESCENT -> "🌒";
			case NEW_MOON -> "🌑";
			case WAXING_CRESCENT -> "🌘";
			case FIRST_QUARTER -> "🌗";
			case WAXING_GIBBOUS -> "🌖";
		};
	}

}
