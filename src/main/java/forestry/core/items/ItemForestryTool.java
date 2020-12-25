/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.items;

import forestry.core.features.CoreItems;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;

public class ItemForestryTool extends ItemForestry {
    private final ItemStack remnants;
    private float efficiencyOnProperMaterial;

    public ItemForestryTool(ItemStack remnants, Item.Properties properties) {
        super(properties);
        efficiencyOnProperMaterial = 6F;
        this.remnants = remnants;
        if (!remnants.isEmpty()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public void setEfficiencyOnProperMaterial(float efficiencyOnProperMaterial) {
        this.efficiencyOnProperMaterial = efficiencyOnProperMaterial;
    }

    @Override
    public boolean canHarvestBlock(BlockState block) {
        if (CoreItems.BRONZE_PICKAXE.itemEqual(this)) {
            Material material = block.getMaterial();
            return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
        }
        return super.canHarvestBlock(block);
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
            return material != Material.IRON && material != Material.ANVIL && material != Material.ROCK
                   ? super.getDestroySpeed(itemstack, state) : this.efficiencyOnProperMaterial;
        }
        return super.getDestroySpeed(itemstack, state);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        Direction facing = context.getFace();

        if (CoreItems.BRONZE_SHOVEL.itemEqual(this)) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (!player.canPlayerEdit(pos.offset(facing), facing, heldItem)) {
                return ActionResultType.FAIL;
            } else {
                BlockState BlockState = world.getBlockState(pos);
                Block block = BlockState.getBlock();

                if (facing != Direction.DOWN && world.getBlockState(pos.up()).getMaterial() == Material.AIR &&
                    block == Blocks.GRASS
                ) {
                    BlockState BlockState1 = Blocks.GRASS_PATH.getDefaultState();
                    world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!world.isRemote) {
                        world.setBlockState(pos, BlockState1, 11);
                        heldItem.damageItem(1, player, this::onBroken);
                    }

                    return ActionResultType.SUCCESS;
                } else {
                    return ActionResultType.PASS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    //	@SubscribeEvent
    //	public void onDestroyCurrentItem(PlayerDestroyItemEvent event) {
    //		if (event.getOriginal().isEmpty() || event.getOriginal().getItem() != this) {
    //			return;
    //		}
    //
    //		PlayerEntity player = event.getEntityPlayer();
    //		World world = player.world;
    //
    //		if (!world.isRemote && !remnants.isEmpty()) {
    //			ItemStackUtil.dropItemStackAsEntity(remnants.copy(), world, player.posX, player.posY, player.posZ);
    //		}
    //	}

    public void onBroken(LivingEntity player) {
        World world = player.world;

        if (!world.isRemote && !remnants.isEmpty()) {
            ItemStackUtil.dropItemStackAsEntity(
                    remnants.copy(),
                    world,
                    player.getPosX(),
                    player.getPosY(),
                    player.getPosZ()
            );
        }
    }

    //TODO - check the consumer is called how I think it is
    @Override
    public boolean onBlockDestroyed(
            ItemStack stack,
            World worldIn,
            BlockState state,
            BlockPos pos,
            LivingEntity entityLiving
    ) {
        if (state.getBlockHardness(worldIn, pos) != 0) {
            stack.damageItem(1, entityLiving, this::onBroken);
        }
        return true;
    }

    //TODO - block shape
    //	@Override
    //	public boolean isFull3D() {
    //		return true;
    //	}
}
