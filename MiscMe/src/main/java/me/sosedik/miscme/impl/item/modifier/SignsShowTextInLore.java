package me.sosedik.miscme.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlockItemDataProperties;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Signs show stored text in lore
 */
// MCCheck: 1.21.1, item block entity tag
public class SignsShowTextInLore extends ItemModifier {

	private static final String BLOCK_ENTITY_TAG = "minecraft:block_entity_data";
	private static final String FRONT_TEXT_TAG = "front_text";
	private static final String BACK_TEXT_TAG = "back_text";
	private static final Component PARENT_LORE = Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);

	public SignsShowTextInLore(@NotNull NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!Tag.ALL_SIGNS.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;
		if (!item.hasData(DataComponentTypes.BLOCK_DATA)) return ModificationResult.PASS;

		BlockItemDataProperties blockItemDataProperties = item.getData(DataComponentTypes.BLOCK_DATA);
		assert blockItemDataProperties != null;
		BlockState blockState = blockItemDataProperties.createBlockData(contextBox.getInitialType().asItemType().getBlockType()).createBlockState();
		if (!(blockState instanceof Sign sign)) return ModificationResult.PASS;

		boolean hasText = NBT.getComponents(item, nbt -> {
			if (!nbt.hasTag(BLOCK_ENTITY_TAG)) return false;

			ReadableNBT blockEntityTag = nbt.getCompound(BLOCK_ENTITY_TAG);
			assert blockEntityTag != null;
			return blockEntityTag.hasTag(FRONT_TEXT_TAG) || blockEntityTag.hasTag(BACK_TEXT_TAG);
		});
		if (!hasText) return ModificationResult.PASS;

		hasText = false;
		List<Component> lore = new ArrayList<>();
		List<Component> lines = sign.getSide(Side.FRONT).lines();
		if (addLines(contextBox.getViewer(), lines, lore)) {
			hasText = true;
		}
		lines = sign.getSide(Side.BACK).lines();
		if (hasText) {
			for (Component line : lines) {
				if (!ChatUtil.getPlainText(line).isEmpty()) {
					lore.add(Component.empty());
					break;
				}
			}
		}
		if (addLines(contextBox.getViewer(), lines, lore)) {
			hasText = true;
		}
		if (!hasText) return ModificationResult.PASS;

		contextBox.addLore(lore);

		return ModificationResult.OK;
	}

	private boolean addLines(@Nullable Player player, @NotNull List<Component> lines, @NotNull List<Component> lore) {
		MiniMessage miniMessage = player == null ? Mini.buildMini() : Messenger.messenger(player).miniMessage();
		boolean hasLines = false;
		List<Component> loreLines = new ArrayList<>();
		for (var i = 0; i < lines.size(); i++) {
			Component line = lines.get(i);
			String rawLine = FancyMessageRenderer.getRawInput(line);
			if (rawLine.isEmpty()) {
				loreLines.add(PARENT_LORE.append(combined(Component.text("#" + (i + 1) + ": ", NamedTextColor.DARK_GRAY), line)));
			} else {
				hasLines = true;
				line = FancyMessageRenderer.renderMessage(miniMessage, rawLine, player, player);
				loreLines.add(PARENT_LORE.append(combined(Component.text("#" + (i + 1) + ": ", NamedTextColor.DARK_GRAY), line)));
			}
		}
		if (hasLines)
			lore.addAll(loreLines);
		return hasLines;
	}

}
