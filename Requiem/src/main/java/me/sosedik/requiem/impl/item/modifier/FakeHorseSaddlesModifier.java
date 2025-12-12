package me.sosedik.requiem.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
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
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;
		if (!(contextBox.getContext() instanceof SlottedItemModifierContext ctx)) return ModificationResult.PASS;
		if (ctx.getSlot() != 0) return ModificationResult.PASS;
		if (contextBox.getItem().getType() != Material.AIR) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;
		if (!(PossessingPlayer.getPossessed(player) instanceof AbstractHorse)) return ModificationResult.PASS;

		// TODO Custom saddle model so that it doesn't render on entities
		// Also saddles need refreshing when dismounting
		ItemStack saddle = modifyItem(ctx, player, contextBox.getLocale(), ItemStack.of(RequiemItems.SADDLE_OUTLINE));
		if (saddle == null) {
			saddle = ItemStack.of(Material.SADDLE);
			saddle.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
		}
		contextBox.setItem(saddle);
		return ModificationResult.RETURN;
	}

	@Override
	public boolean skipAir() {
		return false;
	}

}
