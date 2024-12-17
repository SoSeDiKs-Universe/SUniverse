package me.sosedik.miscme.listener.block;

import io.papermc.paper.block.LidMode;
import io.papermc.paper.block.Lidded;
import io.papermc.paper.block.fluid.FluidData;
import org.bukkit.Fluid;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Opening containers in liquid releases all items
 */
@NullMarked
public class ContainersInLiquidsReleaseContents implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onOpenUnderwater(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		BlockState blockState = block.getState(false);
		if (!(blockState instanceof Lidded lidded)) return;
		if (!(blockState instanceof Container container)) return;

		FluidData fluidData = null;
		BlockFace direction = BlockFace.UP;
		if (block.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
			fluidData = block.getWorld().getFluidData(block.getLocation());
		} else if (block.getBlockData() instanceof Directional directional) {
			if (blockState instanceof Barrel) {
				direction = directional.getFacing();
				fluidData = block.getWorld().getFluidData(block.getRelative(direction).getLocation());
			} else if (blockState instanceof ShulkerBox) {
				BlockFace ignored1 = directional.getFacing();
				BlockFace ignored2 = ignored1.getOppositeFace();
				for (BlockFace facing : directional.getFaces()) {
					if (facing == ignored1) continue;
					if (facing == ignored2) continue;

					fluidData = block.getWorld().getFluidData(block.getRelative(facing).getLocation());
					if (fluidData.getFluidType() != Fluid.EMPTY) {
						direction = facing;
						break;
					}
				}
			}
		}
		if (fluidData == null) {
			fluidData = block.getWorld().getFluidData(block.getRelative(BlockFace.UP).getLocation());
		}
		if (fluidData.getFluidType() == Fluid.EMPTY) return;

		event.setCancelled(true);
		lidded.setLidMode(LidMode.OPEN_UNTIL_VIEWED);

		Inventory inventory = container.getInventory();
		if (inventory.isEmpty()) return;

		Location location = container.getLocation().center().shiftTowards(direction, 0.5);
		World world = location.getWorld();

		boolean lava = fluidData.getFluidType() == Fluid.LAVA || fluidData.getFluidType() == Fluid.FLOWING_LAVA;
		for (ItemStack itemStack : inventory) {
			if (ItemStack.isEmpty(itemStack)) continue;

			world.dropItemNaturally(location, itemStack);
			world.spawnParticle(lava ? Particle.DRIPPING_LAVA : Particle.BUBBLE_COLUMN_UP, location, 1);
		}
		world.playSound(location, lava ? Sound.BLOCK_LAVA_POP : Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 1F, 1F);

		inventory.clear();

		boolean water = fluidData.getFluidType() == Fluid.WATER || fluidData.getFluidType() == Fluid.FLOWING_WATER;
		if (!water) return;
		if (!(block.getBlockData() instanceof Waterlogged waterlogged)) return;
		if (waterlogged.isWaterlogged()) return;

		waterlogged.setWaterlogged(true);
		block.setBlockData(waterlogged);
	}

}
