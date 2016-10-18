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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.tiles.TileBase;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileFabricator;

public class PacketRecipeTransferUpdate extends PacketCoordinates implements IForestryPacketClient {
	private ItemStack[] craftingInventory;

	public PacketRecipeTransferUpdate() {
	}

	public PacketRecipeTransferUpdate(TileBase base, ItemStack[] craftingInventory) {
		super(base);
		this.craftingInventory = craftingInventory;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeItemStacks(craftingInventory);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		craftingInventory = data.readItemStacks();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(player.worldObj);
		if (tile instanceof TileCarpenter) {
			TileCarpenter carpenter = (TileCarpenter) tile;
			int index = 0;
			for(ItemStack stack : craftingInventory){
				carpenter.getCraftingInventory().setInventorySlotContents(index, stack);
				index++;
			}
		}else if(tile instanceof TileFabricator){
			TileFabricator fabricator = (TileFabricator) tile;
			int index = 0;
			for(ItemStack stack : craftingInventory){
				fabricator.getCraftingInventory().setInventorySlotContents(index, stack);
				index++;
			}
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.RECIPE_TRANSFER_UPDATE;
	}
}
