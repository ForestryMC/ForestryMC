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
import forestry.api.core.EnumCamouflageType;
import forestry.api.core.ICamouflageHandler;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.network.packets.PacketCoordinates;

public class PacketCamouflageUpdate extends PacketCoordinates implements IForestryPacketServer {

	private ItemStack camouflageBlock;
	private EnumCamouflageType type;
	private boolean isMultiblock;

	public PacketCamouflageUpdate() {
	}

	public PacketCamouflageUpdate(ICamouflageHandler tile, EnumCamouflageType type, boolean isMultiblock) {
		super(tile.getCoordinates());
		this.camouflageBlock = tile.getCamouflageBlock(type);
		this.isMultiblock = isMultiblock;
		this.type = type;
	}
	
	public PacketCamouflageUpdate(ICamouflageHandler tile, EnumCamouflageType type) {
		super(tile.getCoordinates());
		this.camouflageBlock = tile.getCamouflageBlock(type);
		this.isMultiblock = false;
		this.type = type;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.TILE_FORESTRY_CAMOUFLAGE;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeBoolean(isMultiblock);
		data.writeShort(type.ordinal());
		data.writeItemStack(camouflageBlock);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		isMultiblock = data.readBoolean();
		type = EnumCamouflageType.VALUES[data.readShort()];
		camouflageBlock = data.readItemStack();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) {
		TileEntity tile = getTarget(player.worldObj);
		ICamouflageHandler handler = null;
		if(isMultiblock && tile instanceof IMultiblockComponent){
			IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();
			if(controller instanceof ICamouflageHandler){
				handler = (ICamouflageHandler) controller;
			}
		}
		else if (tile instanceof ICamouflageHandler) {
			handler = (ICamouflageHandler) tile;
		}
		if(handler != null){
			handler.setCamouflageBlock(type, camouflageBlock);
		}
	}
}
