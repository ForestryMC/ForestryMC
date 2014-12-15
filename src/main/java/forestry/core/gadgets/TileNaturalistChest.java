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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileNaturalistChest extends TileBase implements IInventory, IPagedInventory {

	private ItemStack[] inventoryStacks = new ItemStack[125];
	private final ISpeciesRoot speciesRoot;
	private final int guiID;

	public TileNaturalistChest(ISpeciesRoot speciesRoot, int guiId) {
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

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventoryStacks.length; i++) {
			if (inventoryStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventoryStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("Items", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
		inventoryStacks = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {

			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < inventoryStacks.length)
				inventoryStacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}
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
		return inventoryStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventoryStacks[i] == null)
			return null;

		ItemStack product;
		if (inventoryStacks[i].stackSize <= j) {
			product = inventoryStacks[i];
			inventoryStacks[i] = null;
			return product;
		} else {
			product = inventoryStacks[i].splitStack(j);
			if (inventoryStacks[i].stackSize == 0)
				inventoryStacks[i] = null;

			return product;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventoryStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
			itemstack.stackSize = getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (inventoryStacks[slot] == null)
			return null;
		ItemStack toReturn = inventoryStacks[slot];
		inventoryStacks[slot] = null;
		return toReturn;
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
