package me.sosedik.requiem;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class RequiemLoader implements PluginLoader {

	@Override
	public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
		Path resourceLibPath = classpathBuilder.getContext().getPluginSource().resolveSibling("ResourceLib.jar");
		classpathBuilder.addLibrary(new JarLibrary(resourceLibPath));
	}

}
