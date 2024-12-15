package me.sosedik.resourcelib.api.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record FakeItemData(@NonNull Material clientMaterial, @Nullable NamespacedKey model) {
}
