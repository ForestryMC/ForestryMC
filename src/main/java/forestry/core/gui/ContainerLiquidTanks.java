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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.IToolPipette;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gadgets.TileForestry;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class ContainerLiquidTanks<T extends TileForestry & ILiquidTankContainer> extends ContainerTile<T> {

	public ContainerLiquidTanks(T tile, InventoryPlayer playerInventory, int xInv, int yInv) {
		super(tile, playerInventory, xInv, yInv);
	}

	@SideOnly(Side.CLIENT)
	public void handlePipetteClickClient(int slot, EntityPlayer player) {
		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null || !(itemstack.getItem() instanceof IToolPipette)) {
			return;
		}

		PacketPayload payload = new PacketPayload(1, 0, 0);
		payload.intPayload[0] = slot;
		Proxies.net.sendToServer(new PacketUpdate(PacketIds.PIPETTE_CLICK, payload));
	}

	public void handlePipetteClick(int slot, EntityPlayerMP player) {

		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null) {
			return;
		}

		Item held = itemstack.getItem();
		if (!(held instanceof IToolPipette)) {
			return;
		}

		IToolPipette pipette = (IToolPipette) held;
		StandardTank tank = tile.getTankManager().get(slot);
		int liquidAmount = tank.getFluidAmount();

		if (pipette.canPipette(itemstack) && liquidAmount > 0) {
			if (liquidAmount > 0) {
				FluidStack fillAmount = tank.drain(1000, false);
				int filled = pipette.fill(itemstack, fillAmount, true);
				tank.drain(filled, true);
				player.updateHeldItem();
			}
		} else {
			FluidStack potential = pipette.drain(itemstack, pipette.getCapacity(itemstack), false);
			if (potential != null) {
				pipette.drain(itemstack, tank.fill(potential, true), true);
				player.updateHeldItem();
			}
		}
	}

	@Override
	public void updateProgressBar(int messageId, int data) {
		super.updateProgressBar(messageId, data);

		tile.getTankManager().processGuiUpdate(messageId, data);
		tile.getGUINetworkData(messageId, data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		tile.getTankManager().updateGuiData(this, crafters);
		for (Object crafter : crafters) {
			tile.sendGUINetworkData(this, (ICrafting) crafter);
		}
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
