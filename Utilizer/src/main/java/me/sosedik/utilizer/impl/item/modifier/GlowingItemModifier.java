package me.sosedik.utilizer.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Adds glowing indicator for glowing items
 */
@NullMarked
public class GlowingItemModifier extends ItemModifier {

	/**
	 * Custom nbt tag used for rendering item glow
	 */
	public static final String GLOW_MODIFIER_KEY = "glowing";

	public GlowingItemModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (!NBT.get(item, nbt -> (boolean) nbt.getOrDefault(GLOW_MODIFIER_KEY, false))) return ModificationResult.PASS;

		boolean modified = false;
		if (contextBox.getContextType().hasVisibleName() && item.hasData(DataComponentTypes.DYED_COLOR)) {
			Color color = Objects.requireNonNull(item.getData(DataComponentTypes.DYED_COLOR)).color();

			Component name = item.effectiveName();
			if (name.shadowColor() != null) {
				Style style = name.style()
						.shadowColor(ShadowColor.shadowColor(color.asARGB()))
						.decoration(TextDecoration.ITALIC, name.hasDecoration(TextDecoration.ITALIC));
				name = name.style(style);
				item.setData(DataComponentTypes.CUSTOM_NAME, name);
			}
			modified = true;
		}

		if (contextBox.getContextType().hasVisibleLore()) {
			Messenger messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
			contextBox.addLore(messenger.getMessage("item.modifier.glowy"));
			modified = true;
		}

		return modified ? ModificationResult.OK : ModificationResult.PASS;
	}

}
