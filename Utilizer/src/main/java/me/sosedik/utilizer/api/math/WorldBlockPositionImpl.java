package me.sosedik.utilizer.api.math;

import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
record WorldBlockPositionImpl(World world, int blockX, int blockY, int blockZ) implements WorldBlockPosition { }
