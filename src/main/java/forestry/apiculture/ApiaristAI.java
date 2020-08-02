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

package forestry.apiculture;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.apiculture.blocks.BlockApiculture;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ApiaristAI extends MoveToBlockGoal {
    private final VillagerEntity villager;
    private boolean hasDrone;
    private boolean hasPrincess;
    private final Inventory villagerInventory;

    private static final int SLOT_PRODUCT_1 = InventoryBeeHousing.SLOT_PRODUCT_1;
    private static final int SLOT_PRODUCT_COUNT = InventoryBeeHousing.SLOT_PRODUCT_COUNT;
    private static final int SLOT_QUEEN = InventoryBeeHousing.SLOT_QUEEN;
    private static final int SLOT_DRONE = InventoryBeeHousing.SLOT_DRONE;

    public ApiaristAI(VillagerEntity villager, double speed) {
        super(villager, speed, 16);
        this.villager = villager;
        villagerInventory = villager.getVillagerInventory();
    }

    @Override
    public boolean shouldExecute() {
        if (this.runDelay <= 0) {
            this.hasDrone = hasBeeType(EnumBeeType.DRONE);
            this.hasPrincess = hasBeeType(EnumBeeType.PRINCESS);
        }
        return super.shouldExecute();
    }

    //TODO - now tick?
    @Override
    public void tick() {
        super.tick();
        BlockPos housePos = this.destinationBlock.north().up();
        //		this.villager.getLookHelper().setLookPosition(housePos.getX() + 0.5D, housePos.getY(), housePos.getZ() + 0.5D, 10.0F, this.villager.getVerticalFaceSpeed());
        //TODO lookHelper
        if (this.getIsAboveDestination()) {
            World world = this.villager.world;

            TileBeeHouse beeHouse = (TileBeeHouse) TileUtil.getTile(world, housePos);
            if (beeHouse == null) {
                return;
            }
            InventoryBeeHousing inventory = (InventoryBeeHousing) beeHouse.getBeeInventory();

            //fill slots from inside bee house
            for (ItemStack stack : InventoryUtil.getStacks(inventory, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT)) {
                if (!stack.isEmpty() && stack.getItem() instanceof ItemBeeGE) {
                    EnumBeeType type = ((ItemBeeGE) stack.getItem()).getType();
                    if (inventory.getStackInSlot(SLOT_QUEEN).isEmpty() && type == EnumBeeType.PRINCESS) {
                        inventory.setQueen(stack.copy());
                        stack.setCount(0);
                    } else if (type == EnumBeeType.DRONE) {
                        stack.shrink(InventoryUtil.addStack(inventory, stack, SLOT_DRONE, 1, true));
                    }
                }

            }

            //fill slots from villager inventory
            if (inventory.getStackInSlot(SLOT_DRONE).isEmpty() || inventory.getStackInSlot(SLOT_QUEEN).isEmpty()) {
                boolean princessAdded = false;
                boolean droneAdded = false;
                for (ItemStack stack : InventoryUtil.getStacks(villagerInventory)) {
                    if (princessAdded && droneAdded) {
                        break;
                    }
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemBeeGE) {
                        EnumBeeType type = ((ItemBeeGE) stack.getItem()).getType();
                        if (type == EnumBeeType.DRONE && inventory.getStackInSlot(SLOT_DRONE).isEmpty()) {
                            InventoryUtil.addStack(inventory, stack, SLOT_DRONE, 1, true);
                            droneAdded = true;
                        } else if (type == EnumBeeType.PRINCESS && inventory.getStackInSlot(SLOT_QUEEN).isEmpty()) {
                            InventoryUtil.addStack(inventory, stack, SLOT_QUEEN, 1, true);
                            princessAdded = true;
                        }
                    }
                }
            }

            //add remaining bees to villager inventory
            for (ItemStack stack : InventoryUtil.getStacks(inventory, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT)) {
                if (stack.getItem() instanceof ItemBeeGE) {
                    InventoryUtil.addStack(villagerInventory, stack, true);
                }
            }
        }
        this.runDelay = 20;
    }

    public boolean hasBeeType(EnumBeeType type) {
        if (villagerInventory.isEmpty()) {
            return false;
        }
        for (ItemStack stack : InventoryUtil.getStacks(villagerInventory)) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBeeGE) {
                if (((ItemBeeGE) stack.getItem()).getType() == type) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader world, BlockPos pos) {
        pos = pos.north().up();
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof BlockApiculture && TileUtil.getTile(world, pos) instanceof TileBeeHouse) {
            TileBeeHouse beeHouse = (TileBeeHouse) TileUtil.getTile(world, pos);
            InventoryBeeHousing inventory = (InventoryBeeHousing) beeHouse.getBeeInventory();
            if (inventory.isEmpty()) {
                return false;
            }
            if (!inventory.getStackInSlot(SLOT_QUEEN).isEmpty()) {
                EnumBeeType type = ((ItemBeeGE) inventory.getStackInSlot(SLOT_QUEEN).getItem()).getType();
                if (type == EnumBeeType.QUEEN) {
                    return false;
                }
                if (type == EnumBeeType.PRINCESS && !inventory.getStackInSlot(SLOT_DRONE).isEmpty() && !hasDrone) {
                    return false;
                }
            }
            boolean foundPrincess = hasPrincess;
            boolean foundDrone = hasDrone;
            if (foundDrone && foundPrincess) {
                return true;
            }
            for (ItemStack stack : InventoryUtil.getStacks(inventory, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT)) {
                if (!stack.isEmpty() && stack.getItem() instanceof ItemBeeGE) {
                    EnumBeeType type = ((ItemBeeGE) stack.getItem()).getType();
                    if (type == EnumBeeType.PRINCESS) {
                        foundPrincess = true;
                    }
                    if (type == EnumBeeType.DRONE) {
                        foundDrone = true;
                    }
                    if (foundDrone && foundPrincess) {
                        return true;
                    }
                }
            }
            return false;
            //maybe use error states instead?
        }
        return false;
    }
}
