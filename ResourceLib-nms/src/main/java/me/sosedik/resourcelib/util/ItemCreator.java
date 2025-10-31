package me.sosedik.resourcelib.util;

import me.sosedik.resourcelib.impl.item.nms.AbstractBowItem;
import me.sosedik.resourcelib.impl.item.nms.AbstractCrossbowItem;
import me.sosedik.resourcelib.impl.item.nms.AbstractShearsItem;
import me.sosedik.resourcelib.impl.item.nms.AbstractTridentItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ItemCreator {

	public static Object bowItem(Object properties) {
		return bowItem(properties, null);
	}

	public static Object bowItem(Object properties, AbstractBowItem.@Nullable BowReleaseLogic bowReleaseLogic) {
		return new AbstractBowItem(properties, bowReleaseLogic);
	}

	public static Object crossbowItem(Object properties) {
		return crossbowItem(properties, null);
	}

	public static Object crossbowItem(Object properties, AbstractCrossbowItem.@Nullable CrossbowReleaseLogic crossbowReleaseLogic) {
		return new AbstractCrossbowItem(properties, crossbowReleaseLogic);
	}

	public static Object tridentItem(Object properties) {
		return tridentItem(properties, null);
	}

	public static Object tridentItem(Object properties, AbstractTridentItem.@Nullable TridentReleaseLogic tridentReleaseLogic) {
		return new AbstractTridentItem(properties, tridentReleaseLogic);
	}

	public static Object shearsItem(Object properties) {
		return new AbstractShearsItem(((Item.Properties) properties).component(DataComponents.TOOL, ShearsItem.createToolProperties()));
	}

}
