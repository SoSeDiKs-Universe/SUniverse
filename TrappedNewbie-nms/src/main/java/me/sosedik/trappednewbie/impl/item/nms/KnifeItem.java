package me.sosedik.trappednewbie.impl.item.nms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;

// Mostly copied from ShearsItem
@NullMarked
public class KnifeItem extends Item {

	public KnifeItem(Properties properties) {
		super(properties);
	}

	public static Tool createToolProperties() {
		return ShearsItem.createToolProperties();
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity livingEntity) {
		Tool tool = stack.get(DataComponents.TOOL);
		if (tool == null) return false;

		if (!state.is(BlockTags.FIRE) && tool.damagePerBlock() > 0) {
			stack.hurtAndBreak(tool.damagePerBlock(), livingEntity, EquipmentSlot.MAINHAND);
		}

		return true;
	}

}
