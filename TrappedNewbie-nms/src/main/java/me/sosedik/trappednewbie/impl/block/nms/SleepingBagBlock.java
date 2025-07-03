package me.sosedik.trappednewbie.impl.block.nms;

import it.unimi.dsi.fastutil.Pair;
import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.moves.listener.movement.LayingMechanics;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.impl.blockstorage.SleepingBagBlockStorage;
import me.sosedik.utilizer.listener.BlockStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.block.impl.CraftBed;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SleepingBagBlock extends BedBlock implements KiterinoBlock {

	private static final VoxelShape SHAPE = Block.column(16.0, 1.0, 2.5);

	private org.bukkit.block.@Nullable BlockState bukkitState;

	public SleepingBagBlock(Object properties) {
		super(DyeColor.WHITE, ((Properties) properties).noOcclusion());
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		BlockPos footPos = pos;
		if (state.getValue(PART) != BedPart.HEAD) {
			pos = pos.relative(state.getValue(FACING));
			state = level.getBlockState(pos);
			if (!state.is(this)) {
				return InteractionResult.CONSUME;
			}
		} else {
			footPos = pos.relative(state.getValue(FACING).getOpposite());
			if (!level.getBlockState(footPos).is(this)) {
				return InteractionResult.CONSUME;
			}
		}

		if (player instanceof ServerPlayer serverPlayer && !serverPlayer.isShiftKeyDown() && BlockStorage.getByLoc(CraftLocation.toBukkit(footPos, level)) instanceof SleepingBagBlockStorage storage) {
			if (storage.tryToDye(serverPlayer.getBukkitEntity(), EquipmentSlot.HAND)
				|| storage.tryToDye(serverPlayer.getBukkitEntity(), EquipmentSlot.OFF_HAND)) {
				return InteractionResult.SUCCESS_SERVER;
			}
		}

		if (state.getValue(OCCUPIED)) {
			player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
		} else {
			final BlockPos finalBlockPos = pos;
			final BlockFace facing = player.getBukkitEntity().getFacing();
			player.startSleepInBed(pos).ifLeft(bedSleepingProblem -> {
				// Paper start - PlayerBedFailEnterEvent
				if (bedSleepingProblem != null) {
					io.papermc.paper.event.player.PlayerBedFailEnterEvent event = new io.papermc.paper.event.player.PlayerBedFailEnterEvent((org.bukkit.entity.Player) player.getBukkitEntity(), io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason.values()[bedSleepingProblem.ordinal()], org.bukkit.craftbukkit.block.CraftBlock.at(level, finalBlockPos), !level.dimensionType().bedWorks(), io.papermc.paper.adventure.PaperAdventure.asAdventure(bedSleepingProblem.getMessage()));
					if (!event.callEvent()) {
						return;
					}
					// Paper end - PlayerBedFailEnterEvent
					if (bedSleepingProblem.getMessage() != null) {
						final net.kyori.adventure.text.Component message = event.getMessage(); // Paper - PlayerBedFailEnterEvent
						if (message != null) player.displayClientMessage(io.papermc.paper.adventure.PaperAdventure.asVanilla(message), true); // Paper - PlayerBedFailEnterEvent
					}
				} // Paper - PlayerBedFailEnterEvent
			}).ifRight(u -> {
				player.setPos(finalBlockPos.getX() + 0.5, finalBlockPos.getY() + 0.1, finalBlockPos.getZ() + 0.5);
				if (player instanceof ServerPlayer serverPlayer)
					LayingMechanics.lay(serverPlayer.getBukkitEntity(), CraftLocation.toBukkit(finalBlockPos, level).center(0.1), facing);
			});
		}
		return InteractionResult.SUCCESS_SERVER;
	}

	@Override
	public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public org.bukkit.block.BlockState serializeBlockToClient(Object currentState) {
		if (this.bukkitState == null) {
			String mapping = requireNonNull(ResourceLib.storage().getTripwireMapping(Material.AIR.getKey()));
			this.bukkitState = Bukkit.createBlockData(mapping).createBlockState();
		}
		return this.bukkitState;
	}

	@Override
	public Pair<Class<?>, Class<?>> getBlockDataClasses() {
		return Pair.of(SleepingBagBed.class, CraftSleepingBagBed.class);
	}

	public interface SleepingBagBed extends Bed {}
	public static class CraftSleepingBagBed extends CraftBed implements SleepingBagBed {

		public CraftSleepingBagBed(BlockState state) {
			super(state);
		}

	}

}
