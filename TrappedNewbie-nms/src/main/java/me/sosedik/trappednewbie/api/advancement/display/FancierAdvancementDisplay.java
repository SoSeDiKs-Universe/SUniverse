package me.sosedik.trappednewbie.api.advancement.display;

import io.papermc.paper.advancement.AdvancementDisplay;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.advancement.IAdvancementLike;
import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.advancement.linking.LinkingAdvancement;
import me.sosedik.packetadvancements.imlp.display.FancyAdvancementDisplay;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.trappednewbie.api.advancement.AdvancementFrame;
import me.sosedik.trappednewbie.api.advancement.ToastBackgroundAdvancement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@NullMarked
@SuppressWarnings("unchecked")
public abstract class FancierAdvancementDisplay<T extends FancierAdvancementDisplay<T>> extends FancyAdvancementDisplay<T> {

	private static final BiFunction<IAdvancementLike, Player, IAdvancementDisplay> DISPLAY_FUNCTION = (advancementLike, player) -> {
		IAdvancementDisplay display = advancementLike.getDisplay();
		if (advancementLike instanceof IAdvancement advancement && display instanceof FancierAdvancementDisplay<?> fancierDisplay)
			display = fancierDisplay.clone().withObtained(advancement.isDone(player));
		return display;
	};
	private static final Consumer<IAdvancement> REGISTER_HOOK = (advancement) -> {
		IAdvancementDisplay display = advancement.getDisplay();
		if (display instanceof FancierAdvancementDisplay<?> fancierDisplay) {
			fancierDisplay.withReal(true);
			AdvancementFrame advancementFrame = fancierDisplay.getAdvancementFrame();
			if (advancementFrame.getItemModelKey(false) != null) {
				advancement.getAdvancementTab().registerAdvancements(new ToastBackgroundAdvancement(LinkingAdvancement.buildLinking(advancement, advancement), advancementFrame));
			}
		}
	};

	protected @Nullable AdvancementFrame advancementFrame;
	protected boolean obtained;
	protected boolean real;

	@Override
	public T copyFrom(AdvancementDisplay display) {
		super.copyFrom(display);
		if (display instanceof FancierAdvancementDisplay<?> fancierDisplay) {
			withReal(fancierDisplay.isReal());
			withObtained(fancierDisplay.isObtained());
			withAdvancementFrame(fancierDisplay.getAdvancementFrame());
		}
		return (T) this;
	}

	@Override
	public void onRegister(IAdvancement advancement) {
		advancement.setDisplayFunction(DISPLAY_FUNCTION);
		if (!(advancement instanceof ToastBackgroundAdvancement)) REGISTER_HOOK.accept(advancement);
	}

	@Override
	public Frame frame() {
		return switch (getAdvancementFrame()) {
			case TASK -> Frame.TASK;
			case CHALLENGE -> Frame.CHALLENGE;
			default -> Frame.GOAL;
		};
	}

	@Override
	public ItemStack advancementIcon() {
		if (isReal()) return super.advancementIcon();

		NamespacedKey itemModelKey = getAdvancementFrame().getItemModelKey(isObtained());
		if (itemModelKey == null) return super.advancementIcon();

		var icon = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		icon.setData(DataComponentTypes.ITEM_MODEL, itemModelKey);
		return icon;
	}

	@Override
	public Component advancementTitle() {
		if (!isReal()) return super.advancementTitle();

		FontData fontData = getAdvancementFrame().getFontData(isObtained());
		if (fontData == null) return super.advancementTitle();

		return Component.textOfChildren(
			fontData.offsetMapping(-29).shadowColor(ShadowColor.none()),
			super.advancementTitle()
		);
	}

	public boolean isObtained() {
		return this.obtained;
	}

	public T withObtained(boolean obtained) {
		this.obtained = obtained;
		return (T) this;
	}

	public boolean isReal() {
		return this.real;
	}

	public T withReal(boolean real) {
		this.real = real;
		return (T) this;
	}

	@Override
	public T frame(AdvancementDisplay.Frame frame) {
		return switch (frame) {
			case TASK -> withAdvancementFrame(AdvancementFrame.TASK);
			case GOAL -> withAdvancementFrame(AdvancementFrame.GOAL);
			case CHALLENGE -> withAdvancementFrame(AdvancementFrame.CHALLENGE);
		};
	}

	public AdvancementFrame getAdvancementFrame() {
		if (this.advancementFrame == null) withAdvancementFrame(AdvancementFrame.TASK);
		return this.advancementFrame;
	}

	public T withAdvancementFrame(AdvancementFrame advancementFrame) {
		if (advancementFrame == AdvancementFrame.TASK) this.frame = Frame.TASK;
		else if (advancementFrame == AdvancementFrame.CHALLENGE) this.frame = Frame.CHALLENGE;
		else this.frame = Frame.GOAL;
		this.advancementFrame = advancementFrame;
		return (T) this;
	}

	public static FancierAdvancementDisplayImpl fancierDisplay() {
		return new FancierAdvancementDisplayImpl();
	}

	public static final class FancierAdvancementDisplayImpl extends FancierAdvancementDisplay<FancierAdvancementDisplayImpl> {

		@Override
		public FancierAdvancementDisplayImpl clone() {
			return fancierDisplay().copyFrom(this);
		}

	}

}
