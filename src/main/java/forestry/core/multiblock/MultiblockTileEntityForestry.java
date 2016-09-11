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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import forestry.api.core.ILocatable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
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
import forestry.core.utils.PlayerUtil;

public abstract class MultiblockTileEntityForestry<T extends IMultiblockLogic> extends MultiblockTileEntityBase<T> implements ISidedInventory, IFilterSlotDelegate, ILocatable, IGuiHandlerTile {
	@Nullable
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
			NBTTagCompound ownerNbt = data.getCompoundTag("owner");
			this.owner = PlayerUtil.readGameProfileFromNBT(ownerNbt);
		}

		getInternalInventory().readFromNBT(data);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);

		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			PlayerUtil.writeGameProfile(nbt, owner);
			data.setTag("owner", nbt);
		}

		getInternalInventory().writeToNBT(data);
		return data;
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
	public ItemStack removeStackFromSlot(int slotIndex) {
		return getInternalInventory().removeStackFromSlot(slotIndex);
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
	public final void openInventory(EntityPlayer player) {
		getInternalInventory().openInventory(player);
	}

	@Override
	public final void closeInventory(EntityPlayer player) {
		getInternalInventory().closeInventory(player);
	}

	@Override
	public final String getName() {
		return getInternalInventory().getName();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return getInternalInventory().getDisplayName();
	}

	@Override
	public final boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}

	@Override
	public final boolean hasCustomName() {
		return getInternalInventory().hasCustomName();
	}

	@Override
	public final boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().isItemValidForSlot(slotIndex, itemStack);
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (allowsAutomation()) {
			return getInternalInventory().getSlotsForFace(side);
		} else {
			return Constants.SLOTS_NONE;
		}
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if (allowsAutomation()) {
			return getInternalInventory().canInsertItem(slotIndex, itemStack, side);
		} else {
			return false;
		}
	}

	@Override
	public final boolean canExtractItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
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
	public final World getWorldObj() {
		return worldObj;
	}

	/* IMultiblockComponent */
	@Nonnull
	@Override
	public final GameProfile getOwner() {
		return owner;
	}

	public final void setOwner(@Nonnull GameProfile owner) {
		this.owner = owner;
	}
	
	/* Fields */
	@Override
	public int getField(int id) {
		return getInternalInventory().getField(id);
	}
	
	@Override
	public int getFieldCount() {
		return getInternalInventory().getFieldCount();
	}
	
	@Override
	public void setField(int id, int value) {
		getInternalInventory().setField(id, value);
	}
	
	@Override
	public void clear() {
		getInternalInventory().clear();
	}
}
