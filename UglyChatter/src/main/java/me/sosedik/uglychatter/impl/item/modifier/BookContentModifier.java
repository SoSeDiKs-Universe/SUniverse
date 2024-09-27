package me.sosedik.uglychatter.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.uglychatter.api.chat.FancyRendererTag;
import me.sosedik.uglychatter.listener.misc.BookBeautifier;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

public class BookContentModifier extends ItemModifier {

	public BookContentModifier(@NotNull NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleName()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		if (!(contextBox.getMeta() instanceof BookMeta meta)) return ModificationResult.PASS;

		boolean render = contextBox.getItem().getType() == Material.WRITTEN_BOOK;
		FancyRendererTag[] tags = render ? new FancyRendererTag[0] : new FancyRendererTag[] { FancyRendererTag.SKIP_MARKDOWN, FancyRendererTag.SKIP_PLACEHOLDERS };
		BookBeautifier.updateMeta(player, meta, render, tags);

		return ModificationResult.OK;
	}

}
