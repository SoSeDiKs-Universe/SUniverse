package me.sosedik.trappednewbie.impl.block.nms;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.Pair;
import me.sosedik.resourcelib.impl.block.nms.DirectionalBarrierNMSBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public class ClayKilnBlock extends DirectionalBarrierNMSBlock {

	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty BURNED = BooleanProperty.create("burned");

	public ClayKilnBlock(Object properties, String key) {
		super((Properties) properties, NamespacedKey.fromString(key));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BURNED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, BURNED);
	}

	@Override
	public Pair<Class<?>, Class<?>> getBlockDataClasses() {
		return Pair.of(ClayKiln.class, ClayKilnCraftBlockData.class);
	}

	public interface ClayKiln extends BlockData, Directional {

		boolean isBurned();

		void setBurned(boolean burned);

	}
	public static class ClayKilnCraftBlockData extends CraftBlockData implements ClayKiln {

		public ClayKilnCraftBlockData(BlockState state) {
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

		@Override
		public boolean isBurned() {
			return this.get(BURNED);
		}

		@Override
		public void setBurned(boolean burned) {
			this.set(BURNED, burned);
		}

	}

}
