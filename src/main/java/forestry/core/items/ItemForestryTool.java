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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;

import forestry.core.features.CoreItems;
import forestry.core.utils.ItemStackUtil;

public class ItemForestryTool extends ItemForestry {
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;
	private final ItemStack remnants;
	private float efficiencyOnProperMaterial;

	public ItemForestryTool(ItemStack remnants, double damageBonus, double speedModifier, Item.Properties properties) {
		super(properties);
		efficiencyOnProperMaterial = 6F;
		this.remnants = remnants;
		if (!remnants.isEmpty()) {
			MinecraftForge.EVENT_BUS.register(this);
		}
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		double damageModifier = 1 + damageBonus;
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", damageModifier, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", speedModifier, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
	}

	public void setEfficiencyOnProperMaterial(float efficiencyOnProperMaterial) {
		this.efficiencyOnProperMaterial = efficiencyOnProperMaterial;
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState block) {
		if (CoreItems.BRONZE_PICKAXE.itemEqual(this)) {
			Material material = block.getMaterial();
			return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL;
		}
		return super.isCorrectToolForDrops(block);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		for (ToolType type : getToolTypes(itemstack)) {
			if (state.getBlock().isToolEffective(state, type)) {
				return efficiencyOnProperMaterial;
			}
		}
		if (CoreItems.BRONZE_PICKAXE.itemEqual(this)) {
			Material material = state.getMaterial();
			return material != Material.METAL && material != Material.HEAVY_METAL && material != Material.STONE ? super.getDestroySpeed(itemstack, state) : this.efficiencyOnProperMaterial;
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
			ItemStack heldItem = player.getItemInHand(hand);
			if (!player.mayUseItemAt(pos.relative(facing), facing, heldItem)) {
				return ActionResultType.FAIL;
			} else {
				BlockState BlockState = world.getBlockState(pos);
				Block block = BlockState.getBlock();

				if (facing != Direction.DOWN && world.getBlockState(pos.above()).getMaterial() == Material.AIR && block == Blocks.GRASS) {
					BlockState BlockState1 = Blocks.GRASS_PATH.defaultBlockState();
					world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

					if (!world.isClientSide) {
						world.setBlock(pos, BlockState1, 11);
						heldItem.hurtAndBreak(1, player, this::onBroken);
					}

					return ActionResultType.SUCCESS;
				} else {
					return ActionResultType.PASS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	public void onBroken(LivingEntity player) {
		World world = player.level;

		player.broadcastBreakEvent(EquipmentSlotType.MAINHAND);

		if (!world.isClientSide && !remnants.isEmpty()) {
			ItemStackUtil.dropItemStackAsEntity(remnants.copy(), world, player.getX(), player.getY(), player.getZ());
		}
	}

	@Override
	public boolean mineBlock(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (!worldIn.isClientSide && state.getDestroySpeed(worldIn, pos) != 0) {
			stack.hurtAndBreak(1, entityLiving, this::onBroken);
		}
		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return slot == EquipmentSlotType.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
	}
}
