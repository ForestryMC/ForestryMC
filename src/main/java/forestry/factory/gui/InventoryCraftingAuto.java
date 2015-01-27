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
package forestry.factory.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingAuto extends InventoryCrafting {

	private final ContainerCarpenter eventHandler;
	public final ItemStack stackList[];
	private final int inventoryWidth;

	public InventoryCraftingAuto(ContainerCarpenter container, int i, int j) {
		super(container, i, j);
		int k = i * j;
		stackList = new ItemStack[k];
		eventHandler = container;
		inventoryWidth = i;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= getSizeInventory()) {
			return null;
		} else {
			return stackList[i];
		}
	}

	@Override
	public ItemStack getStackInRowAndColumn(int i, int j) {
		if (i < 0 || i >= inventoryWidth) {
			return null;
		} else {
			int k = i + j * inventoryWidth;
			return getStackInSlot(k);
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (stackList[i] != null) {
			if (stackList[i].stackSize <= j) {
				ItemStack itemstack = stackList[i];
				stackList[i] = null;
				eventHandler.onCraftMatrixChanged(this, i);
				return itemstack;
			}
			ItemStack itemstack1 = stackList[i].splitStack(j);
			if (stackList[i].stackSize == 0) {
				stackList[i] = null;
			}
			eventHandler.onCraftMatrixChanged(this, i);
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		stackList[i] = itemstack;
		eventHandler.onCraftMatrixChanged(this, i);
	}

	@Override
	public String getInventoryName() {
		return "Crafting";
	}

	@Override
	public int getSizeInventory() {
		return stackList.length;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

}
