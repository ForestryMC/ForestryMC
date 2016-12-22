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
package forestry.core.gui;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitBoard;
import forestry.core.circuits.ISocketable;
import forestry.core.network.packets.PacketChipsetClick;
import forestry.core.network.packets.PacketSocketUpdate;
import forestry.core.network.packets.PacketSolderingIronClick;
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerSocketedHelper<T extends TileEntity & ISocketable> implements IContainerSocketed {

	private final T tile;

	public ContainerSocketedHelper(T tile) {
		this.tile = tile;
	}

	@Override
	public void handleChipsetClick(int slot) {
		Proxies.net.sendToServer(new PacketChipsetClick(slot));
	}

	@Override
	public void handleChipsetClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		if (!tile.getSocket(slot).isEmpty()) {
			return;
		}

		if (!ChipsetManager.circuitRegistry.isChipset(itemstack)) {
			return;
		}

		ICircuitBoard circuitBoard = ChipsetManager.circuitRegistry.getCircuitBoard(itemstack);
		if (circuitBoard == null) {
			return;
		}

		if (!tile.getSocketType().equals(circuitBoard.getSocketType())) {
			return;
		}

		ItemStack toSocket = itemstack.copy();
		toSocket.setCount(1);
		tile.setSocket(slot, toSocket);

		ItemStack stack = player.inventory.getItemStack();
		stack.shrink(1);
		player.updateHeldItem();

		PacketSocketUpdate packet = new PacketSocketUpdate(tile);
		Proxies.net.sendToPlayer(packet, player);
	}

	@Override
	public void handleSolderingIronClick(int slot) {
		Proxies.net.sendToServer(new PacketSolderingIronClick(slot));
	}

	@Override
	public void handleSolderingIronClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		ItemStack socket = tile.getSocket(slot);
		if (socket.isEmpty()) {
			return;
		}

		InventoryUtil.stowInInventory(socket, player.inventory, true);
		// Not sufficient space in player's inventory. failed to stow.
		if (!socket.isEmpty()) {
			return;
		}

		tile.setSocket(slot, ItemStack.EMPTY);
		itemstack.damageItem(1, player);
		player.updateHeldItem();

		PacketSocketUpdate packet = new PacketSocketUpdate(tile);
		Proxies.net.sendToPlayer(packet, player);
	}
}
