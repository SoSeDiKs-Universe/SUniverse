package me.sosedik.delightfulfarming;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.delightfulfarming.impl.block.nms.GlowBerryPipsBushBlock;
import me.sosedik.delightfulfarming.impl.block.nms.SweetBerryPipsBushBlock;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.BlockCreator;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class DelightfulFarmingBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		ResourceLibBootstrap.parseResources(context, null);
		ResourceLibBootstrap.setupBlocks(context, null, (key, properties) -> switch (key.substring("delightful_farming:".length())) {
			case "glow_berry_pips_bush" -> new GlowBerryPipsBushBlock((BlockBehaviour.Properties) properties, requireNonNull(NamespacedKey.fromString(key)));
			case "sweet_berry_pips_bush" -> new SweetBerryPipsBushBlock((BlockBehaviour.Properties) properties, requireNonNull(NamespacedKey.fromString(key)));
			case String k when k.endsWith("_basket") -> BlockCreator.barrier(properties, key);
			case "charcoal_block" -> BlockCreator.simpleBlock(properties, key);
			default -> throw new IllegalArgumentException("Unknown blockstate: %s".formatted(key));
		});
		ResourceLibBootstrap.setupItems(context, DelightfulFarmingItems.class, null, null);
	}

}
