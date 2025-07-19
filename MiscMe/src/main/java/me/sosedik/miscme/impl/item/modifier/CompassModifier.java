package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;

import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Shows coordinates in compass's lore
 */
@NullMarked
public class CompassModifier extends ItemModifier {

	private static final List<String> DIRECTIONS = List.of("south", "south_west", "west", "north_west", "north", "north_east", "east", "south_east");
	private static final NamespacedKey STILL_COMPASS = MiscMe.miscMeKey("compass_00");
	private static final NamespacedKey STILL_RECOVERY_COMPASS = MiscMe.miscMeKey("recovery_compass_00");
	private static final NamespacedKey COMPASS_MIMIC = MiscMe.miscMeKey("compass");

	public CompassModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!Tag.ITEMS_COMPASSES.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		if (ClockModifier.shouldFreeze(contextBox)) {
			if (contextBox.getItem().isDataOverridden(DataComponentTypes.ITEM_MODEL)) return ModificationResult.PASS;

			NamespacedKey model = contextBox.getInitialType() == Material.RECOVERY_COMPASS ? STILL_RECOVERY_COMPASS : STILL_COMPASS;
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, model);
			return ModificationResult.OK;
		}

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		// Properly render compass in custom overworlds
		boolean updated = false;
		World world = player.getWorld();
		if (contextBox.getInitialType() == Material.COMPASS
				&& world.getEnvironment() == World.Environment.NORMAL
				&& world != Bukkit.getWorlds().getFirst()
				&& !contextBox.getItem().isDataOverridden(DataComponentTypes.ITEM_MODEL)) {
			updated = true;
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, COMPASS_MIMIC);
		}

		if (!contextBox.getContextType().hasVisibleLore()) return updated ? ModificationResult.OK : ModificationResult.PASS;
		if (!(contextBox.getContext() instanceof SlottedItemModifierContext)) return updated ? ModificationResult.OK : ModificationResult.PASS;

		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));

		if (world.getEnvironment() == World.Environment.NETHER) {
			contextBox.addLore(messenger.getMessage("item.compass.position.nether", raw("x", x), raw("z", z)));
			return ModificationResult.OK;
		}

		if (world.getEnvironment() == World.Environment.THE_END) {
			contextBox.addLore(messenger.getMessage("item.compass.position.the_end", raw("x", x), raw("z", z)));
			return ModificationResult.OK;
		}
		contextBox.addLore(messenger.getMessage("item.compass.position", component("direction", getDirection(player, contextBox.getLocale())), raw("x", x), raw("z", z)));

		return ModificationResult.OK;
	}

	/**
	 * Gets the localized short direction message
	 *
	 * @param player player
	 * @param locale locale
	 * @return the localized short direction message
	 */
	public static Component getDirection(Player player, Locale locale) {
		int yaw = (int) player.getYaw();
		if (yaw < 0)
			yaw += 360;
		yaw += 22;
		yaw %= 360;
		yaw /= 45;
		if (yaw < 0)
			yaw *= -1;
		String directionKey = "compass." + DIRECTIONS.get(yaw);
		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(locale));
		return messenger.getMessage(directionKey);
	}

}
