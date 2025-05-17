package me.sosedik.resourcelib.impl.block.nms;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.ResourceLib;
import net.minecraft.world.level.block.BushBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SimpleFakeVinesBlock extends BushBlock implements KiterinoBlock {

	private @Nullable BlockState bukkitState;
	private final NamespacedKey blockId;

	public SimpleFakeVinesBlock(Properties settings, NamespacedKey blockId) {
		super(settings);
		this.blockId = blockId;
	}

	@Override
	public @Nullable BlockState serializeBlockToClient(Object currentState) {
		if (this.bukkitState == null) {
			int age = requireNonNull(ResourceLib.storage().getWeepingVinesMapping(this.blockId)).get("age").getAsInt();
			this.bukkitState = Material.WEEPING_VINES.createBlockData("age=" + age).createBlockState();
		}
		return this.bukkitState;
	}

}
