package me.sosedik.resourcelib.impl.item.nms;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbstractBowItem extends BowItem  {

	private final @Nullable BowReleaseLogic bowReleaseLogic;

	public AbstractBowItem(@NotNull Object properties, @Nullable BowReleaseLogic bowReleaseLogic) {
		super((Item.Properties) properties);
		this.bowReleaseLogic = bowReleaseLogic;
	}

	@Override
	public boolean releaseUsing(net.minecraft.world.item.ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity, int timeLeft) {
		if (bowReleaseLogic == null || !bowReleaseLogic.onRelease(stack.asBukkitMirror(), entity.getBukkitLivingEntity(), timeLeft))
			return super.releaseUsing(stack, level, entity, timeLeft);
		return true;
	}

	public interface BowReleaseLogic {

		boolean onRelease(@NotNull ItemStack item, @NotNull LivingEntity entity, int timeLeft);

	}

}
