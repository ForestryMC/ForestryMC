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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fluids.IFluidTank;

import forestry.core.circuits.ISocketable;
import forestry.core.tiles.ILiquidTankTile;

public abstract class ContainerLiquidTanksSocketed<T extends TileEntity & ILiquidTankTile & ISocketable> extends ContainerTile<T> implements IContainerSocketed, IContainerLiquidTanks {

	private final ContainerSocketedHelper<T> socketedHelper;
	private final ContainerLiquidTanksHelper<T> tanksHelper;

	protected ContainerLiquidTanksSocketed(T tile, InventoryPlayer playerInventory, int xInv, int yInv) {
		super(tile, playerInventory, xInv, yInv);
		this.socketedHelper = new ContainerSocketedHelper<>(tile);
		this.tanksHelper = new ContainerLiquidTanksHelper<>(tile);
	}

	/* IContainerLiquidTanks */
	@Override
	public void handlePipetteClickClient(int slot, EntityPlayer player) {
		tanksHelper.handlePipetteClickClient(slot, player);
	}

	@Override
	public void handlePipetteClick(int slot, EntityPlayerMP player) {
		tanksHelper.handlePipetteClick(slot, player);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		tile.getTankManager().updateGuiData(this, crafters);
	}

	@Override
	public void addCraftingToCrafters(ICrafting icrafting) {
		super.addCraftingToCrafters(icrafting);
		tile.getTankManager().containerAdded(this, icrafting);
	}

	@Override
	public void onContainerClosed(EntityPlayer entityPlayer) {
		super.onContainerClosed(entityPlayer);
		tile.getTankManager().containerRemoved(this);
	}

	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}

	/* IContainerSocketed */
	@Override
	public void handleChipsetClick(int slot) {
		socketedHelper.handleChipsetClick(slot);
	}

	@Override
	public void handleChipsetClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		socketedHelper.handleChipsetClickServer(slot, player, itemstack);
	}

	@Override
	public void handleSolderingIronClick(int slot) {
		socketedHelper.handleSolderingIronClick(slot);
	}

	@Override
	public void handleSolderingIronClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		socketedHelper.handleSolderingIronClickServer(slot, player, itemstack);
	}

}
