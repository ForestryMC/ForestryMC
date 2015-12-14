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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.access.AccessHandler;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.ILocatable;

public abstract class MultiblockControllerForestry extends MultiblockControllerBase implements ISidedInventory, IRestrictedAccess, IErrorLogicSource, ILocatable {
	private final AccessHandler accessHandler;
	private final IErrorLogic errorLogic;

	protected MultiblockControllerForestry(World world) {
		super(world);

		this.accessHandler = new AccessHandler(this);
		this.errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
	}

	@Override
	public IAccessHandler getAccessHandler() {
		return accessHandler;
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	protected void onMachineAssembled() {
		super.onMachineAssembled();

		if (worldObj.isRemote) {
			return;
		}

		// Figure out who owns the multiblock, by majority

		Multiset<GameProfile> owners = HashMultiset.create();
		for (IMultiblockComponent part : connectedParts) {
			GameProfile owner = part.getOwner();
			if (owner != null) {
				owners.add(owner);
			}
		}

		GameProfile owner = null;
		int max = 0;
		for (Multiset.Entry<GameProfile> entry : owners.entrySet()) {
			int count = entry.getCount();
			if (count > max) {
				max = count;
				owner = entry.getElement();
			}
		}

		getAccessHandler().setOwner(owner);
	}

	/* INBTTagable */
	@Override
	public void writeToNBT(NBTTagCompound data) {
		accessHandler.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		accessHandler.readFromNBT(data);
	}

	/* INVENTORY */
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public void markDirty() {
		getInternalInventory().markDirty();
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
		return getInternalInventory().getAccessibleSlotsFromSide(side);
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		return getInternalInventory().canInsertItem(slotIndex, itemStack, side);
	}

	@Override
	public final boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		return getInternalInventory().canExtractItem(slotIndex, itemStack, side);
	}
}
