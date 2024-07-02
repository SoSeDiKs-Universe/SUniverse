package me.sosedik.resourcelib.impl.item.nms;

import me.sosedik.kiterino.world.item.KiterinoNMSItem;
import me.sosedik.resourcelib.api.item.FakeableItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbstractBowItem extends BowItem implements KiterinoNMSItem, FakeableItem {

	private final @Nullable BowReleaseLogic bowReleaseLogic;

	public AbstractBowItem(@Nullable BowReleaseLogic bowReleaseLogic) {
		super(new Item.Properties());
		this.bowReleaseLogic = bowReleaseLogic;
	}

	@Override
	public void releaseUsing(net.minecraft.world.item.ItemStack stack, Level world, net.minecraft.world.entity.LivingEntity user, int remainingUseTicks) {
		if (bowReleaseLogic == null || !bowReleaseLogic.onRelease(stack.asBukkitMirror(), user.getBukkitLivingEntity(), remainingUseTicks))
			super.releaseUsing(stack, world, user, remainingUseTicks);
	}

	public interface BowReleaseLogic {

		boolean onRelease(@NotNull ItemStack item, @NotNull LivingEntity entity, int remainingUseTicks);

	}

}
