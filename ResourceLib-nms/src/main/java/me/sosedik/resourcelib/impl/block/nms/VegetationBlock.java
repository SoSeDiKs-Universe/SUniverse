package me.sosedik.resourcelib.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DryVegetationBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class VegetationBlock extends DryVegetationBlock implements KiterinoBlock {

	private @Nullable BlockState bukkitState;
	private final @Nullable VoxelShape shapeOverride;
	private final PlacementRule placementRule;
	private final NamespacedKey blockId;

	public VegetationBlock(Properties settings, NamespacedKey blockId, PlacementRule placementRule, @Nullable VoxelShape shapeOverride) {
		super(settings);
		this.placementRule = placementRule;
		this.blockId = blockId;
		this.shapeOverride = shapeOverride;
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

	@Override
	protected VoxelShape getShape(net.minecraft.world.level.block.state.BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return this.shapeOverride == null ? super.getShape(state, level, pos, context) : this.shapeOverride;
	}

	@Override
	public void animateTick(net.minecraft.world.level.block.state.BlockState state, Level level, BlockPos pos, RandomSource random) {
	}

	@FunctionalInterface
	public interface PlacementRule {

		boolean mayPlaceOn(Material type);

	}

}
