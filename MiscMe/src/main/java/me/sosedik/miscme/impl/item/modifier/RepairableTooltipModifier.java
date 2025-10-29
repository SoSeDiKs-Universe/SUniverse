package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.item.FakeItemData;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
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
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.REPAIRABLE)) return ModificationResult.PASS;

		Repairable data = item.getData(DataComponentTypes.REPAIRABLE);
		assert data != null;

		List<Component> icons = new ArrayList<>();
		data.types().forEach(type -> {
			Key key = type.key();
			ItemType itemType = Registry.ITEM.get(key);
			if (itemType == null) return;

			FakeItemData fakeItemData = ResourceLib.storage().getFakeItemData(new NamespacedKey(key.namespace(), key.value()));
			if (fakeItemData != null && fakeItemData.model() != null)
				key = Key.key(fakeItemData.model().namespace(), fakeItemData.model().value());

			boolean blocksAtlas = itemType.hasBlockType();
			Key texture = Key.key(key.namespace(), (blocksAtlas ? "block/" : "item/") + key.value());
			icons.add(Mini.asIcon(Component.object(ObjectContents.sprite(texture))));
		});
		if (icons.isEmpty()) return ModificationResult.PASS;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));

		Component itemsComponent = Mini.combine(SpacingUtil.getSpacing(1), icons);
		boolean newLine = icons.size() > 5;
		Component message = messenger.getMessage("attribute.repairable", component("items", newLine ? Component.empty() : itemsComponent));

		contextBox.addLore(combined(ICON, space(), message.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
		if (newLine)
			contextBox.addLore(combined(SpacingUtil.ICON_SPACE, space(), itemsComponent));

		return ModificationResult.OK;
	}

}
