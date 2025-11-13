package me.sosedik.trappednewbie.listener.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.item.SoulboundNecronomicon;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.impl.item.modifier.CustomLoreModifier;
import me.sosedik.resourcelib.impl.item.modifier.CustomNameModifier;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.impl.item.modifier.CustomTotemOfUndyingModifier;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.window.Window;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Travelling through Necronomicon
 */
@NullMarked
public class NecronomiconTravel implements Listener {

	private static final String NECRONOMICON_PENDING_KEY = "necronomicon_tp";
	private static final Set<UUID> PENDING = new HashSet<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerDataSaveEvent event) {
		if (!event.isQuit()) return;
		
		Player player = event.getPlayer();
		if (!PENDING.remove(player.getUniqueId())) return;

		event.getData().setBoolean(NECRONOMICON_PENDING_KEY, true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLoad(PlayerDataLoadedEvent event) {
		if (!event.getData().hasTag(NECRONOMICON_PENDING_KEY)) return;

		Player player = event.getPlayer();
		PENDING.add(player.getUniqueId());

		if (!player.hasPotionEffect(PotionEffectType.LEVITATION))
			player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 0));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEffect(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getNewEffect() != null) return;

		PotionEffect oldEffect = event.getOldEffect();
		if (oldEffect == null) return;
		if (oldEffect.getType() != PotionEffectType.LEVITATION) return;
		if (!PENDING.remove(player.getUniqueId())) return;

		LocationUtil.smartTeleport(player, PerPlayerWorlds.resolveWorld(player, World.Environment.CUSTOM).getSpawnLocation().center(1), false)
			.thenRun(() -> {
				CustomTotemOfUndyingModifier.playTotemEffect(player, ItemStack.of(RequiemItems.NECRONOMICON));
			});
	}

	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.useItemInHand() == Event.Result.DENY) return;

		ItemStack handItem = event.getItem();
		if (!ItemStack.isType(handItem, RequiemItems.NECRONOMICON)) return;

		Player player = event.getPlayer();
		if (!SoulboundNecronomicon.isValid(player, handItem) || PossessingPlayer.isPossessing(player)) {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2F, 0.3F);
			CustomTotemOfUndyingModifier.playTotemEffect(player, handItem);
			return;
		}

		ItemStack background = ItemStack.of(TrappedNewbieItems.MATERIAL_AIR);
		background.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/filled")));

		Window.builder()
			.setTitle(Messenger.messenger(player).getMessage("gui.soul_travel"))
			.setUpperGui(Gui.builder()
				.setStructure(5, 1, "##L##")
				.addIngredient('L', Item.builder()
					.setItemProvider(viewer -> {
						var messenger = Messenger.messenger(viewer);

						int exp = player.calculateTotalExperiencePoints();
						int requiredExp = MathUtil.getExpForLevel(10);
						boolean canUse = exp >= requiredExp;

						var item = ItemStack.of(Material.BEDROCK);
						item = CustomNameModifier.named(item, messenger.getMessage("gui.soul_travel.destination.void"));
						item = CustomLoreModifier.lored(item, List.of(
							messenger.getMessages("gui.soul_travel.travel", raw("level", 10), raw("color", canUse ? "<green>" : "<red>"))
						));
						return new ItemWrapper(item);
					})
					.addClickHandler(click -> {
						if (PENDING.contains(player.getUniqueId()) || PossessingPlayer.isPossessing(player)) {
							player.closeInventory();
							player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2F, 0.3F);
							return;
						}

						int exp = player.calculateTotalExperiencePoints();
						int requiredExp = MathUtil.getExpForLevel(10);
						if (exp < requiredExp) {
							player.closeInventory();
							player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2F, 0.3F);
							player.damage(1, DamageSource.builder(TrappedNewbieDamageTypes.SUICIDE).build());
							return;
						}

						PENDING.add(player.getUniqueId());
						player.closeInventory();
						player.setExperienceLevelAndProgress(exp - requiredExp);
						player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 7, 0));
					})
				)
				.setBackground(background)
				.build()
			)
			.setViewer(player)
			.build()
			.open();
	}

}
