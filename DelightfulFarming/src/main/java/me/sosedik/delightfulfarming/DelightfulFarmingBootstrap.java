package me.sosedik.delightfulfarming;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DelightfulFarmingBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		ResourceLibBootstrap.parseResources(context, null);
		ResourceLibBootstrap.setupItems(context, DelightfulFarmingItems.class, null, null);
	}

}
