package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.moves.listener.movement.CrawlingMechanics;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import me.sosedik.trappednewbie.listener.world.RainRefillsWaterAndMakesPuddles;
import me.sosedik.utilizer.listener.misc.SneakEmptyHandRightClickCatcher;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Water is drinkable
 */
@NullMarked
public class DrinkableWater implements Listener {

	public static final Color PURE_WATER_COLOR = Color.fromRGB(84, 233, 228);
	public static final Color DIRTY_WATER_COLOR = Color.fromRGB(1, 110, 89);

	private static final Set<UUID> COOLDOWNS = new HashSet<>();

	@EventHandler(ignoreCancelled = true)
	public void onWaterDrink(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!ItemStack.isEmpty(event.getItem())) return;

		Player player = event.getPlayer();
		if (hasCooldown(player)) return;
		if (player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WATER) return;
		if (!player.isSneaking() && !CrawlingMechanics.isCrawling(player)) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		if (!isDrinkable(block)) {
			block = block.getRelative(event.getBlockFace());
			if (block.getType() != Material.WATER) return;
		} else if (block.getType() == Material.WATER_CAULDRON && event.getBlockFace() == BlockFace.DOWN) {
			return;
		}

		if (!drink(player, block, ThirstData.of(block))) return;

		event.setCancelled(true);
		player.swingMainHand();
	}

	@EventHandler(ignoreCancelled = true)
	public void onWaterDrink(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Interaction interaction)) return;

		Player player = event.getPlayer();
		Block block = getDrinkableWaterBlock(player);
		if (block == null) return;
		if (SneakEmptyHandRightClickCatcher.getInteraction(player) != interaction) return;

		event.setCancelled(true);
		if (!drink(player, block, ThirstData.of(block))) return;

		event.setCancelled(true);
		player.swingMainHand();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSneak(PlayerToggleSneakEvent event) {
		if (!event.isSneaking()) return;

		Player player = event.getPlayer();
		TrappedNewbie.scheduler().sync(task -> {
			if (!player.isSneaking()) return true;
			if (!player.isOnline()) return true;

			Interaction interaction = SneakEmptyHandRightClickCatcher.getInteraction(player);
			if (interaction == null) return true;

			SneakEmptyHandRightClickCatcher.markInteractor(interaction, "drinkable_water", getDrinkableWaterBlock(player) == null);

			return false;
		}, 1L, 1L);
	}

	private @Nullable Block getDrinkableWaterBlock(Player player) {
		if (hasCooldown(player)) return null;

		var thirstyPlayer = ThirstyPlayer.of(player);
		if (thirstyPlayer.hasFullThirst()) return null;

		boolean crawling = CrawlingMechanics.isCrawling(player);
		boolean sneaking = player.isSneaking();
		if (!crawling && !sneaking) return null;

		if (player.getLocation().addY(1).getBlock().getType() == Material.WATER) return null;

		RayTraceResult rayTraceResult = player.rayTraceBlocks(4, FluidCollisionMode.ALWAYS);
		if (rayTraceResult == null) return null;
		if (rayTraceResult.getHitEntity() != null) return null;

		Block block = rayTraceResult.getHitBlock();
		if (block == null) return null;
		if (block.getType() != Material.WATER) return null;

		if (player.rayTraceBlocks(EntityUtil.PLAYER_REACH) != null) return null; // Block interact will catch it anyway

		return block;
	}

	public static boolean hasCooldown(Player player) {
		return COOLDOWNS.contains(player.getUniqueId());
	}

	public static void triggerCooldown(Player player, int cooldown) {
		UUID uuid = player.getUniqueId();
		COOLDOWNS.add(uuid);
		TrappedNewbie.scheduler().sync(() -> COOLDOWNS.remove(uuid), cooldown);
	}

	public static boolean isDrinkable(Block block) {
		if (block.getType() == Material.WATER) return true;
		if (block.getType() == Material.WATER_CAULDRON) return true;
		if (block.getType() == Material.WET_SPONGE) return true;
		if (block.getBlockData() instanceof Waterlogged waterlogged) return waterlogged.isWaterlogged();
		return false;
	}

	public static boolean drink(Player player, Block block, ThirstData thirstData) {
		var thirstyPlayer = ThirstyPlayer.of(player);
		if (thirstyPlayer.hasFullThirst()) return false;
		if (!decreaseWater(block, false)) return false;

		ThirstyPlayer.of(player).addThirst(thirstData);
		player.emitSound(Sound.AMBIENT_UNDERWATER_EXIT, 0.5F, 2F);
		player.getWorld().spawnParticle(Particle.SPLASH, block.getLocation().center(1.1), 5);
		triggerCooldown(player, 15);

		return true;
	}

	/**
	 * Decreases water level for watery blocks
	 *
	 * @param block block
	 * @param pickup whether this action is supposed to pick up the water
	 * @see LocationUtil#isWatery(Block)
	 * @return whether water was decreased
	 */
	public static boolean decreaseWater(Block block, boolean pickup) {
		if (RainRefillsWaterAndMakesPuddles.isWaterPuddle(block)) {
			if (!pickup && Math.random() > 0.8) return true;

			block.setType(Material.AIR);
			RainRefillsWaterAndMakesPuddles.removePuddle(block);
			return true;
		}

		if (block.getBlockData() instanceof Waterlogged waterlogged) {
			if (!waterlogged.isWaterlogged()) return false;
			if (!pickup && Math.random() > 0.2) return true;

			waterlogged.setWaterlogged(false);
			block.setBlockData(waterlogged);
			return true;
		}

		if (block.getType() == Material.WATER_CAULDRON && block.getBlockData() instanceof Levelled levelled) {
			if (!pickup && Math.random() > 0.2) return true;

			if (levelled.getLevel() == 1) {
				block.setType(Material.CAULDRON);
			} else {
				levelled.setLevel(levelled.getLevel() - 1);
				block.setBlockData(levelled);
			}
			return true;
		}

		if (block.getType() == Material.WATER && block.getBlockData() instanceof Levelled levelled) {
			if (!pickup && Math.random() > 0.2) return true;

			int currentLevel = levelled.getLevel();
			if (currentLevel < 8 && currentLevel > 0) {
				if (currentLevel == 7) {
					block.setType(Material.AIR);
					RainRefillsWaterAndMakesPuddles.removePuddle(block);
				} else {
					levelled.setLevel(levelled.getLevel() + 1);
					block.setBlockData(levelled);
				}
			}
			return true;
		}
		
		if (block.getType() == Material.WET_SPONGE) {
			if (!pickup && Math.random() > 0.8) return true;

			block.setType(Material.SPONGE);
			return true;
		}

		return false;
	}

	/**
	 * Increases water level for watery blocks
	 *
	 * @param block block
	 * @see LocationUtil#isWatery(Block)
	 * @return whether water was decreased
	 */
	public static boolean increaseWater(Block block) {
		if (block.getBlockData() instanceof Waterlogged waterlogged) {
			if (waterlogged.isWaterlogged()) return false;

			waterlogged.setWaterlogged(true);
			block.setBlockData(waterlogged);
			return true;
		}

		if (block.getType() == Material.CAULDRON) {
			block.setType(Material.WATER_CAULDRON);
			return true;
		}

		if (block.getType() == Material.WATER_CAULDRON) {
			if (!(block.getBlockData() instanceof Levelled levelled)) return false;
			if (levelled.getLevel() == levelled.getMaximumLevel()) return false;

			levelled.setLevel(levelled.getLevel() + 1);
			block.setBlockData(levelled);
			return true;
		}

		if (block.getType() == Material.SPONGE) {
			block.setType(Material.WET_SPONGE);
			return true;
		}

		return false;
	}

}
