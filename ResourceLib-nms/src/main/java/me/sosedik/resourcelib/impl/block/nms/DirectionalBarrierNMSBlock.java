package me.sosedik.resourcelib.impl.block.nms;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public class DirectionalBarrierNMSBlock extends BarrierNMSBlock {

	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	public DirectionalBarrierNMSBlock(Properties settings, NamespacedKey key) {
		super(settings, key);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public Pair<Class<?>, Class<?>> getBlockDataClasses() {
		return Pair.of(DirectionalBarrier.class, ClayKilnDirectionalBarrier.class);
	}

	public interface DirectionalBarrier extends BlockData, Directional { }

	public static class ClayKilnDirectionalBarrier extends CraftBlockData implements DirectionalBarrier {

		public ClayKilnDirectionalBarrier(BlockState state) {
			super(state);
		}

		@Override
		public BlockFace getFacing() {
			return this.get(FACING, BlockFace.class);
		}

		@Override
		public void setFacing(final BlockFace blockFace) {
			Preconditions.checkArgument(blockFace != null, "blockFace cannot be null!");
			Preconditions.checkArgument(blockFace.isCartesian() && blockFace.getModY() == 0, "Invalid face, only cartesian horizontal face are allowed for this property!");
			this.set(FACING, blockFace);
		}

		@Override
		public Set<BlockFace> getFaces() {
			return this.getValues(FACING, BlockFace.class);
		}

	}

}
