package me.sosedik.miscme;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MiscMeBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		ResourceLibBootstrap.setupItems(context, MiscMeItems.class, null, (key, properties) -> null);
	}

}
