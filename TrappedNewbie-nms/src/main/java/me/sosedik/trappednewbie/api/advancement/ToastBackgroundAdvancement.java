package me.sosedik.trappednewbie.api.advancement;

import me.sosedik.packetadvancements.api.advancement.linking.LinkingAdvancementBuilder;
import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.advancement.linking.LinkingAdvancement;
import me.sosedik.trappednewbie.api.advancement.display.FancierAdvancementDisplay;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ToastBackgroundAdvancement extends LinkingAdvancement {

	public ToastBackgroundAdvancement(LinkingAdvancementBuilder<?, ?> advancementBuilder, AdvancementFrame advancementFrame) {
		super(advancementBuilder.display(FancierAdvancementDisplay.fancierDisplay().copyFrom(advancementBuilder.getDisplay()).isHidden(false).withReal(false).withAdvancementFrame(advancementFrame)));
	}

	@Override
	public IAdvancementDisplay getDisplay(Player player) {
		IAdvancementDisplay display = super.getDisplay(player);
		if (display instanceof FancierAdvancementDisplay<?> fancierDisplay) {
			display = fancierDisplay.clone().withObtained(getLinked().isDone(player));
		}
		return display;
	}

}
