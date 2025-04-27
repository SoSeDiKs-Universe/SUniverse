package me.sosedik.resourcelib.impl.item.nms;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.level.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class AbstractCrossbowItem extends CrossbowItem {

	private final @Nullable CrossbowReleaseLogic crossbowReleaseLogic;

	public AbstractCrossbowItem(Object properties, @Nullable CrossbowReleaseLogic crossbowReleaseLogic) {
		super((Properties) properties);
		this.crossbowReleaseLogic = crossbowReleaseLogic;
	}

	@Override
	public boolean releaseUsing(net.minecraft.world.item.ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity, int timeLeft) {
		if (crossbowReleaseLogic == null || !crossbowReleaseLogic.onRelease(stack.asBukkitMirror(), entity.getBukkitLivingEntity(), timeLeft))
			return super.releaseUsing(stack, level, entity, timeLeft);
		return true;
	}

	public interface CrossbowReleaseLogic {

		boolean onRelease(ItemStack item, LivingEntity entity, int timeLeft);

	}

}
