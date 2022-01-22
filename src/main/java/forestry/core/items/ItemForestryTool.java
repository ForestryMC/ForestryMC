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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.common.ToolActions;

import forestry.core.features.CoreItems;
import forestry.core.items.definitions.ToolTier;
import forestry.core.utils.ItemStackUtil;

public class ItemForestryTool extends DiggerItem {

	private final ItemStack remnants;

	public ItemForestryTool(ItemStack remnants, float damageBonus, float speedModifier, Item.Properties properties) {
		super(damageBonus, speedModifier, ToolTier.BRONZE, CoreItems.BROKEN_BRONZE_PICKAXE.itemEqual(remnants.getItem()) ? BlockTags.MINEABLE_WITH_PICKAXE : BlockTags.MINEABLE_WITH_SHOVEL, properties);
		this.remnants = remnants;
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState state) {
		if (CoreItems.BRONZE_PICKAXE.itemEqual(this)) {
			int i = this.getTier().getLevel();
			if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
				return i >= 0; //state.getHarvestLevel();
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
		// for (ToolType type : getToolTypes(itemstack)) {
		// 	if (state.getBlock().isToolEffective(state, type)) {
		// 		return speed;
		// 	}
		// }
		if (CoreItems.BRONZE_PICKAXE.itemEqual(this)) {
			Material material = state.getMaterial();
			return material != Material.METAL && material != Material.HEAVY_METAL && material != Material.STONE ? super.getDestroySpeed(itemstack, state) : speed;
		}
		return super.getDestroySpeed(itemstack, state);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		BlockPos pos = context.getClickedPos();
		Level world = context.getLevel();
		Direction facing = context.getClickedFace();

		if (CoreItems.BRONZE_SHOVEL.itemEqual(this)) {
			BlockState state = world.getBlockState(pos);
			if (facing == Direction.DOWN) {
				return InteractionResult.PASS;
			} else {
				BlockState modifiedState = state.getToolModifiedState(world, pos, player, context.getItemInHand(), ToolActions.SHOVEL_FLATTEN);
				BlockState usedState = null;
				if (modifiedState != null && world.isEmptyBlock(pos.above())) {
					world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
					usedState = modifiedState;
				} else if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
					if (!world.isClientSide()) {
						world.levelEvent(null, 1009, pos, 0);
					}

					CampfireBlock.dowse(player, world, pos, state);
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

					return InteractionResult.sidedSuccess(world.isClientSide);
				} else {
					return InteractionResult.PASS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	public void onBroken(LivingEntity player, InteractionHand hand) {
		Level world = player.level;

		player.broadcastBreakEvent(hand);

		if (!world.isClientSide && !remnants.isEmpty()) {
			ItemStackUtil.dropItemStackAsEntity(remnants.copy(), world, player.getX(), player.getY(), player.getZ());
		}
	}

	public void onBroken(LivingEntity player) {
		onBroken(player, InteractionHand.MAIN_HAND);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity player) {
		stack.hurtAndBreak(2, player, this::onBroken);
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
		if (!world.isClientSide && state.getDestroySpeed(world, pos) != 0.0F) {
			stack.hurtAndBreak(1, entity, this::onBroken);
		}

		return true;
	}
}
