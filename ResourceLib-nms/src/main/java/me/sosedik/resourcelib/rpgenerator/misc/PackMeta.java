package me.sosedik.resourcelib.rpgenerator.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PackMeta {

	public static void generatePackMeta(@NotNull ResourcePackGenerator generator) {
		var packOptions = new JsonObject();
		packOptions.addProperty("pack_format", generator.getPackOptions().getPackVersion());

		var gson = new Gson();
		var newDesc = new JsonArray();
		var packIconFile = new File(generator.getDataDir(), "pack_icon.png");
		if (packIconFile.exists()) {
			String namespace = generator.getPackOptions().getPackNamespace();
			String packIcon = "font/server/pack_icon";
			var iconOptions = new JsonObject();
			iconOptions.addProperty("ascent", 19);
			iconOptions.addProperty("height", 32);
			String iconSymbols = generator.getFontCreator().addIcon(packIconFile, packIcon, iconOptions, namespace);
			String spacingFont = SpacingUtil.SPACINGS_FONT.asString();
			newDesc.add(gson.fromJson("{\"text\":\"" + SpacingUtil.getSpacingSymbols(-34) + "\",\"font\":\"" + spacingFont + "\"}", JsonObject.class));
			newDesc.add(gson.fromJson("{\"text\":\"" + iconSymbols + "\",\"font\":\"" + namespace + ":fonts\"}", JsonObject.class));
			newDesc.add(gson.fromJson("{\"text\":\"" + SpacingUtil.getSpacingSymbols(1) + "\",\"font\":\"" + spacingFont + "\"}", JsonObject.class));
		} else {
			ResourceLib.logger().warn("Resource pack icon is missing! (\"plugins/ResourceLib/pack_icon.png\")");
		}
		String[] description = generator.getPackOptions().getDescription();
		for (String desc : description) {
			if (desc.startsWith("{")) {
				newDesc.add(desc);
			} else {
				String jsonDesc = "{\"text\":\"" + desc + "\",\"font\":\"default\"}";
				newDesc.add(gson.fromJson(jsonDesc, JsonObject.class));
			}
		}
		packOptions.add("description", newDesc);

		var metaOptions = new JsonObject();
		metaOptions.add("pack", packOptions);

		var metaFile = new File(generator.getOutputDir(), "pack.mcmeta");
		FileUtil.createPrettyJsonFile(metaFile, metaOptions);

		ResourceLib.logger().info("Created pack.mcmeta");
	}

}
