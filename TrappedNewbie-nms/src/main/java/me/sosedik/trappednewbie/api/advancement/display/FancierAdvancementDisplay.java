package me.sosedik.trappednewbie.api.advancement.display;

import io.papermc.paper.advancement.AdvancementDisplay;
import me.sosedik.packetadvancements.PacketAdvancementsAPI;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.display.CoordinateMode;
import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.api.reward.SimpleAdvancementReward;
import me.sosedik.packetadvancements.imlp.advancement.fake.FakeAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.mimic.MimicAdvancement;
import me.sosedik.packetadvancements.imlp.display.FancyAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.display.SimpleAdvancementDisplay;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.trappednewbie.api.advancement.reward.FancyAdvancementReward;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.raw;

@NullMarked
@SuppressWarnings("unchecked")
// MCCheck: 1.21.8, hopefully background workaround is no longer needed
public abstract class FancierAdvancementDisplay<T extends FancierAdvancementDisplay<T>> extends FancyAdvancementDisplay<T> {

	private static final Component DEFAULT_PARENT = Component.empty().shadowColor(ShadowColor.none());
	private static final Consumer<IAdvancement> REGISTER_HOOK = (advancement) -> {
		if (advancement instanceof MimicAdvancement) return;

		IAdvancementDisplay display = advancement.getDisplay();
		if (display instanceof FancierAdvancementDisplay<?> fancierDisplay) {
			AdvancementFrame advancementFrame = fancierDisplay.getAdvancementFrame();
			if (advancementFrame.requiresBackground()) {
				var leftLine = FakeAdvancement.buildFake(advancement).display(SimpleAdvancementDisplay.simpleDisplay().isHidden(true).x(0F)).buildAndRegister();
				var bg = FakeAdvancement.buildFake(leftLine).display(new FrameAdvancementDisplay(advancement, advancementFrame).x(0F)).buildAndRegister();
				MimicAdvancement.buildMimic(bg)
					.advancementMimic(advancement)
					.coordMimic(CoordinateMode.offset(0F, 0F))
					.buildAndRegister();
			}
		}
	};

	protected @Nullable IAdvancement advancement;
	protected @Nullable AdvancementFrame advancementFrame;
	protected @Nullable AnnouncementMessage announcementMessage;

	@Override
	public T copyFrom(AdvancementDisplay display) {
		super.copyFrom(display);
		if (display instanceof FancierAdvancementDisplay<?> fancierDisplay) {
			this.advancement = fancierDisplay.advancement;
			this.advancementFrame = fancierDisplay.advancementFrame;
			this.announcementMessage = fancierDisplay.announcementMessage;
		}
		return (T) this;
	}

	@Override
	public void onRegister(IAdvancement advancement) {
		super.onRegister(advancement);
		this.advancement = advancement;
		REGISTER_HOOK.accept(advancement);
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
	public Component renderTitle(@Nullable Player viewer) {
		if (isHidden()) return super.renderTitle(viewer);
		if (this.advancement == null) return super.renderTitle(viewer);
		if (this.title != null && this.title != Component.empty()) return super.renderTitle(viewer);

		var messenger = viewer == null ? Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()) : Messenger.messenger(viewer);
		Component title = messenger.getMessage("adv." + this.advancement.getKey().value().replace('/', '.') + ".title");
		Component parent = getFancyTitleParent();
		return parent == null ? title : parent.append(title);
	}

	@Override
	public Component renderDescription(@Nullable Player viewer) {
		if (isHidden()) return super.renderDescription(viewer);
		if (this.advancement == null) return super.renderDescription(viewer);
		if (this.description != null && this.description != Component.empty()) return super.renderDescription(viewer);

		var messenger = viewer == null ? Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()) : Messenger.messenger(viewer);
		Component description = messenger.getMessage("adv." + this.advancement.getKey().value().replace('/', '.') + ".description");
		Component parent = getFancyDescriptionParent();
		return parent == null ? description : parent.append(description);
	}

	@Override
	public Component renderAdvancementDescription(@Nullable Player viewer) {
		if (isHidden()) return super.renderAdvancementDescription(viewer);
		if (this.advancement == null) return super.renderAdvancementDescription(viewer);
		if (viewer == null) return super.renderAdvancementDescription(null);
		if (!(this.advancement.getAdvancementReward() instanceof SimpleAdvancementReward simpleReward)) return super.renderAdvancementDescription(viewer);

		Component description = super.renderAdvancementDescription(viewer);

		List<Component> awards = new ArrayList<>();
		int exp = simpleReward.getExp();
		if (exp != 0)
			awards.add(FancyAdvancementReward.getExpMessage(viewer, exp));

		List<ItemStack> items = simpleReward.getItems();
		if (!items.isEmpty())
			items.forEach(item -> awards.add(FancyAdvancementReward.getItemMessage(item, NamedTextColor.GREEN)));

		if (simpleReward instanceof FancyAdvancementReward fancyReward) {
			ItemStack trophyItem = fancyReward.getTrophyItem();
			if (trophyItem != null)
				awards.add(FancyAdvancementReward.getItemMessage(trophyItem, NamedTextColor.GOLD));

			List<Function<Player, @Nullable Component>> extraMessages = fancyReward.getExtraMessages();
			if (extraMessages != null) {
				extraMessages.forEach(m -> {
					Component message = m.apply(viewer);
					if (message != null)
						awards.add(message);
				});
			}
		}

		if (awards.isEmpty()) return description;

		return combined(description, Component.newline(), Component.newline(), combine(Component.newline(), awards));
	}

	@Override
	public Component renderAdvancementTitle(@Nullable Player viewer) {
		if (isHidden()) return super.renderAdvancementTitle(viewer);
		if (viewer == null) return super.renderAdvancementTitle(null);
		if (this.advancement == null) return super.renderAdvancementTitle(null);

		boolean obtained = this.advancement.isDone(viewer);
		FontData fontData = getAdvancementFrame().getFontData(obtained);
		if (fontData == null) return super.renderAdvancementTitle(viewer);

		return Component.textOfChildren(
			fontData.offsetMapping(-29).shadowColor(ShadowColor.none()),
			super.renderAdvancementTitle(viewer)
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
	public Component renderChatAnnouncement(@Nullable Player viewer, Component completerDisplayName) {
		if (this.announcementMessage == null) return super.renderChatAnnouncement(viewer, completerDisplayName);

		Component title = PacketAdvancementsAPI.produceAnnounceAdvancementDisplay(viewer, this).colorIfAbsent(this.announcementMessage.getColor());
		var messenger = viewer == null ? Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()) : Messenger.messenger(viewer);

		return messenger.getMessage("advancement.announcement." + this.announcementMessage.name().toLowerCase(Locale.US),
				raw("player", completerDisplayName),
				raw("advancement", title)
			);
	}

	@Override
	public Component getFancyDescriptionParent() {
		Component parent = this.fancyDescriptionParent;
		return parent == null ? DEFAULT_PARENT : parent;
	}

	@Override
	public T fancyDescriptionParent(@Nullable Component fancyDescriptionParent) {
		this.fancyDescriptionParent = fancyDescriptionParent == null ? null : fancyDescriptionParent.shadowColorIfAbsent(ShadowColor.none());
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
