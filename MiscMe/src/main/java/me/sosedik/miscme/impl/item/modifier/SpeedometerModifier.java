package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.packet.EntityDataPacketContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.miscme.listener.player.PlayerSpeedTracker;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Controls speedometer's display & lore
 */
@NullMarked
public class SpeedometerModifier extends ItemModifier {

	public static final double MAX_SPEED = 48D;

	private static final NamespacedKey[] MODELS = new NamespacedKey[13];

	static {
		for (int i = 0; i < MODELS.length; i++) {
			MODELS[i] = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("speedometer/speedometer_" + String.format("%02d", i)));
		}
	}

	public SpeedometerModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != MiscMeItems.SPEEDOMETER) return ModificationResult.PASS;

		contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, MODELS[0]);

		if (ClockModifier.shouldFreeze(contextBox)) return ModificationResult.OK;

		Entity target = null;
		if (contextBox.getContext() instanceof EntityEquipmentPacketContext context) {
			target = context.getEntity();
		} else if (contextBox.getContext() instanceof EntityDataPacketContext context) {
			target = context.getEntity();
		}
		if (target == null) {
			target = contextBox.getViewer();
			if (target == null)
				return ModificationResult.OK;
		}
		if (!(target instanceof Player player)) return ModificationResult.OK;

		double speed = PlayerSpeedTracker.getSpeed(player);
		int model = Math.min(12, (int) ((12 * speed) / MAX_SPEED));
		if (model == 0 && speed > 0) model++;

		contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, MODELS[model]);

		if (contextBox.getContextType().hasVisibleLore()) {
			var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
			contextBox.addLore(messenger.getMessage("item.speedometer.speed",
				raw("speed", speed)
			));
		}

		return ModificationResult.OK;
	}

}
