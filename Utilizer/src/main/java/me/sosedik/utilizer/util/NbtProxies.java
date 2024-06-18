package me.sosedik.utilizer.util;

import de.tr7zw.changeme.nbtapi.iface.NBTHandler;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NbtProxies {

	public static final NBTHandler<Location> LOCATION = new NBTHandler<>() {

		@Override
		public boolean fuzzyMatch(@NotNull Object obj) {
			return obj instanceof Location;
		}

		@Override
		public void set(@NotNull ReadWriteNBT nbt, @NotNull String key, @NotNull Location value) {
			nbt.removeKey(key);
			ReadWriteNBT tag = nbt.getOrCreateCompound(key);
			World world = value.getWorld();
			if (world != null) tag.setUUID("world", world.getUID());
			tag.setDouble("x", value.getX());
			tag.setDouble("y", value.getY());
			tag.setDouble("z", value.getZ());
			tag.setFloat("yaw", value.getYaw());
			tag.setFloat("pitch", value.getPitch());
		}

		@Override
		public @Nullable Location get(@NotNull ReadableNBT nbt, @NotNull String key) {
			ReadableNBT tag = nbt.getCompound(key);
			if (tag == null) return null;

			World world = tag.hasTag("world") ? Bukkit.getWorld(Objects.requireNonNull(tag.getUUID("world"))) : null;
			double x = tag.getDouble("x");
			double y = tag.getDouble("y");
			double z = tag.getDouble("z");
			float yaw = tag.getFloat("yaw");
			float pitch = tag.getFloat("pitch");
			return new Location(world, x, y, z, yaw, pitch);
		}

	};

}
