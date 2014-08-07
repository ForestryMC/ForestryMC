/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.core.interfaces.ISocketable;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketSocketUpdate;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;

public class ContainerSocketed extends ContainerForestry {

	ISocketable tile;

	public ContainerSocketed(IInventory inventory, ISocketable tile) {
		super(inventory);
		this.tile = tile;
	}

	public void handleChipsetClick(int slot, EntityPlayer player, ItemStack itemstack) {
		if (!Proxies.common.isSimulating(player.worldObj)) {
			PacketPayload payload = new PacketPayload(1, 0, 0);
			payload.intPayload[0] = slot;
			Proxies.net.sendToServer(new PacketUpdate(PacketIds.CHIPSET_CLICK, payload));
			player.inventory.setItemStack(null);
			return;
		}

		ItemStack toSocket = itemstack.copy();
		toSocket.stackSize = 1;
		tile.setSocket(slot, toSocket);

		if (Proxies.common.isSimulating(player.worldObj)) {
			ItemStack stack = player.inventory.getItemStack();
			stack.stackSize--;
			if(stack.stackSize <= 0)
				player.inventory.setItemStack(null);
			Proxies.net.inventoryChangeNotify(player);
			
			TileEntity te = (TileEntity) tile;
			Proxies.net.sendToPlayer(new PacketSocketUpdate(PacketIds.SOCKET_UPDATE, te.xCoord, te.yCoord, te.zCoord, tile), player);
		}

	}

	public void handleSolderingIronClick(int slot, EntityPlayer player, ItemStack itemstack) {
		if (!Proxies.common.isSimulating(player.worldObj)) {
			PacketPayload payload = new PacketPayload(1, 0, 0);
			payload.intPayload[0] = slot;
			Proxies.net.sendToServer(new PacketUpdate(PacketIds.SOLDERING_IRON_CLICK, payload));
			return;
		}

		ItemStack socket = tile.getSocket(slot);
		if (socket == null)
			return;

		StackUtils.stowInInventory(socket, player.inventory, true);
		// Not sufficient space in player's inventory. failed to stow.
		if (socket.stackSize > 0)
			return;

		tile.setSocket(slot, null);
		itemstack.damageItem(1, player);
		if (itemstack.stackSize <= 0)
			player.inventory.setItemStack(null);

		TileEntity te = (TileEntity) tile;
		Proxies.net.sendToPlayer(new PacketSocketUpdate(PacketIds.SOCKET_UPDATE, te.xCoord, te.yCoord, te.zCoord, tile), player);

	}

}
