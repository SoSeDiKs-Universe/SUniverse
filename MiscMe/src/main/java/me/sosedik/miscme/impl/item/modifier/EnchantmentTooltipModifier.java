package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.MiscUtil;
import me.sosedik.utilizer.util.RomanNumerals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.checkerframework.common.value.qual.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.sosedik.miscme.MiscMe.miscMeKey;
import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Renders fancy enchantments in tooltip
 */
@NullMarked
public class EnchantmentTooltipModifier extends ItemModifier {

	public static final Component DEFAULT_ICON = ResourceLib.requireFontData(miscMeKey("enchantment/blank")).icon();
	public static final Map<NamespacedKey, Component> ENCHANTMENT_ICONS = new HashMap<>();
	public static final Map<String, Component> TOOL_ICONS = Map.ofEntries(
		Map.entry("universal", toolIcon("universal")),
		Map.entry("unknown", toolIcon("unknown")),
		Map.entry("curse", toolIcon("curse")),
		Map.entry("helmet", toolIcon("helmet")),
		Map.entry("chestplate", toolIcon("chestplate")),
		Map.entry("leggings", toolIcon("leggings")),
		Map.entry("boots", toolIcon("boots")),
		Map.entry("axe", toolIcon("axe")),
		Map.entry("pickaxe", toolIcon("pickaxe")),
		Map.entry("shovel", toolIcon("shovel")),
		Map.entry("hoe", toolIcon("hoe")),
		Map.entry("sword", toolIcon("sword")),
		Map.entry("bow", toolIcon("bow")),
		Map.entry("crossbow", toolIcon("crossbow")),
		Map.entry("trident", toolIcon("trident")),
		Map.entry("mace", toolIcon("mace")),
		Map.entry("spear", toolIcon("spear")),
		Map.entry("fishing_rod", toolIcon("fishing_rod")),
		Map.entry("shears", toolIcon("shears")),
		Map.entry("bucket", toolIcon("bucket")),
		Map.entry("hammer", toolIcon("hammer")),
		Map.entry("knife", toolIcon("knife"))
	);
	public static final List<Map.Entry<String, Material>> ENCHANTABLE_SAMPLES = new ArrayList<>(List.of(
		Map.entry("helmet", Material.IRON_HELMET),
		Map.entry("chestplate", Material.IRON_CHESTPLATE),
		Map.entry("leggings", Material.IRON_LEGGINGS),
		Map.entry("boots", Material.IRON_BOOTS),
		Map.entry("axe", Material.IRON_AXE),
		Map.entry("pickaxe", Material.IRON_PICKAXE),
		Map.entry("shovel", Material.IRON_SHOVEL),
		Map.entry("hoe", Material.IRON_HOE),
		Map.entry("sword", Material.IRON_SWORD),
		Map.entry("bow", Material.BOW),
		Map.entry("crossbow", Material.CROSSBOW),
		Map.entry("trident", Material.TRIDENT),
		Map.entry("fishing_rod", Material.FISHING_ROD),
		Map.entry("shears", Material.SHEARS),
		Map.entry("bucket", Material.BUCKET),
		Map.entry("mace", Material.MACE),
		Map.entry("spear", Material.IRON_SPEAR)
	));
	public static final Map<NamespacedKey, Component> ENCHANTMENT_TO_TOOL_ICONS = new HashMap<>();
	private static final Component DESCRIPTION_PREFIX = combined(SpacingUtil.getSpacing(10), Component.text("◇", NamedTextColor.GRAY), SpacingUtil.getSpacing(3));
	private static final Component DESCRIPTION_INDENT = SpacingUtil.getSpacing(19);
	private static final TextColor LVL_COLOR = Objects.requireNonNull(TextColor.fromHexString("#444455"));
	private static final List<Enchantment> SORT_ORDER = List.copyOf(MiscUtil.getTagValues(EnchantmentTagKeys.TOOLTIP_ORDER));

	private static Component toolIcon(String key) {
		return ResourceLib.requireFontData(miscMeKey("enchantment/tool_type/" + key)).icon().color(null);
	}

