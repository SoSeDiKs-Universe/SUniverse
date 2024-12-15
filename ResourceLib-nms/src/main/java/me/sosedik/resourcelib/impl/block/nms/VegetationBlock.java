package me.sosedik.resourcelib.impl.block.nms;

import com.mojang.serialization.MapCodec;
import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class VegetationBlock extends BushBlock implements KiterinoBlock {

	private BlockState bukkitState;
	private final NamespacedKey blockId;

	public VegetationBlock(Properties settings, NamespacedKey blockId) {
		super(Properties.ofFullCopy(Blocks.SHORT_GRASS).setId(requireNonNull(settings.getId())));
		this.blockId = blockId;
	}

	@Override
	public @Nullable BlockState serializeBlockToClient() {
		if (this.bukkitState == null) {
			String mapping = requireNonNull(ResourceLib.storage().getTripwireMapping(this.blockId));
			this.bukkitState = Bukkit.createBlockData(mapping).createBlockState();
		}
		return this.bukkitState;
	}

	@Override
	protected MapCodec<? extends BushBlock> codec() {
		return TallGrassBlock.CODEC;
	}

}
