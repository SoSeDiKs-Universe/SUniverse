package me.sosedik.resourcelib.impl.item.nms;

import net.minecraft.world.item.ShearsItem;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AbstractShearsItem extends ShearsItem {

	public AbstractShearsItem(Object properties) {
		super((Properties) properties);
	}

}
