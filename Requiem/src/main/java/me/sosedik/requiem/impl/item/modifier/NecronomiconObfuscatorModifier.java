package me.sosedik.requiem.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.listener.item.SoulboundNecronomicon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class NecronomiconObfuscatorModifier extends ItemModifier {

	public NecronomiconObfuscatorModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != RequiemItems.NECRONOMICON) return ModificationResult.PASS;

		Player viewer = contextBox.getViewer();
		if (viewer == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (SoulboundNecronomicon.isValid(viewer, item)) return ModificationResult.PASS;

		if (item.hasData(DataComponentTypes.ITEM_NAME)) {
			Component name = item.getData(DataComponentTypes.ITEM_NAME);
			assert name != null;
			name = Component.text().decorate(TextDecoration.OBFUSCATED).append(name).build();
			item.setData(DataComponentTypes.ITEM_NAME, name);
		}

		if (item.hasData(DataComponentTypes.LORE)) {
			ItemLore data = item.getData(DataComponentTypes.LORE);
			assert data != null;
			List<Component> lore = new ArrayList<>();
			data.lines().forEach(line -> lore.add(Component.text().decorate(TextDecoration.OBFUSCATED).append(line).build()));
			item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
		}

		return ModificationResult.OK;
	}

}