	public EnchantmentTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);

		RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).forEach(enchantment -> {
			NamespacedKey key = enchantment.getKey();
			FontData fontData = ResourceLib.storage().getFontData(miscMeKey("enchantment/" + key.namespace() + "/" + key.value()));
			if (fontData != null)
				ENCHANTMENT_ICONS.put(key, fontData.icon());

			List<Component> toolIcons = new ArrayList<>();
			if (enchantment.isCursed())
				toolIcons.add(TOOL_ICONS.get("curse"));

			RegistryKeySet<ItemType> supportedItems = enchantment.getSupportedItems();
			if (supportedItems.contains(TypedKey.create(RegistryKey.ITEM, Material.CARROT_ON_A_STICK.key()))) {
				if (!toolIcons.isEmpty())
					toolIcons.add(Component.space());
				toolIcons.add(TOOL_ICONS.get("universal"));
				ENCHANTMENT_TO_TOOL_ICONS.put(key, combined(toolIcons));
				return;
			}

			RegistryKeySet<ItemType> primaryItems = enchantment.getPrimaryItems();
			if (primaryItems != null) {
				if (!toolIcons.isEmpty())
					toolIcons.add(Component.space());
				ENCHANTABLE_SAMPLES.forEach(entry -> {
					if (primaryItems.contains(TypedKey.create(RegistryKey.ITEM, entry.getValue().key())))
						toolIcons.add(TOOL_ICONS.get(entry.getKey()));
				});
				if (!toolIcons.isEmpty())
					toolIcons.add(Component.space());
			}

			ENCHANTABLE_SAMPLES.forEach(entry -> {
				TypedKey<ItemType> valueKey = TypedKey.create(RegistryKey.ITEM, entry.getValue().key());
				if (primaryItems != null && primaryItems.contains(valueKey)) return;
				if (supportedItems.contains(valueKey))
					toolIcons.add(TOOL_ICONS.get(entry.getKey()));
			});

			if (toolIcons.isEmpty())
				toolIcons.add(TOOL_ICONS.get("unknown"));

			ENCHANTMENT_TO_TOOL_ICONS.put(key, combined(toolIcons));
		});
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		boolean rendered = false;

		if (item.hasData(DataComponentTypes.STORED_ENCHANTMENTS)) {
			rendered = true;
			contextBox.addHiddenComponents(DataComponentTypes.STORED_ENCHANTMENTS);
			renderEnchants(contextBox, item.getData(DataComponentTypes.STORED_ENCHANTMENTS), true);
		}
		if (item.hasData(DataComponentTypes.ENCHANTMENTS)) {
			rendered = true;
			contextBox.addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
			renderEnchants(contextBox, item.getData(DataComponentTypes.ENCHANTMENTS), false);
		}
		if (!rendered) return ModificationResult.PASS;

		contextBox.addHiddenComponents(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.STORED_ENCHANTMENTS);

		return ModificationResult.OK;
	}

	private boolean renderEnchants(ItemContextBox contextBox, @Nullable ItemEnchantments data, boolean storedEnchants) {
		if (data == null) return false;

		Map<Enchantment, @IntRange(from = 1L, to = 255L) Integer> enchantments = data.enchantments();
		if (enchantments.isEmpty()) return false;

		boolean renderDescriptions = storedEnchants && enchantments.size() < 6;
		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		List<Component> enchantDisplays = renderDescriptions ? new ArrayList<>() : new ArrayList<>(enchantments.size());
		enchantments.entrySet().stream()
			.sorted((e1, e2) -> {
				int index1 = SORT_ORDER.indexOf(e1.getKey());
				int index2 = SORT_ORDER.indexOf(e2.getKey());

				if (index1 == -1) index1 = Integer.MAX_VALUE;
				if (index2 == -1) index2 = Integer.MAX_VALUE;

				return Integer.compare(index1, index2);
			})
			.forEach(entry -> {
				Enchantment enchantment = entry.getKey();
				int level = entry.getValue();
				int maxLevel = enchantment.getMaxLevel();
				NamespacedKey key = enchantment.getKey();

				Component description = GlobalTranslator.render(enchantment.description(), contextBox.getLocale()).colorIfAbsent(enchantment.isCursed() ? NamedTextColor.RED : NamedTextColor.GRAY);
				if (maxLevel != 1)
					description = description.append(Component.text(" " + RomanNumerals.toRoman(level)));
				if (maxLevel > 1 && level < maxLevel)
					description = description.append(Component.text(" (" + RomanNumerals.toRoman(maxLevel) + ")").color(LVL_COLOR));

				if (storedEnchants) {
					Component toolIcon = ENCHANTMENT_TO_TOOL_ICONS.get(key);
					if (toolIcon != null)
						description = description.append(SpacingUtil.getSpacing(5), toolIcon.color(LVL_COLOR));
				}

				Component display = combined(
					ENCHANTMENT_ICONS.getOrDefault(key, DEFAULT_ICON),
					SpacingUtil.getSpacing(3),
					description
				).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
				enchantDisplays.add(display);

				if (renderDescriptions) {
					Component[] descriptions = messenger.getMessagesIfExists("enchantment." + key.namespace() + "." + key.value() + ".description");
					if (descriptions != null && descriptions.length > 0) {
						enchantDisplays.add(combined(DESCRIPTION_PREFIX, descriptions[0]).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.DARK_GRAY));
						for (int i = 1; i < descriptions.length; i++)
							enchantDisplays.add(combined(DESCRIPTION_INDENT, descriptions[i]).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.DARK_GRAY));
					}
				}
			});

		if (!storedEnchants) {
			contextBox.addLore(combined(
				SpacingUtil.getSpacing(3),
				messenger.getMessage("attribute.enchantments").colorIfAbsent(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			));
		}
		contextBox.addLore(enchantDisplays);

		return true;
	}

}
