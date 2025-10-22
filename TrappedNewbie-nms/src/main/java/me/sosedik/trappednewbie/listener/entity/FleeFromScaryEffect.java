package me.sosedik.trappednewbie.listener.entity;

import com.destroystokyo.paper.entity.ai.PaperGoal;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Boss;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.function.Predicate;

/**
 * Entities flee from scary effect
 */
@NullMarked
public class FleeFromScaryEffect implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof Mob entity)) return;
		if (entity instanceof Boss) return;

		addAvoidGoal(entity, mob -> mob instanceof Player player && player.hasPotionEffect(TrappedNewbieEffects.SCARY));
	}

	private void addAvoidGoal(Mob entity, Predicate<LivingEntity> check) {
		if (!(((CraftMob) entity).getHandle() instanceof PathfinderMob nmsEntity)) return;
		var goal = new AvoidEntityGoal<>(
			nmsEntity,
			net.minecraft.world.entity.LivingEntity.class,
			livingEntity -> check.test(livingEntity.getBukkitLivingEntity()),
			8,
			1.8,
			1.8,
			EntitySelector.NO_CREATIVE_OR_SPECTATOR::test
		);
		Bukkit.getMobGoals().addGoal(entity, 1, new PaperGoal<>(goal));
	}

}
