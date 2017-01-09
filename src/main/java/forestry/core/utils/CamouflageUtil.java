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

import java.io.IOException;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.tiles.TileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CamouflageUtil {

	public static void writeCamouflageBlockToNBT(NBTTagCompound data, ICamouflageHandler handler, String type){
		ItemStack camouflageBlock = handler.getCamouflageBlock(type);
		if(camouflageBlock != null){
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			data.setTag("Camouflage" + type, nbtTag);
		}
	}
	
	public static void readCamouflageBlockFromNBT(NBTTagCompound data, ICamouflageHandler handler, String type){
		if(data.hasKey("Camouflage" + type)){
			handler.setCamouflageBlock(type, ItemStack.loadItemStackFromNBT(data.getCompoundTag("Camouflage" + type)), false);
		}
	}
	
	public static void writeCamouflageBlockToData(DataOutputStreamForestry data, ICamouflageHandler handler, String type) throws IOException{
		ItemStack camouflageBlock = handler.getCamouflageBlock(type);
		if(camouflageBlock != null){
			data.writeShort(1);
			data.writeUTF(type);
			data.writeItemStack(camouflageBlock);
		}else{
			data.writeShort(0);
		}
	}
	
	public static void readCamouflageBlockFromData(DataInputStreamForestry data, ICamouflageHandler handler) throws IOException{
		if(data.readShort() == 1){
			handler.setCamouflageBlock(data.readUTF(), data.readItemStack(), false);
		}
	}
	
	public static ICamouflageHandler getCamouflageHandler(IBlockAccess world, BlockPos pos){
		if(pos == null || world == null){
			return null;
		}
		TileEntity tile = TileUtil.getTile(world, pos, TileEntity.class);
		if(tile instanceof ICamouflagedTile){
			ICamouflagedTile block = (ICamouflagedTile) tile;
			String type = block.getCamouflageType();
			ICamouflageHandler handler = null;
			if(tile instanceof ICamouflageHandler){
				handler = (ICamouflageHandler) tile;
			}
			if((handler == null || handler.getCamouflageBlock(type) == null) && tile instanceof IMultiblockComponent){
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockController controller = component.getMultiblockLogic().getController();
				if(controller instanceof ICamouflageHandler){
					handler = (ICamouflageHandler) controller;
				}
			}
			return handler;
		}
		return null;
	}
	
	public static ItemStack getCamouflageBlock(IBlockAccess world, BlockPos pos){
		if(pos == null || world == null){
			return null;
		}
		TileEntity tile = TileUtil.getTile(world, pos, TileEntity.class);
		if(tile instanceof ICamouflagedTile){
			ICamouflagedTile block = (ICamouflagedTile) tile;
			String type = block.getCamouflageType();
			ItemStack camouflageStack = null;
			ICamouflageHandler tileHandler = null;
			ICamouflageHandler multiblockHandler = null;
			if(tile instanceof ICamouflageHandler){
				tileHandler = (ICamouflageHandler) tile;
				camouflageStack = tileHandler.getCamouflageBlock(type);
			}
			if(camouflageStack == null && tile instanceof IMultiblockComponent){
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockController controller = component.getMultiblockLogic().getController();
				if(controller.isAssembled() && controller instanceof ICamouflageHandler){
					multiblockHandler = (ICamouflageHandler) controller;
					camouflageStack = multiblockHandler.getCamouflageBlock(type);
				}
			}
			if(camouflageStack == null){
				if(tileHandler != null){
					camouflageStack = tileHandler.getDefaultCamouflageBlock(type);
				}
				if(multiblockHandler != null && camouflageStack == null){
					camouflageStack = multiblockHandler.getDefaultCamouflageBlock(type);
				}
			}
			return camouflageStack;
		}
		return null;
	}
	
}
