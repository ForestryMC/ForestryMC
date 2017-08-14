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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;
import forestry.greenhouse.camouflage.CamouflageHandlerType;

public class PacketCamouflageSelectionClient extends PacketCamouflageSelection implements IForestryPacketClient {
	public PacketCamouflageSelectionClient(ICamouflageHandler handler, CamouflageHandlerType handlerType) {
		super(handler, handlerType);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CAMOUFLAGE_SELECTION;
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			CamouflageHandlerType handlerType = CamouflageHandlerType.values()[data.readShort()];
			ItemStack camouflageStack = data.readItemStack();

			TileEntity tile = TileUtil.getTile(player.world, pos);
			ICamouflageHandler handler;
			switch (handlerType) {
				case STRUCTURE: {
					if (!(tile instanceof IMultiblockComponent)) {
						return;
					}

					IMultiblockController controller = ((IMultiblockComponent) tile).getMultiblockLogic().getController();
					if (!(controller instanceof ICamouflageHandler)) {
						return;
					}
					handler = (ICamouflageHandler) controller;
					if (!handler.setCamouflageBlock(camouflageStack, false)) {
						return;
					}
					for (IMultiblockComponent comp : controller.getComponents()) {
						if (comp instanceof ICamouflagedTile) {
							ICamouflagedTile camBlock = (ICamouflagedTile) comp;
							BlockPos coordinates = camBlock.getCoordinates();
							World world = camBlock.getWorldObj();

							world.markBlockRangeForRenderUpdate(coordinates, coordinates);
						}
					}
					break;
				}
				case TILE: {
					if (tile instanceof ICamouflageHandler) {
						handler = (ICamouflageHandler) tile;
						if (!handler.setCamouflageBlock(camouflageStack, false)) {
							return;
						}
						player.world.markBlockRangeForRenderUpdate(pos, pos);
					}
					break;
				}
			}
		}
	}
}
