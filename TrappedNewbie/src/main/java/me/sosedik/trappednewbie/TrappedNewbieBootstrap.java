package me.sosedik.trappednewbie;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.BlockCreator;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TrappedNewbieBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		ResourceLibBootstrap.parseResources(context, null);
		ResourceLibBootstrap.setupBlocks(context, null, (key, properties) -> {
			if (key.endsWith("_twig")) return BlockCreator.vegetation(properties, key);
			throw new IllegalArgumentException("Unknown blockstate: %s".formatted(key));
		});
		ResourceLibBootstrap.setupItems(context, TrappedNewbieItems.class, null, null);
	}

}
