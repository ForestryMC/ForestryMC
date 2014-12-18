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
package forestry.core.gadgets;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.GuiHandler;
import forestry.core.config.Config;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.TileInventoryAdapter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class TileNaturalistChest extends TileBase implements IInventory, IPagedInventory {

	private final ISpeciesRoot speciesRoot;
	private final int guiID;

	public TileNaturalistChest(ISpeciesRoot speciesRoot, int guiId) {
		setInternalInventory(new TileInventoryAdapter(this, 125, "Items"));
		setHints(Config.hints.get("apiarist.chest"));
		this.speciesRoot = speciesRoot;
		this.guiID = guiId;
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, guiID, player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void flipPage(EntityPlayer player, int page) {
		player.openGui(ForestryAPI.instance, GuiHandler.encodeGuiData(guiID, page), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* UPDATING */
	@Override
	public void updateServerSide() {
	}

	/* ERROR HANDLING */
	@Override
	public boolean throwsErrors() {
		return false;
	}

	/* IINVENTORY */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return itemstack == null || speciesRoot.isMember(itemstack);
	}

	@Override
	public int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		return getInternalInventory().getStackInSlot(slotIndex);
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int count) {
		return getInternalInventory().decrStackSize(slotIndex, count);
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInternalInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
}
