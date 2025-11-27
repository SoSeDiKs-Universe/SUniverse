package me.sosedik.moves.listener.block;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.moves.api.event.PlayerResetFallingEvent;
import me.sosedik.moves.api.event.PlayerStopFallingEvent;
import me.sosedik.utilizer.listener.misc.DelayedActions;
import me.sosedik.utilizer.util.DelayedAction;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.NbtProxies;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * Falling in water puddle hurts
 */
@NullMarked
public class WaterPuddleHurts implements Listener {

	private static final String WATER_PUDDLE_DELAYED_ACTION_ID = "water_puddle_fall";

	public WaterPuddleHurts() {
		DelayedActions.registerDelayedAction(WATER_PUDDLE_DELAYED_ACTION_ID, WaterPuddleFallDelayedAction::new);
	}

	@EventHandler
	public void onFallInWater(PlayerStopFallingEvent event) {
		Player player = event.getPlayer();
		float fallDistance = event.getFallDistance();
		if (isExempt(player, fallDistance)) return;

		Block block = player.getLocation().getBlock();
		checkWaterFallDamage(player, block, fallDistance);
	}

	// For some reason vanilla randomly resets fall distance before touching water
	@EventHandler
	public void onFallInWater(PlayerResetFallingEvent event) {
		Player player = event.getPlayer();
		float fallDistance = event.getFallDistance();
		if (isExempt(player, fallDistance)) return;

		Block block = player.getLocation().getBlock();
		if (block.isEmpty())
			block = block.getRelative(BlockFace.DOWN); // Sometimes it resets even higher, but ¯\_(ツ)_/¯
		checkWaterFallDamage(player, block, fallDistance);
	}

	private boolean isExempt(Player player, float fallDistance) {
		return player.getGameMode().isInvulnerable()
			|| fallDistance < 3.4F
			|| player.getInventory().getBoots().hasEnchant(Enchantment.FEATHER_FALLING);
	}

	private void checkWaterFallDamage(Player player, Block block, float fallDistance) {
		if (!isWaterHurtLoc(player, block)) return;

		Entity causingDamager = EntityUtil.getCausingDamager(player);
		Entity directDamager = EntityUtil.getDirectDamager(player);
		if (causingDamager == null) causingDamager = player;
		if (directDamager == null) directDamager = player;

		Location damageLoc = block.getLocation().center(0);

		double damage = fallDistance / 2;
		// We haven't touched the bottom yet, delay damage a bit to somewhat "sync" visuals
		var action = new WaterPuddleFallDelayedAction(player, damage, damageLoc, causingDamager, directDamager);
		DelayedActions.scheduleAction(player, action, 2);
	}

	private static boolean isWaterHurtLoc(Player player, Block block) {
		return LocationUtil.isWatery(block)
			&& !LocationUtil.isWatery(block.getRelative(BlockFace.UP))
			&& LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.DOWN));
	}

	private static class WaterPuddleFallDelayedAction extends DelayedAction {

		private static final String DAMAGE_TAG = "damage";
		private static final String DAMAGE_LOCATION_TAG = "location";
		private static final String DAMAGER = "damager";
		private static final String DIRECT_DAMAGER = "direct_damager";

		private final Player player;
		private final double damage;
		private final Location damageLoc;
		private final Entity damager;
		private final Entity directDamager;

		public WaterPuddleFallDelayedAction(Player player, double damage, Location damageLoc, Entity damageCauser, Entity directDamager) {
			super(WATER_PUDDLE_DELAYED_ACTION_ID, null);
			this.player = player;
			this.damage = damage;
			this.damageLoc = damageLoc;
			this.damager = damageCauser;
			this.directDamager = directDamager;
		}

		public WaterPuddleFallDelayedAction(Player player, ReadableNBT data) {
			super(WATER_PUDDLE_DELAYED_ACTION_ID, data);
			this.player = player;
			this.damage = data.getOrDefault(DAMAGE_TAG, 0);
			this.damageLoc = getDamageLoc(data);
			this.damager = getDamager(DAMAGER, data);
			this.directDamager = getDamager(DIRECT_DAMAGER, data);
		}

		private Location getDamageLoc(ReadableNBT data) {
			Location loc = data.get(DAMAGE_LOCATION_TAG, NbtProxies.LOCATION);
			return loc == null ? this.player.getLocation() : loc;
		}

		private Entity getDamager(String key, ReadableNBT data) {
			UUID uuid = data.getUUID(key);
			if (uuid == null) return this.player;

			Entity entity = Bukkit.getEntity(uuid);
			return entity == null ? this.player : entity;
		}

		@Override
		public void tick() {
			if (this.ticksLeft != 1) return;
			if (isWaterHurtLoc(this.player, this.damageLoc.getBlock())) return;

			abortExecution(this.player);
		}

		@Override
		public void execute() {
			if (!this.player.isValid()) return;

			var damageSource = DamageSource.builder(DamageType.FALL)
				.withCausingEntity(this.damager)
				.withDirectEntity(this.directDamager)
				.withDamageLocation(this.damageLoc)
				.build();

			this.player.damage(this.damage, damageSource);
		}

		@Override
		public ReadWriteNBT save() {
			ReadWriteNBT nbt = NBT.createNBTObject();
			nbt.setDouble(DAMAGE_TAG, this.damage);
			nbt.set(DAMAGE_LOCATION_TAG, this.damageLoc, NbtProxies.LOCATION);
			if (this.damager != this.player) nbt.setUUID(DAMAGER, this.damager.getUniqueId());
			if (this.directDamager != this.player) nbt.setUUID(DIRECT_DAMAGER, this.directDamager.getUniqueId());
			return nbt;
		}


	}

}
