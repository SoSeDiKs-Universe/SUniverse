package me.sosedik.resourcelib.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class CustomCrossbowArrowModifier extends ItemModifier {

	private static final Map<Material, ArrowModel> MODELS = new HashMap<>();

	public CustomCrossbowArrowModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (item.getType() != Material.CROSSBOW) return ModificationResult.PASS;
		if (!item.hasData(DataComponentTypes.CHARGED_PROJECTILES)) return ModificationResult.PASS;

		ChargedProjectiles data = item.getData(DataComponentTypes.CHARGED_PROJECTILES);
		assert data != null;
		List<ItemStack> projectiles = data.projectiles();
		for (ItemStack projectile : projectiles) {
			ArrowModel arrowModel = MODELS.get(projectile.getType());
			if (arrowModel == null) continue;

			boolean multishot = item.hasEnchant(Enchantment.MULTISHOT);
			item.setData(DataComponentTypes.ITEM_MODEL, multishot ? arrowModel.multishotModelKey() : arrowModel.modelKey());
			return ModificationResult.OK;
		}

		return ModificationResult.PASS;
	}

	private record ArrowModel(NamespacedKey modelKey, NamespacedKey multishotModelKey) {}

	public static void addModels(Material... types) {
		for (Material type : types) {
			NamespacedKey itemKey = type.getKey();
			NamespacedKey modelKey = ResourceLib.storage().getItemModelMapping(new NamespacedKey(itemKey.namespace(), "crossbow_" + itemKey.value()));
			NamespacedKey multishotModelKey = ResourceLib.storage().getItemModelMapping(new NamespacedKey(itemKey.namespace(), "crossbow_" + itemKey.value() + "_multishot"));
			MODELS.put(type, new ArrowModel(modelKey, multishotModelKey));
		}
	}

}
