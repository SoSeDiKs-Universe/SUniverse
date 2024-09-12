package me.sosedik.trappednewbie;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import org.jetbrains.annotations.NotNull;

public class TrappedNewbieBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext context) {
		ResourceLibBootstrap.setupItems(context, TrappedNewbieItems.class);
	}

}
