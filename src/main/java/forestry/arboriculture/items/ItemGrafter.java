/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.IToolGrafter;
import forestry.api.core.ItemGroups;
import forestry.core.data.ForestryTags;
import forestry.core.items.ItemForestry;

public class ItemGrafter extends ItemForestry implements IToolGrafter {

	public ItemGrafter(int maxDamage) {
		super(new Item.Properties()
				.durability(maxDamage)
				.tab(ItemGroups.tabArboriculture));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
		super.appendHoverText(stack, world, tooltip, advanced);
		if (!stack.isDamaged()) {
			tooltip.add(Component.translatable("item.forestry.uses", stack.getMaxDamage() + 1).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState state) {
		Block block = state.getBlock();
		return block instanceof LeavesBlock ||
				state.getMaterial() == Material.LEAVES ||
				state.is(BlockTags.LEAVES) ||
				super.isCorrectToolForDrops(state);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		if (state.is(ForestryTags.Blocks.MINEABLE_GRAFTER)) {
			return 4.0F;
		} else {
			return 1.0F;
		}
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
		if (!world.isClientSide && !state.is(BlockTags.FIRE)) {
			stack.hurtAndBreak(1, entity, (p_220036_0_) -> {
				p_220036_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		}
		return state.is(BlockTags.LEAVES);
	}

	@Override
	public float getSaplingModifier(ItemStack stack, Level world, Player player, BlockPos pos) {
		return 100f;
	}
}
