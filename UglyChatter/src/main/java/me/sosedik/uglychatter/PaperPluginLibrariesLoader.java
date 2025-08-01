package me.sosedik.uglychatter;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@NullMarked
public final class PaperPluginLibrariesLoader implements PluginLoader {

	private static final List<String> MAVEN_CENTRAL_URLS = List.of(
		"https://repo1.maven.org/maven2/",
		"http://repo1.maven.org/maven2/",
		"https://repo.maven.apache.org/maven2/",
		"http://repo.maven.apache.org/maven2/"
	);

	@Override
	public void classloader(PluginClasspathBuilder classpathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();
		PluginLibraries pluginLibraries = load();
		pluginLibraries.asDependencies().forEach(resolver::addDependency);
		pluginLibraries.asRepositories().forEach(repo -> {
			if (MAVEN_CENTRAL_URLS.contains(repo.getUrl())) {
				resolver.addRepository(new RemoteRepository.Builder(
						"central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
				).build());
			} else {
				resolver.addRepository(repo);
			}
		});
		classpathBuilder.addLibrary(resolver);
	}

	private PluginLibraries load() {
		try (InputStream in = getClass().getResourceAsStream("/paper-libraries.json")) {
			assert in != null;
			return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {

		public Stream<Dependency> asDependencies() {
			return dependencies.stream()
					.map(d -> new Dependency(new DefaultArtifact(d), null));
		}

		public Stream<RemoteRepository> asRepositories() {
			return repositories.entrySet().stream()
					.map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
		}

	}

}
