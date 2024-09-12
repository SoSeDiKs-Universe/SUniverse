package me.sosedik.resourcelib.api.item;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FakeItemData(@NotNull Material clientMaterial, @Nullable Integer customModelData) {
}
