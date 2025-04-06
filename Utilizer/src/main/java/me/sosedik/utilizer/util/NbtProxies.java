package me.sosedik.utilizer.util;

import de.tr7zw.nbtapi.iface.NBTHandler;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class NbtProxies {

	public static final NBTHandler<Location> LOCATION = new NBTHandler<>() {

		@Override
		public boolean fuzzyMatch(Object obj) {
			return obj instanceof Location;
		}

		@Override
		public void set(ReadWriteNBT nbt, String key, Location value) {
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
		public @Nullable Location get(ReadableNBT nbt, String key) {
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
