package me.sosedik.resourcelib.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class VegetationBlock extends BushBlock implements KiterinoBlock {

	private @Nullable BlockState bukkitState;
	private final PlacementRule placementRule;
	private final NamespacedKey blockId;

	public VegetationBlock(Properties settings, NamespacedKey blockId, PlacementRule placementRule) {
		super(settings);
		this.placementRule = placementRule;
		this.blockId = blockId;
	}

	@Override
	public @Nullable BlockState serializeBlockToClient(Object currentState) {
		if (this.bukkitState == null) {
			String mapping = requireNonNull(ResourceLib.storage().getTripwireMapping(this.blockId));
			this.bukkitState = Bukkit.createBlockData(mapping).createBlockState();
		}
		return this.bukkitState;
	}

	@Override
	protected boolean mayPlaceOn(net.minecraft.world.level.block.state.BlockState state, BlockGetter level, BlockPos pos) {
		return this.placementRule.mayPlaceOn(state.getBukkitMaterial());
	}

	@FunctionalInterface
	public interface PlacementRule {

		boolean mayPlaceOn(Material type);

	}

}
