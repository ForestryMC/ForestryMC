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
package forestry.core.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.IFilterSlotDelegate;
import forestry.core.utils.InventoryUtil;

public abstract class EntityMinecartContainerForestry extends EntityMinecartForestry implements ISidedInventory, IFilterSlotDelegate {
	/**
	 * When set to true, the minecart will drop all items when setDead() is called. When false (such as when travelling
	 * dimensions) it preserves its contents.
	 */
	private boolean dropContentsWhenDead = true;

	@SuppressWarnings("unused")
	public EntityMinecartContainerForestry(World world) {
		super(world);
	}

	public EntityMinecartContainerForestry(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		getInternalInventory().readFromNBT(nbtTagCompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		getInternalInventory().writeToNBT(nbtTagCompound);
	}

	@Override
	public void setDead() {
		if (dropContentsWhenDead && !worldObj.isRemote) {
			InventoryUtil.dropInventory(getInternalInventory(), worldObj, posX, posY, posZ);
		}
		super.setDead();
	}

	@Override
	protected void applyDrag() {
		int redstoneLevel = 15 - Container.calcRedstoneFromInventory(this);
		double drag = 0.98F + (float) redstoneLevel * 0.001F;
		this.motionX *= drag;
		this.motionY *= 0.0D;
		this.motionZ *= drag;
	}

	@Override
	public void travelToDimension(int dimensionId) {
		dropContentsWhenDead = false;
		super.travelToDimension(dimensionId);
	}

	/* IInventory */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return !isDead && player.getDistanceSqToEntity(this) <= 64.0D;
	}

	@Override
	public int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		return getInternalInventory().getStackInSlot(slotIndex);
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		return getInternalInventory().decrStackSize(slotIndex, amount);
	}

	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return getInternalInventory().getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
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
	public final boolean hasCustomInventoryName() {
		return false; // stop RailCraft from rendering a halo text of the inventory name
	}

	@Override
	public final boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().isItemValidForSlot(slotIndex, itemStack);
	}

	@Override
	public final boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return getInternalInventory().isLocked(slotIndex);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return getInternalInventory().getAccessibleSlotsFromSide(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return getInternalInventory().canInsertItem(slot, stack, side);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return getInternalInventory().canExtractItem(slot, stack, side);
	}

	@Override
	public void markDirty() {

	}

	protected abstract IInventoryAdapter getInternalInventory();
}
