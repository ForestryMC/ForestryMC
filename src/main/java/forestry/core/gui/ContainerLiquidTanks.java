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
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fluids.IFluidTank;

import forestry.core.interfaces.ILiquidTankContainer;

public abstract class ContainerLiquidTanks<T extends TileEntity & ILiquidTankContainer> extends ContainerTile<T> implements IContainerLiquidTanks {

	private final ContainerLiquidTanksHelper<T> helper;

	protected ContainerLiquidTanks(T tile, InventoryPlayer playerInventory, int xInv, int yInv) {
		super(tile, playerInventory, xInv, yInv);
		this.helper = new ContainerLiquidTanksHelper<T>(tile);
	}

	@Override
	public void handlePipetteClickClient(int slot, EntityPlayer player) {
		helper.handlePipetteClickClient(slot, player);
	}

	@Override
	public void handlePipetteClick(int slot, EntityPlayerMP player) {
		helper.handlePipetteClick(slot, player);
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
	public void onCraftGuiOpened(ICrafting icrafting) {
		super.onCraftGuiOpened(icrafting);
		tile.getTankManager().initGuiData(this, icrafting);
	}

	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}
}
