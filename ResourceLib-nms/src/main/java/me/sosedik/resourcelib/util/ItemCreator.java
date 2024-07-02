package me.sosedik.resourcelib.util;

import me.sosedik.kiterino.world.item.KiterinoNMSItem;
import me.sosedik.resourcelib.impl.item.nms.AbstractBowItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static me.sosedik.kiterino.world.item.KiterinoItem.nmsItem;

public class ItemCreator {

	public static @NotNull Supplier<@NotNull KiterinoNMSItem> bowItem() {
		return bowItem(null);
	}

	public static @NotNull Supplier<@NotNull KiterinoNMSItem> bowItem(@Nullable AbstractBowItem.BowReleaseLogic bowReleaseLogic) {
		return nmsItem(AbstractBowItem.class, bowReleaseLogic);
	}

}
