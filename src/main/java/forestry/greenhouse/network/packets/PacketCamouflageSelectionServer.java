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
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import forestry.api.core.ICamouflageHandler;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.camouflage.CamouflageHandlerType;
import forestry.greenhouse.gui.ContainerCamouflageSprayCan;
import forestry.greenhouse.inventory.ItemInventoryCamouflageSprayCan;

public class PacketCamouflageSelectionServer extends PacketCamouflageSelection implements IForestryPacketServer {

	public PacketCamouflageSelectionServer(ICamouflageHandler handler, CamouflageHandlerType handlerType) {
		super(handler, handlerType);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CAMOUFLAGE_SELECTION;
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos pos = data.readBlockPos();
			CamouflageHandlerType handlerType = CamouflageHandlerType.values()[data.readShort()];
			ItemStack camouflageStack = data.readItemStack();

			TileEntity tile = TileUtil.getTile(player.world, pos);
			ICamouflageHandler handler = null;
			switch (handlerType) {
				case STRUCTURE: {
					if (tile instanceof IMultiblockComponent) {
						IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();

						if (controller instanceof ICamouflageHandler) {
							handler = (ICamouflageHandler) controller;
						}
					}
					break;
				}
				case TILE: {
					if (tile instanceof IMultiblockComponent) {
						IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();

						if (controller instanceof ICamouflageHandler) {
							handler = (ICamouflageHandler) controller;
						}
					}
					break;
				}
				case ITEM: {
					Container openContainer = player.openContainer;
					if (openContainer instanceof ContainerCamouflageSprayCan) {
						ContainerCamouflageSprayCan container = (ContainerCamouflageSprayCan) openContainer;
						ItemInventoryCamouflageSprayCan sprayCanInv = container.getItemInventory();
						if (!sprayCanInv.isEmpty()) {
							handler = sprayCanInv;
						}
					}
					break;
				}
			}

			if (handler != null) {
				if (handler.setCamouflageBlock(camouflageStack, true) && handlerType != CamouflageHandlerType.ITEM) {
					NetworkUtil.sendNetworkPacket(new PacketCamouflageSelectionClient(handler, handlerType), pos, player.world);
				}
			}
		}
	}
}
