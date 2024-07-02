package me.sosedik.requiem;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.requiem.dataset.RequiemItems;
import org.jetbrains.annotations.NotNull;

public class RequiemBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext context) {
		context.injectMaterials(RequiemItems.class);
	}

}
