package me.sosedik.resourcelib.impl.item.nms;

import net.minecraft.world.item.ShearsItem;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AbstractShearstItem extends ShearsItem {

	public AbstractShearstItem(Object properties) {
		super((Properties) properties);
	}

}
