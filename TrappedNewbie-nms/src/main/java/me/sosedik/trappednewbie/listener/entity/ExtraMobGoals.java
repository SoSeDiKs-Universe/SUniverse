package me.sosedik.trappednewbie.listener.entity;

import com.destroystokyo.paper.entity.ai.PaperGoal;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import me.sosedik.trappednewbie.impl.entity.ai.JumpToTargetGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftBogged;
import org.bukkit.craftbukkit.entity.CraftCaveSpider;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftDrowned;
import org.bukkit.craftbukkit.entity.CraftEnderman;
import org.bukkit.craftbukkit.entity.CraftEndermite;
import org.bukkit.craftbukkit.entity.CraftGoat;
import org.bukkit.craftbukkit.entity.CraftHusk;
import org.bukkit.craftbukkit.entity.CraftIllusioner;
import org.bukkit.craftbukkit.entity.CraftOcelot;
import org.bukkit.craftbukkit.entity.CraftParrot;
import org.bukkit.craftbukkit.entity.CraftPigZombie;
import org.bukkit.craftbukkit.entity.CraftPiglin;
import org.bukkit.craftbukkit.entity.CraftPiglinBrute;
import org.bukkit.craftbukkit.entity.CraftPillager;
import org.bukkit.craftbukkit.entity.CraftPolarBear;
import org.bukkit.craftbukkit.entity.CraftSheep;
import org.bukkit.craftbukkit.entity.CraftSilverfish;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.craftbukkit.entity.CraftStray;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.entity.CraftVillagerZombie;
import org.bukkit.craftbukkit.entity.CraftVindicator;
import org.bukkit.craftbukkit.entity.CraftWanderingTrader;
import org.bukkit.craftbukkit.entity.CraftWitch;
import org.bukkit.craftbukkit.entity.CraftWitherSkeleton;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Some extra entity AI goals
 */
// MCCheck: 1.21.10, new mobs
@NullMarked
public class ExtraMobGoals implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSpawn(EntityAddToWorldEvent event) {
		switch (event.getEntity()) {
			case CraftVillagerZombie entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftWanderingTrader entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftVillager entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftPillager entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftPiglin entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftPiglinBrute entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftVindicator entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftWitherSkeleton entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftSkeleton entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftStray entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftBogged entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftHusk entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftPigZombie entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftDrowned entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftZombie entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftSilverfish entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftEndermite entity ->
				Bukkit.getMobGoals().addGoal(entity, 1, new JumpToTargetGoal(entity));
			case CraftCaveSpider entity ->
				Bukkit.getMobGoals().addGoal(entity, 2, new JumpToTargetGoal(entity));
			case CraftSpider entity ->
				Bukkit.getMobGoals().addGoal(entity, 2, new JumpToTargetGoal(entity));
			case CraftWitch entity ->
				Bukkit.getMobGoals().addGoal(entity, 2, new JumpToTargetGoal(entity));
			case CraftIllusioner entity ->
				Bukkit.getMobGoals().addGoal(entity, 3, new JumpToTargetGoal(entity));
			case CraftCreeper entity ->
				Bukkit.getMobGoals().addGoal(entity, 3, new JumpToTargetGoal(entity));
			case CraftEnderman entity ->
				Bukkit.getMobGoals().addGoal(entity, 3, new JumpToTargetGoal(entity));
			case CraftGoat entity ->
				Bukkit.getMobGoals().addGoal(entity, 3, new JumpToTargetGoal(entity));
			case CraftSheep entity ->
				addGoal(entity, 2, new AvoidEntityGoal<>(entity.getHandle(), Wolf.class, 8F, 1, 1.6));
			case CraftOcelot entity -> {
				addGoal(entity, 2, new NearestAttackableTargetGoal<>(entity.getHandle(), Phantom.class, 10, true, true, null));
				addGoal(entity, 3, new NearestAttackableTargetGoal<>(entity.getHandle(), Creeper.class, 10, true, true, null));
				addGoal(entity, 4, new NearestAttackableTargetGoal<>(entity.getHandle(), Parrot.class, 10, true, true, null));
			}
			case CraftParrot entity ->
				addGoal(entity, 2, new AvoidEntityGoal<>(entity.getHandle(), Ocelot.class, 8F, 1, 5));
			case CraftPolarBear entity ->
				addGoal(entity, 2, new NearestAttackableTargetGoal<>(entity.getHandle(), AbstractFish.class, 10, true, true, null));
			default -> {}
		}
	}

	private void addGoal(Mob entity, int priority, Goal goal) {
		Bukkit.getMobGoals().addGoal(entity, priority, new PaperGoal<>(goal));
	}

}
