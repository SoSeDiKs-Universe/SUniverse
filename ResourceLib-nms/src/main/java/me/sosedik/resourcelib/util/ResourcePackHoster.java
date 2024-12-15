package me.sosedik.resourcelib.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.packs.ResourcePack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class ResourcePackHoster {

	public static void hostResourcePack(@NotNull Plugin plugin) {
		File resourcePackFile = new File(plugin.getDataFolder(), "resource-pack.zip");
		if (resourcePackFile.exists())
			FileUtil.deleteFile(resourcePackFile);

		plugin.saveResource("resource-pack.zip", true);
		if (!resourcePackFile.exists()) {
			plugin.getLogger().warning("Couldn't host resource pack, missing file: " + resourcePackFile);
			return;
		}

		int port = plugin.getConfig().getInt("resource-pack.port");
		if (port <= 0) {
			plugin.getLogger().warning("Couldn't host resource pack, invalid port: " + port);
			return;
		}

		UUID resourcePackId;
		if (plugin.getConfig().contains("resource-pack.id")) {
			resourcePackId = UUID.fromString(requireNonNull(plugin.getConfig().getString("resource-pack.id")));
		} else {
			resourcePackId = UUID.randomUUID();
			plugin.getConfig().set("resource-pack.id", resourcePackId.toString());
			plugin.saveConfig();
		}

		hostResourcePack(resourcePackFile, port);

		String ip = Bukkit.getIp();
		ip = "http://" + (ip.isEmpty() ? "localhost" : ip) + ":" + port;
		ResourcePack resourcePack = Bukkit.createResourcePack(resourcePackId, ip, resolveRpHash(ip), true, Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()).getMessage("resource_pack.prompt"));
		Bukkit.setServerResourcePack(resourcePack);
	}

	private static @Nullable String resolveRpHash(@NotNull String ip) {
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

	private static void hostResourcePack(@NotNull File resourcePackFile, int port) {
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
