package me.sosedik.requiem.listener.entity;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.tag.EntityTags;
import me.sosedik.requiem.Requiem;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The only thing undead feat is the holy might. And fire, unless...
 */
// MCCheck: 1.21.5, new fire damages
public class UndeadConsecration implements Listener {

	private static final Map<UUID, HealTask> UNDEAD_MOBS = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (!EntityTags.UNDEADS.isTagged(entity.getType())) return;

		UNDEAD_MOBS.put(entity.getUniqueId(), new HealTask(entity));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpawn(EntitiesLoadEvent event) {
		for (Entity entity : event.getEntities()) {
			if (!(entity instanceof LivingEntity livingEntity)) continue;
			if (!EntityTags.UNDEADS.isTagged(livingEntity.getType())) continue;

			UNDEAD_MOBS.put(entity.getUniqueId(), new HealTask(livingEntity));
		}
	}

	@EventHandler
	public void onDespawn(EntitiesUnloadEvent event) {
		for (Entity entity : event.getEntities()) {
			HealTask healTask = UNDEAD_MOBS.get(entity.getUniqueId());
			if (healTask == null) continue;

			healTask.cancel();
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		HealTask healTask = UNDEAD_MOBS.get(event.getEntity().getUniqueId());
		if (healTask == null) return;
		if (healTask.isVulnerable()) return;

		if (isFireDamageCause(event.getCause())) {
			if (event.getEntity().isImmuneToFire()) {
				event.setCancelled(true);
			} else {
				healTask.updateVulnerabilityTime(20);
			}
			return;
		}

		if (event instanceof EntityDamageByEntityEvent damageEvent) {
			if (damageEvent.getDamager() instanceof Golem) return;

			if (damageEvent.getDamager() instanceof Projectile projectile) {
				if (projectile.getFireTicks() > 0 || projectile.isVisualFire()) {
					if (event.getEntity().isImmuneToFire()) {
						event.setCancelled(true);
					} else {
						healTask.updateVulnerabilityTime(20);
					}
					return;
				}
				if (projectile instanceof ThrownPotion thrownPotion) {
					if (thrownPotion.getEffects().stream().anyMatch(potionEffect -> potionEffect.getType() == PotionEffectType.INSTANT_HEALTH)) {
						healTask.updateVulnerabilityTime(20);
						return;
					}
				}
			} else if (damageEvent.getDamager() instanceof LivingEntity damager && damager.getEquipment() != null) {
				ItemStack weapon = damager.getEquipment().getItemInMainHand();
				if (weapon.containsEnchantment(Enchantment.SMITE)) {
					healTask.updateVulnerabilityTime(2);
					return;
				}
			}

		}

		event.setDamage(event.getDamage() * 0.2);
	}

	private boolean isFireDamageCause(EntityDamageEvent.DamageCause cause) {
		return cause == EntityDamageEvent.DamageCause.FIRE
				|| cause == EntityDamageEvent.DamageCause.FIRE_TICK
				|| cause == EntityDamageEvent.DamageCause.LAVA
				|| cause == EntityDamageEvent.DamageCause.HOT_FLOOR;
	}

	private static class HealTask extends BukkitRunnable {

		private static final String INVULNERABILITY_TAG = "undead_invulnerability";

		private final LivingEntity entity;
		private int vulnerabilityTime;

		public HealTask(LivingEntity entity) {
			this.entity = entity;
			this.vulnerabilityTime = NBT.getPersistentData(entity, nbt -> nbt.getOrDefault(INVULNERABILITY_TAG, 0));

			runTaskTimer(Requiem.instance(), 20L, 20L);
		}

		@Override
		public void run() {
			if (!this.entity.isValid()) {
				cancel();
				return;
			}

			if ((this.entity.getFireTicks() > 0 || this.entity.isVisualFire()) && !this.entity.isImmuneToFire()) {
				updateVulnerabilityTime(20);
			}

			if (this.vulnerabilityTime > 0) {
				if (this.entity.isInWaterOrRain()) {
					this.vulnerabilityTime = 0;
				} else {
					this.vulnerabilityTime--;

					vulnerabilityTick();
					return;
				}
			}

			healTick();
		}

		public void vulnerabilityTick() {
			double width = this.entity.getWidth() / 2.5;
			double height = this.entity.getHeight() / 3.5;
			Location loc = this.entity.getEyeLocation().addY(-0.5);
			this.entity.getWorld().spawnParticle(Particle.CRIT, loc, 20, width, height, width, 0.3);
		}

		public void healTick() {
			if (this.entity.getHealth() >= this.entity.getMaxHealth()) return;

			this.entity.setHealth(Math.min(this.entity.getHealth() + 1, this.entity.getMaxHealth()));
		}

		@Override
		public void cancel() {
			UNDEAD_MOBS.remove(this.entity.getUniqueId());

			NBT.modifyPersistentData(this.entity, nbt -> {
				if (this.vulnerabilityTime > 0) nbt.setInteger(INVULNERABILITY_TAG, this.vulnerabilityTime);
				else nbt.removeKey(INVULNERABILITY_TAG);
			});
		}

		public void updateVulnerabilityTime(int time) {
			this.vulnerabilityTime = Math.max(0, Math.max(this.vulnerabilityTime, time));
		}

		public boolean isVulnerable() {
			return this.vulnerabilityTime > 0;
		}

	}

}
