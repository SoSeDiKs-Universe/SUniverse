package me.sosedik.requiem.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTType;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.requiem.dataset.RequiemTags;
import me.sosedik.requiem.impl.block.TombstoneBlockStorage;
import me.sosedik.resourcelib.listener.misc.LocalizedDeathMessages;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class TombstoneDeathMessageModifier extends ItemModifier {

	private static final Component ICON_PARENT_COMPONENT = Mini.combined(
		Mini.asIcon(Component.text("☠")),
		Component.space()
	).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
	private static final Component PARENT_COMPONENT = Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
	private static final Component LINE_SPACING = SpacingUtil.getSpacing(14);

	public TombstoneDeathMessageModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;
		if (!RequiemTags.TOMBSTONES.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		boolean changed = NBT.get(item, nbt -> {
			if (!nbt.hasTag(TombstoneBlockStorage.DEATH_MESSAGE_KEY, NBTType.NBTTagCompound)) return false;

			Component message = JSONComponentSerializer.json().deserialize(Objects.requireNonNull(nbt.getCompound(TombstoneBlockStorage.DEATH_MESSAGE_KEY)).toString());
			message = LocalizedDeathMessages.formatDeathMessage(contextBox.getLocale(), message);
			List<Component> lines = ChatUtil.wrapComponent(message, 35);
			if (lines.isEmpty()) return false; // Huh?

			lines.set(0, ICON_PARENT_COMPONENT.append(lines.getFirst()));
			for (int i = 1; i < lines.size(); i++)
				lines.set(i, Mini.combined(LINE_SPACING, PARENT_COMPONENT.append(lines.get(i))));
			contextBox.addLore(lines);
			return true;
		});

		return changed ? ModificationResult.OK : ModificationResult.PASS;
	}

}
