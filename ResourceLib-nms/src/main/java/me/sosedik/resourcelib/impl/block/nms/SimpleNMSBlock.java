package me.sosedik.resourcelib.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SimpleNMSBlock extends Block implements KiterinoBlock {

	private @Nullable BlockState bukkitState;
	private final NamespacedKey blockId;

	public SimpleNMSBlock(Properties settings, NamespacedKey blockId) {
		super(settings);
		this.blockId = blockId;
	}

	@Override
	public @Nullable BlockState serializeBlockToClient(Object currentState) {
		if (this.bukkitState == null) {
			String mapping = requireNonNull(ResourceLib.storage().getNoteBlockMapping(this.blockId));
			this.bukkitState = Bukkit.createBlockData(mapping).createBlockState();
		}
		return this.bukkitState;
	}

}
