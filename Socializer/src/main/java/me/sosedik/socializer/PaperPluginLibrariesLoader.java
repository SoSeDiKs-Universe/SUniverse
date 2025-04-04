package me.sosedik.socializer;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class PaperPluginLibrariesLoader implements PluginLoader {

	@Override
	public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();
		PluginLibraries pluginLibraries = load();
		pluginLibraries.asDependencies().forEach(resolver::addDependency);
		pluginLibraries.asRepositories().forEach(resolver::addRepository);
		classpathBuilder.addLibrary(resolver);
	}

	private @NotNull PluginLibraries load() {
		try (InputStream in = getClass().getResourceAsStream("/paper-libraries.json")) {
			assert in != null;
			return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	record PluginLibraries(@NotNull Map<@NotNull String, @NotNull String> repositories, @NotNull List<@NotNull String> dependencies) {

		public @NotNull Stream<@NotNull Dependency> asDependencies() {
			return dependencies.stream()
					.map(d -> new Dependency(new DefaultArtifact(d), null));
		}

		public @NotNull Stream<@NotNull RemoteRepository> asRepositories() {
			return repositories.entrySet().stream()
					.map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
		}

	}

}
