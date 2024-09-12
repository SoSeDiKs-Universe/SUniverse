package me.sosedik.resourcelib.api.item;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.KiterinoItemModifier;
import me.sosedik.resourcelib.ResourceLib;
import org.jetbrains.annotations.NotNull;

public interface FakeableItem extends KiterinoItemModifier {

	@Override
	default void modify(@NotNull ItemContextBox contextBox) {
		FakeItemData fakeItemData = ResourceLib.storage().getFakeItemData(contextBox.getItem().getType().getKey());
		if (fakeItemData == null) return;

		contextBox.setNewMaterial(fakeItemData.clientMaterial());

		Integer cmd = fakeItemData.customModelData();
		if (cmd != null)
			contextBox.getMeta().setCustomModelData(cmd);
	}

}
