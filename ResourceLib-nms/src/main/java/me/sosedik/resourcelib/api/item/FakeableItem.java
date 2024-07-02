package me.sosedik.resourcelib.api.item;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.world.item.KiterinoNMSItem;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.FakeItemData;
import org.jetbrains.annotations.NotNull;

public interface FakeableItem extends KiterinoNMSItem {

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
