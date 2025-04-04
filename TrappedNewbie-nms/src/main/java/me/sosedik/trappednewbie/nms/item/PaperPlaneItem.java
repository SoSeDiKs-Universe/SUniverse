package me.sosedik.trappednewbie.nms.item;

import me.sosedik.trappednewbie.entity.nms.PaperPlaneImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperPlaneItem extends TridentItem implements ProjectileItem {

	public PaperPlaneItem(Object properties) {
		super(((Properties) properties).stacksTo(16));
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (!(level instanceof ServerLevel serverLevel)) return true;

		int i = this.getUseDuration(stack, entity) - timeLeft;
		float powerForTime = BowItem.getPowerForTime(i);
		if (powerForTime < 0.1) return false;

		Projectile.spawnProjectileFromRotation(PaperPlaneImpl::new, serverLevel, stack.split(1), entity, 0F, powerForTime * 2.2F, 1F);
		return true;
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		return new PaperPlaneImpl(level, pos.x(), pos.y(), pos.z(), stack);
	}

}
