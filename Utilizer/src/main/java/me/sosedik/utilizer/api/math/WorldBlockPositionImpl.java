package me.sosedik.utilizer.api.math;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

record WorldBlockPositionImpl(@NotNull World world, int blockX, int blockY, int blockZ) implements WorldBlockPosition {

}
