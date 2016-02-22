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
package forestry.core.multiblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.multiblock.IMultiblockLogic;
import forestry.api.multiblock.MultiblockTileEntityBase;
import forestry.core.config.Constants;
import forestry.core.gui.GuiHandler;
import forestry.core.gui.IGuiHandlerTile;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.IFilterSlotDelegate;
import forestry.core.tiles.ILocatable;

public abstract class MultiblockTileEntityForestry<T extends IMultiblockLogic> extends MultiblockTileEntityBase<T> implements ISidedInventory, IFilterSlotDelegate, ILocatable, IGuiHandlerTile {
	private GameProfile owner;

	public MultiblockTileEntityForestry(T multiblockLogic) {
		super(multiblockLogic);
	}

	/**
	 * Called by a structure block when it is right clicked by a player.
	 */
	public void openGui(EntityPlayer player) {
		GuiHandler.openGui(player, this);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		if (data.hasKey("owner")) {
			owner = NBTUtil.func_152459_a(data.getCompoundTag("owner"));
		}

		getInternalInventory().readFromNBT(data);
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.func_152460_a(nbt, owner);
			nbt.removeTag("Properties");
			data.setTag("owner", nbt);
		}

		getInternalInventory().writeToNBT(data);
	}

	/* INVENTORY */
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	public boolean allowsAutomation() {
		return false;
	}

	@Override
	public final int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		return getInternalInventory().getStackInSlot(slotIndex);
	}

	@Override
	public final ItemStack decrStackSize(int slotIndex, int amount) {
		return getInternalInventory().decrStackSize(slotIndex, amount);
	}

	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return getInternalInventory().getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public final int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public final void openInventory() {
		getInternalInventory().openInventory();
	}

	@Override
	public final void closeInventory() {
		getInternalInventory().closeInventory();
	}

	@Override
	public final String getInventoryName() {
		return getInternalInventory().getInventoryName();
	}

	@Override
	public final boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}

	@Override
	public final boolean hasCustomInventoryName() {
		return getInternalInventory().hasCustomInventoryName();
	}

	@Override
	public final boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().isItemValidForSlot(slotIndex, itemStack);
	}

	@Override
	public final int[] getAccessibleSlotsFromSide(int side) {
		if (allowsAutomation()) {
			return getInternalInventory().getAccessibleSlotsFromSide(side);
		} else {
			return Constants.SLOTS_NONE;
		}
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		if (allowsAutomation()) {
			return getInternalInventory().canInsertItem(slotIndex, itemStack, side);
		} else {
			return false;
		}
	}

	@Override
	public final boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if (allowsAutomation()) {
			return getInternalInventory().canExtractItem(slotIndex, itemStack, side);
		} else {
			return false;
		}
	}

	@Override
	public final boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public final boolean isLocked(int slotIndex) {
		return getInternalInventory().isLocked(slotIndex);
	}

	/* ILocatable */
	@Override
	public final World getWorld() {
		return worldObj;
	}

	/* IMultiblockComponent */
	@Override
	public final GameProfile getOwner() {
		return owner;
	}

	public final void setOwner(GameProfile owner) {
		this.owner = owner;
	}
}
