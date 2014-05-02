/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gadgets;

import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.inventory.ISpecialInventory;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketInventoryStack;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ForestryTank;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;

public class TileAnalyzer extends TileBase implements ISpecialInventory, ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final int TIME_TO_ANALYZE = 500;
	public static final int HONEY_REQUIRED = 100;

	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_CAN = 1;
	public static final short SLOT_INPUT_1 = 2;
	public static final short SLOT_OUTPUT_1 = 8;

	/* MEMBER */
	private final InventoryAdapter inventory = new InventoryAdapter(12, "Items");

	private int analyzeTime;

	private final short analyzeSlot = 0;
	private final short canSlot = 1;
	private final short inputSlot1 = 2;
	private final short outputSlot1 = 8;

	public FluidStack resource = FluidRegistry.getFluidStack(Defaults.LIQUID_HONEY, HONEY_REQUIRED);
	@EntityNetData
	public ForestryTank resourceTank = new ForestryTank(Defaults.PROCESSOR_TANK_CAPACITY);
	private final Stack<ItemStack> pendingProducts = new Stack<ItemStack>();

	/* CONSTRUCTOR */
	public TileAnalyzer() {
	}

	@Override
	public String getInventoryName() {
		return StringUtil.localize("core.0");
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

		// / Resource tank
		resourceTank.writeToNBT(nbttagcompound);

		// / Pending Products
		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] pending = pendingProducts.toArray(new ItemStack[pendingProducts.size()]);
		for (int i = 0; i < pending.length; i++)
			if (pending[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				pending[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingProducts", nbttaglist);

		// / Inventory
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		analyzeTime = nbttagcompound.getInteger("AnalyzeTime");

		// / Resource tank
		resourceTank.readFromNBT(nbttagcompound);

		// / Pending Products
		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingProducts", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingProducts.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

		// / Inventory
		inventory.readFromNBT(nbttagcompound);
	}

	/* WORKING */
	@Override
	public void updateServerSide() {
		// If we add pending products, we skip to the next work cycle.
		if (tryAddPending())
			return;

		if (!pendingProducts.isEmpty()) {
			setErrorState(EnumErrorCode.NOSPACE);
			return;
		}

		// Check if we have suitable items waiting in the can slot
		if (getStackInSlot(canSlot) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(getStackInSlot(canSlot));
			if (container != null && resource.isFluidEqual(container.fluid)) {

				setInventorySlotContents(canSlot, StackUtils.replenishByContainer(this, getStackInSlot(canSlot), container, resourceTank));
				if (getStackInSlot(canSlot).stackSize <= 0)
					setInventorySlotContents(canSlot, null);
			}
		}

		if (analyzeTime > 0 && getStackInSlot(analyzeSlot) != null && AlleleManager.alleleRegistry.isIndividual(getStackInSlot(analyzeSlot))) {

			analyzeTime--;

			// Still not done
			if (analyzeTime > 0) {
				setErrorState(EnumErrorCode.OK);
				return;
			}

			// Analyzation is done.
			IIndividual individual = AlleleManager.alleleRegistry.getIndividual(getStackInSlot(analyzeSlot));
			// No bee, abort
			if (individual == null)
				return;

			individual.analyze();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			individual.writeToNBT(nbttagcompound);
			getStackInSlot(analyzeSlot).setTagCompound(nbttagcompound);

			pendingProducts.push(getStackInSlot(analyzeSlot));
			setInventorySlotContents(analyzeSlot, null);
			sendNetworkUpdate();

		} else {
			analyzeTime = 0;

			// Don't start if analyze slot already occupied
			if (getStackInSlot(analyzeSlot) != null)
				return;

			// We need our liquid honey
			if (resourceTank.getFluidAmount() < resource.amount) {
				setErrorState(EnumErrorCode.NORESOURCE);
				return;
			}

			// Look for bees in input slots.
			for (int i = inputSlot1; i < outputSlot1; i++) {
				if (getStackInSlot(i) == null || !AlleleManager.alleleRegistry.isIndividual(getStackInSlot(i)))
					continue;

				// Analyzed bees in the input buffer are added to the output
				// queue at once.
				IIndividual individual = AlleleManager.alleleRegistry.getIndividual(getStackInSlot(i));
				if (individual.isAnalyzed()) {
					pendingProducts.push(getStackInSlot(i));
					setInventorySlotContents(i, null);
					continue;
				}

				setInventorySlotContents(analyzeSlot, getStackInSlot(i));
				setInventorySlotContents(i, null);
				resourceTank.drain(resource.amount, true);
				analyzeTime = TIME_TO_ANALYZE;
				sendNetworkUpdate();
				return;
			}

			// Nothing to analyze
			setErrorState(EnumErrorCode.NOTHINGANALYZE);
		}
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty())
			return false;

		ItemStack next = pendingProducts.peek();
		if (inventory.tryAddStack(next, outputSlot1, inventory.getSizeInventory() - outputSlot1, true)) {
			pendingProducts.pop();
			return true;
		}
		return false;
	}

	/* STATE INFORMATION */
	// @Override
	public boolean isWorking() {
		return analyzeTime > 0;
	}

	public int getProgressScaled(int i) {
		return (analyzeTime * i) / TIME_TO_ANALYZE;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public ItemStack getIndividualOnDisplay() {
		return getStackInSlot(analyzeSlot);
	}

	/* SMP */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			analyzeTime = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, analyzeTime);

	}

	@Override
	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(new PacketInventoryStack(PacketIds.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_ANALYZE, inventory.getStackInSlot(SLOT_ANALYZE)),
				xCoord, yCoord, zCoord);
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketInventoryStack(PacketIds.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_ANALYZE, inventory.getStackInSlot(SLOT_ANALYZE)).getPacket();
	}

	/* ISIDEDINVENTORY */
	@Override
	public InventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	protected boolean canTakeStackFromSide(int slotIndex, ItemStack itemstack, int side) {
		if(!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		return slotIndex >= SLOT_OUTPUT_1 && slotIndex < SLOT_OUTPUT_1 + 4;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if(!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		if(slotIndex >= SLOT_INPUT_1 && slotIndex < SLOT_INPUT_1 + 6)
			return AlleleManager.alleleRegistry.isIndividual(itemstack);

		if(slotIndex == SLOT_CAN) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(itemstack);
			return container != null && LiquidHelper.isLiquid(Defaults.LIQUID_HONEY, container.fluid);
		}

		return false;
	}

	@Override public int getSizeInventory() { return inventory.getSizeInventory(); }
	@Override public ItemStack getStackInSlot(int i) { return inventory.getStackInSlot(i); }
	@Override public ItemStack decrStackSize(int i, int j) { return inventory.decrStackSize(i, j); }
	@Override public void setInventorySlotContents(int i, ItemStack itemstack) { inventory.setInventorySlotContents(i, itemstack); }
	@Override public int getInventoryStackLimit() { return inventory.getInventoryStackLimit(); }
	@Override public ItemStack getStackInSlotOnClosing(int slot) { return inventory.getStackInSlotOnClosing(slot); }
	@Override public void openInventory() {}
	@Override public void closeInventory() {}

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

	/* ISPECIALINVENTORY */
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		ItemStack product = null;

		for (int i = outputSlot1; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;

			product = getStackInSlot(i).copy();
			if (doRemove) {
				getStackInSlot(i).stackSize = 0;
				setInventorySlotContents(i, null);
			}
			break;
		}
		return new ItemStack[] { product };
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		if (!AlleleManager.alleleRegistry.isIndividual(stack)) {

			FluidContainerData container = LiquidHelper.getLiquidContainer(stack);
			if (container == null || !container.fluid.isFluidEqual(resource))
				return 0;

			if (getStackInSlot(canSlot) == null) {
				if (doAdd)
					setInventorySlotContents(canSlot, stack.copy());

				return stack.stackSize;
			}

			int space = getStackInSlot(canSlot).getMaxStackSize() - getStackInSlot(canSlot).stackSize;
			if (space <= 0)
				return 0;

			if (doAdd) {
				getStackInSlot(canSlot).stackSize += stack.stackSize;
				if (getStackInSlot(canSlot).stackSize > getStackInSlot(canSlot).getMaxStackSize())
					getStackInSlot(canSlot).stackSize = getStackInSlot(canSlot).getMaxStackSize();
			}

			return space;
		}

		for (int i = inputSlot1; i < outputSlot1; i++)
			if (getStackInSlot(i) == null) {
				if (doAdd)
					setInventorySlotContents(i, stack.copy());

				return stack.stackSize;
			}

		return 0;
	}

	/* ILIQUIDCONTAINER */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		// We only accept what is already in the tank or valid ingredients
		if (resourceTank.getFluidAmount() > 0 && !resourceTank.getFluid().isFluidEqual(resource))
			return 0;
		else if (!LiquidHelper.isLiquid(Defaults.LIQUID_HONEY, resource))
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0)
			sendNetworkUpdate();

		return used;
	}

	@Override
	public ForestryTank[] getTanks() {
		return new ForestryTank[] { resourceTank };
	}

	/*
	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return resourceTank;
	}
	 */

}
