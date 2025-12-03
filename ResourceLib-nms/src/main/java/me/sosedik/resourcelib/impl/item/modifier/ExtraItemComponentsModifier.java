package me.sosedik.resourcelib.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.PaperDataComponentType;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NullMarked
public class ExtraItemComponentsModifier extends ItemModifier {

	private static final Map<Material, List<DataComponentType>> EXTRAS = new HashMap<>();

	public ExtraItemComponentsModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		List<DataComponentType> dataComponentTypes = EXTRAS.get(contextBox.getInitialType());
		if (dataComponentTypes == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();

		boolean modified = false;
		for (DataComponentType dataComponentType : dataComponentTypes) {
			if (item.isDataOverridden(dataComponentType)) continue;

			if (dataComponentType instanceof PaperDataComponentType.ValuedImpl<?, ?> type) {
				net.minecraft.world.item.ItemStack nmsItem = item instanceof CraftItemStack stack ? stack.handle : net.minecraft.world.item.ItemStack.fromBukkitCopy(item);
				if (!(nmsItem.getComponents() instanceof PatchedDataComponentMap map)) continue;

				Object data = item.getData(type);
				Object nmsData = toVanilla(data, type);
				map.ensureMapOwnership();
				map.patch.put(type.getHandle(), Optional.ofNullable(nmsData));
				contextBox.setItem(nmsItem.asBukkitMirror());
				modified = true;
			} else if (dataComponentType instanceof PaperDataComponentType.NonValuedImpl<?, ?> type) {
				net.minecraft.world.item.ItemStack nmsItem = item instanceof CraftItemStack stack ? stack.handle : net.minecraft.world.item.ItemStack.fromBukkitCopy(item);
				if (!(nmsItem.getComponents() instanceof PatchedDataComponentMap map)) continue;

				map.ensureMapOwnership();
				map.patch.put(type.getHandle(), Optional.ofNullable(toVanilla(type)));
				contextBox.setItem(nmsItem.asBukkitMirror());
				modified = true;
			}
		}

		return modified ? ModificationResult.OK : ModificationResult.PASS;
	}

	private static <T, N> Object toVanilla(@Nullable Object data, PaperDataComponentType.ValuedImpl<T, N> type) {
		return type.getAdapter().toVanilla((T) data, type.getHolder());
	}

	private static <T, N> Object toVanilla(PaperDataComponentType.NonValuedImpl<T, N> type) {
		return type.getAdapter().toVanilla(null, type.getHolder());
	}

	public static void addExtra(Material material, DataComponentType... types) {
		EXTRAS.computeIfAbsent(material, k -> new ArrayList<>()).addAll(List.of(types));
	}

}
