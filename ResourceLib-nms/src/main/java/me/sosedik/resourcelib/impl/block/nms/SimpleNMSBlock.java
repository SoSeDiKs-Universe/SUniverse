package me.sosedik.resourcelib.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class SimpleNMSBlock extends Block implements KiterinoBlock {

	private BlockState bukkitState;
	private final NamespacedKey blockId;

	public SimpleNMSBlock(Properties settings, NamespacedKey blockId) {
		super(settings);
		this.blockId = blockId;
	}

	@Override
	public @Nullable BlockState serializeBlockToClient() {
		if (this.bukkitState == null) {
			String mapping = requireNonNull(ResourceLib.storage().getTripwireMapping(this.blockId));
			this.bukkitState = Bukkit.createBlockData(mapping).createBlockState();
		}
		return this.bukkitState;
	}

}
