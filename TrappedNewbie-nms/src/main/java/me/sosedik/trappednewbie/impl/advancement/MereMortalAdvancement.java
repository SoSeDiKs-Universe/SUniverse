package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class MereMortalAdvancement extends BaseAdvancement {

	public MereMortalAdvancement(BaseAdvancementBuilder advancementBuilder) {
		super(advancementBuilder);
	}

	@Override
	public IAdvancementDisplay getDisplay(@Nullable Player player) {
		if (player == null) return super.getDisplay(null);

		return super.getDisplay(player).clone().icon(ItemUtil.playerHead(player));
	}

}
