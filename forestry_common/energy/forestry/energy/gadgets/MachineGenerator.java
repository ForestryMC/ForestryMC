/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.inventory.ISpecialInventory;
import ic2.api.energy.prefab.BasicSource;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.GeneratorFuel;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.ForestryTank;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Utils;
import forestry.plugins.PluginIC2;

public class MachineGenerator extends TileBase implements ISpecialInventory, ILiquidTankContainer, IRenderableMachine {

	// / CONSTANTS
	public static final short SLOT_CAN = 0;
	public static final int maxEnergy = 30000;

	@EntityNetData
	public ForestryTank resourceTank = new ForestryTank(Defaults.PROCESSOR_TANK_CAPACITY);
	private int tickCount = 0;

	InventoryAdapter inventory = new InventoryAdapter(1, "Items");
	protected BasicSource ic2EnergySource = new BasicSource(this, maxEnergy, 1);

	public MachineGenerator() {
		setHints(Config.hints.get("generator"));
	}

	@Override
	public String getInventoryName() {
		return StringUtil.localize("engine.3");
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.GeneratorGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		ic2EnergySource.writeToNBT(nbttagcompound);

		NBTTagCompound NBTresourceSlot = new NBTTagCompound();
		resourceTank.writeToNBT(NBTresourceSlot);
		nbttagcompound.setTag("ResourceTank", NBTresourceSlot);

		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		ic2EnergySource.readFromNBT(nbttagcompound);

		resourceTank = new ForestryTank(Defaults.PROCESSOR_TANK_CAPACITY);
		if (nbttagcompound.hasKey("ResourceTank"))
			resourceTank.readFromNBT(nbttagcompound.getCompoundTag("ResourceTank"));

		inventory.readFromNBT(nbttagcompound);
	}

	@Override
	public void onChunkUnload() {
		ic2EnergySource.onChunkUnload();

		super.onChunkUnload();
	}

	@Override
	public void invalidate() {
		ic2EnergySource.invalidate();

		super.invalidate();
	}
	@Override
	public void updateServerSide() {

		// Check inventory slots for fuel
		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_CAN));
			if (container != null)
				if (GeneratorFuel.fuels.containsKey(container.fluid.fluidID)) {
					inventory.setInventorySlotContents(SLOT_CAN, StackUtils.replenishByContainer(this, inventory.getStackInSlot(SLOT_CAN), container, resourceTank));
					if (inventory.getStackInSlot(SLOT_CAN).stackSize <= 0)
						inventory.setInventorySlotContents(SLOT_CAN, null);
				}
		}

		// No work to be done if IC2 is unavailable.
		if (!PluginIC2.instance.isAvailable()) {
			setErrorState(EnumErrorCode.NOENERGYNET);
			return;
		}

		ic2EnergySource.updateEntity();

		if (resourceTank.getFluidAmount() > 0 &&
				GeneratorFuel.fuels.containsKey(resourceTank.getFluid().fluidID)) {
			GeneratorFuel fuel = GeneratorFuel.fuels.get(resourceTank.getFluid().fluidID);

			if (resourceTank.getFluidAmount() >= fuel.fuelConsumed.amount &&
					ic2EnergySource.getFreeCapacity() >= fuel.eu) {
				ic2EnergySource.addEnergy(fuel.eu);
				this.tickCount++;

				if (tickCount >= fuel.rate) {
					tickCount = 0;
					resourceTank.drain(fuel.fuelConsumed.amount, true);
				}
			}

		}

		if (resourceTank.getFluidAmount() <= 0)
			setErrorState(EnumErrorCode.NOFUEL);
		else
			setErrorState(EnumErrorCode.OK);
	}

	public boolean isWorking() {
		return resourceTank.getFluidAmount() > 0;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public int getStoredScaled(int i) {
		return (int) (ic2EnergySource.getEnergyStored() * i) / maxEnergy;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			ic2EnergySource.setEnergyStored(j);
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, (short) ic2EnergySource.getEnergyStored());
	}

	/* IINVENTORY */
	@Override public ItemStack getStackInSlot(int i) { return inventory.getStackInSlot(i); }
	@Override public void setInventorySlotContents(int i, ItemStack itemstack) { inventory.setInventorySlotContents(i, itemstack); }
	@Override public int getSizeInventory() { return inventory.getSizeInventory(); }
	@Override public ItemStack decrStackSize(int i, int j) { return inventory.decrStackSize(i, j); }
	@Override public ItemStack getStackInSlotOnClosing(int slot) { return inventory.getStackInSlotOnClosing(slot); }
	@Override public int getInventoryStackLimit() { return inventory.getInventoryStackLimit(); }
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

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		FluidContainerData container = LiquidHelper.getLiquidContainer(stack);
		if (container == null)
			return 0;

		if (!GeneratorFuel.fuels.containsKey(container.fluid.fluidID))
			return 0;

		if (inventory.getStackInSlot(SLOT_CAN) == null) {
			if (doAdd)
				inventory.setInventorySlotContents(SLOT_CAN, stack.copy());

			return stack.stackSize;
		}

		if (!inventory.getStackInSlot(SLOT_CAN).isItemEqual(stack))
			return 0;

		int space = inventory.getStackInSlot(SLOT_CAN).getMaxStackSize() - inventory.getStackInSlot(SLOT_CAN).stackSize;
		if (space <= 0)
			return 0;

		if (doAdd)
			inventory.getStackInSlot(SLOT_CAN).stackSize += stack.stackSize;

		return Math.min(space, stack.stackSize);

	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		return null;
	}

	/* ILIQUIDCONTAINER IMPLEMENTATION */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// We only accept water
		if (!GeneratorFuel.fuels.containsKey(resource.fluidID))
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0)
			// TODO: Slow down updates
			sendNetworkUpdate();

		return used;
	}

	@Override
	public ForestryTank[] getTanks() {
		return new ForestryTank[] { resourceTank };
	}
}
