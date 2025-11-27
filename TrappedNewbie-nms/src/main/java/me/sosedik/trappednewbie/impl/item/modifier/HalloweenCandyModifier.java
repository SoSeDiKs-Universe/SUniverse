package me.sosedik.trappednewbie.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HalloweenCandyModifier extends ItemModifier {

	public HalloweenCandyModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!TrappedNewbieTags.HALLOWEEN_CANDIES.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		Messenger messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		contextBox.addLore(Mini.combined(Mini.asIcon(Component.text("🎃")), Component.space(), messenger.getMessage("item.modifier.halloween_candy")));

		return ModificationResult.OK;
	}

}
