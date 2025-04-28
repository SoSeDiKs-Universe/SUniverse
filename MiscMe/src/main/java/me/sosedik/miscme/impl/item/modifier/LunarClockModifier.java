package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

/**
 * Controls lunar clock display & lore
 */
@NullMarked
public class LunarClockModifier extends ItemModifier {

	private static final NamespacedKey STILL_LUNAR_CLOCK = MiscMe.miscMeKey("lunar_clock_00");
	private static final NamespacedKey NO_MOON_LUNAR_CLOCK = MiscMe.miscMeKey("lunar_clock_00");
	private static final NamespacedKey LUNAR_CLOCK = MiscMe.miscMeKey("lunar_clock");

	public LunarClockModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != MiscMeItems.LUNAR_CLOCK) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null || ClockModifier.shouldFreeze(contextBox)) {
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
			contextBox.addLore(messenger.getMessage("item.lunar_clock.phase." + world.getMoonPhase().name().toLowerCase(Locale.US)).colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
		}

		return ModificationResult.OK;
	}

}
