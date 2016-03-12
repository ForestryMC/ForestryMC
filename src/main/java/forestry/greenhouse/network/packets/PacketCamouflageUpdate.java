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
package forestry.greenhouse.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.tiles.ICamouflagedBlock;

public class PacketCamouflageUpdate extends PacketCoordinates implements IForestryPacketServer {

	private ItemStack camouflageBlock;
	private boolean isMultiblock;

	public PacketCamouflageUpdate() {
	}

	public PacketCamouflageUpdate(ICamouflagedBlock tile, boolean isMultiblock) {
		super(tile.getCoordinates());
		this.camouflageBlock = tile.getCamouflageBlock();
		this.isMultiblock = isMultiblock;
	}
	
	public PacketCamouflageUpdate(ICamouflagedBlock tile) {
		super(tile.getCoordinates());
		this.camouflageBlock = tile.getCamouflageBlock();
		this.isMultiblock = false;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.TILE_FORESTRY_CAMOUFLAGE;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeBoolean(isMultiblock);
		data.writeItemStack(camouflageBlock);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		isMultiblock = data.readBoolean();
		camouflageBlock = data.readItemStack();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) {
		TileEntity tile = getTarget(player.worldObj);
		if (tile instanceof ICamouflagedBlock) {
			if(isMultiblock){
				if(tile instanceof IMultiblockComponent){
					IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();
					if(controller instanceof ICamouflagedBlock){
						((ICamouflagedBlock) controller).setCamouflageBlock(camouflageBlock);
					}
				}
			}else{
				((ICamouflagedBlock) tile).setCamouflageBlock(camouflageBlock);
			}
		}
	}
}
