package me.sosedik.resourcelib.api.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record FakeItemData(Material clientMaterial, @Nullable NamespacedKey model) {
}
