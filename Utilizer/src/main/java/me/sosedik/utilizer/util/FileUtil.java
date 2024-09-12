package me.sosedik.utilizer.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.Utilizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
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
	 * Creates pretty json file
	 *
	 * @param result file to be created
	 * @param json   json to write into the file
	 */
	public static void createPrettyJsonFile(@NotNull File result, @NotNull JsonElement json) {
		createJsonFile(result, getPrettyJson(json));
	}

	/**
	 * Creates pretty json file
	 *
	 * @param result file to be created
	 * @param json   json to write into the file
	 */
	public static void createJsonFile(@NotNull File result, @NotNull JsonElement json) {
		createJsonFile(result, json.toString());
	}

	/**
	 * Creates json file
	 *
	 * @param result file to be created
	 * @param json   json to write into the file
	 */
	public static void createJsonFile(@NotNull File result, @NotNull String json) {
		createFolder(result.getParentFile());
		deleteFile(result);
		try (var writer = new FileWriter(result)) {
			writer.write(json);
		} catch (IOException e) {
			Utilizer.logger().error("Could not create json file!", e);
		}
	}

	/**
	 * Returns json as pretty String
	 *
	 * @param jsonElement json element to prettify
	 * @return prettified json
	 */
	public static @NotNull String getPrettyJson(@NotNull JsonElement jsonElement) {
		var gsonPrettyPrinting = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		try (var sWriter = new StringWriter();
		     JsonWriter jWriter = gsonPrettyPrinting.newJsonWriter(sWriter)) {
			jWriter.setIndent("\t");
			gsonPrettyPrinting.toJson(jsonElement, jWriter);
			sWriter.write(System.lineSeparator());
			return sWriter.toString();
		} catch (IOException e) {
			Utilizer.logger().error("Could not create pretty print!", e);
			return jsonElement.toString();
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
	 * Creates folder if it's not created already
	 *
	 * @param dir the directory to create
	 */
	public static void createFolder(@NotNull File dir) {
		boolean dirCreated = dir.exists();
		if (!dirCreated)
			dirCreated = dir.mkdirs();
		if (!dirCreated)
			Utilizer.logger().error("Folder could not be created: {}", dir.getPath());
	}

	/**
	 * Deletes the file
	 *
	 * @param file file to delete
	 */
	public static void deleteFile(@NotNull File file) {
		if (!file.exists()) return;

		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			Utilizer.logger().error("Could not delete file: {}", file.getPath(), e);
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

	/**
	 * Copies source file to target file
	 *
	 * @param from source file
	 * @param to   target file
	 */
	public static void copyFile(@NotNull File from, @NotNull File to) {
		if (!from.exists()) {
			Utilizer.logger().error("Copy operation: Source file {} does not exist.", from.getPath());
		}

		if (to.exists() && !from.isDirectory()) {
			Utilizer.logger().warn("Copy operation: Target file {} already exists (possible mapping: {}). Overriding it.", to.getPath(), from.getName(), new IOException("Overriding target file, shouldn't happen"));
			deleteFile(to);
		}

		if (from.isDirectory()) {
			try (Stream<Path> files = Files.walk(from.toPath())) {
				files.forEach(source -> {
					Path destination = Paths.get(to.getPath(), source.toString()
							.substring(from.getPath().length()));
					if (Files.isRegularFile(source)) {
						createFolder(destination.toFile().getParentFile());
						try {
							Files.copy(source, destination);
						} catch (IOException e) {
							Utilizer.logger().error("Couldn't copy file", e);
						}
					}
				});
			} catch (IOException e) {
				Utilizer.logger().error("Couldn't copy file", e);
			}
			return;
		}

		try {
			createFolder(to.getParentFile());
			Files.copy(from.toPath(), to.toPath());
		} catch (IOException e) {
			Utilizer.logger().error("Couldn't copy file", e);
		}
	}

	/**
	 * Finds file with provided name inside the folder
	 *
	 * @param base starting folder
	 * @param name file name
	 * @return file with provided name if found
	 */
	public static @Nullable File findFile(@NotNull File base, @NotNull String name) {
		if (!base.exists()) return null;

		for (File file : Objects.requireNonNull(base.listFiles())) {
			if (file.isDirectory()) {
				var f = findFile(file, name);
				if (f != null)
					return f;
				continue;
			}
			if (file.getName().equals(name))
				return file;
		}
		return null;
	}

}
