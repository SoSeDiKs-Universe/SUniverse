package me.sosedik.resourcelib.impl.block.nms;

import com.google.gson.JsonObject;
import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.SculkSensor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SimpleFakeSculkNMSBlock extends Block implements KiterinoBlock, SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE = Block.column(16, 0, 8);

	private final NamespacedKey blockId;
	private SculkSensor.@Nullable Phase phase;
	private int power;

	public SimpleFakeSculkNMSBlock(Properties settings, NamespacedKey blockId) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
		this.blockId = blockId;
	}

	@Override
	public @Nullable BlockState serializeBlockToClient(Object currentState) {
		if (this.phase == null) {
			JsonObject data = requireNonNull(ResourceLib.storage().getSculkMapping(this.blockId));
			this.phase = SculkSensor.Phase.valueOf(data.get("sculk_sensor_phase").getAsString().toUpperCase(Locale.US));
			this.power = data.get("power").getAsInt();
		}

		FluidState fluidState = getFluidState(((net.minecraft.world.level.block.state.BlockState) currentState));
		SculkSensor blockData = (SculkSensor) Material.SCULK_SENSOR.createBlockData();
		blockData.setSculkSensorPhase(this.phase);
		blockData.setPower(this.power);
		blockData.setWaterlogged(!fluidState.isEmpty());
		return blockData.createBlockState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	// Copied from SculkSensorBlock
	@Override
	protected VoxelShape getShape(net.minecraft.world.level.block.state.BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public net.minecraft.world.level.block.state.@Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos clickedPos = context.getClickedPos();
		FluidState fluidState = context.getLevel().getFluidState(clickedPos);
		return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	protected FluidState getFluidState(net.minecraft.world.level.block.state.BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected net.minecraft.world.level.block.state.BlockState updateShape(
			net.minecraft.world.level.block.state.BlockState state,
			LevelReader level,
			ScheduledTickAccess scheduledTickAccess,
			BlockPos pos,
			Direction direction,
			BlockPos neighborPos,
			net.minecraft.world.level.block.state.BlockState neighborState,
			RandomSource random
	) {
		if (state.getValue(WATERLOGGED)) {
			scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
	}

}
