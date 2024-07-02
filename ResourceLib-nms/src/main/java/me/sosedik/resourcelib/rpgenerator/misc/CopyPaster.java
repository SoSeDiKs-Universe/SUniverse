package me.sosedik.resourcelib.rpgenerator.misc;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.utilizer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class CopyPaster {

	private final ResourcePackGenerator generator;

	public CopyPaster(@NotNull ResourcePackGenerator generator) {
		this.generator = generator;
	}

	public void copyFiles(@NotNull File dataDir) {
		var destDir = new File(this.generator.getOutputDir(), "assets");
		for (File featureDir : Objects.requireNonNull(dataDir.listFiles())) {
			String dirName = featureDir.getName();
			if (dirName.startsWith("#")) {
				ResourceLib.logger().warn("[CopyPaste] Skipping {}", dirName);
				continue;
			}
			FileUtil.copyFile(featureDir, destDir);
		}
	}

}
