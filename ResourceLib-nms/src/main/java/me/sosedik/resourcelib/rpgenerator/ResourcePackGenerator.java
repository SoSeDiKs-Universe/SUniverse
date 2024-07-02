package me.sosedik.resourcelib.rpgenerator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.dataset.ResourcePackStorage;
import me.sosedik.resourcelib.rpgenerator.item.ItemParser;
import me.sosedik.resourcelib.rpgenerator.misc.CopyPaster;
import me.sosedik.resourcelib.rpgenerator.misc.FontCreator;
import me.sosedik.resourcelib.rpgenerator.misc.PackMeta;
import me.sosedik.resourcelib.rpgenerator.misc.PackOptions;
import me.sosedik.resourcelib.rpgenerator.extras.PackRemapper;
import me.sosedik.resourcelib.rpgenerator.extras.ZipPacker;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.FileUtil;
import net.lingala.zip4j.ZipFile;
import org.bukkit.Bukkit;
import org.bukkit.packs.ResourcePack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

public class ResourcePackGenerator {

	private final File dataDir;
	private final File userDataDir;
	private final PackOptions packOptions;
	private final CopyPaster copyPaster;
	private final ItemParser itemParser;
	private final FontCreator fontCreator;
	private File outputDir;
	private File mcAssetsDir;

	public ResourcePackGenerator(@NotNull Plugin plugin) {
		this.dataDir = plugin.getDataFolder();
		this.userDataDir = new File(getDataDir(), "datasets");
		this.packOptions = new PackOptions(getDataDir());
		this.copyPaster = new CopyPaster(this);
		this.itemParser = new ItemParser(this);
		this.fontCreator = new FontCreator(this);
		this.outputDir = new File(dataDir, "resource-pack");
		this.mcAssetsDir = new File(dataDir, "mcassets");
	}

	public @NotNull File getDataDir() {
		return this.dataDir;
	}

	public @NotNull File getUserDataDir() {
		return this.userDataDir;
	}

	public @NotNull File getOutputDir() {
		return this.outputDir;
	}

	public void setOutputDir(@NotNull File outputDir) {
		this.outputDir = outputDir;
	}

	public @NotNull File getMcAssetsDir() {
		return this.mcAssetsDir;
	}

	public @NotNull PackOptions getPackOptions() {
		return this.packOptions;
	}

	public @NotNull CopyPaster getCopyPaster() {
		return this.copyPaster;
	}

	public @NotNull ItemParser getItemParser() {
		return this.itemParser;
	}

	public @NotNull FontCreator getFontCreator() {
		return this.fontCreator;
	}

	public @NotNull ResourcePackStorage getStorage() {
		return ResourceLib.storage();
	}

	public void init() {
		ResourceLib.logger().info("Preparing to generating server resource pack");
		FileUtil.deleteFolder(this.outputDir);

		checkMcAssets();

		this.packOptions.save();
		this.fontCreator.prepareFonts();
	}

	public void parseResources(@NotNull File datasetsDir) {
		if (!datasetsDir.isDirectory()) return;
		if (datasetsDir.listFiles() == null) return;

		for (File namespaceDir : Objects.requireNonNull(datasetsDir.listFiles())) {
			if (!namespaceDir.isDirectory()) continue;
			if (namespaceDir.listFiles() == null) continue;

			String namespace = namespaceDir.getName();
			for (File typeDir : Objects.requireNonNull(namespaceDir.listFiles())) {
				if (!namespaceDir.isDirectory()) continue;
				if (typeDir.listFiles() == null) continue;

				String type = typeDir.getName();
				parseResources(typeDir, namespace, type);
			}
		}
	}

	public void parseResources(@NotNull File inputDir, @NotNull String namespace, @NotNull String type) {
		switch (type) {
			case "copypaste":
				getCopyPaster().copyFiles(inputDir);
				break;
			case "item":
				getItemParser().parseItems(inputDir, namespace, "");
				break;
			case "font":
				getFontCreator().parseIcons(inputDir, namespace);
				break;
		}
	}

