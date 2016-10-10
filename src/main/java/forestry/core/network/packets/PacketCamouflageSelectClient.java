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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;

public class PacketCamouflageSelectClient extends PacketCoordinates implements IForestryPacketClient{

	private ItemStack camouflageStack;
	private String camouflageType;
	private CamouflageSelectionType selectionType;

	public PacketCamouflageSelectClient() {
	}
	
	public PacketCamouflageSelectClient(ICamouflageHandler handler, String camouflageType, CamouflageSelectionType selectionType) {
		super(handler.getCoordinates());
		this.camouflageStack = handler.getCamouflageBlock(camouflageType);
		this.selectionType = selectionType;
		this.camouflageType = camouflageType;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CAMOUFLAGE_SELECTION;
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
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) {
		TileEntity tile = getTarget(player.worldObj);
		ICamouflageHandler handler = null;
		if(selectionType == CamouflageSelectionType.MULTIBLOCK){
			if (tile instanceof IMultiblockComponent) {
				IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();
				
				if (controller instanceof ICamouflageHandler) {
					handler = (ICamouflageHandler) controller;
					handler.setCamouflageBlock(camouflageType, camouflageStack);
					for (IMultiblockComponent comp : controller.getComponents()) {
						if (comp instanceof ICamouflagedTile) {
							ICamouflagedTile camBlock = (ICamouflagedTile) comp;
							if (camouflageType != null && camBlock.getCamouflageType() != null && camBlock.getCamouflageType().equals(camouflageType)) {
								player.worldObj.markBlockRangeForRenderUpdate(camBlock.getCoordinates(), camBlock.getCoordinates());
							}
						}
					}
				}
			}
		}else if(selectionType == CamouflageSelectionType.TILE){
			if (tile instanceof ICamouflageHandler) {
				handler = (ICamouflageHandler) tile;
				handler.setCamouflageBlock(camouflageType, camouflageStack);
				player.worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
			}
		}
	}
}
