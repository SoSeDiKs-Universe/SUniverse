package me.sosedik.resourcelib.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@NullMarked
public class BarrierNMSBlock extends Block implements KiterinoBlock {

	private static final Set<NamespacedKey> KEYS = new HashSet<>();

	protected static org.bukkit.block.@Nullable BlockState bukkitState;

	public BarrierNMSBlock(Properties settings, NamespacedKey key) {
		super(settings.noOcclusion());
		KEYS.add(key);
	}

	@Override
	public org.bukkit.block.@Nullable BlockState serializeBlockToClient(Object currentState) {
		if (bukkitState == null)
			bukkitState = Material.BARRIER.createBlockData().createBlockState();
		return bukkitState;
	}

	public static boolean isBarrierBlock(Material type) {
		return KEYS.contains(type.getKey());
	}

}
