package me.sosedik.utilizer;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UtilizerBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		// Init! (make static utils available for other bootstrappers)
	}

}
