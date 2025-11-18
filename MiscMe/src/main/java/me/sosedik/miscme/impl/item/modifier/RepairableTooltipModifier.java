package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.miscme.MiscMe.miscMeKey;
import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.component;
import static net.kyori.adventure.text.Component.space;

/**
 * Shows item's durability in lore
 */
@NullMarked
public class RepairableTooltipModifier extends ItemModifier {

	public static final Component ICON = ResourceLib.requireFontData(miscMeKey("repairable")).icon();

	public RepairableTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.REPAIRABLE)) return ModificationResult.PASS;

		Repairable data = item.getData(DataComponentTypes.REPAIRABLE);
		assert data != null;

		List<Component> icons = new ArrayList<>();
		data.types().forEach(type -> icons.add(ResourceLib.getItemIcon(type.key())));
		if (icons.isEmpty()) return ModificationResult.PASS;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));

		boolean newLine = icons.size() > 5;
		Component message = messenger.getMessage("attribute.repairable", component("items", newLine ? Component.empty() : Mini.combine(SpacingUtil.getSpacing(1), icons)));

		contextBox.addLore(combined(ICON, space(), message.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
		if (newLine) {
			for (int i = 0; i < icons.size(); i += 12) {
				List<Component> chunk = icons.subList(i, Math.min(i + 12, icons.size()));
				Component chunkComponent = Mini.combine(SpacingUtil.getSpacing(1), chunk);
				contextBox.addLore(combined(SpacingUtil.ICON_SPACE, space(), chunkComponent));
			}
		}

		return ModificationResult.OK;
	}

}
