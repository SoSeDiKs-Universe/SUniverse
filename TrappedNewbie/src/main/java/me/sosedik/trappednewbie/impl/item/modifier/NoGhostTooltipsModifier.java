package me.sosedik.trappednewbie.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class NoGhostTooltipsModifier extends ItemModifier {

	public NoGhostTooltipsModifier(@NotNull NamespacedKey key) {
		super(key);
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!shouldRemoveTooltip(contextBox.getItem().getType())) return ModificationResult.PASS;

		contextBox.getMeta().setHideTooltip(true);

		return ModificationResult.OK;
	}

	private boolean shouldRemoveTooltip(@NotNull Material type) { // TODO should be item tag
		return false;
	}

}
