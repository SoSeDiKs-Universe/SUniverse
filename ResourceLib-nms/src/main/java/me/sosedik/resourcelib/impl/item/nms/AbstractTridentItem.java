package me.sosedik.resourcelib.impl.item.nms;

import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class AbstractTridentItem extends TridentItem {

	private final @Nullable TridentReleaseLogic tridentReleaseLogic;

	public AbstractTridentItem(Object properties, @Nullable TridentReleaseLogic tridentReleaseLogic) {
		super((Properties) properties);
		this.tridentReleaseLogic = tridentReleaseLogic;
	}

	@Override
	public boolean releaseUsing(net.minecraft.world.item.ItemStack stack, Level world, net.minecraft.world.entity.LivingEntity user, int remainingUseTicks) {
		if (tridentReleaseLogic == null || !tridentReleaseLogic.onRelease(stack.asBukkitMirror(), user.getBukkitLivingEntity(), remainingUseTicks))
			return super.releaseUsing(stack, world, user, remainingUseTicks);
		return true;
	}

	public interface TridentReleaseLogic {

		boolean onRelease(ItemStack item, LivingEntity entity, int remainingUseTicks);

	}

}
