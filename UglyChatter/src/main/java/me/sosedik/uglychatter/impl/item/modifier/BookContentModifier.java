package me.sosedik.uglychatter.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WritableBookContent;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.uglychatter.listener.misc.BookBeautifier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BookContentModifier extends ItemModifier {

	public BookContentModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleName()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (item.hasData(DataComponentTypes.WRITTEN_BOOK_CONTENT)) {
			WrittenBookContent writtenBookContent = item.getData(DataComponentTypes.WRITTEN_BOOK_CONTENT);
			assert writtenBookContent != null;
			WrittenBookContent newContent = BookBeautifier.updateContent(player, writtenBookContent);
			if (newContent != null) {
				item.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT, newContent);
				return ModificationResult.OK;
			}
		} else if (item.hasData(DataComponentTypes.WRITABLE_BOOK_CONTENT)) {
			WritableBookContent writableBookContent = item.getData(DataComponentTypes.WRITABLE_BOOK_CONTENT);
			assert writableBookContent != null;
			WritableBookContent newContent = BookBeautifier.updateContent(writableBookContent);
			if (newContent != null) {
				item.setData(DataComponentTypes.WRITABLE_BOOK_CONTENT, newContent);
				return ModificationResult.OK;
			}
		}

		return ModificationResult.PASS;
	}

}
