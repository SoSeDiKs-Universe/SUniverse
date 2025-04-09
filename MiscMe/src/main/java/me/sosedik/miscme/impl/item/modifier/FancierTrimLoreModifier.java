package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.miscme.MiscMe;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.intellij.lang.annotations.Subst;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

/**
 * Makes trim lore fancier with icons and shorter displays
 */
// MCCheck: 1.21.5, armor trim materials
// https://minecraft.wiki/w/Smithing#List_of_tooltip_text_colors_for_all_materials
@NullMarked
public class FancierTrimLoreModifier extends ItemModifier {

	private static final Map<Key, Map.Entry<Component, TextColor>> MATERIAL_COLORS = new HashMap<>(Map.ofEntries(
		vanillaTrim(Material.AMETHYST_SHARD, "amethyst", TextColor.color(0xFF6C49AA)),
		vanillaTrim(Material.COPPER_INGOT, "copper", TextColor.color(0xFF9A472C)),
		vanillaTrim(Material.DIAMOND, "diamond", TextColor.color(0xFF2CBAA8)),
		vanillaTrim(Material.EMERALD, "emerald", TextColor.color(0xFF11A036)),
		vanillaTrim(Material.GOLD_INGOT, "gold", TextColor.color(0xFFDEB12D)),
		vanillaTrim(Material.IRON_INGOT, "iron", TextColor.color(0xFF9DAAAA)),
		vanillaTrim(Material.LAPIS_LAZULI, "lapis", TextColor.color(0xFF21497B)),
		vanillaTrim(Material.QUARTZ, "quartz", TextColor.color(0xFFE3DBC4)),
		vanillaTrim(Material.NETHERITE_INGOT, "netherite", TextColor.color(0xFF312E31)),
		vanillaTrim(Material.REDSTONE, "redstone", TextColor.color(0xFF971607)),
		vanillaTrim(Material.RESIN_BRICK, "resin", TextColor.color(0xFFF97712))
	));

	static {
		Registry<TrimMaterial> trimMaterialRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
		trimMaterialRegistry.forEach(trimMaterial -> {
			Key materialKey = trimMaterialRegistry.getKey(trimMaterial);
			if (!MATERIAL_COLORS.containsKey(materialKey))
				MiscMe.logger().warn("[FancierTrimLoreModifier] Missing trim material color mapping: {}", materialKey);
		});
	}

	public FancierTrimLoreModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.TRIM)) return ModificationResult.PASS;

		ArmorTrim armorTrim = Objects.requireNonNull(item.getData(DataComponentTypes.TRIM)).armorTrim();
		Key trimKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKey(armorTrim.getPattern());
		if (trimKey == null) return ModificationResult.PASS;

		Key materialKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKey(armorTrim.getMaterial());
		if (materialKey == null) return ModificationResult.PASS;

		Map.Entry<Component, TextColor> trimData = MATERIAL_COLORS.get(materialKey);
		Component trimIcon = trimData == null ? null : trimData.getKey();
		TextColor trimColor = trimData == null ? NamedTextColor.GRAY : trimData.getValue();
		Component trimText = Component.translatable("trim_pattern.%s.%s".formatted(trimKey.namespace(), trimKey.value()), trimColor);
		if (trimIcon != null) trimText = Mini.combine(Component.space(), trimIcon, trimText);
		item.editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM)); // TODO replace with data component once available

		contextBox.addLore(trimText);

		return ModificationResult.OK;
	}

	private static Map.Entry<Key, Map.Entry<Component, TextColor>> vanillaTrim(Material icon, @Subst("key") String key, TextColor color) {
		Component iconText = ResourceLib.requireFontData(NamespacedKey.minecraft("item/" + icon.key().value())).mapping();
		return entry(Key.key(key), entry(Mini.asIcon(iconText), color));
	}

	public static void addTrimMaterialMapping(Key materialKey, Component icon, TextColor color) {
		MATERIAL_COLORS.put(materialKey, entry(icon, color));
	}

}
