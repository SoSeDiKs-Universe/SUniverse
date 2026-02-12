package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.miscme.listener.item.PaintingSwitcher;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Art;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomPaintingDescriptionModifier extends ItemModifier {

	public CustomPaintingDescriptionModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;
		if (contextBox.getInitialType() != Material.PAINTING) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.PAINTING_VARIANT)) {
			contextBox.addLore(Component.translatable("painting.random", NamedTextColor.YELLOW));
			return ModificationResult.OK;
		}

		Art data = item.getData(DataComponentTypes.PAINTING_VARIANT);
		assert data != null;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		Key key = data.assetId();

		String rawKey = "painting." + key.namespace() + "." + key.value();
		String titleKey = rawKey + ".title";
		String authorKey = rawKey + ".author";

		Component title = messenger.getMessageIfExists(titleKey);
		if (title == null) title = Component.translatable(titleKey);

		Component author = messenger.getMessageIfExists(authorKey);
		if (author == null) author = Component.translatable(authorKey);

		FontData fontData = ResourceLib.requireFontData(new NamespacedKey(key.namespace(), "painting/" + key.value()));
		int titleWidth = SpacingUtil.getWidth(title);
		int authorWidth = SpacingUtil.getWidth(author);
		author = Component.textOfChildren(author, titleWidth > authorWidth ? SpacingUtil.getSpacing(titleWidth - authorWidth + 4) : SpacingUtil.getSpacing(4), fontData.icon());

		title = title.colorIfAbsent(NamedTextColor.YELLOW).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
		author = author.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);

		Component size = Component.text(data.getBlockWidth() + "×" + data.getBlockHeight(), NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
		contextBox.addLore(
			title,
			author,
			size
		);

		if (contextBox.getContext().getRootContext().getContextType() != PaintingSwitcher.PAINTING_SWITCHER_CONTEXT) {
			contextBox.addLore(Component.empty());
			contextBox.addLore(messenger.getMessages("paintings.picker.use"));
		}

		contextBox.addHiddenComponents(DataComponentTypes.PAINTING_VARIANT);

		return ModificationResult.OK;
	}

}
