package me.sosedik.trappednewbie.api.advancement.display;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.imlp.display.FancyAdvancementDisplay;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FrameAdvancementDisplay extends FancyAdvancementDisplay<FrameAdvancementDisplay> {

	private final IAdvancement advancement;
	private final AdvancementFrame advancementFrame;

	public FrameAdvancementDisplay(IAdvancement advancement, AdvancementFrame advancementFrame) {
		this.advancement = advancement;
		this.advancementFrame = advancementFrame;
	}

	@Override
	public ItemStack advancementIcon(@Nullable Player viewer) {
		if (viewer == null) return super.advancementIcon(null);

		boolean obtained = this.advancement.isDone(viewer);
		NamespacedKey itemModelKey = getAdvancementFrame().getItemModelKey(obtained);
		if (itemModelKey == null) return super.advancementIcon(viewer);

		var icon = ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		icon.setData(DataComponentTypes.ITEM_MODEL, itemModelKey);
		return icon;
	}

	@Override
	public Frame frame() {
		return Frame.GOAL;
	}

	@Override
	public FrameAdvancementDisplay clone() {
		return copyFrom(this);
	}

	public AdvancementFrame getAdvancementFrame() {
		return this.advancementFrame;
	}

}
