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
package forestry.factory.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileUtil;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileFabricator;

public class PacketRecipeTransferUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final NonNullList<ItemStack> craftingInventory;

	public PacketRecipeTransferUpdate(TileBase base, NonNullList<ItemStack> craftingInventory) {
		this.pos = base.getPos();
		this.craftingInventory = craftingInventory;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeItemStacks(craftingInventory);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.RECIPE_TRANSFER_UPDATE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException {
			BlockPos pos = data.readBlockPos();
			NonNullList<ItemStack> craftingInventory = data.readItemStacks();

			TileEntity tile = TileUtil.getTile(player.world, pos);
			if (tile instanceof TileCarpenter) {
				TileCarpenter carpenter = (TileCarpenter) tile;
				int index = 0;
				for (ItemStack stack : craftingInventory) {
					carpenter.getCraftingInventory().setInventorySlotContents(index, stack);
					index++;
				}
			} else if (tile instanceof TileFabricator) {
				TileFabricator fabricator = (TileFabricator) tile;
				int index = 0;
				for (ItemStack stack : craftingInventory) {
					fabricator.getCraftingInventory().setInventorySlotContents(index, stack);
					index++;
				}
			}
		}
	}
}
