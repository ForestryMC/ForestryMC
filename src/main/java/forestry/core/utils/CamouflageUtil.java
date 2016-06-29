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
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
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
			handler.setCamouflageBlock(type, ItemStack.loadItemStackFromNBT(data.getCompoundTag("Camouflage" + type)));
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
			handler.setCamouflageBlock(data.readUTF(), data.readItemStack());
		}
	}
	
	public static ICamouflageHandler getCamouflageHandler(IBlockAccess world, BlockPos pos){
		if(pos == null){
			return null;
		}
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof ICamouflagedTile){
			ICamouflagedTile block = (ICamouflagedTile) tile;
			String type = block.getCamouflageType();
			ItemStack camouflageStack = null;
			ICamouflageHandler handler = null;
			if(tile instanceof ICamouflageHandler){
				handler = (ICamouflageHandler) tile;
				camouflageStack = handler.getCamouflageBlock(type);
			}
			if(camouflageStack == null && tile instanceof IMultiblockComponent){
				IMultiblockComponent component = (IMultiblockComponent) tile;
				if(component.getMultiblockLogic().getController() instanceof ICamouflageHandler){
					handler = (ICamouflageHandler) component.getMultiblockLogic().getController();
				}
			}
			return handler;
		}
		return null;
	}
	
	public static ItemStack getCamouflageBlock(IBlockAccess world, BlockPos pos){
		if(pos == null){
			return null;
		}
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof ICamouflagedTile){
			ICamouflagedTile block = (ICamouflagedTile) tile;
			String type = block.getCamouflageType();
			ItemStack camouflageStack = null;
			if(tile instanceof ICamouflageHandler){
				ICamouflageHandler handler = (ICamouflageHandler) tile;
				camouflageStack = handler.getCamouflageBlock(type);
			}
			if(camouflageStack == null && tile instanceof IMultiblockComponent){
				IMultiblockComponent component = (IMultiblockComponent) tile;
				if(component.getMultiblockLogic().getController() instanceof ICamouflageHandler){
					ICamouflageHandler handler = (ICamouflageHandler) component.getMultiblockLogic().getController();
					camouflageStack = handler.getCamouflageBlock(type);
					if(camouflageStack == null){
						camouflageStack = handler.getDefaultCamouflageBlock(type);
					}
				}
			}
			return camouflageStack;
		}
		return null;
	}
	
}
