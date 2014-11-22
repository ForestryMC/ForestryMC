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
package forestry.energy.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import ic2.api.energy.prefab.BasicSource;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.TankManager;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.plugins.PluginIC2;
import net.minecraftforge.fluids.FluidTankInfo;

public class MachineGenerator extends TileBase implements ISpecialInventory, ILiquidTankContainer, IRenderableMachine {

	// / CONSTANTS
	public static final short SLOT_CAN = 0;
	public static final int maxEnergy = 30000;

	@EntityNetData
	public FilteredTank resourceTank;
	private final TankManager tankManager;
	private int tickCount = 0;

	InventoryAdapter inventory = new InventoryAdapter(1, "Items");
	protected BasicSource ic2EnergySource;

	public MachineGenerator() {
		setHints(Config.hints.get("generator"));

		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, FuelManager.generatorFuel.keySet());
		tankManager = new TankManager(resourceTank);

		if (PluginIC2.instance.isAvailable()) {
			ic2EnergySource = new BasicSource(this, maxEnergy, 1);
		}
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.GeneratorGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (ic2EnergySource != null) ic2EnergySource.writeToNBT(nbttagcompound);

		tankManager.writeTanksToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (ic2EnergySource != null) ic2EnergySource.readFromNBT(nbttagcompound);

		tankManager.readTanksFromNBT(nbttagcompound);
		inventory.readFromNBT(nbttagcompound);
	}

	@Override
	public void onChunkUnload() {
		if (ic2EnergySource != null) ic2EnergySource.onChunkUnload();

		super.onChunkUnload();
	}

	@Override
	public void invalidate() {
		if (ic2EnergySource != null) ic2EnergySource.invalidate();

		super.invalidate();
	}
	@Override
	public void updateServerSide() {

		// Check inventory slots for fuel
		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_CAN));
			if (container != null)

				if (resourceTank.accepts(container.fluid.getFluid())) {
					inventory.setInventorySlotContents(SLOT_CAN, StackUtils.replenishByContainer(this, inventory.getStackInSlot(SLOT_CAN), container, resourceTank));
					if (inventory.getStackInSlot(SLOT_CAN).stackSize <= 0)
						inventory.setInventorySlotContents(SLOT_CAN, null);
				}
		}

		// No work to be done if IC2 is unavailable.
		if (ic2EnergySource == null) {
			setErrorState(EnumErrorCode.NOENERGYNET);
			return;
		}

		ic2EnergySource.updateEntity();

		if (resourceTank.getFluidAmount() > 0) {
			GeneratorFuel fuel = FuelManager.generatorFuel.get(resourceTank.getFluid().getFluid());

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
		if (ic2EnergySource == null) return 0;

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
		int firstMessageId = tankManager.maxMessageId() + 1;
		if (i == firstMessageId) {
			if (ic2EnergySource != null) ic2EnergySource.setEnergyStored(j);
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int firstMessageId = tankManager.maxMessageId() + 1;
		if (ic2EnergySource != null) {
			iCrafting.sendProgressBarUpdate(container, firstMessageId, (short) ic2EnergySource.getEnergyStored());
		}
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

		if (container.fluid == null || !FuelManager.generatorFuel.containsKey(container.fluid.getFluid()))
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

	/* ILiquidTankContainer */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

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
