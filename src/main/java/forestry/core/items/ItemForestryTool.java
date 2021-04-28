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
package forestry.core.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.ToolType;

import forestry.core.features.CoreItems;
import forestry.core.items.definitions.ToolTier;
import forestry.core.utils.ItemStackUtil;

public class ItemForestryTool extends ToolItem {

	private final ItemStack remnants;

	public ItemForestryTool(ItemStack remnants, float damageBonus, float speedModifier, Item.Properties properties) {
		super(damageBonus, speedModifier, ToolTier.BRONZE, CoreItems.BROKEN_BRONZE_PICKAXE.itemEqual(remnants.getItem()) ? PickaxeItem.DIGGABLES : ShovelItem.DIGGABLES, properties);
		this.remnants = remnants;
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState state) {
		if (CoreItems.BRONZE_PICKAXE.itemEqual(this)) {
			int i = this.getTier().getLevel();
			if (state.getHarvestTool() == ToolType.PICKAXE) {
				return i >= state.getHarvestLevel();
			}
			Material material = state.getMaterial();
			return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL;
		} else if (CoreItems.BROKEN_BRONZE_SHOVEL.itemEqual(this)) {
			return state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK);
		}
		return super.isCorrectToolForDrops(state);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		for (ToolType type : getToolTypes(itemstack)) {
			if (state.getBlock().isToolEffective(state, type)) {
				return speed;
			}
		}
		if (CoreItems.BRONZE_PICKAXE.itemEqual(this)) {
			Material material = state.getMaterial();
			return material != Material.METAL && material != Material.HEAVY_METAL && material != Material.STONE ? super.getDestroySpeed(itemstack, state) : speed;
		}
		return super.getDestroySpeed(itemstack, state);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		BlockPos pos = context.getClickedPos();
		World world = context.getLevel();
		Direction facing = context.getClickedFace();

		if (CoreItems.BRONZE_SHOVEL.itemEqual(this)) {
			BlockState state = world.getBlockState(pos);
			if (facing == Direction.DOWN) {
				return ActionResultType.PASS;
			} else {
				BlockState modifiedState = state.getToolModifiedState(world, pos, player, context.getItemInHand(), ToolType.SHOVEL);
				BlockState usedState = null;
				if (modifiedState != null && world.isEmptyBlock(pos.above())) {
					world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
					usedState = modifiedState;
				} else if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
					if (!world.isClientSide()) {
						world.levelEvent(null, 1009, pos, 0);
					}

					CampfireBlock.dowse(world, pos, state);
					usedState = state.setValue(CampfireBlock.LIT, Boolean.FALSE);
				}

				if (usedState != null) {
					if (!world.isClientSide) {
						world.setBlock(pos, usedState, 11);
						if (player != null) {
							context.getItemInHand().hurtAndBreak(1, player, (entity) -> {
								onBroken(entity, hand);
							});
						}
					}

					return ActionResultType.sidedSuccess(world.isClientSide);
				} else {
					return ActionResultType.PASS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	public void onBroken(LivingEntity player, Hand hand) {
		World world = player.level;

		player.broadcastBreakEvent(hand);

		if (!world.isClientSide && !remnants.isEmpty()) {
			ItemStackUtil.dropItemStackAsEntity(remnants.copy(), world, player.getX(), player.getY(), player.getZ());
		}
	}

	public void onBroken(LivingEntity player) {
		onBroken(player, Hand.MAIN_HAND);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity player) {
		stack.hurtAndBreak(2, player, this::onBroken);
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
		if (!world.isClientSide && state.getDestroySpeed(world, pos) != 0.0F) {
			stack.hurtAndBreak(1, entity, this::onBroken);
		}

		return true;
	}
}
