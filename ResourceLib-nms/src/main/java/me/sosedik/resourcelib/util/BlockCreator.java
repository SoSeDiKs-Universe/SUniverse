package me.sosedik.resourcelib.util;

import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.resourcelib.impl.block.nms.BarrierNMSBlock;
import me.sosedik.resourcelib.impl.block.nms.SimpleFakeSculkNMSBlock;
import me.sosedik.resourcelib.impl.block.nms.SimpleNMSBlock;
import me.sosedik.resourcelib.impl.block.nms.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class BlockCreator {

	public static KiterinoBlock simpleBlock(Object properties, String key) {
		return new SimpleNMSBlock((BlockBehaviour.Properties) properties, requireNonNull(NamespacedKey.fromString(key)));
	}

	public static KiterinoBlock fakeSculk(Object properties, String key) {
		return new SimpleFakeSculkNMSBlock((BlockBehaviour.Properties) properties, requireNonNull(NamespacedKey.fromString(key)));
	}

	public static KiterinoBlock barrier(Object properties, String key) {
		return new BarrierNMSBlock(((BlockBehaviour.Properties) properties).strength(0.3F).pushReaction(PushReaction.BLOCK), requireNonNull(NamespacedKey.fromString(key)));
	}

	public static KiterinoBlock vegetation(Object properties, String key) {
		return new VegetationBlock((BlockBehaviour.Properties) properties, requireNonNull(NamespacedKey.fromString(key)));
	}

}
