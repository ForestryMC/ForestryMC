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
import forestry.api.core.ICamouflagedTile;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketCamouflageSelectClient extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final ItemStack camouflageStack;
	private final String camouflageType;
	private final CamouflageSelectionType selectionType;

	public PacketCamouflageSelectClient(ICamouflageHandler handler, String camouflageType, CamouflageSelectionType selectionType) {
		this.pos = handler.getCoordinates();
		this.camouflageStack = handler.getCamouflageBlock(camouflageType);
		this.selectionType = selectionType;
		this.camouflageType = camouflageType;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CAMOUFLAGE_SELECTION;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeShort(selectionType.ordinal());
		data.writeString(camouflageType);
		data.writeItemStack(camouflageStack);
	}

	public static class Handler implements IForestryPacketHandlerClient {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			CamouflageSelectionType selectionType = CamouflageSelectionType.values()[data.readShort()];
			String camouflageType = data.readString();
			ItemStack camouflageStack = data.readItemStack();

			TileEntity tile = player.world.getTileEntity(pos);
			ICamouflageHandler handler;
			if (selectionType == CamouflageSelectionType.MULTIBLOCK) {
				if (tile instanceof IMultiblockComponent) {
					IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();

					if (controller instanceof ICamouflageHandler) {
						handler = (ICamouflageHandler) controller;
						if (handler.setCamouflageBlock(camouflageType, camouflageStack, false)) {
							for (IMultiblockComponent comp : controller.getComponents()) {
								if (comp instanceof ICamouflagedTile) {
									ICamouflagedTile camBlock = (ICamouflagedTile) comp;
									if (camBlock.getCamouflageType().equals(camouflageType)) {
										player.world.markBlockRangeForRenderUpdate(camBlock.getCoordinates(), camBlock.getCoordinates());
									}
								}
							}
						}
					}
				}
			} else if (selectionType == CamouflageSelectionType.TILE) {
				if (tile instanceof ICamouflageHandler) {
					handler = (ICamouflageHandler) tile;
					if (handler.setCamouflageBlock(camouflageType, camouflageStack, false)) {
						player.world.markBlockRangeForRenderUpdate(pos, pos);
					}
				}
			}
		}
	}
}
