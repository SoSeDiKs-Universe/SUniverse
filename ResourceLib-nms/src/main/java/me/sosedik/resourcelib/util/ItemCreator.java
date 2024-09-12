package me.sosedik.resourcelib.util;

import me.sosedik.resourcelib.impl.item.nms.AbstractBowItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemCreator {

	public static @NotNull Object bowItem(@NotNull Object properties) {
		return bowItem(properties, null);
	}

	public static @NotNull Object bowItem(@NotNull Object properties, @Nullable AbstractBowItem.BowReleaseLogic bowReleaseLogic) {
		return new AbstractBowItem(properties, bowReleaseLogic);
	}

}
