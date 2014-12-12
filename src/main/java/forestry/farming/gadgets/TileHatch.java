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
package forestry.farming.gadgets;

import buildcraft.api.statements.ITriggerExternal;
import cpw.mods.fml.common.Optional;
import forestry.api.core.ITileStructure;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.farming.triggers.FarmingTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class TileHatch extends TileFarm implements ISidedInventory {

	public TileHatch() {
		fixedType = TYPE_HATCH;
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	@Override
	protected void updateServerSide() {
		if (worldObj.getTotalWorldTime() % 40 == 0)
			dumpStash();
	}

	/* AUTO-EJECTING */
	protected void dumpStash() {

		if (!hasMaster())
			return;

		ArrayList<ForgeDirection> pipes = new ArrayList<ForgeDirection>();
		ForgeDirection[] tmp = BlockUtil.getPipeDirections(worldObj, Coords(), ForgeDirection.UP);
		Collections.addAll(pipes, tmp);

		if (pipes.size() > 0)
			dumpToPipe(pipes);
		else {
			IInventory[] inventories = BlockUtil.getAdjacentInventories(worldObj, Coords(), ForgeDirection.UP);
			dumpToInventory(inventories);
		}
	}

	private void dumpToPipe(ArrayList<ForgeDirection> pipes) {

		ItemStack[] products = extractItem(true, ForgeDirection.DOWN, 1);
		for (ItemStack product : products)
			while (product.stackSize > 0)
				BlockUtil.putFromStackIntoPipe(this, pipes, product);

	}

	private void dumpToInventory(IInventory[] inventories) {

		ITileStructure central = getCentralTE();
		if (central == null)
			return;
		IInventory inv = central.getInventory();

		for (int i = TileFarmPlain.SLOT_PRODUCTION_1; i < TileFarmPlain.SLOT_PRODUCTION_1 + TileFarmPlain.SLOT_COUNT_PRODUCTION; i++) {
			if (inv.getStackInSlot(i) == null)
				continue;

			ItemStack stack = inv.getStackInSlot(i);

			if (stack.stackSize <= 0)
				continue;

			for (IInventory inventory : inventories) {

				// Don't dump in arboretums!
				if (inventory.getSizeInventory() < 4)
					continue;

				// Get complete inventory (for double chests)
				IInventory completeInventory = Utils.getChest(inventory);
				if (completeInventory instanceof ISidedInventory) {
					ISidedInventory sidedInventory = (ISidedInventory) completeInventory;
					int[] slots = sidedInventory.getAccessibleSlotsFromSide(ForgeDirection.UP.ordinal());
					for (int sl = 0; sl < slots.length; ++sl) {
						StackUtils.stowInInventory(stack, sidedInventory, true, sl, 1);
					}
				} else {
					StackUtils.stowInInventory(stack, completeInventory, true);
					if (stack.stackSize <= 0) {
						inv.setInventorySlotContents(i, null);
						break;
					}
				}
			}
		}

	}

	/* IINVENTORY */
	@Override
	public InventoryAdapter getInternalInventory() {
		return (InventoryAdapter)getStructureInventory();
	}

	private IInventory getStructureInventory() {

		if (hasMaster()) {
			ITileStructure central = getCentralTE();
			if (central != null)
				return central.getInventory();
		}

		return null;
	}

	@Override
	public int getSizeInventory() {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getStackInSlot(slotIndex);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.decrStackSize(slotIndex, amount);
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getStackInSlotOnClosing(slotIndex);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		IInventory inv = getStructureInventory();
		if (inv != null)
			inv.setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getInventoryStackLimit();
		else
			return 0;
	}

	@Override
	protected boolean canTakeStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if(!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		if(slotIndex >= TileFarmPlain.SLOT_PRODUCTION_1 && slotIndex < TileFarmPlain.SLOT_PRODUCTION_1 + TileFarmPlain.SLOT_COUNT_PRODUCTION)
			return true;

		return false;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if (!hasMaster())
			return false;

		ITileStructure struct = getCentralTE();
		if (!(struct instanceof TileFarmPlain))
			return false;

		if(!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		TileFarmPlain housing = (TileFarmPlain) struct;
		if (slotIndex == TileFarmPlain.SLOT_FERTILIZER && housing.acceptsAsFertilizer(itemstack))
			return true;
		if (slotIndex >= TileFarmPlain.SLOT_RESOURCES_1 && slotIndex < TileFarmPlain.SLOT_RESOURCES_1 + TileFarmPlain.SLOT_COUNT_RESERVOIRS
				&& housing.acceptsAsResource(itemstack))
			return true;
		if (slotIndex >= TileFarmPlain.SLOT_GERMLINGS_1 && slotIndex < TileFarmPlain.SLOT_GERMLINGS_1 + TileFarmPlain.SLOT_COUNT_RESERVOIRS
				&& housing.acceptsAsGermling(itemstack))
			return true;
		if(slotIndex == TileFarmPlain.SLOT_CAN)
			return FluidContainerRegistry.isFilledContainer(itemstack);

		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.isUseableByPlayer(player);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return super.hasCustomInventoryName();
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return super.isItemValidForSlot(slotIndex, itemstack);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return super.canInsertItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return super.canExtractItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return super.getAccessibleSlotsFromSide(side);
	}

	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		IInventory inv;
		if (hasMaster()) {
			ITileStructure central = getCentralTE();
			if (central == null)
				return new ItemStack[0];
			inv = getCentralTE().getInventory();
		} else
			return StackUtils.EMPTY_STACK_ARRAY;

		ItemStack product = null;

		for (int i = TileFarmPlain.SLOT_PRODUCTION_1; i < TileFarmPlain.SLOT_PRODUCTION_1 + TileFarmPlain.SLOT_COUNT_PRODUCTION; i++) {
			if (inv.getStackInSlot(i) == null)
				continue;

			ItemStack stack = inv.getStackInSlot(i);

			if (doRemove)
				product = inv.decrStackSize(i, 1);
			else {
				product = stack.copy();
				product.stackSize = 1;
			}
			break;
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return StackUtils.EMPTY_STACK_ARRAY;
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		if (!hasMaster())
			return null;

		LinkedList<ITriggerExternal> list = new LinkedList<ITriggerExternal>();
		list.add(FarmingTriggers.lowResourceLiquid50);
		list.add(FarmingTriggers.lowResourceLiquid25);
		list.add(FarmingTriggers.lowSoil128);
		list.add(FarmingTriggers.lowSoil64);
		list.add(FarmingTriggers.lowSoil32);
		list.add(FarmingTriggers.lowFertilizer50);
		list.add(FarmingTriggers.lowFertilizer25);
		list.add(FarmingTriggers.lowGermlings25);
		list.add(FarmingTriggers.lowGermlings10);
		return list;
	}

}
