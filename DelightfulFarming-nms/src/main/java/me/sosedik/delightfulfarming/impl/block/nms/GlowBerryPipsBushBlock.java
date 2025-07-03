package me.sosedik.delightfulfarming.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class GlowBerryPipsBushBlock extends BushBlock implements KiterinoBlock {

	private @Nullable BlockState bukkitState;
	private final NamespacedKey blockId;

	public GlowBerryPipsBushBlock(Properties settings, NamespacedKey blockId) {
		super(settings.noOcclusion().randomTicks());
		this.blockId = blockId;
	}

	@Override
	protected void randomTick(net.minecraft.world.level.block.state.BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int modifier = level.spigotConfig.glowBerryModifier;
		double growPerTickProbability = 0.1;
		if (random.nextDouble() < ((modifier / 100.0D) * growPerTickProbability)) {
			net.minecraft.world.level.block.state.BlockState blockState = Blocks.CAVE_VINES.defaultBlockState();
			if (!org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(level, pos, blockState, Block.UPDATE_CLIENTS)) return;

			level.setBlock(pos, blockState, Block.UPDATE_ALL);
		}
	}

	@Override
	protected boolean canSurvive(net.minecraft.world.level.block.state.BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockPos = pos.above();
		net.minecraft.world.level.block.state.BlockState blockState = level.getBlockState(blockPos);
		return blockState.isFaceSturdy(level, blockPos, Direction.DOWN);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, net.minecraft.world.level.block.state.BlockState blockState) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
		level.setBlock(pos, Blocks.WEEPING_VINES.defaultBlockState(), Block.UPDATE_ALL);
	}

	@Override
	public @Nullable BlockState serializeBlockToClient(Object currentState) {
		if (this.bukkitState == null) {
			int age = requireNonNull(ResourceLib.storage().getWeepingVinesMapping(this.blockId)).get("age").getAsInt();
			this.bukkitState = Material.WEEPING_VINES.createBlockData("[age=" + age + "]").createBlockState();
		}
		return this.bukkitState;
	}

}
