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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.IToolPipette;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class ContainerLiquidTanks extends ContainerForestry {

	private ILiquidTankContainer tile;

	public ContainerLiquidTanks(IInventory inventory, ILiquidTankContainer tile) {
		super(inventory);
		this.tile = tile;
	}

	public void handlePipetteClick(int slot, EntityPlayer player) {

		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null)
			return;

		Item held = itemstack.getItem();
		if (!(held instanceof IToolPipette))
			return;

		if (!Proxies.common.isSimulating(player.worldObj)) {
			PacketPayload payload = new PacketPayload(1, 0, 0);
			payload.intPayload[0] = slot;
			Proxies.net.sendToServer(new PacketUpdate(PacketIds.PIPETTE_CLICK, payload));
			return;
		}

		IToolPipette pipette = (IToolPipette) held;
		StandardTank tank = tile.getTankManager().get(slot);
		int liquidAmount = tank.getFluid().amount;

		if (pipette.canPipette(itemstack) && liquidAmount > 0) {
			if (liquidAmount > 0) {
				FluidStack fillAmount = tank.drain(1000, false);
				int filled = pipette.fill(itemstack, fillAmount, true);
				tank.drain(filled, true);
			}
		} else {
			FluidStack potential = pipette.drain(itemstack, pipette.getCapacity(itemstack), false);
			if (potential != null)
				pipette.drain(itemstack, tank.fill(potential, true), true);
		}
	}

	@Override
	public void updateProgressBar(int messageId, int data) {
		super.updateProgressBar(messageId, data);

		tile.getTankManager().processGuiUpdate(messageId, data);
		tile.getGUINetworkData(messageId, data);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		tile.getTankManager().updateGuiData(this, crafters);
		for (int i = 0; i < crafters.size(); i++)
			tile.sendGUINetworkData(this, (ICrafting) crafters.get(i));
	}

	@Override
	public void addCraftingToCrafters(ICrafting icrafting) {
		super.addCraftingToCrafters(icrafting);
		tile.getTankManager().initGuiData(this, icrafting);
	}

	public StandardTank getTank(int slot) {
		return tile.getTankManager().get(slot);
	}
}
