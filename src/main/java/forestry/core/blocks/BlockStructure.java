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

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.circuits.ISocketable;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.multiblock.MultiblockUtil;
import forestry.core.utils.InventoryUtil;

public abstract class BlockStructure extends BlockForestry {

	protected BlockStructure(Material material) {
		super(material);
		setHardness(1.0f);
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	protected long previousMessageTick = 0;

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			return false;
		}

		TileEntity tile = worldIn.getTileEntity(pos);
		if (!(tile instanceof MultiblockTileEntityForestry)) {
			return false;
		}

		MultiblockTileEntityForestry part = (MultiblockTileEntityForestry) tile;
		IMultiblockController controller = part.getMultiblockLogic().getController();

		// If the player's hands are empty and they right-click on a multiblock, they get a
		// multiblock-debugging message if the machine is not assembled.
		if (heldItem == null) {
			if (controller != null) {
				if (!controller.isAssembled()) {
					String validationError = controller.getLastValidationError();
					if (validationError != null) {
						long tick = worldIn.getTotalWorldTime();
						if (tick > previousMessageTick + 20) {
							playerIn.addChatMessage(new TextComponentString(validationError));
							previousMessageTick = tick;
						}
						return true;
					}
				}
			} else {
				playerIn.addChatMessage(new TextComponentTranslation("for.multiblock.error.notConnected"));
				return true;
			}
		}

		// Don't open the GUI if the multiblock isn't assembled
		if (controller == null || !controller.isAssembled()) {
			return false;
		}

		if (!worldIn.isRemote) {
			part.openGui(playerIn);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (world.isRemote) {
			return;
		}

		if (placer instanceof EntityPlayer) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof MultiblockTileEntityForestry) {
				EntityPlayer player = (EntityPlayer) placer;
				GameProfile gameProfile = player.getGameProfile();
				((MultiblockTileEntityForestry) tile).setOwner(gameProfile);
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote) {
			return;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof IMultiblockComponent) {
			IMultiblockComponent part = (IMultiblockComponent) tile;

			// drop inventory if we're the last part remaining
			if (MultiblockUtil.getNeighboringParts(world, part).isEmpty()) {
				if (tile instanceof IInventory) {
					InventoryUtil.dropInventory((IInventory) tile, world, pos);
				}
				if (tile instanceof ISocketable) {
					InventoryUtil.dropSockets((ISocketable) tile, world, pos);
				}
			}
		}
		super.breakBlock(world, pos, state);
	}

}
