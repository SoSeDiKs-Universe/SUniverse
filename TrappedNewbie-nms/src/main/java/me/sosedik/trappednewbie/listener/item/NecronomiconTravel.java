package me.sosedik.trappednewbie.listener.item;

import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.sosedik.miscme.dataset.MoreMobHeads;
import me.sosedik.miscme.impl.item.modifier.BookAuthorOnlineModifier;
import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.item.SoulboundNecronomicon;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.impl.item.modifier.CustomLoreModifier;
import me.sosedik.resourcelib.impl.item.modifier.CustomNameModifier;
import me.sosedik.socializer.listener.FriendlyPlayers;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.world.LimboWorldFall;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.impl.item.modifier.CustomTotemOfUndyingModifier;
import me.sosedik.utilizer.util.ItemUtil;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import me.sosedik.utilizer.util.MetadataUtil;
import net.kyori.adventure.text.Component;
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
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Travelling through Necronomicon
 */
@NullMarked
public class NecronomiconTravel implements Listener {

	private static final String NECRONOMICON_PENDING_KEY = "necronomicon_tp";
	private static final Map<UUID, @Nullable UUID> PENDING = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerDataSaveEvent event) {
		if (!event.isQuit()) return;
		
		Player player = event.getPlayer();
		if (!PENDING.containsKey(player.getUniqueId())) return;

		UUID worldOwnerUuid = PENDING.remove(player.getUniqueId());
		if (worldOwnerUuid == null)
			event.getData().setBoolean(NECRONOMICON_PENDING_KEY, true);
		else
			event.getData().setUUID(NECRONOMICON_PENDING_KEY, worldOwnerUuid);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLoad(PlayerDataLoadedEvent event) {
		ReadWriteNBT data = event.getData();
		if (!data.hasTag(NECRONOMICON_PENDING_KEY)) return;

		Player player = event.getPlayer();
		PENDING.put(player.getUniqueId(), data.hasTag(NECRONOMICON_PENDING_KEY, NBTType.NBTTagByte) ? null : data.getUUID(NECRONOMICON_PENDING_KEY));

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
		if (!PENDING.containsKey(player.getUniqueId())) return;

		UUID worldOwnerUuid = PENDING.remove(player.getUniqueId());
		World world;
		if (worldOwnerUuid == null) {
			world = PerPlayerWorlds.resolveWorld(player, World.Environment.CUSTOM);
			if (player.getWorld() == world)
				world = Utilizer.limboWorld();
		} else {
			world = PerPlayerWorlds.getPersonalWorld(worldOwnerUuid);
		}
		LocationUtil.smartTeleport(player, world.getSpawnLocation().center(1), false)
			.thenRun(() -> CustomTotemOfUndyingModifier.playTotemEffect(player, ItemStack.of(RequiemItems.NECRONOMICON)));
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

		player.swingMainHand();
		if (PENDING.containsKey(player.getUniqueId()) || MetadataUtil.hasMetadata(player, LocationUtil.PENDING_TP_META) || FreeFall.isLeaping(player)) {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2F, 0.3F);
			return;
		}

		if (player.getWorld() == Utilizer.limboWorld() || player.getWorld().key().value().startsWith("worlds-personal/")) {
			openFriendsTP(player);
		} else {
			openVoidTP(player);
		}
	}

	private void openFriendsTP(Player player) {
		List<Item> menuItems = new ArrayList<>();
		menuItems.add(getItem(player, null));
		menuItems.add(getItem(player, player.getUniqueId()));
		FriendlyPlayers.getFriendshipData(player).getFriends()
			.forEach(uuid -> menuItems.add(getItem(player, uuid)));
		LimboWorldFall.openTPMenu(player, menuItems);
	}

	private Item getItem(Player player, @Nullable UUID worldOwnerUuid) {
		return Item.builder()
			.setItemProvider(viewer -> {
				var messenger = Messenger.messenger(viewer);

				boolean freeUse = player.getWorld() == Utilizer.limboWorld();
				boolean canUse;
				if (freeUse) {
					canUse = true;
				} else {
					int exp = player.calculateTotalExperiencePoints();
					int requiredExp = MathUtil.getExpForLevel(1);
					canUse = exp >= requiredExp;
				}

				ItemStack item;
				if (worldOwnerUuid == null) {
					item = ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER);
				} else {
					item = ItemStack.of(Material.PLAYER_HEAD);
					item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().uuid(worldOwnerUuid).build());
				}
				if (worldOwnerUuid == null) {
					item = CustomNameModifier.named(item, messenger.getMessage("gui.soul_travel.destination.limbo"));
				} else {
					Component status = BookAuthorOnlineModifier.getStatus(worldOwnerUuid);
					if (status == null) status = Component.text(worldOwnerUuid.toString());
					item = CustomNameModifier.named(item, messenger.getMessage("gui.soul_travel.destination.void", raw("owner", status)));
				}
				item = CustomLoreModifier.lored(item, List.of(
					messenger.getMessages("gui.soul_travel.travel", raw("level", freeUse ? 0 : 1), raw("color", canUse ? "<green>" : "<red>"))
				));
				item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.PROFILE).build());
				return new ItemWrapper(item);
			})
			.addClickHandler(click -> {
				if (PENDING.containsKey(player.getUniqueId()) || PossessingPlayer.isPossessing(player)) {
					player.closeInventory();
					player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2F, 0.3F);
					return;
				}

				if (player.getWorld() != Utilizer.limboWorld()) {
					int exp = player.calculateTotalExperiencePoints();
					int requiredExp = MathUtil.getExpForLevel(1);
					if (exp < requiredExp) {
						player.closeInventory();
						player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2F, 0.3F);
						player.damage(1, DamageSource.builder(TrappedNewbieDamageTypes.SUICIDE).build());
						return;
					}
					player.setExperienceLevelAndProgress(exp - requiredExp);
				}

				PENDING.put(player.getUniqueId(), worldOwnerUuid);
				player.closeInventory();
				player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 3, 0));
			})
			.build();
	}

	private void openVoidTP(Player player) {
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
						item = CustomNameModifier.named(item, messenger.getMessage("gui.soul_travel.destination.void", raw("owner", player.displayName())));
						item = CustomLoreModifier.lored(item, List.of(
							messenger.getMessages("gui.soul_travel.travel", raw("level", 10), raw("color", canUse ? "<green>" : "<red>"))
						));
						return new ItemWrapper(item);
					})
					.addClickHandler(click -> {
						if (PENDING.containsKey(player.getUniqueId()) || PossessingPlayer.isPossessing(player)) {
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

						PENDING.put(player.getUniqueId(), null);
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
