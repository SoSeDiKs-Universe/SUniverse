package me.sosedik.requiem.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FakeHorseSaddlesModifier extends ItemModifier {

	public FakeHorseSaddlesModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;
		if (!(contextBox.getContext() instanceof SlottedItemModifierContext ctx)) return ModificationResult.PASS;
		if (ctx.slot() != 0) return ModificationResult.PASS;
		if (contextBox.getItem().getType() != Material.AIR) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;
		if (!(PossessingPlayer.getPossessed(player) instanceof AbstractHorse)) return ModificationResult.PASS;

		ItemStack saddle = ItemModifier.modifyItem(player, contextBox.getLocale(), new ItemStack(RequiemItems.SADDLE_OUTLINE));
		if (saddle == null) saddle = new ItemStack(Material.SADDLE);
		contextBox.setItem(saddle);
		return ModificationResult.RETURN;
	}

	@Override
	public boolean skipAir() {
		return false;
	}

}
