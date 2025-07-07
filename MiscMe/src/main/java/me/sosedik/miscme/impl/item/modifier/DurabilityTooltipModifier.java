package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.miscme.dataset.MiscMeTags;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.DurabilityUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static me.sosedik.miscme.MiscMe.miscMeKey;
import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.component;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

/**
 * Shows item's durability in lore
 */
@NullMarked
public class DurabilityTooltipModifier extends ItemModifier {

	public static final Component DURABILITY_ICON = ResourceLib.requireFontData(miscMeKey("durability")).icon();
	public static final Component CAPACITY_ICON = ResourceLib.requireFontData(miscMeKey("capacity")).icon();

	public DurabilityTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (contextBox.getInitialType() != item.getType()) item = item.withType(contextBox.getInitialType());
		if (!item.hasData(DataComponentTypes.DAMAGE)) return ModificationResult.PASS;
		if (!item.hasData(DataComponentTypes.MAX_DAMAGE)) return ModificationResult.PASS;

		int durability = DurabilityUtil.getDurability(item);
		if (durability < 0) return ModificationResult.PASS;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));

		int maxDurability = Objects.requireNonNull(item.getData(DataComponentTypes.MAX_DAMAGE));

		Component durabilityComponent = text(durability, getColor(durability, maxDurability));
		durabilityComponent = combined(durabilityComponent, text("/"), text(maxDurability));

		boolean capacity = MiscMeTags.DURABILITY_CAPACITY_TOOLTIP.isTagged(item.getType());
		Component message = messenger.getMessage("attribute.durability" + (capacity ? ".capacity" : ""), component("value", durabilityComponent));

		contextBox.addLore(combined(capacity ? CAPACITY_ICON : DURABILITY_ICON, space(), message.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)));

		return ModificationResult.OK;
	}

	private NamedTextColor getColor(int durability, int maxDurability) {
		if (durability == maxDurability)
			return NamedTextColor.GOLD;

		double percentLeft = ((double) durability) / maxDurability;
		if (percentLeft <= 0.25)
			return NamedTextColor.RED;
		else if (percentLeft <= 0.5)
			return NamedTextColor.YELLOW;
		else if (percentLeft <= 0.75)
			return NamedTextColor.GREEN;

		return NamedTextColor.GRAY;
	}

}
