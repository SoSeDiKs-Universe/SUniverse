package me.sosedik.utilizer.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.Utilizer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileUtil {

	private FileUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Reads json file
	 *
	 * @param json file
	 * @return json object representing the read file
	 */
	public static @NotNull JsonObject readJsonObject(@NotNull File json) {
		return readJsonElement(json).getAsJsonObject();
	}

	/**
	 * Reads json file
	 *
	 * @param json file
	 * @return json element representing the read file
	 */
	public static @NotNull JsonElement readJsonElement(@NotNull File json) {
		try (var reader = new FileReader(json)) {
			return new JsonStreamParser(reader).next();
		} catch (IOException e) {
			Utilizer.logger().error("Could not read json file!", e);
			return new JsonObject();
		}
	}

	/**
	 * Reads nbt file
	 *
	 * @param file file
	 * @return nbt data
	 */
	public static @NotNull ReadWriteNBT readNbtFile(@NotNull File file) {
		try {
			return NBTFile.readFrom(file);
		} catch (IOException e) {
			Utilizer.logger().error("Could not read nbt file!", e);
			return NBT.createNBTObject();
		}
	}

	/**
	 * Deletes folder with its contents
	 */
	public static void deleteFolder(@NotNull File folder) {
		deleteFolder(folder.toPath());
	}

	private static void deleteFolder(@NotNull Path path) {
		if (!Files.exists(path)) return;
		try {
			if (Files.isRegularFile(path)) { // Delete regular file directly
				Files.delete(path);
				return;
			}
			try (Stream<Path> paths = Files.walk(path)) {
				paths.filter(p -> p.compareTo(path) != 0).forEach(FileUtil::deleteFolder); // Delete all the child folders or files
				Files.delete(path); // Delete the folder itself
			}
		} catch (AccessDeniedException e) {
			Utilizer.logger().error("Access denied for deleting the file! ({})", path, e);
		} catch (IOException e) {
			Utilizer.logger().error("Could not delete folder: {}", path, e);
		}
	}

}
