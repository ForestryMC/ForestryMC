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
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.GuiId;
import java.util.Stack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class TileAnalyzer extends TilePowered implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final int TIME_TO_ANALYZE = 125;
	public static final int HONEY_REQUIRED = 100;

	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_CAN = 1;
	public static final short SLOT_INPUT_1 = 2;
	public static final short SLOT_OUTPUT_1 = 8;

	/* MEMBER */
	private int analyzeTime;

	public final FilteredTank resourceTank;

	private final TankManager tankManager;

	private final Stack<ItemStack> pendingProducts = new Stack<ItemStack>();

	private final IInventory invInput;

	/* CONSTRUCTOR */
	public TileAnalyzer() {
		super(800, 40, Defaults.MACHINE_MAX_ENERGY);
		setInternalInventory(new TileInventoryAdapter(this, 12, "Items"));
		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, Fluids.HONEY.get());
		tankManager = new TankManager(resourceTank);
		invInput = new InventoryMapper(getInternalInventory(), SLOT_INPUT_1, 6);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.AnalyzerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("AnalyzeTime", analyzeTime);

		tankManager.writeTanksToNBT(nbttagcompound);

		// / Pending Products
		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] pending = pendingProducts.toArray(new ItemStack[pendingProducts.size()]);
		for (int i = 0; i < pending.length; i++) {
			if (pending[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				pending[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("PendingProducts", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		analyzeTime = nbttagcompound.getInteger("AnalyzeTime");

		tankManager.readTanksFromNBT(nbttagcompound);

		// / Pending Products
		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingProducts", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingProducts.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}
	}

	@Override
	protected void updateServerSide() {
		// Check if we have suitable items waiting in the can slot
		FluidHelper.drainContainers(tankManager, this, SLOT_CAN);
		ItemStack can = getStackInSlot(SLOT_CAN);
		if (ForestryItem.honeyDrop.isItemEqual(can) && resourceTank.fill(Fluids.HONEY.get(Defaults.FLUID_PER_HONEY_DROP), false) == Defaults.FLUID_PER_HONEY_DROP) {
			setInventorySlotContents(SLOT_CAN, InvTools.depleteItem(can));
			resourceTank.fill(Fluids.HONEY.get(Defaults.FLUID_PER_HONEY_DROP), true);
		}

		for (int i = 0; i < invInput.getSizeInventory(); i++) {
			ItemStack inputStack = invInput.getStackInSlot(i);
			if (inputStack == null || !AlleleManager.alleleRegistry.isIndividual(inputStack))
				continue;
			// Analyzed bees in the input buffer are added to the output queue.
			IIndividual individual = AlleleManager.alleleRegistry.getIndividual(inputStack);
			if (individual.isAnalyzed()) {
				pendingProducts.push(inputStack);
				invInput.decrStackSize(i, inputStack.stackSize);
			}
		}

		tryAddPending();
		if (!pendingProducts.isEmpty()) {
			setErrorState(EnumErrorCode.NOSPACE);
			return;
		}

		if (analyzeTime == 0) {
			// Look for bees in input slots.
			IInvSlot slot = getInputSlot();
			if (slot == null) {
				// Nothing to analyze
				setErrorState(EnumErrorCode.NOTHINGANALYZE);
				return;
			}
		}

		// We need our liquid honey
		if (resourceTank.getFluidAmount() < HONEY_REQUIRED) {
			setErrorState(EnumErrorCode.NORESOURCE);
			return;
		}

		if (energyManager.getTotalEnergyStored() == 0) {
			setErrorState(EnumErrorCode.NOPOWER);
			return;
		}

		setErrorState(EnumErrorCode.OK);
	}

	/* WORKING */
	@Override
	public boolean workCycle() {
		ItemStack stackToAnalyze = getStackInSlot(SLOT_ANALYZE);
		if (analyzeTime > 0 && stackToAnalyze != null && AlleleManager.alleleRegistry.isIndividual(stackToAnalyze)) {

			analyzeTime--;

			// Still not done
			if (analyzeTime > 0) {
				setErrorState(EnumErrorCode.OK);
				return true;
			}

			// Analyzation is done.
			IIndividual individual = AlleleManager.alleleRegistry.getIndividual(stackToAnalyze);
			// No bee, abort
			if (individual == null)
				return false;

			individual.analyze();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			individual.writeToNBT(nbttagcompound);
			stackToAnalyze.setTagCompound(nbttagcompound);

			pendingProducts.push(stackToAnalyze);
			setInventorySlotContents(SLOT_ANALYZE, null);
			sendNetworkUpdate();
			return true;
		}

		analyzeTime = 0;

		// Don't start if analyze slot already occupied
		if (stackToAnalyze != null)
			return false;

		if (getErrorState() != EnumErrorCode.OK)
			return false;

		// Look for bees in input slots.
		IInvSlot slot = getInputSlot();
		ItemStack inputStack = slot.getStackInSlot();
		setInventorySlotContents(SLOT_ANALYZE, inputStack);
		slot.setStackInSlot(null);
		resourceTank.drain(HONEY_REQUIRED, true);
		analyzeTime = TIME_TO_ANALYZE;
		sendNetworkUpdate();
		return true;
	}

	private IInvSlot getInputSlot() {
		for (IInvSlot slot : InventoryIterator.getIterable(invInput)) {
			ItemStack inputStack = slot.getStackInSlot();
			if (inputStack != null && AlleleManager.alleleRegistry.isIndividual(inputStack))
				return slot;
		}
		return null;
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty())
			return false;

		ItemStack next = pendingProducts.peek();
		if (getInternalInventory().tryAddStack(next, SLOT_OUTPUT_1, getInternalInventory().getSizeInventory() - SLOT_OUTPUT_1, true)) {
			pendingProducts.pop();
			return true;
		}
		return false;
	}

	/* STATE INFORMATION */
	// @Override
	@Override
	public boolean isWorking() {
		return analyzeTime > 0;
	}

	@Override
	public boolean hasWork() {
		if (!pendingProducts.isEmpty())
			return true;
		if (analyzeTime > 0)
			return true;

		return getErrorState() == EnumErrorCode.OK || getErrorState() == EnumErrorCode.NOPOWER;
	}

	public int getProgressScaled(int i) {
		return (analyzeTime * i) / TIME_TO_ANALYZE;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public ItemStack getIndividualOnDisplay() {
		return getStackInSlot(SLOT_ANALYZE);
	}

	/* SMP */
	@Override
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
		case 0:
			analyzeTime = j;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, analyzeTime);

	}

	/* ISIDEDINVENTORY */
	@Override
	public int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getInternalInventory().getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getInternalInventory().decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInternalInventory().getStackInSlotOnClosing(slot);
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

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (!getInternalInventory().isItemValidForSlot(slot, stack))
			return false;

		if (slot >= SLOT_INPUT_1 && slot < SLOT_INPUT_1 + 6)
			return AlleleManager.alleleRegistry.isIndividual(stack);

		if (slot == SLOT_CAN)
			return Fluids.HONEY.isContained(stack);

		return false;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return isItemValidForSlot(side, stack);
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
		if (!getInternalInventory().canExtractItem(slotIndex, stack, side))
			return false;

		return slotIndex >= SLOT_OUTPUT_1 && slotIndex < SLOT_OUTPUT_1 + 4;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return getInternalInventory().getAccessibleSlotsFromSide(side);
	}

	/* ILIQUIDCONTAINER */
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tankManager.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tankManager.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return tankManager.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return tankManager.getTankInfo(from);
	}

}
