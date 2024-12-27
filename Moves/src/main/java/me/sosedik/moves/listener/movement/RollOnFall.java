package me.sosedik.moves.listener.movement;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.utilizer.listener.misc.DelayedActions;
import me.sosedik.utilizer.util.DelayedAction;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Roll on fall to decrease damage
 */
@NullMarked
public class RollOnFall implements Listener {

	private static final String ROLL_DELAYED_ACTION_ID = "fall_roll";
	private static final int[] ROLL_PITCHES = new int[]{3, 7, 10, 15, 25, 20, -95, 15, 15, 10};
	private static final float[] ROLL_VELOCITY = new float[]{.1F, .1F, .1F, .1F, .14F, .14F, .14F, .2F, .2F, .2F, .3F};

	public RollOnFall() {
		DelayedActions.registerDelayedAction(ROLL_DELAYED_ACTION_ID, RollDelayedAction::new);
	}

	@EventHandler(ignoreCancelled = true)
	public void onLand(EntityDamageEvent event) {
		if (event.getDamageSource().getDamageType() != DamageType.FALL) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!player.isSneaking()) return;
		if (FreeFall.isLeaping(player)) return;
		if (SneakCounter.getSneaksCount(player) != 1) return;
		if (SneakCounter.getTimeSinceLastSneak(player) > 2) return; // Yea, velocity is a weird thing… very fast :/

		Location loc = player.getLocation();

		if (player.getFallDistance() < 6) {
			event.setDamage(0);
		} else {
			Block block = player.getSupportingBlock();
			if (block == null) return;

			minifyBlockDamage(player, block, event);
		}

		if (event.getFinalDamage() <= 0)
			event.setCancelled(true);

		CrawlingMechanics.crawl(player);
		Vector dir = loc.getDirection().setY(0).multiply(.1F);
		loc.setPitch(0F);
		player.teleport(loc);
		player.setVelocity(dir);

		var action = new RollDelayedAction(player, null);
		DelayedActions.scheduleAction(player, action, ROLL_VELOCITY.length + 1);
	}

	private void minifyBlockDamage(Player player, Block block, EntityDamageEvent event) {
		Material blockType = block.getType();
		if (blockType == Material.SNOW_BLOCK
			|| blockType == Material.POWDER_SNOW
			|| blockType == Material.SNOW
			|| Tag.WOOL.isTagged(blockType)
			|| Tag.BEDS.isTagged(blockType)
			|| Tag.LEAVES.isTagged(blockType)
		) {
			event.setDamage(player.getFallDistance() < 16 ? 0 : event.getFinalDamage() / 2);
		} else if (blockType == Material.HAY_BLOCK) {
			event.setDamage(player.getFallDistance() < 24 ? 0 : event.getFinalDamage() / 3);
		} else if (Tag.SAND.isTagged(blockType)) {
			event.setDamage(player.getFallDistance() < 8 ? 0 : event.getFinalDamage() / 1.5);
		} else {
			event.setDamage(event.getFinalDamage() * 0.7);
		}
	}

	private static class RollDelayedAction extends DelayedAction {

		private final Player player;

		public RollDelayedAction(Player player, @Nullable ReadableNBT data) {
			super(ROLL_DELAYED_ACTION_ID, data);
			this.player = player;
		}

		@Override
		public void tick() {
			int value = ROLL_VELOCITY.length - this.ticksLeft;
			if (value >= ROLL_VELOCITY.length) value = ROLL_VELOCITY.length - 1;

			this.player.setFireTicks(this.player.getFireTicks() - 20);
			Location loc = this.player.getLocation();
			Vector dir = loc.getDirection().setY(0).multiply(ROLL_VELOCITY[value]).setY(0.1); // Extra Y makes the player roll from edges while sneaking
			if (value < ROLL_PITCHES.length) loc.setPitch(loc.getPitch() + ROLL_PITCHES[value]);
			this.player.teleport(loc);
			this.player.setVelocity(dir);
			loc.addY(0.02);
			loc.getWorld().spawnParticle(Particle.DUST, loc, 2, 0.2, 0.03, 0.02, 0.01, new Particle.DustOptions(Color.GRAY, 2F));
		}

		@Override
		public void execute() {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, false, false, false));
			if (CrawlingMechanics.shouldStandUp(player))
				CrawlingMechanics.standUp(player);
		}

		@Override
		public ReadWriteNBT save() {
			return NBT.createNBTObject();
		}

	}

}
