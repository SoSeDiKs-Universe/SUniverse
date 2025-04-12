package me.sosedik.moves.listener.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.moves.Moves;
import me.sosedik.moves.listener.movement.CrawlingMechanics;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Handles shulker entity responsible for player crawling
 */
@NullMarked
public class ShulkerCrawlerHandler implements Listener {

	private static final Map<UUID, Crawlers> KNOWN_CRAWLERS = new HashMap<>();
	private static final Plugin INSTANCE = Moves.instance();

	@EventHandler
	public void onDespawn(EntityRemoveFromWorldEvent event) {
		Entity entity = event.getEntity();
		Crawlers crawlers = KNOWN_CRAWLERS.remove(entity.getUniqueId());
		if (crawlers == null) return;

		for (LivingEntity livingEntity : crawlers.getEntities()) {
			if (livingEntity == entity) continue;
			if (!livingEntity.isValid()) continue;

			livingEntity.remove();
		}
	}
	
	public static void startCrawling(Player player) {
		ArmorStand holder = getHolder(player);
		Shulker crawler = getCrawler(player, holder);
		ArmorStand helperHolder = getHolder(player);
		Shulker helperCrawler = getCrawler(player, holder);

		var crawlers = new Crawlers(player, holder, crawler, helperHolder, helperCrawler);
		crawlers.getEntities().forEach(entity -> KNOWN_CRAWLERS.put(entity.getUniqueId(), crawlers));
		crawlers.runTasks();
	}

	private static ArmorStand getHolder(Player player) {
		Location loc = player.getLocation().addY(1);
		return loc.getWorld().spawn(loc, ArmorStand.class, as -> {
			as.setPersistent(false);
			as.setSmall(true);
			as.setGravity(false);
			as.setVisible(false);
			as.setCollidable(false);
			as.setInvulnerable(true);
			as.setRemoveWhenFarAway(true);
			as.setCanTick(false);
			as.setMarker(true);
		});
	}

	private static Shulker getCrawler(Player player, ArmorStand stand) {
		Location loc = player.getLocation().addY(1);
		return loc.getWorld().spawn(loc, Shulker.class, shulker -> {
			shulker.setAI(false);
			shulker.setInvisible(true);
			shulker.setPersistent(false);
			shulker.setGravity(false);
			shulker.setInvulnerable(true);
			shulker.setRemoveWhenFarAway(true);
			shulker.setVisibleByDefault(false);
			Objects.requireNonNull(shulker.getAttribute(Attribute.SCALE)).setBaseValue(1.2);
			stand.addPassenger(shulker);
			player.showEntity(INSTANCE, shulker);
		});
	}

	private record Crawlers(
		Player player,
		ArmorStand holder,
		Shulker crawler,
		ArmorStand helperHolder,
		Shulker helperCrawler
	) {

		public List<LivingEntity> getEntities() {
			return List.of(
				this.holder,
				this.crawler,
				this.helperHolder,
				this.helperCrawler
			);
		}

		public void runTasks() {
			runTeleporting();
			runHeaderTeleporting();
			runCrawlerPosValidation();
		}

		private void runTeleporting() {
			Moves.scheduler().sync(task -> {
				if (!this.holder.isValid()) return true;

				Location loc = this.player.getLocation();
				Vector direction = loc.getDirection().setY(0).normalize();
				loc = loc.add(direction.clone().multiply(0.4));
				if (loc.isBlockSame(this.player.getLocation())) {
					this.holder.teleport(loc.addY(MathUtil.getDecimalPartAbs(loc.getY()) >= 0.5 ? 1.1 : 1), TeleportFlag.EntityState.RETAIN_PASSENGERS);
					return false;
				}

				loc = loc.add(direction.clone().multiply(0.2));

				Block blockTo = loc.getBlock();
				double maxY = LocationUtil.getMaxYPoint(blockTo);
				double playerY = MathUtil.getDecimalPartAbs(loc.getY());
				double sub = playerY - maxY;
				if (sub >= 0.5) {
					this.holder.teleport(loc.addY(0.8), TeleportFlag.EntityState.RETAIN_PASSENGERS);
					return false;
				}
				if (sub <= 0.5 && !LocationUtil.isTrulySolid(this.player, blockTo.getRelative(BlockFace.UP))) {
					this.holder.teleport(loc.addY(1.1), TeleportFlag.EntityState.RETAIN_PASSENGERS);
					return false;
				}

				this.holder.teleport(loc.addY(1), TeleportFlag.EntityState.RETAIN_PASSENGERS);
				return false;
			}, 0L, 1L);
		}

		private void runHeaderTeleporting() {
			Moves.scheduler().sync(task -> {
				if (!this.helperHolder.isValid()) return true;

				Location loc = this.player.getLocation();
				Vector direction = loc.getDirection().setY(0).normalize().multiply(1.4).setY(1);
				loc = loc.add(direction);
				this.helperHolder.teleport(loc, TeleportFlag.EntityState.RETAIN_PASSENGERS);

				return false;
			}, 0L, 1L);
		}

		private void runCrawlerPosValidation() {
			Moves.scheduler().sync(task -> {
				if (!CrawlingMechanics.isCrawling(this.player)) {
					getEntities().forEach(Entity::remove);
					return true;
				}

				if (!this.player.isOnline() || this.player.isSwimming() || isInFluid(this.player.getLocation().getBlock())) {
					getEntities().forEach(Entity::remove);
					CrawlingMechanics.standUp(this.player);
					return true;
				}

				return false;
			}, 0L, 3L);
		}

		private boolean isInFluid(Block block) {
			return LocationUtil.isFluid(block)
					&& (LocationUtil.isFluid(block.getRelative(BlockFace.UP)) || LocationUtil.isFluid(block.getRelative(BlockFace.DOWN)));
		}
		
	}
	
}
