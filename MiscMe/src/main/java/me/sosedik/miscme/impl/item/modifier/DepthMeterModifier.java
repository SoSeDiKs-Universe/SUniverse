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
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Controls depth meter's display & lore
 */
@NullMarked
public class DepthMeterModifier extends ItemModifier {

	private static final NamespacedKey DEPTH_SURFACE = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("depth_meter/depth_surface"));
	private static final NamespacedKey DEPTH_CAVE = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("depth_meter/depth_cave"));
	private static final NamespacedKey DEPTH_SKY = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("depth_meter/depth_sky"));
	private static final NamespacedKey DEPTH_LAVA = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("depth_meter/depth_lava"));
	private static final NamespacedKey DEPTH_VOID = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("depth_meter/depth_void"));

	public DepthMeterModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != MiscMeItems.DEPTH_METER) return ModificationResult.PASS;

		contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, DEPTH_SURFACE);

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

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		boolean addLore = contextBox.getContext().getContextType().hasVisibleLore();

		int playerY = target.getLocation().getBlockY();

		World world = target.getWorld();
		if (world.getEnvironment() == World.Environment.NETHER) {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, DEPTH_LAVA);
			if (addLore) contextBox.addLore(messenger.getMessage("item.depth_meter.depth.nether", raw("y", playerY)));
			return ModificationResult.OK;
		}

		if (world.getEnvironment() == World.Environment.THE_END) {
			contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, DEPTH_VOID);
			if (addLore) contextBox.addLore(messenger.getMessage("item.depth_meter.depth.the_end", raw("y", playerY)));
			return ModificationResult.OK;
		}

		if (playerY < world.getMinHeight()) contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, DEPTH_VOID);
		else if (playerY >= 190) contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, DEPTH_SKY);
		else if (playerY <= 51) contextBox.getItem().setData(DataComponentTypes.ITEM_MODEL, DEPTH_CAVE);

		if (addLore) {
			int seaLevel = world.getSeaLevel();
			boolean above = playerY >= seaLevel;
			int depth = Math.abs(playerY - seaLevel);
			contextBox.addLore(messenger.getMessage("item.depth_meter.depth." + (above ? "above" : "below"),
				raw("depth", depth), raw("y", playerY)
			));
		}

		return ModificationResult.OK;
	}

}
