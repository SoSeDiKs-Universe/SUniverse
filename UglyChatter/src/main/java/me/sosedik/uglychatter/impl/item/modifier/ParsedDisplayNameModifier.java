package me.sosedik.uglychatter.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ParsedDisplayNameModifier extends ItemModifier {

	public ParsedDisplayNameModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.CUSTOM_NAME)) return ModificationResult.PASS;

		Component data = item.getData(DataComponentTypes.CUSTOM_NAME);
		assert data != null;
		if (data == Component.empty()) return ModificationResult.PASS;

		Player viewer = contextBox.getViewer();
		String rawInput = FancyMessageRenderer.getRawInput(data);
		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		data = FancyMessageRenderer.renderMessage(Mini.buildMini(messenger), rawInput, viewer, viewer);
		item.setData(DataComponentTypes.CUSTOM_NAME, data.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

		return ModificationResult.OK;
	}

}
