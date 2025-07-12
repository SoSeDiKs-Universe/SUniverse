package me.sosedik.trappednewbie.api.advancement.display;

import io.papermc.paper.advancement.AdvancementDisplay;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.packetadvancements.PacketAdvancementsAPI;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.advancement.IAdvancementLike;
import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.advancement.linking.LinkingAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.mimic.MimicAdvancement;
import me.sosedik.packetadvancements.imlp.display.FancyAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.display.coordinatemodes.OffsetCoordinateMode;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.trappednewbie.api.advancement.ToastBackgroundAdvancement;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static me.sosedik.utilizer.api.message.Mini.raw;

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
			AdvancementFrame advancementFrame = fancierDisplay.getAdvancementFrame();
			if (advancementFrame.getItemModelKey(false) != null) {
				ToastBackgroundAdvancement toastBackgroundAdvancement = new ToastBackgroundAdvancement(LinkingAdvancement.buildLinking(advancement, advancement), advancementFrame);
				advancement.getAdvancementTab().registerAdvancements(toastBackgroundAdvancement);
				MimicAdvancement.buildMimic(advancement) // TODO this is a huge hack, also kinda broken since 1.21.6
					.advancementMimic(advancement)
					.displayMimic(() -> FancyAdvancementDisplay.fancyDisplay().copyFrom(advancement.getDisplay()))
					.coordMimic(new OffsetCoordinateMode(0F, 0F))
					.buildAndRegister();
			}
		}
	};

	protected @Nullable IAdvancement advancement;
	protected @Nullable AdvancementFrame advancementFrame;
	protected @Nullable AnnouncementMessage announcementMessage;
	protected boolean obtained;
	protected boolean real = true;

	@Override
	public T copyFrom(AdvancementDisplay display) {
		super.copyFrom(display);
		if (display instanceof FancierAdvancementDisplay<?> fancierDisplay) {
			withReal(fancierDisplay.isReal());
			withObtained(fancierDisplay.isObtained());
			withAdvancementFrame(fancierDisplay.getAdvancementFrame());
			withAnnouncementMessage(fancierDisplay.getAnnouncementMessage());
		}
		return (T) this;
	}

	@Override
	public void onRegister(IAdvancement advancement) {
		this.advancement = advancement;
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
	public Component title(@Nullable Player viewer) {
		if (!isReal()) return super.title(viewer);
		if (this.advancement == null) return super.title(viewer);

		var messenger = viewer == null ? Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()) : Messenger.messenger(viewer);
		return messenger.getMessage("adv." + this.advancement.getKey().value().replace('/', '.') + ".title");
	}

	@Override
	public Component description(@Nullable Player viewer) {
		if (!isReal()) return super.description(viewer);
		if (this.advancement == null) return super.description(viewer);

		var messenger = viewer == null ? Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()) : Messenger.messenger(viewer);
		return messenger.getMessage("adv." + this.advancement.getKey().value().replace('/', '.') + ".description");
	}

	@Override
	public Component advancementTitle(@Nullable Player viewer) {
		if (!isReal()) return super.advancementTitle(viewer);

		FontData fontData = getAdvancementFrame().getFontData(isObtained());
		if (fontData == null) return super.advancementTitle(viewer);

		return Component.textOfChildren(
			fontData.offsetMapping(-29).shadowColor(ShadowColor.none()),
			super.advancementTitle(viewer)
		);
	}
	
	@Override
	public TextColor chatAnnouncementTitleColor(@Nullable Player viewer) {
		if (this.announcementMessage == null) {
			if (this.fancyDescriptionParent != null) {
				TextColor fancyParentColor = this.fancyDescriptionParent.color();
				if (fancyParentColor != null)
					return fancyParentColor;
			}
			return super.chatAnnouncementTitleColor(viewer);
		}
		return this.announcementMessage.getColor();
	}

	@Override
	public Component chatAnnouncement(@Nullable Player viewer, Component completerDisplayName) {
		if (this.announcementMessage == null) return super.chatAnnouncement(viewer, completerDisplayName);

		Component title = PacketAdvancementsAPI.produceAnnounceAdvancementDisplay(viewer, this);
		var messenger = viewer == null ? Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()) : Messenger.messenger(viewer);

		return messenger.getMessage("advancement.announcement." + this.announcementMessage.name().toLowerCase(Locale.US),
				raw("player", completerDisplayName),
				raw("advancement", title)
			).colorIfAbsent(this.announcementMessage.getColor());
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

	public @Nullable AnnouncementMessage getAnnouncementMessage() {
		return this.announcementMessage;
	}

	public T superChallenge() {
		return withAnnouncementMessage(AnnouncementMessage.SUPER_CHALLENGE);
	}

	public T torture() {
		return withAnnouncementMessage(AnnouncementMessage.TORTURE);
	}

	public T superTorture() {
		return withAnnouncementMessage(AnnouncementMessage.SUPER_TORTURE);
	}

	public T cheat() {
		return withAnnouncementMessage(AnnouncementMessage.CHEAT);
	}

	public T withAnnouncementMessage(@Nullable AnnouncementMessage announcementMessage) {
		this.announcementMessage = announcementMessage;
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
