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
package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import forestry.api.core.ICamouflageHandler;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.gui.ContainerCamouflageSprayCan;
import forestry.core.inventory.ItemInventoryCamouflageSprayCan;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.proxy.Proxies;

public class PacketCamouflageSelectServer extends PacketCoordinates implements IForestryPacketServer{

	private ItemStack camouflageStack;
	private String camouflageType;
	private CamouflageSelectionType selectionType;

	public PacketCamouflageSelectServer() {
	}
	
	public PacketCamouflageSelectServer(ICamouflageHandler handler, String camouflageType, CamouflageSelectionType selectionType) {
		super(handler.getCoordinates());
		this.camouflageStack = handler.getCamouflageBlock(camouflageType);
		this.selectionType = selectionType;
		this.camouflageType = camouflageType;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CAMOUFLAGE_SELECTION;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeShort(selectionType.ordinal());
		data.writeBoolean(camouflageType != null);
		if(camouflageType != null){
			data.writeUTF(camouflageType);
		}
		data.writeItemStack(camouflageStack);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		selectionType = CamouflageSelectionType.values()[data.readShort()];
		if(data.readBoolean()){
			camouflageType = data.readUTF();
		}
		camouflageStack = data.readItemStack();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) {
		TileEntity tile = getTarget(player.worldObj);
		ICamouflageHandler handler = null;
		if(selectionType == CamouflageSelectionType.MULTIBLOCK){
			if (tile instanceof IMultiblockComponent) {
				IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();
				
				if (controller instanceof ICamouflageHandler) {
					handler = (ICamouflageHandler) controller;
				}
				
			}
		}else if(selectionType == CamouflageSelectionType.TILE){
			if (tile instanceof ICamouflageHandler) {
				handler = (ICamouflageHandler) tile;
			}
		}else{
			if(player.openContainer instanceof ContainerCamouflageSprayCan){
				ContainerCamouflageSprayCan container = (ContainerCamouflageSprayCan) player.openContainer;
				ItemInventoryCamouflageSprayCan itemInventoryCamouflageSprayCan = container.getItemInventory();
				if(itemInventoryCamouflageSprayCan != null){
					handler = itemInventoryCamouflageSprayCan;
				}
			}
		}
		
		if (handler != null) {
			handler.setCamouflageBlock(camouflageType, camouflageStack);
			if(selectionType != CamouflageSelectionType.ITEM){
				Proxies.net.sendNetworkPacket(new PacketCamouflageSelectClient(handler, camouflageType, selectionType), player.worldObj);
			}
		}
	}
}
