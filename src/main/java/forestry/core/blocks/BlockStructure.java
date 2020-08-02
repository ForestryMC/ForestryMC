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
package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.circuits.ISocketable;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.multiblock.MultiblockUtil;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;

public abstract class BlockStructure extends BlockForestry {

    protected BlockStructure(Block.Properties properties) {
        super(properties.hardnessAndResistance(1f));
    }

    protected long previousMessageTick = 0;

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        if (playerIn.isSneaking()) { //isSneaking
            return ActionResultType.PASS;
        }

        MultiblockTileEntityForestry part = TileUtil.getTile(worldIn, pos, MultiblockTileEntityForestry.class);
        if (part == null) {
            return ActionResultType.FAIL;
        }
        IMultiblockController controller = part.getMultiblockLogic().getController();

        ItemStack heldItem = playerIn.getHeldItem(hand);
        // If the player's hands are empty and they right-click on a multiblock, they get a
        // multiblock-debugging message if the machine is not assembled.
        if (heldItem.isEmpty()) {
            if (controller != null) {
                if (!controller.isAssembled()) {
                    String validationError = controller.getLastValidationError();
                    if (validationError != null) {
                        long tick = worldIn.getGameTime();
                        if (tick > previousMessageTick + 20) {
                            playerIn.sendMessage(new StringTextComponent(validationError), Util.DUMMY_UUID);
                            previousMessageTick = tick;
                        }
                        return ActionResultType.SUCCESS;
                    }
                }
            } else {
                playerIn.sendMessage(new TranslationTextComponent("for.multiblock.error.notConnected"), Util.DUMMY_UUID);
                return ActionResultType.SUCCESS;
            }
        }

        // Don't open the GUI if the multiblock isn't assembled
        if (controller == null || !controller.isAssembled()) {
            return ActionResultType.PASS;
        }

        if (!worldIn.isRemote) {
            part.openGui((ServerPlayerEntity) playerIn, pos);    //TODO cast is safe because on server?
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (world.isRemote) {
            return;
        }

        if (placer instanceof PlayerEntity) {
            TileUtil.actOnTile(world, pos, MultiblockTileEntityForestry.class, tile -> {
                PlayerEntity player = (PlayerEntity) placer;
                GameProfile gameProfile = player.getGameProfile();
                tile.setOwner(gameProfile);
            });
        }
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (world.isRemote) {
            return;
        }

        TileUtil.actOnTile(world, pos, IMultiblockComponent.class, tile -> {
            // drop inventory if we're the last part remaining
            if (MultiblockUtil.getNeighboringParts(world, tile).isEmpty()) {
                if (tile instanceof IInventory) {
                    InventoryUtil.dropInventory((IInventory) tile, world, pos);
                }
                if (tile instanceof ISocketable) {
                    InventoryUtil.dropSockets((ISocketable) tile, world, pos);
                }
            }
        });

        super.harvestBlock(world, player, pos, state, te, stack);
    }

}
