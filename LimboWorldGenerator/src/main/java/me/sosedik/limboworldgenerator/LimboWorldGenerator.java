package me.sosedik.limboworldgenerator;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LimboWorldGenerator extends JavaPlugin {

	@Override
	public @NotNull ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
		return VoidChunkGenerator.GENERATOR;
	}

}
