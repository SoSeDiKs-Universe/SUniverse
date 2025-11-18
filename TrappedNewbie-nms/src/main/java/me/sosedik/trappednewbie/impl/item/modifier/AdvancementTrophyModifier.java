package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.miscme.impl.item.modifier.BookAuthorOnlineModifier;
import me.sosedik.packetadvancements.PacketAdvancementsAPI;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Shows achiever in trophy's lore and applies unique item models
 */
@NullMarked
public class AdvancementTrophyModifier extends ItemModifier {

	private static final Map<String, NamespacedKey> MODEL_MAPPINGS = new HashMap<>();

	public AdvancementTrophyModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		String trophyId = AdvancementTrophies.getTrophyId(item);
		if (trophyId == null) return ModificationResult.PASS;

		boolean updated = false;
		NamespacedKey modelKey = MODEL_MAPPINGS.get(trophyId);
		if (modelKey != null) {
			updated = true;
			item.setData(DataComponentTypes.ITEM_MODEL, modelKey);
		}

		if (!item.hasData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) {
			item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
			updated = true;
		}

		if (!contextBox.getContext().getContextType().hasVisibleLore()) return updated ? ModificationResult.OK : ModificationResult.PASS;

		UUID achieverUuid = AdvancementTrophies.getAchiever(item);
		if (achieverUuid == null) return updated ? ModificationResult.OK : ModificationResult.PASS;

		IAdvancement advancement = AdvancementTrophies.getAdvancement(item);
		if (advancement == null) return updated ? ModificationResult.OK : ModificationResult.PASS;

		Component status = BookAuthorOnlineModifier.getStatus(achieverUuid);
		if (status == null) return updated ? ModificationResult.OK : ModificationResult.PASS;

		Component message = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale())).getMessage("advancement.trophy",
				raw("player", status),
				raw("advancement", PacketAdvancementsAPI.produceAnnounceAdvancementDisplay(contextBox.getViewer(), advancement.getDisplay(contextBox.getViewer())))
			);
		contextBox.addLore(message);

		return ModificationResult.OK;
	}

	public static void addModelMapping(String trophyId, NamespacedKey modelKey) {
		MODEL_MAPPINGS.put(trophyId, modelKey);
	}

}
