package me.sosedik.limboworldgenerator;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class LimboWorldGenerator extends JavaPlugin {

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, @Nullable String id) {
		return VoidChunkGenerator.GENERATOR;
	}

}
