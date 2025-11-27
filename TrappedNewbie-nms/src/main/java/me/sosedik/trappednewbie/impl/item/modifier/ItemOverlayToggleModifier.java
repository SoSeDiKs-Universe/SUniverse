package me.sosedik.trappednewbie.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeItems;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.listener.misc.DynamicInventoryInfoGatherer;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@NullMarked
public class ItemOverlayToggleModifier extends ItemModifier {

	private static final Map<Material, ToggleableData> OVERLAY_ITEMS = new HashMap<>();
	private static final Component USAGE_ICON = ResourceLib.requireFontData(MiscMe.miscMeKey("usage")).icon();

	static {
		addOverlayToggleable("clock", Material.CLOCK, null);
		for (Material compass : Tag.ITEMS_COMPASSES.getValues())
			addOverlayToggleable("compass", compass, null);
		addOverlayToggleable("depth_meter", MiscMeItems.DEPTH_METER, null);
		addOverlayToggleable("speedometer", MiscMeItems.SPEEDOMETER, null);
		addOverlayToggleable("barometer", MiscMeItems.BAROMETER, player -> DynamicInventoryInfoGatherer.getInventoryData(player).hasClock());
		addOverlayToggleable("lunar_clock", MiscMeItems.LUNAR_CLOCK, player -> DynamicInventoryInfoGatherer.getInventoryData(player).hasClock());
	}

	public ItemOverlayToggleModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Player viewer = contextBox.getViewer();
		if (viewer == null) return ModificationResult.PASS;

		ToggleableData toggleableData = getOverlayToggleable(contextBox.getInitialType());
		if (toggleableData == null) return ModificationResult.PASS;
		if (toggleableData.condition() != null && !toggleableData.condition().test(viewer)) return ModificationResult.PASS;

		List<Component> texts = SpacingUtil.iconize(Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale())), USAGE_ICON, "overlay.toggle");
		contextBox.addLore(texts);

		return ModificationResult.OK;
	}

	@Override
	public boolean skipContext(ItemModifierContext context) {
		return ItemUtil.shouldFreeze(context);
	}

	public static @Nullable ToggleableData getOverlayToggleable(Material item) {
		return OVERLAY_ITEMS.get(item);
	}

	public static void addOverlayToggleable(String id, Material item, @Nullable Predicate<Player> condition) {
		OVERLAY_ITEMS.put(item, new ToggleableData(id, condition));
	}

	public record ToggleableData(String id, @Nullable Predicate<Player> condition) {}

}
