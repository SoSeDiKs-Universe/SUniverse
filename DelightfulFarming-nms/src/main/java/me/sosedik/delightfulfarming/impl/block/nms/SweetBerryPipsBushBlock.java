package me.sosedik.delightfulfarming.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.impl.block.nms.VegetationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SweetBerryPipsBushBlock extends VegetationBlock implements KiterinoBlock, BonemealableBlock {

	public SweetBerryPipsBushBlock(Properties settings, NamespacedKey blockId) {
		super(settings.noOcclusion().randomTicks(), blockId, (type) -> true, null);
	}

	@Override
	protected void randomTick(net.minecraft.world.level.block.state.BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int modifier = level.spigotConfig.sweetBerryModifier;
		double growPerTickProbability = 0.1;
		if (random.nextDouble() < ((modifier / 100.0D) * growPerTickProbability)) {
			BlockState blockState = Blocks.SWEET_BERRY_BUSH.defaultBlockState();
			if (!org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(level, pos, blockState, Block.UPDATE_CLIENTS)) return;

			level.setBlock(pos, blockState, Block.UPDATE_ALL);
		}
	}

	@Override
	protected boolean mayPlaceOn(net.minecraft.world.level.block.state.BlockState state, BlockGetter level, BlockPos pos) {
		return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, net.minecraft.world.level.block.state.BlockState blockState) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, net.minecraft.world.level.block.state.BlockState blockState) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
		level.setBlock(pos, Blocks.SWEET_BERRY_BUSH.defaultBlockState(), Block.UPDATE_ALL);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
		if (!new io.papermc.paper.event.entity.EntityInsideBlockEvent(entity.getBukkitEntity(), org.bukkit.craftbukkit.block.CraftBlock.at(level, pos)).callEvent()) return;

		if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE) {
			entity.makeStuckInBlock(state, new Vec3(0.8F, 0.75, 0.8F));
		}
	}

}
