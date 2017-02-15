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

import forestry.api.core.ICamouflageHandler;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.gui.ContainerCamouflageSprayCan;
import forestry.core.inventory.ItemInventoryCamouflageSprayCan;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.utils.NetworkUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketCamouflageSelectServer extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final ItemStack camouflageStack;
	private final String camouflageType;
	private final CamouflageSelectionType selectionType;

	public PacketCamouflageSelectServer(ICamouflageHandler handler, String camouflageType, CamouflageSelectionType selectionType) {
		this.pos = handler.getCoordinates();
		this.camouflageStack = handler.getCamouflageBlock(camouflageType);
		this.selectionType = selectionType;
		this.camouflageType = camouflageType;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CAMOUFLAGE_SELECTION;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeShort(selectionType.ordinal());
		data.writeString(camouflageType);
		data.writeItemStack(camouflageStack);
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos pos = data.readBlockPos();
			CamouflageSelectionType selectionType = CamouflageSelectionType.values()[data.readShort()];
			String camouflageType = data.readString();
			ItemStack camouflageStack = data.readItemStack();

			TileEntity tile = player.world.getTileEntity(pos);
			ICamouflageHandler handler = null;
			if (selectionType == CamouflageSelectionType.MULTIBLOCK) {
				if (tile instanceof IMultiblockComponent) {
					IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();

					if (controller instanceof ICamouflageHandler) {
						handler = (ICamouflageHandler) controller;
					}

				}
			} else if (selectionType == CamouflageSelectionType.TILE) {
				if (tile instanceof ICamouflageHandler) {
					handler = (ICamouflageHandler) tile;
				}
			} else {
				if (player.openContainer instanceof ContainerCamouflageSprayCan) {
					ContainerCamouflageSprayCan container = (ContainerCamouflageSprayCan) player.openContainer;
					ItemInventoryCamouflageSprayCan itemInventoryCamouflageSprayCan = container.getItemInventory();
					if (!itemInventoryCamouflageSprayCan.isEmpty()) {
						handler = itemInventoryCamouflageSprayCan;
					}
				}
			}

			if (handler != null) {
				if (handler.setCamouflageBlock(camouflageType, camouflageStack, true) && selectionType != CamouflageSelectionType.ITEM) {
					NetworkUtil.sendNetworkPacket(new PacketCamouflageSelectClient(handler, camouflageType, selectionType), pos, player.world);
				}
			}
		}
	}
}
