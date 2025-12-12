package me.sosedik.trappednewbie.impl.hud;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.hud.SimpleHudRenderer;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

// MCCheck: 1.21.11, new armor
@NullMarked
public class ArmorRenderer extends SimpleHudRenderer {

	private static final int DEFENCE_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("armor/defence_points")).width() + 1;
	private static final int ARMOR_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("armor/elytra")).width() + 1;
	private static final int CHAR_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("armor/digit_0")).width() + 1;
	private static final int HUD_OFFSET = -91;
	private static final Component[] DEFENCE_POINTS = new Component[] {mapping("armor/defence_points"), mapping("armor/defence_points_up")};
	private static final Component[] ELYTRA = new Component[] {mapping("armor/elytra"), mapping("armor/elytra_up")};
	private static final Component[] TURTLE_HELMET = new Component[] {mapping("armor/turtle_helmet"), mapping("armor/turtle_helmet_up")};
	private static final Component[] CARVED_PUMPKIN = new Component[] {mapping("armor/carved_pumpkin"), mapping("armor/carved_pumpkin_up")};
	private static final Component[] SADDLE = new Component[] {mapping("armor/saddle"), mapping("armor/saddle_up")};
	private static final Component[] CHAINMAIL_BUCKET = new Component[] {mapping("armor/chainmail_bucket"), mapping("armor/chainmail_bucket_up")};
	private static final Component[] LEATHER_GLOVES = new Component[] {mapping("armor/leather_gloves"), mapping("armor/leather_gloves_up")};
	private static final Component[][] LEATHER_ARMOR = armor("leather");
	private static final Component[][] CHAINMAIL_ARMOR = armor("chainmail");
	private static final Component[][] COPPER_ARMOR = armor("copper");
	private static final Component[][] IRON_ARMOR = armor("iron");
	private static final Component[][] GOLDEN_ARMOR = armor("golden");
	private static final Component[][] DIAMOND_ARMOR = armor("diamond");
	private static final Component[][] NETHERITE_ARMOR = armor("netherite");
	private static final Component[] PET_HEART = new Component[] {mapping("armor/pet_heart"), mapping("armor/pet_heart_up")};
	private static final Component[] TIMES = new Component[] {mapping("armor/times"), mapping("armor/times_up")};
	private static final Component[][] DIGITS = new Component[][] {
		new Component[] {
			mapping("armor/digit_0"),
			mapping("armor/digit_1"),
			mapping("armor/digit_2"),
			mapping("armor/digit_3"),
			mapping("armor/digit_4"),
			mapping("armor/digit_5"),
			mapping("armor/digit_6"),
			mapping("armor/digit_7"),
			mapping("armor/digit_8"),
			mapping("armor/digit_9")
		},
		new Component[] {
			mapping("armor/digit_0_up"),
			mapping("armor/digit_1_up"),
			mapping("armor/digit_2_up"),
			mapping("armor/digit_3_up"),
			mapping("armor/digit_4_up"),
			mapping("armor/digit_5_up"),
			mapping("armor/digit_6_up"),
			mapping("armor/digit_7_up"),
			mapping("armor/digit_8_up"),
			mapping("armor/digit_9_up")
		}
	};
	private static final Component[][] DURABILITY_BAR = new Component[][] {
		new Component[] {
			mapping("armor/durability_bar-1"),
			mapping("armor/durability_bar-2"),
			mapping("armor/durability_bar-3"),
			mapping("armor/durability_bar-4"),
			mapping("armor/durability_bar-5")
		},
		new Component[] {
			mapping("armor/durability_bar_up-1"),
			mapping("armor/durability_bar_up-2"),
			mapping("armor/durability_bar_up-3"),
			mapping("armor/durability_bar_up-4"),
			mapping("armor/durability_bar_up-5")
		},
	};

	private final List<Component> display = new ArrayList<>();
	private final List<Component> defenceDisplay = new ArrayList<>();
	private final List<Component> petDisplay = new ArrayList<>();
	private final List<Component> armorDisplay = new ArrayList<>();

	public ArmorRenderer(Player player) {
		super("armor_hud", player);
	}

	@Override
	public @Nullable Component render() {
		if (this.player.getGameMode().isInvulnerable()) return null;
		if (GhostyPlayer.isGhost(this.player)) return null;

		int length = 0;
		this.display.clear();
		this.defenceDisplay.clear();
		this.petDisplay.clear();
		this.armorDisplay.clear();

		boolean moveUp = AbsorptionRenderer.getAbsorption(this.player) > 0;

		PlayerInventory inv = this.player.getInventory();
		ItemStack helmet = inv.getHelmet();
		ItemStack chestplate = inv.getChestplate();
		ItemStack leggings = inv.getLeggings();
		ItemStack boots = inv.getBoots();

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(this.player);
		ItemStack gloves = visualArmor.canUseVisualArmor() && visualArmor.hasGloves() ? visualArmor.getGloves() : null;

		int armorPoints = (int) (getArmor(helmet) + getArmor(chestplate) + getArmor(leggings) + getArmor(boots));
		refreshDigits(DEFENCE_POINTS, this.defenceDisplay, armorPoints, moveUp);

		if (!this.defenceDisplay.isEmpty()) {
			Component combined = combine(SpacingUtil.getNegativePixel(), this.defenceDisplay);
			this.display.add(combined);
			length += (DEFENCE_WIDTH - 1) + (CHAR_WIDTH - 1) * (String.valueOf(armorPoints).length() + 1) + 1;
		}

		if (this.player.getVehicle() instanceof Vehicle vehicle && !vehicle.hasRider() && vehicle instanceof LivingEntity entity) {
			int health = (int) Math.round(entity.getHealth());
			refreshDigits(PET_HEART, this.petDisplay, health, moveUp);

			if (!this.petDisplay.isEmpty()) {
				Component combined = combine(SpacingUtil.getNegativePixel(), this.petDisplay);
				this.display.add(combined);
				length += (DEFENCE_WIDTH - 1) + (CHAR_WIDTH - 1) * (String.valueOf(health).length() + 1) + 1;
			}
		}

		renderArmor(helmet, moveUp);
		renderArmor(chestplate, moveUp);
		renderArmor(leggings, moveUp);
		renderArmor(boots, moveUp);
		renderArmor(gloves, moveUp);
		if (visualArmor.hasHelmet() && visualArmor.getHelmet().getType() == Material.SADDLE)
			renderArmor(visualArmor.getHelmet(), moveUp);

		if (!this.armorDisplay.isEmpty()) {
			Component combined = combine(SpacingUtil.getSpacing(-2), this.armorDisplay);
			this.display.add(combined);
			length += (ARMOR_WIDTH - 2) * this.armorDisplay.size() + 2;
		}

		if (this.display.isEmpty()) return null;

		Component combined = combined(this.display);
		return SpacingUtil.getOffset(HUD_OFFSET, length, combined.shadowColor(ShadowColor.none()));
	}

	private void renderArmor(@Nullable ItemStack item, boolean up) {
		if (item == null) return;

		Component render = getArmorIcon(item, up);
		if (render == null) return;
		if (DurabilityUtil.isBroken(item)) return;

		if (item.hasData(DataComponentTypes.MAX_DAMAGE)) {
			int durability = DurabilityUtil.getDurability(item);
			if (durability > 0) {
				int maxDurability = Objects.requireNonNull(item.getData(DataComponentTypes.MAX_DAMAGE));
				double percentage = ((double) durability) / maxDurability;
				if (percentage < 0.51) {
					Component durabilityBar;
					if (percentage > 0.45)
						durabilityBar = DURABILITY_BAR[up ? 1 : 0][0];
					else if (percentage > 0.35)
						durabilityBar = DURABILITY_BAR[up ? 1 : 0][1];
					else if (percentage > 0.2)
						durabilityBar = DURABILITY_BAR[up ? 1 : 0][2];
					else if (percentage > 0.05 && (durability > 10 || maxDurability < 11))
						durabilityBar = DURABILITY_BAR[up ? 1 : 0][3];
					else
						durabilityBar = DURABILITY_BAR[up ? 1 : 0][4];

					render = combine(SpacingUtil.getNegativePixel(), render, SpacingUtil.getOffset(-ARMOR_WIDTH + 1, ARMOR_WIDTH - 1, durabilityBar));
				}
			}
		}

		this.armorDisplay.add(render);
	}

	private @Nullable Component getArmorIcon(ItemStack item, boolean up) {
		return switch (item.getType()) {
			case Material m when m == TrappedNewbieItems.LEATHER_GLOVES -> LEATHER_GLOVES[up ? 1 : 0].color(color(item));
			case Material m when m == TrappedNewbieItems.CHAINMAIL_BUCKET -> CHAINMAIL_BUCKET[up ? 1 : 0];
			case ELYTRA -> ELYTRA[up ? 1 : 0];
			case TURTLE_HELMET -> TURTLE_HELMET[up ? 1 : 0];
			case CARVED_PUMPKIN -> CARVED_PUMPKIN[up ? 1 : 0];
			case SADDLE -> SADDLE[up ? 1 : 0];
			case LEATHER_HELMET -> getArmorIcon(LEATHER_ARMOR, up, 0).color(color(item));
			case LEATHER_CHESTPLATE -> getArmorIcon(LEATHER_ARMOR, up, 1).color(color(item));
			case LEATHER_LEGGINGS -> getArmorIcon(LEATHER_ARMOR, up, 2).color(color(item));
			case LEATHER_BOOTS -> getArmorIcon(LEATHER_ARMOR, up, 3).color(color(item));
			case CHAINMAIL_HELMET -> getArmorIcon(CHAINMAIL_ARMOR, up, 0);
			case CHAINMAIL_CHESTPLATE -> getArmorIcon(CHAINMAIL_ARMOR, up, 1);
			case CHAINMAIL_LEGGINGS -> getArmorIcon(CHAINMAIL_ARMOR, up, 2);
			case CHAINMAIL_BOOTS -> getArmorIcon(CHAINMAIL_ARMOR, up, 3);
			case COPPER_HELMET -> getArmorIcon(COPPER_ARMOR, up, 0);
			case COPPER_CHESTPLATE -> getArmorIcon(COPPER_ARMOR, up, 1);
			case COPPER_LEGGINGS -> getArmorIcon(COPPER_ARMOR, up, 2);
			case COPPER_BOOTS -> getArmorIcon(COPPER_ARMOR, up, 3);
			case IRON_HELMET -> getArmorIcon(IRON_ARMOR, up, 0);
			case IRON_CHESTPLATE -> getArmorIcon(IRON_ARMOR, up, 1);
			case IRON_LEGGINGS -> getArmorIcon(IRON_ARMOR, up, 2);
			case IRON_BOOTS -> getArmorIcon(IRON_ARMOR, up, 3);
			case GOLDEN_HELMET -> getArmorIcon(GOLDEN_ARMOR, up, 0);
			case GOLDEN_CHESTPLATE -> getArmorIcon(GOLDEN_ARMOR, up, 1);
			case GOLDEN_LEGGINGS -> getArmorIcon(GOLDEN_ARMOR, up, 2);
			case GOLDEN_BOOTS -> getArmorIcon(GOLDEN_ARMOR, up, 3);
			case DIAMOND_HELMET -> getArmorIcon(DIAMOND_ARMOR, up, 0);
			case DIAMOND_CHESTPLATE -> getArmorIcon(DIAMOND_ARMOR, up, 1);
			case DIAMOND_LEGGINGS -> getArmorIcon(DIAMOND_ARMOR, up, 2);
			case DIAMOND_BOOTS -> getArmorIcon(DIAMOND_ARMOR, up, 3);
			case NETHERITE_HELMET -> getArmorIcon(NETHERITE_ARMOR, up, 0);
			case NETHERITE_CHESTPLATE -> getArmorIcon(NETHERITE_ARMOR, up, 1);
			case NETHERITE_LEGGINGS -> getArmorIcon(NETHERITE_ARMOR, up, 2);
			case NETHERITE_BOOTS -> getArmorIcon(NETHERITE_ARMOR, up, 3);
			default -> null;
		};
	}

	private TextColor color(ItemStack item) {
		if (!item.hasData(DataComponentTypes.DYED_COLOR))
			return Bukkit.getItemFactory().getDefaultLeatherColor();

		DyedItemColor dyedItemColor = item.getData(DataComponentTypes.DYED_COLOR);
		assert dyedItemColor != null;
		return dyedItemColor.color();
	}

	private Component getArmorIcon(Component[][] icons, boolean up, int piece) {
		return icons[up ? 1 : 0][piece];
	}

	private void refreshDigits(Component[] icon, List<Component> list, int points, boolean up) {
		if (points <= 0) return;

		list.add(icon[up ? 1 : 0]);

		list.add(TIMES[up ? 1 : 0]);
		for (char ch : String.valueOf(points).toCharArray()) {
			int digit = Integer.parseInt(String.valueOf(ch));
			list.add(DIGITS[up ? 1 : 0][digit]);
		}
	}

	private double getArmor(@Nullable ItemStack item) {
		if (item == null) return 0D;
		if (DurabilityUtil.isBroken(item)) return 0D;
		return ItemUtil.getAttributeValue(item, Attribute.ARMOR, this.player);
	}

	private static Component[][] armor(String key) {
		key = "armor/" + key;
		return new Component[][]{
			new Component[]{mapping(key + "_helmet"), mapping(key + "_chestplate"), mapping(key + "_leggings"), mapping(key + "_boots")},
			new Component[]{mapping(key + "_helmet_up"), mapping(key + "_chestplate_up"), mapping(key + "_leggings_up"), mapping(key + "_boots_up")}
		};
	}

}
