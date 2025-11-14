package me.sosedik.trappednewbie.listener.entity;

import com.destroystokyo.paper.entity.ai.PaperGoal;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.utilizer.Utilizer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.entity.CraftTraderLlama;
import org.bukkit.craftbukkit.entity.CraftWanderingTrader;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Limbo is a special place for entities
 */
@NullMarked
public class LimboEntities implements Listener {

	public LimboEntities() {
		Utilizer.limboWorld().getEntities().forEach(this::applyEntityRules);
		Chunk[] loadedChunks = Utilizer.limboWorld().getLoadedChunks();
		for (Chunk chunk : loadedChunks)
			applyTileEntityRules(chunk);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSpawn(EntitiesLoadEvent event) {
		if (event.getWorld() != Utilizer.limboWorld()) return;

		event.getEntities().forEach(this::applyEntityRules);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSpawn(EntitySpawnEvent event) {applyEntityRules(event.getEntity());
		if (event.getEntity().getWorld() != Utilizer.limboWorld()) return;

		applyEntityRules(event.getEntity());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onUnleash(HangingBreakEvent event) {
		if (event.getEntity().getWorld() != Utilizer.limboWorld()) return;
		if (event.getEntity().getType() != EntityType.LEASH_KNOT) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(ChunkLoadEvent event) {
		if (event.getWorld() != Utilizer.limboWorld()) return;

		applyTileEntityRules(event.getChunk());
	}

	private void applyTileEntityRules(Chunk chunk) {
		BlockState[] tileEntities = chunk.getTileEntities();
		for (BlockState state : tileEntities) {
			if (!(state instanceof Beacon beacon)) continue;

			beacon.setLockItem(ItemStack.of(Material.BARRIER));
			beacon.setTierOverride(4);
			beacon.setPrimaryEffect(PotionEffectType.REGENERATION);
			beacon.setSecondaryEffect(PotionEffectType.SPEED);
			beacon.update(true, false);
			beacon.getBlock().tick();
		}
	}

	private void applyEntityRules(Entity entity) {
		if (entity instanceof WanderingTrader wanderingTrader) {
			wanderingTrader.setCanDrinkPotion(false);
			wanderingTrader.setCanDrinkMilk(false);
			wanderingTrader.setCollidable(false);
			wanderingTrader.setRecipes(List.of());

			net.minecraft.world.entity.npc.WanderingTrader nms = ((CraftWanderingTrader) wanderingTrader).getHandle();

			Bukkit.getMobGoals().removeAllGoals(wanderingTrader);
			Bukkit.getMobGoals().addGoal(wanderingTrader, 0, new PaperGoal<>(new FloatGoal(nms)));
			Bukkit.getMobGoals().addGoal(wanderingTrader, 1, new PaperGoal<>(new InteractGoal(nms, Player.class, 3F, 1F)));
			Bukkit.getMobGoals().addGoal(wanderingTrader, 2, new PaperGoal<>(new LookAtPlayerGoal(nms, Mob.class, 8F)));
		} else if (entity instanceof TraderLlama traderLlama) {
			traderLlama.setCollidable(false);

			net.minecraft.world.entity.animal.horse.TraderLlama nms = ((CraftTraderLlama) traderLlama).getHandle();

			Bukkit.getMobGoals().removeAllGoals(traderLlama);
			Bukkit.getMobGoals().addGoal(traderLlama, 0, new PaperGoal<>(new FloatGoal(nms)));
			Bukkit.getMobGoals().addGoal(traderLlama, 1, new PaperGoal<>(new RangedAttackGoal(nms, 1.25, 40, 20F)));
			Bukkit.getMobGoals().addGoal(traderLlama, 2, new PaperGoal<>(new NearestAttackableTargetGoal<>(nms, Player.class, true)));
			Bukkit.getMobGoals().addGoal(traderLlama, 3, new PaperGoal<>(new LookAtPlayerGoal(nms, Player.class, 6F)));
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (!event.isCancelled()) return;
		if (!(event.getEntity() instanceof TraderLlama)) return;
		if (!(event.getTarget() instanceof org.bukkit.entity.Player player)) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(false);
	}

}
