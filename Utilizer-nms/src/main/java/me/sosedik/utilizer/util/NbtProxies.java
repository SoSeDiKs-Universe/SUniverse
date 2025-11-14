package me.sosedik.utilizer.util;

import de.tr7zw.nbtapi.iface.NBTHandler;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
		public void set(ReadWriteNBT nbt, @Nullable String key, Location value) {
			if (key != null) {
				nbt.removeKey(key);
				nbt = nbt.getOrCreateCompound(key);
			}
			World world = value.getWorld();
			if (world != null) nbt.setUUID("world", world.getUID());
			nbt.setDouble("x", value.getX());
			nbt.setDouble("y", value.getY());
			nbt.setDouble("z", value.getZ());
			nbt.setFloat("yaw", value.getYaw());
			nbt.setFloat("pitch", value.getPitch());
		}

		@Override
		public @Nullable Location get(ReadableNBT nbt, @Nullable String key) {
			if (key != null) {
				nbt = nbt.getCompound(key);
				if (nbt == null) return null;
			}

			World world = nbt.hasTag("world") ? Bukkit.getWorld(Objects.requireNonNull(nbt.getUUID("world"))) : null;
			double x = nbt.getDouble("x");
			double y = nbt.getDouble("y");
			double z = nbt.getDouble("z");
			float yaw = nbt.getFloat("yaw");
			float pitch = nbt.getFloat("pitch");
			return new Location(world, x, y, z, yaw, pitch);
		}

	};

	public static final NBTHandler<PotionEffect> POTION_EFFECT = new NBTHandler<>() {

		@Override
		public boolean fuzzyMatch(Object obj) {
			return obj instanceof PotionEffect;
		}

		@Override
		public void set(ReadWriteNBT nbt, @Nullable String key, PotionEffect value) {
			if (key != null) {
				nbt.removeKey(key);
				nbt = nbt.getOrCreateCompound(key);
			}
			nbt.setString("type", value.getType().key().asString());
			nbt.setInteger("duration", value.getDuration());
			nbt.setInteger("amplifier", value.getAmplifier());
			nbt.setBoolean("ambient", value.isAmbient());
			nbt.setBoolean("particles", value.hasParticles());
			nbt.setBoolean("icon", value.hasIcon());

			PotionEffect hiddenPotionEffect = value.getHiddenPotionEffect();
			if (hiddenPotionEffect != null)
				nbt.set("hidden_effect", hiddenPotionEffect, POTION_EFFECT);
		}

		@Override
		public @Nullable PotionEffect get(ReadableNBT nbt, @Nullable String key) {
			if (key != null) {
				nbt = nbt.getCompound(key);
				if (nbt == null) return null;
			}

			PotionEffectType type = Registry.MOB_EFFECT.get(Key.key(nbt.getString("type")));
			if (type == null) return null;

			int duration = nbt.getInteger("duration");
			int amplifier = nbt.getOrDefault("amplifier", 0);
			boolean ambient = nbt.getOrDefault("ambient", true);
			boolean particles = nbt.getOrDefault("particles", true);
			boolean icon = nbt.getOrDefault("icon", true);
			PotionEffect hiddenPotionEffect = nbt.get("hidden_effect", POTION_EFFECT);

			return new PotionEffect(type, duration, amplifier, ambient, particles, icon, hiddenPotionEffect);
		}

	};

}
