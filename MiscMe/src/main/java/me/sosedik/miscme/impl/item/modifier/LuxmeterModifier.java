package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.packet.EntityDataPacketContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Controls speedometer's display & lore
 */
@NullMarked
public class LuxmeterModifier extends ItemModifier {

	private static final NamespacedKey[] MODELS = new NamespacedKey[16];

	static {
		for (int i = 0; i < MODELS.length; i++) {
			MODELS[i] = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("luxmeter/luxmeter_" + String.format("%02d", i)));
		}
	}

	public LuxmeterModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != MiscMeItems.LUXMETER) return ModificationResult.PASS;

		contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, MODELS[0]);

		if (ItemUtil.shouldFreeze(contextBox.getContext())) return ModificationResult.OK;

		Entity target = null;
		if (contextBox.getContext().getRootContext() instanceof EntityEquipmentPacketContext context) {
			target = context.getEntity();
		} else if (contextBox.getContext().getRootContext() instanceof EntityDataPacketContext context) {
			target = context.getEntity();
		}
		if (target == null) {
			target = contextBox.getViewer();
			if (target == null)
				return ModificationResult.OK;
		}
		if (!(target instanceof Player player)) return ModificationResult.OK;

		Block block = player.getLocation().getBlock();
		int lightLevel = block.getLightLevel();

		contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, MODELS[lightLevel]);

		if (contextBox.getContext().getContextType().hasVisibleLore()) {
			var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
			contextBox.addLore(messenger.getMessage("item.luxmeter.light_level",
				raw("level", lightLevel),
				raw("sky", block.getLightFromSky()),
				raw("block", block.getLightFromBlocks())
			));
		}

		return ModificationResult.OK;
	}

}
