package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.listener.item.FillingBowlWithWater;
import me.sosedik.trappednewbie.listener.thirst.DrinkableWater;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Custom visuals for water in containers
 */
@NullMarked
public class DirtyWaterModifier extends ItemModifier {

	public static final Color MILK_COLOR = Color.fromRGB(255, 255, 255);
	public static final Color CACTUS_COLOR = Color.fromRGB(187, 193, 132);

	public DirtyWaterModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (!isFluidContainer(contextBox.getInitialType())) return ModificationResult.PASS;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));

		var thirstData = ThirstData.of(item, contextBox.getInitialType());
		if (thirstData.thirst() == 0 && thirstData.thirstChance() == 0) {
			if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.PASS;

			applyName(messenger, item, contextBox.getInitialType(), "empty");
			return ModificationResult.OK;
		}

		if (thirstData.drinkType() == ThirstData.DrinkType.MILK) {
			applyColor(item, MILK_COLOR);
			if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.OK;

			applyName(messenger, item, contextBox.getInitialType(), "milk");
			return ModificationResult.OK;
		}

		if (thirstData.drinkType() == ThirstData.DrinkType.CACTUS_JUICE) {
			applyColor(item, CACTUS_COLOR);
			if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.OK;

			applyName(messenger, item, contextBox.getInitialType(), "cactus");
			return ModificationResult.OK;
		}

		if (thirstData.drinkType() != ThirstData.DrinkType.WATER) return ModificationResult.PASS;

		if (thirstData.thirstChance() == 0) {
			applyColor(item, DrinkableWater.PURE_WATER_COLOR);
			if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.OK;

			applyName(messenger, item, contextBox.getInitialType(), "purified");
			return ModificationResult.OK;
		}

		if (thirstData.thirstChance() >= 0.7) {
			applyColor(item, DrinkableWater.DIRTY_WATER_COLOR);
			if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.OK;

			applyName(messenger, item, contextBox.getInitialType(), "dirty");
			return ModificationResult.OK;
		}

		if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.PASS;

		applyName(messenger, item, contextBox.getInitialType(), "water");
		return ModificationResult.OK;
	}

	private void applyColor(ItemStack item, Color color) {
		if (item.hasData(DataComponentTypes.DYED_COLOR)) {
			item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color));
		} else if (item.hasData(DataComponentTypes.POTION_CONTENTS)) {
			PotionContents data = item.getData(DataComponentTypes.POTION_CONTENTS);
			assert data != null;
			item.setData(DataComponentTypes.POTION_CONTENTS,
				PotionContents.potionContents()
					.potion(data.potion())
					.addCustomEffects(data.customEffects())
					.customName(data.customName())
					.customColor(color)
					.build()
			);
		}
	}

	private void applyName(Messenger messenger, ItemStack item, Material type, String prefix) {
		String key = "item." + TrappedNewbie.NAMESPACE + "." + prefix + "_" + type.getKey().value() + ".name";
		Component name = messenger.getMessageIfExists(key);
		if (name == null) return;

		item.setData(DataComponentTypes.ITEM_NAME, name);
	}

	private boolean isFluidContainer(Material type) {
		return type == Material.WATER_BUCKET
			|| TrappedNewbieTags.CANTEENS.isTagged(type)
			|| FillingBowlWithWater.REVERSED_BOWLS.containsKey(type);
	}

}
