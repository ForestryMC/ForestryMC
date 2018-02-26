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
package forestry.core.utils;

import javax.annotation.Nullable;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.tiles.TileUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CamouflageUtil {

	@Nullable
	public static ICamouflageHandler getCamouflageHandler(IBlockAccess world, BlockPos pos) {
		TileEntity tile = TileUtil.getTile(world, pos, TileEntity.class);
		if (tile instanceof ICamouflagedTile) {
			ICamouflagedTile block = (ICamouflagedTile) tile;
			ICamouflageHandler handler = null;
			if (tile instanceof ICamouflageHandler) {
				handler = (ICamouflageHandler) tile;
			}
			if ((handler == null || handler.getCamouflageBlock().isEmpty()) && tile instanceof IMultiblockComponent) {
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockController controller = component.getMultiblockLogic().getController();
				if (controller instanceof ICamouflageHandler) {
					handler = (ICamouflageHandler) controller;
				}
			}
			return handler;
		}
		return null;
	}

	public static ItemStack getCamouflageBlock(@Nullable IBlockAccess world, @Nullable BlockPos pos) {
		if (world == null || pos == null) {
			return ItemStack.EMPTY;
		}
		TileEntity tile = TileUtil.getTile(world, pos, TileEntity.class);
		if (tile instanceof ICamouflagedTile) {
			ICamouflagedTile block = (ICamouflagedTile) tile;
			ItemStack camouflageStack = ItemStack.EMPTY;

			if (tile instanceof ICamouflageHandler) {
				ICamouflageHandler tileHandler = (ICamouflageHandler) tile;
				ItemStack tileCamouflageStack = tileHandler.getCamouflageBlock();
				ItemStack defaultCamouflageStack = tileHandler.getDefaultCamouflageBlock();
				if (!ItemStackUtil.isIdenticalItem(tileCamouflageStack, defaultCamouflageStack)) {
					camouflageStack = tileCamouflageStack;
				}
			}
			if (camouflageStack.isEmpty() && tile instanceof IMultiblockComponent) {
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockController controller = component.getMultiblockLogic().getController();
				if (controller.isAssembled() && controller instanceof ICamouflageHandler) {
					ICamouflageHandler multiblockHandler = (ICamouflageHandler) controller;
					camouflageStack = multiblockHandler.getCamouflageBlock();
				}
			}

			return camouflageStack;
		}
		return ItemStack.EMPTY;
	}

}
