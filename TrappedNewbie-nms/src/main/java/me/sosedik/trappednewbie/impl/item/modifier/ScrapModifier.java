package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Custom visuals for scrap
 */
@NullMarked
public class ScrapModifier extends ItemModifier {

	private static final String ITEM_TAG = "item";

	public ScrapModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != TrappedNewbieItems.SCRAP) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		ItemStack scrapItem = NBT.get(item, nbt -> nbt.hasTag(ITEM_TAG) ? nbt.getItemStack(ITEM_TAG) : null);
		if (scrapItem == null) return ModificationResult.PASS;

		Material scrapType = scrapItem.getType();

		if (scrapItem.hasData(DataComponentTypes.MAX_DAMAGE)) {
			int damage = scrapItem.getData(DataComponentTypes.MAX_DAMAGE);
			scrapItem.setData(DataComponentTypes.DAMAGE, damage);
		}

		scrapItem = modifyItem(contextBox.getViewer(), contextBox.getLocale(), scrapItem);
		if (scrapItem == null) return ModificationResult.PASS;

		scrapItem = scrapItem.withType(Material.IRON_NUGGET);
		scrapItem.setData(DataComponentTypes.MAX_DAMAGE, 1000);
		scrapItem.setData(DataComponentTypes.DAMAGE, 997);

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		NamespacedKey key = scrapType.getKey();
		Component name = messenger.getMessageIfExists("item." + key.namespace() + ".broken_" + key.value() + ".name");
		if (name != null)
			scrapItem.setData(DataComponentTypes.ITEM_NAME, name);

		if (TrappedNewbieTags.SCRAPPABLE.isTagged(scrapType))
			scrapItem.setData(DataComponentTypes.ITEM_MODEL, new NamespacedKey(key.namespace(), "broken_" + key.value()));

		contextBox.setItem(scrapItem);

		return ModificationResult.RETURN;
	}

	public static ItemStack makeScrap(ItemStack item) {
		var scrapItem = ItemStack.of(TrappedNewbieItems.SCRAP);
		NBT.modify(scrapItem, nbt -> {
			ItemStack brokenItem = item;
			if (brokenItem.hasData(DataComponentTypes.MAX_DAMAGE)) {
				brokenItem = brokenItem.clone();
				brokenItem.setData(DataComponentTypes.DAMAGE, brokenItem.getData(DataComponentTypes.MAX_DAMAGE));
			}
			nbt.setItemStack(ITEM_TAG, brokenItem);
		});
		return scrapItem;
	}

	public static ItemStack extractScrap(ItemStack scrapItem) {
		return NBT.get(scrapItem, nbt -> {
			if (!nbt.hasTag(ITEM_TAG)) return ItemStack.empty();

			ItemStack brokenItem = nbt.getItemStack(ITEM_TAG);
			return brokenItem == null ? ItemStack.empty() : brokenItem;
		});
	}

	public static @NullMarked ItemStack extractScrap(ItemStack scrapItem, Material brokenType) {
		return NBT.get(scrapItem, nbt -> {
			if (!nbt.hasTag(ITEM_TAG)) return null;

			ItemStack brokenItem = nbt.getItemStack(ITEM_TAG);
			return ItemStack.isType(brokenItem, brokenType) ? brokenItem : null;
		});
	}

}
