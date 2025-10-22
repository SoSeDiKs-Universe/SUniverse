package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.miscme.impl.item.modifier.BookAuthorOnlineModifier;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.listener.misc.CandiesDropOnHalloween;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class GoodieBagModifier extends ItemModifier {

	public GoodieBagModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!TrappedNewbieTags.GOODIE_BAGS.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		UUID looterUuid = NBT.get(item, nbt -> {
			if (!nbt.hasTag(CandiesDropOnHalloween.LOOTER_TAG)) return null;
			return nbt.getUUID(CandiesDropOnHalloween.LOOTER_TAG);
		});
		if (looterUuid == null) return ModificationResult.PASS;

		Player viewer = contextBox.getViewer();

		Messenger messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));

		if (viewer != null && looterUuid.equals(viewer.getUniqueId())) {
			contextBox.addLore(messenger.getMessage("item.modifier.goodie_bag"));
		} else {
			Component status = BookAuthorOnlineModifier.getStatus(looterUuid);
			if (status == null) return ModificationResult.PASS;

			contextBox.addLore(messenger.getMessage("item.modifier.goodie_bag.looter", Mini.raw("player", status)));
		}

		return ModificationResult.OK;
	}

}
