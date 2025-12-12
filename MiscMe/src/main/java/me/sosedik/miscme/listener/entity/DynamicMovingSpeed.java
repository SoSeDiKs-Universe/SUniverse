package me.sosedik.miscme.listener.entity;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.util.MetadataUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Change player's speed depending on blocks below
 */
// MCCheck: 1.21.11, new blocks
@NullMarked
public class DynamicMovingSpeed implements Listener {

	private static final String BLOCK_SPEED_META = "block_speed";
	private static final NamespacedKey MODIFIER_KEY = MiscMe.miscMeKey("block_speed");
	private static final Map<Material, Float> SPEED_MODIFIERS = new HashMap<>();

	static {
		addModifier(-0.1F, Material.MUD);
		addModifier(-0.7F, Material.SNOW);
		addModifier(-0.03F, Material.SNOW_BLOCK);
		addModifier(-0.02F, Material.FARMLAND, Material.GRAVEL, Material.SUSPICIOUS_GRAVEL);
		addModifier(-0.02F, Tag.SAND.getValues());
		addModifier(0.02F, Tag.WOOL.getValues());
		addModifier(0.02F, Tag.WOOL_CARPETS.getValues());
		addModifier(0.02F, Tag.PLANKS.getValues());
		addModifier(0.02F, Tag.WOODEN_STAIRS.getValues());
		addModifier(0.02F, Tag.WOODEN_SLABS.getValues());
		addModifier(0.03F, Material.ICE, Material.BLUE_ICE, Material.PACKED_ICE, Material.FROSTED_ICE);
		addModifier(0.05F, Material.MAGMA_BLOCK);
		addModifier(0.07F,
			Material.SMOOTH_SANDSTONE, Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_STONE, Material.SMOOTH_QUARTZ, Material.CUT_SANDSTONE, Material.CUT_RED_SANDSTONE,
			Material.SMOOTH_SANDSTONE_STAIRS, Material.SMOOTH_RED_SANDSTONE_STAIRS, Material.SMOOTH_QUARTZ_STAIRS, Material.CRACKED_STONE_BRICKS, Material.BRICKS, Material.BRICK_SLAB, Material.BRICK_STAIRS,
			Material.SMOOTH_SANDSTONE_SLAB, Material.SMOOTH_RED_SANDSTONE_SLAB, Material.SMOOTH_STONE_SLAB, Material.SMOOTH_QUARTZ_SLAB, Material.CUT_SANDSTONE_SLAB, Material.CUT_RED_SANDSTONE_SLAB,
			Material.POLISHED_ANDESITE, Material.POLISHED_DIORITE, Material.POLISHED_GRANITE, Material.POLISHED_ANDESITE_STAIRS, Material.POLISHED_DIORITE_STAIRS, Material.POLISHED_GRANITE_STAIRS,
			Material.POLISHED_ANDESITE_SLAB, Material.POLISHED_DIORITE_SLAB, Material.POLISHED_GRANITE_SLAB, Material.INFESTED_CRACKED_STONE_BRICKS, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS,
			Material.CHISELED_STONE_BRICKS, Material.STONE_BRICK_STAIRS, Material.STONE_BRICK_SLAB, Material.INFESTED_STONE_BRICKS, Material.INFESTED_MOSSY_STONE_BRICKS, Material.INFESTED_CHISELED_STONE_BRICKS,
			Material.PURPUR_BLOCK, Material.PURPUR_PILLAR, Material.PURPUR_SLAB, Material.PURPUR_STAIRS, Material.OBSIDIAN, Material.END_STONE_BRICKS, Material.END_STONE_BRICK_SLAB, Material.END_STONE_BRICK_STAIRS,
			Material.NETHER_BRICKS, Material.RED_NETHER_BRICKS, Material.NETHER_BRICK_SLAB, Material.RED_NETHER_BRICK_SLAB, Material.NETHER_BRICK_STAIRS, Material.RED_NETHER_BRICK_STAIRS,
			Material.CHISELED_SANDSTONE, Material.CHISELED_RED_SANDSTONE, Material.CHISELED_QUARTZ_BLOCK, Material.PRISMARINE_BRICKS, Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_BRICK_STAIRS
		);
		addModifier(0.1F, Material.DIRT_PATH);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMove(EntityMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		LivingEntity entity = event.getEntity();
		if (entity.getTicksLived() % 3 != 0) return;
		if (entity.isInsideVehicle()) return;

		AttributeInstance attribute = entity.getAttribute(Attribute.MOVEMENT_SPEED);
		if (attribute == null) return;

		float speed = calculateSpeedBonus(event.getTo().getBlock());
		speed *= (float) attribute.getBaseValue() / 0.2F;

		attribute.removeModifier(MODIFIER_KEY);
		attribute.addTransientModifier(new AttributeModifier(MODIFIER_KEY, speed, AttributeModifier.Operation.ADD_NUMBER));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		Player player = event.getPlayer();
		if (player.isFlying()) return;

		AttributeInstance attribute = player.getAttribute(Attribute.MOVEMENT_SPEED);
		if (attribute == null) return;

		if (player.isSwimming()) {
			MetadataUtil.setMetadata(player, BLOCK_SPEED_META, (float) attribute.getBaseValue() * 2F);
			return;
		}

		Block block = event.getTo().getBlock();
		MetadataUtil.setMetadata(player, BLOCK_SPEED_META, (float) attribute.getBaseValue() * 2F + calculateSpeedBonus(block));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpeedChange(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Player player = event.getPlayer();
		if (player.isInsideVehicle()) return;
		if (player.isFlying()) return;

		AttributeInstance attribute = player.getAttribute(Attribute.MOVEMENT_SPEED);
		if (attribute == null) return;

		if (!player.hasMetadata(BLOCK_SPEED_META))
			MetadataUtil.setMetadata(player, BLOCK_SPEED_META, (float) attribute.getBaseValue() * 2F + calculateSpeedBonus(event.getTo().getBlock()));

		float walkSpeed = player.getWalkSpeed();
		float targetSpeed = MetadataUtil.getMetadata(player, BLOCK_SPEED_META, (float) attribute.getBaseValue() * 2F).asFloat();
		if (walkSpeed != 0.2F) targetSpeed *= walkSpeed / 0.2F;

		targetSpeed /= 2;
		targetSpeed -= (float) attribute.getBaseValue();

		double step = 0.0025;
		boolean abort = Math.abs(targetSpeed) < step;
		if (abort) targetSpeed = 0;

		AttributeModifier modifier = attribute.getModifier(MODIFIER_KEY);
		attribute.removeModifier(MODIFIER_KEY);

		double speedBoost;
		if (modifier != null) {
			speedBoost = modifier.getAmount();
			if (Math.abs(speedBoost - targetSpeed) < step) {
				if (abort) return;
			} else {
				speedBoost += speedBoost > targetSpeed ? -step : step;
			}
		} else {
			if (abort) return;
			speedBoost = targetSpeed < 0 ? -step : step;
		}

		attribute.addTransientModifier(new AttributeModifier(MODIFIER_KEY, speedBoost, AttributeModifier.Operation.ADD_NUMBER));
	}

	public float calculateSpeedBonus(Block block) {
		float slowness = 0F;
		boolean down = block.getType().isSolid();
		Block downBlock;
		if (down) {
			downBlock = block;
			block = block.getRelative(BlockFace.UP);
		} else {
			downBlock = block.getRelative(BlockFace.DOWN);
		}
		if (!block.getType().isAir() && block.getBoundingBox().getHeight() > 0.095F)
			slowness += 0.05F;
		if (!block.getRelative(BlockFace.UP).getType().isAir())
			slowness += 0.05F;
		if (!downBlock.getType().isSolid())
			downBlock = downBlock.getRelative(BlockFace.DOWN);
		float target = SPEED_MODIFIERS.getOrDefault(downBlock.getType(), 0F);
		return target - slowness;
	}

	public static void addModifier(float baseSpeed, Material... blocks) {
		for (Material type : blocks)
			SPEED_MODIFIERS.put(type, baseSpeed);
	}

	public static void addModifier(float baseSpeed, Collection<Material> blocks) {
		for (Material type : blocks)
			SPEED_MODIFIERS.put(type, baseSpeed);
	}

}
