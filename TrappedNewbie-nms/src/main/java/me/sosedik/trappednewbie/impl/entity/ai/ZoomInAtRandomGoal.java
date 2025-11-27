package me.sosedik.trappednewbie.impl.entity.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Material;
import org.bukkit.entity.Pillager;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;

@NullMarked
public class ZoomInAtRandomGoal implements Goal<Pillager> {

	public static final GoalKey<Pillager> ZOOM_IN = GoalKey.of(Pillager.class, TrappedNewbie.trappedNewbieKey("zoom_in"));

	private final Pillager pillager;
	private int zoomInTicks;

	public ZoomInAtRandomGoal(Pillager pillager) {
		this.pillager = pillager;
	}

	@Override
	public boolean shouldActivate() {
		return this.pillager.getTarget() == null
			&& Math.random() < 0.1
			&& this.pillager.getEquipment().getItemInOffHand().getType() == Material.SPYGLASS;
	}

	@Override
	public boolean shouldStayActive() {
		return this.zoomInTicks > 0 && this.pillager.getTarget() == null;
	}

	@Override
	public void start() {
		this.zoomInTicks = 100;
		this.pillager.startUsingItem(EquipmentSlot.OFF_HAND);
	}

	@Override
	public void tick() {
		this.zoomInTicks--;
	}

	@Override
	public void stop() {
		this.pillager.completeUsingActiveItem();
	}

	@Override
	public GoalKey<Pillager> getKey() {
		return ZOOM_IN;
	}

	@Override
	public EnumSet<GoalType> getTypes() {
		return EnumSet.of(GoalType.TARGET);
	}

}
