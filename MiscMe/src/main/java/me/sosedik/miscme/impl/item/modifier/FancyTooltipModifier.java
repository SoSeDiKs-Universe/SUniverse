package me.sosedik.miscme.impl.item.modifier;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import static me.sosedik.miscme.MiscMe.miscMeKey;

/**
 * Custom item tooltip styles
 */
@NullMarked
public class FancyTooltipModifier extends ItemModifier {

	private static final Map<Material, Key> TOOLTIPS = new HashMap<>();
	private static final List<TooltipRule> EXTRA_TOOLTIPS = new ArrayList<>();

	public FancyTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);

		addTooltip("magic", Material.ENCHANTED_BOOK);
		addTooltip("netherite", Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
		addTooltip("netherite", MaterialTags.NETHERITE_TOOLS.getValues());
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		Key tooltipKey = TOOLTIPS.get(contextBox.getInitialType());
		if (tooltipKey == null) {
			for (TooltipRule tooltipRule : EXTRA_TOOLTIPS) {
				if (!tooltipRule.itemCheck().test(item)) continue;

				tooltipKey = tooltipRule.key();
				break;
			}
			if (tooltipKey == null)
				return ModificationResult.PASS;
		}

		item.setData(DataComponentTypes.TOOLTIP_STYLE, tooltipKey);

		return ModificationResult.OK;
	}

	private static void addTooltip(String key, Material... items) {
		for (Material material : items)
			TOOLTIPS.put(material, miscMeKey(key));
	}

	private static void addTooltip(String key, Collection<Material> items) {
		for (Material material : items)
			TOOLTIPS.put(material, miscMeKey(key));
	}

	public static void addTooltipRule(Tooltip tooltip, Predicate<ItemStack> itemCheck) {
		EXTRA_TOOLTIPS.add(new TooltipRule(itemCheck, tooltip.key));
	}

	private record TooltipRule(Predicate<ItemStack> itemCheck, Key key) {}

	public enum Tooltip {

		MAGIC,
		NETHERITE;

		private final Key key;

		Tooltip() {
			this.key = miscMeKey(name().toLowerCase(Locale.US));
		}

	}

}