	public void generate() {
		if (!packOptions.isValid()) {
			ResourceLib.logger().warn("Resource pack options are not initialized yet");
			return;
		}
		PackMeta.generatePackMeta(this);
		this.itemParser.generateItems();
		this.fontCreator.generateFonts();

		var packRemapper = new PackRemapper(this);
		packRemapper.remap();

		File resourcePackFile = new ZipPacker().packZip(this);
		if (!resourcePackFile.exists()) return;

		hostResourcePack(resourcePackFile);

		String ip = Bukkit.getIp();
		ip = "http://" + (ip.isEmpty() ? "localhost" : ip) + ":" + packOptions.getResourcePackPort();
		ResourcePack resourcePack = Bukkit.createResourcePack(UUID.randomUUID(), ip, resolveRpHash(ip), true, Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()).getMessage("resource_pack.prompt"));
		Bukkit.setServerResourcePack(resourcePack);
	}

	private @Nullable String resolveRpHash(@NotNull String ip) {
		try {
			URL url = new URI(ip).toURL();
			try (InputStream inputStream = url.openStream()) {
				MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

				byte[] data = new byte[1024];
				int read;
				while ((read = inputStream.read(data)) != -1) {
					sha1.update(data, 0, read);
				}
				byte[] hashBytes = sha1.digest();

				var sb = new StringBuilder();
				for (byte hashByte : hashBytes) sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));

				return sb.toString();
			}
		} catch (URISyntaxException | NoSuchAlgorithmException | IOException e) {
			ResourceLib.logger().warn("Couldn't obtain resource pack hash", e);
		}
		return null;
	}

	private void checkMcAssets() {
		String mcVer = Bukkit.getMinecraftVersion();
		this.mcAssetsDir = new File(this.mcAssetsDir, mcVer);
		findMcAssets();
		var packMetaFile = new File(this.mcAssetsDir, "1.21.json");
		if (!packMetaFile.exists()) {
			FileUtil.createFolder(packMetaFile.getParentFile());
			ResourceLib.logger().info("Minecraft {} assets are missing, downloading from https://mcasset.cloud/", mcVer);
			String assetsLink = "https://github.com/InventivetalentDev/minecraft-assets/zipball/refs/heads/" + mcVer;
			try {
				var file = new File(this.mcAssetsDir, mcVer + ".zip");
				if (!file.exists()) {
					URL url = new URI(assetsLink).toURL();
					try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
					    var fileOutputStream = new FileOutputStream(file);
					    FileChannel fileChannel = fileOutputStream.getChannel()) {
						fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
					}
				}
				ResourceLib.logger().info("Unpacking Minecraft assets");
				try (var zipFile = new ZipFile(file)) {
					zipFile.extractAll(this.mcAssetsDir.getPath());
				}
				findMcAssets();
				ResourceLib.logger().info("Minecraft assets are ready");
			} catch (URISyntaxException | IOException e) {
				ResourceLib.logger().error("Couldn't download Minecraft assets", e);
			}
		}
	}

	private void findMcAssets() {
		if (!this.mcAssetsDir.exists()) return;

		for (File file : Objects.requireNonNull(this.mcAssetsDir.listFiles())) {
			if (!file.isDirectory()) continue;
			if (!file.getName().contains("minecraft-assets")) continue;

			this.mcAssetsDir = file;
			break;
		}
	}

	private void hostResourcePack(@NotNull File resourcePackFile) {
		int port = getPackOptions().getResourcePackPort();
		if (port < 0) return;

		try {
			var server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", new ResourcePackHandler(Files.readAllBytes(resourcePackFile.toPath())));
			server.setExecutor(null); // Creates a default executor
			server.start();
			ResourceLib.logger().info("Started resource pack hosting on port {}", port);
		} catch (IOException e) {
			ResourceLib.logger().error("Couldn't start http server to host the resource pack", e);
		}
	}

	private record ResourcePackHandler(byte[] resourcePack) implements HttpHandler {

		@Override
			public void handle(@NotNull HttpExchange httpExchange) throws IOException {
				httpExchange.setAttribute("Content-Type", "application/zip");
				httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=resource_pack.zip");
				httpExchange.sendResponseHeaders(200, resourcePack.length);
				try (OutputStream os = httpExchange.getResponseBody()) {
					os.write(resourcePack);
				}
			}

		}

}
