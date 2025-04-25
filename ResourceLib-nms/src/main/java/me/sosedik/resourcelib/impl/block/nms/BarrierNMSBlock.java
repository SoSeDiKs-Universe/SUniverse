package me.sosedik.resourcelib.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class BarrierNMSBlock extends Block implements KiterinoBlock {

	private static @Nullable BlockState bukkitState;

	public BarrierNMSBlock(Properties settings) {
		super(settings);
	}

	@Override
	public @Nullable BlockState serializeBlockToClient() {
		if (bukkitState == null)
			bukkitState = Material.BARRIER.createBlockData().createBlockState();
		return bukkitState;
	}

}
