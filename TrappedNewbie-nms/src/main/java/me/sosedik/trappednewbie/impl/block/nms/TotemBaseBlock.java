package me.sosedik.trappednewbie.impl.block.nms;

import com.mojang.serialization.MapCodec;
import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.impl.block.nms.BarrierNMSBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class TotemBaseBlock extends BaseEntityBlock implements KiterinoBlock {

	private static final MapCodec<TotemBaseBlock> CODEC = simpleCodec(TotemBaseBlock::new);
	private static @UnknownNullability BlockEntityType<TotemBaseBlockEntity> TOTEM;

	protected static org.bukkit.block.@Nullable BlockState bukkitState;

	private TotemBaseBlock(Properties settings) {
		this(settings, null);
	}

	public TotemBaseBlock(Object settings, @Nullable NamespacedKey key) {
		super(((Properties) settings).noOcclusion());
		if (key != null) BarrierNMSBlock.addBarrier(key);
		if (TOTEM == null) {
			TOTEM = BlockEntityType.register("totem_base", (p, s) -> new TotemBaseBlockEntity(TOTEM, p, s), this);
		} else {
			TOTEM.validBlocks.add(this);
		}
	}

	@Override
	public void postInit() {
		CraftBlockStates.register(TOTEM, TotemBaseCraftEntityState.class, TotemBaseCraftEntityState::new);
	}

	@Override
	public MapCodec<TotemBaseBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TotemBaseBlockEntity(TOTEM, pos, state);
	}

	@Override
	public org.bukkit.block.@Nullable BlockState serializeBlockToClient(Object currentState) {
		if (bukkitState == null)
			bukkitState = Material.BARRIER.createBlockData().createBlockState();
		return bukkitState;
	}

	public static class TotemBaseCraftEntityState extends CraftBlockEntityState<TotemBaseBlockEntity> {

		public TotemBaseCraftEntityState(World world, TotemBaseBlockEntity blockEntity) {
			super(world, blockEntity);
		}

		protected TotemBaseCraftEntityState(TotemBaseCraftEntityState state, @Nullable Location location) {
			super(state, location);
		}

		@Override
		public TotemBaseCraftEntityState copy() {
			return new TotemBaseCraftEntityState(this, null);
		}

		@Override
		public TotemBaseCraftEntityState copy(Location location) {
			return new TotemBaseCraftEntityState(this, location);
		}

	}

	public static class TotemBaseBlockEntity extends BlockEntity {

		public TotemBaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
			super(blockEntityType, pos, blockState);
		}

	}

}
