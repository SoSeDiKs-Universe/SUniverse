package me.sosedik.requiem.listener.player.possessed;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import de.tr7zw.nbtapi.NBT;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.api.event.player.PlayerPossessedCuredEvent;
import me.sosedik.requiem.api.event.player.PlayerPossessedTransformEvent;
import me.sosedik.requiem.api.event.player.PlayerStartPossessingEntityEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Undead zombies can be cured when possessed
 */
public class PossessedUndeadZombieCuring implements Listener {

	private static final int CONVERSION_TICKS_MIN = 3600;
	private static final int CONVERSION_TICKS_LIUE = 6000 - CONVERSION_TICKS_MIN + 1;
	private static final Random RANDOM = new Random();
	private static final String CONVERSION_TAG = "conversion_curing";

	private static final Map<UUID, CureTask> CURE_TASKS = new HashMap<>();

	@EventHandler
	public void onLoad(PlayerStartPossessingEntityEvent event) {
		LivingEntity entity = event.getEntity();
		if (!NBT.getPersistentData(entity, nbt -> nbt.hasTag(CONVERSION_TAG))) return;
		if (CURE_TASKS.containsKey(entity.getUniqueId())) return;

		CURE_TASKS.put(entity.getUniqueId(), new CureTask(entity));
	}

	@EventHandler
	public void onDespawn(EntityRemoveFromWorldEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		CureTask cureTask = CURE_TASKS.remove(entity.getUniqueId());
		if (cureTask == null) return;

		cureTask.cancel();
	}

	@EventHandler
	public void onTransform(PlayerPossessedTransformEvent event) {
		LivingEntity entity = event.getEntity();
		if (!NBT.getPersistentData(entity, nbt -> nbt.hasTag(CONVERSION_TAG))) return;

		CureTask cureTask = CURE_TASKS.get(entity.getUniqueId());
		if (cureTask == null) return;

		cureTask.updateEntity(event.getTransformed());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() != Material.GOLDEN_APPLE) return;

		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;
		if (!player.hasPotionEffect(PotionEffectType.WEAKNESS)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;
		if (!canBeCured(entity)) return;
		if (CURE_TASKS.containsKey(entity.getUniqueId())) return;

		player.removePotionEffect(PotionEffectType.WEAKNESS);
		CURE_TASKS.put(entity.getUniqueId(), new CureTask(entity));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
		if (!canBeCured(entity)) return;
		if (CURE_TASKS.containsKey(entity.getUniqueId())) return;

		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() != Material.GOLDEN_APPLE) return;

		Player rider = entity.getRider();
		if (rider == null) return;
		if (player == rider) return;
		if (!PossessingPlayer.isPossessing(rider)) return;
		if (!rider.hasPotionEffect(PotionEffectType.WEAKNESS)) return;

		event.setCancelled(true);
		player.swingMainHand();
		if (!player.getGameMode().isInvulnerable())
			item.subtract();

		rider.removePotionEffect(PotionEffectType.WEAKNESS);
		CURE_TASKS.put(entity.getUniqueId(), new CureTask(entity));
	}

	private boolean canBeCured(LivingEntity entity) {
		return entity instanceof Zombie;
	}

	private static final class CureTask extends BukkitRunnable {

		private LivingEntity entity;
		private int cureTicks;

		public CureTask(LivingEntity entity) {
			this.entity = entity;
			this.cureTicks = NBT.getPersistentData(entity, nbt -> {
				if (nbt.hasTag(CONVERSION_TAG))
					return nbt.getInteger(CONVERSION_TAG);
				return CONVERSION_TICKS_MIN + RANDOM.nextInt(CONVERSION_TICKS_LIUE);
			});

			runTaskTimer(Requiem.instance(), 1L, 1L);
		}

		@Override
		public void run() {
			if (shouldAbort()) {
				cancel();
				return;
			}

			cureTicks -= calcProgress();
			if (cureTicks <= 0) {
				cancel();
				cure();
			}
		}

		// Mimics vanilla mechanic
		private int calcProgress() {
			int progress = 1;
			if (RANDOM.nextFloat() < 0.01F) {
				int hits = 0;
				Location loc = this.entity.getLocation();
				for (int x = loc.getBlockX() - 4; x < loc.getBlockX() + 4 && hits < 14; x++) {
					for (int y = loc.getBlockY() - 4; y < loc.getBlockY() + 4 && hits < 14; y++) {
						for (int z = loc.getBlockZ() - 4; z < loc.getBlockZ() + 4 && hits < 14; z++) {
							Block block = loc.getWorld().getBlockAt(x, y, z);
							if (block.getType() == Material.IRON_BARS || Tag.BEDS.isTagged(block.getType())) {
								if (RANDOM.nextFloat() < 0.3F) {
									progress++;
								}

								hits++;
							}
						}
					}
				}
			}
			return progress;
		}

		private boolean shouldAbort() {
			if (!this.entity.isValid()) return true;

			Player rider = this.entity.getRider();
			if (rider == null) return true;
			if (!PossessingPlayer.isPossessing(rider)) return true;

			if (!rider.hasPotionEffect(PotionEffectType.STRENGTH)) {
				rider.addPotionEffect(PotionEffectType.STRENGTH.createEffect(this.cureTicks, 0));
			}

			return false;
		}

		private void cure() {
			Player rider = this.entity.getRider();
			if (rider == null) return; // Huh??

			Collection<PotionEffect> effects = rider.getActivePotionEffects();
			PossessingPlayer.stopPossessing(rider, this.entity, false);
			this.entity.remove();
			GhostyPlayer.clearGhost(rider);
			rider.addPotionEffects(effects);
			rider.addPotionEffect(PotionEffectType.NAUSEA.createEffect(20 * 10, 0));

			new PlayerPossessedCuredEvent(rider, this.entity).callEvent();
		}

		@Override
		public void cancel() {
			CURE_TASKS.remove(this.entity.getUniqueId());
			NBT.modifyPersistentData(this.entity, nbt -> {
				if (this.cureTicks > 0)
					nbt.setInteger(CONVERSION_TAG, this.cureTicks);
				else
					nbt.removeKey(CONVERSION_TAG);
			});
		}

		public void updateEntity(LivingEntity entity) {
			this.entity = entity;
		}

	}

}
