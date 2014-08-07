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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.inventory.ISpecialInventory;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.utils.ForestryTank;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.TileInventoryAdapter;

public class EngineBronze extends Engine implements ISpecialInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_CAN = 0;

	/* NETWORK */
	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = super.getPacketPayload();

		if (shutdown)
			payload.append(new int[] { 1 });
		else
			payload.append(new int[] { 0 });

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		super.fromPacketPayload(payload);

		if (payload.intPayload[3] > 0)
			shutdown = true;
		else
			shutdown = false;
	}
	public ForestryTank fuelTank = new ForestryTank(Defaults.ENGINE_TANK_CAPACITY);
	public ForestryTank heatingTank = new ForestryTank(Defaults.ENGINE_TANK_CAPACITY);
	private final TileInventoryAdapter inventory;
	public int burnTime;
	public int totalTime;
	public int currentFluidId = -1;
	/**
	 * true if the engine is too cold and needs to warm itself up.
	 */
	private boolean shutdown;

	public EngineBronze() {
		super(Defaults.ENGINE_BRONZE_HEAT_MAX, 30000, 500);
		setHints(Config.hints.get("engine.bronze"));

		inventory = new TileInventoryAdapter(this, 1, "Items");
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.EngineBronzeGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void updateServerSide() {

		super.updateServerSide();

		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null) {

			FluidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_CAN));
			if (container != null) {

				ForestryTank tank = null;

				if (container.fluid.isFluidEqual(LiquidHelper.getLiquid(Defaults.LIQUID_LAVA, 1)))
					tank = heatingTank;
				else if (FuelManager.bronzeEngineFuel.containsKey(container.fluid))
					tank = fuelTank;

				if (tank != null) {
					inventory.setInventorySlotContents(SLOT_CAN, replenishByContainer(inventory.getStackInSlot(SLOT_CAN), container, tank));
					if (inventory.getStackInSlot(SLOT_CAN).stackSize <= 0)
						inventory.setInventorySlotContents(0, null);
				}
			}
		}

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		if (getHeatLevel() <= 0.2 && heatingTank.getFluidAmount() <= 0)
			setErrorState(EnumErrorCode.NOHEAT);
		else if (burnTime <= 0 && fuelTank.getFluidAmount() <= 0)
			setErrorState(EnumErrorCode.NOFUEL);
		else
			setErrorState(EnumErrorCode.OK);
	}

	/**
	 * Burns fuel increasing stored energy
	 */
	@Override
	public void burn() {

		currentOutput = 0;

		if (isActivated() && (fuelTank.getFluidAmount() >= Defaults.BUCKET_VOLUME || burnTime > 0)) {

			double heatStage = getHeatLevel();

			// If we have reached a safe temperature, we reenable energy
			// transfer
			if (heatStage > 0.25 && shutdown)
				shutdown(false);
			else if (shutdown)
				if (heatingTank.getFluidAmount() > 0 && heatingTank.getFluid().getFluid().getName().equals(Defaults.LIQUID_LAVA)) {
					addHeat(Defaults.ENGINE_HEAT_VALUE_LAVA);
					heatingTank.drain(1, true);
				}

			// We need a minimum temperature to generate energy
			if (heatStage > 0.2) {

				if (burnTime > 0) {
					burnTime--;
					currentOutput = determineFuelValue(FluidRegistry.getFluid(currentFluidId));
					addEnergy(currentOutput);
				} else {
					burnTime = totalTime = this.determineBurnTime(fuelTank.getFluid().getFluid());
					currentFluidId = fuelTank.getFluid().getFluid().getID();
					fuelTank.drain(Defaults.BUCKET_VOLUME, true);
				}

			} else
				shutdown(true);
		}

		if(burnTime <= 0)
			currentFluidId = -1;
	}

	private void shutdown(boolean val) {
		shutdown = val;
	}

	@Override
	public int dissipateHeat() {
		if (heat <= 0)
			return 0;

		int loss = 1; // Basic loss even when running

		if (!isBurning())
			loss++;

		double heatStage = getHeatLevel();
		if (heatStage > 0.55)
			loss++;

		// Lose extra heat when using water as fuel.
		EngineBronzeFuel fuel = FuelManager.bronzeEngineFuel.get(fuelTank.getFluid());
		if (fuel != null)
			loss = loss * fuel.dissipationMultiplier;

		heat -= loss;
		return loss;
	}

	@Override
	public int generateHeat() {

		int generate = 0;

		if (isActivated() && burnTime > 0) {
			double heatStage = getHeatLevel();
			if (heatStage >= 0.75)
				generate += Defaults.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 3;
			else if (heatStage > 0.24)
				generate += Defaults.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 2;
			else if (heatStage > 0.2)
				generate += Defaults.ENGINE_BRONZE_HEAT_GENERATION_ENERGY;
		}

		heat += generate;
		return generate;

	}

	/**
	 * Returns the fuel value (power per cycle) an item of the passed ItemStack
	 * provides
	 *
	 * @param item
	 * @return
	 */
	private int determineFuelValue(Fluid fluid) {
		if (FuelManager.bronzeEngineFuel.containsKey(fluid))
			return FuelManager.bronzeEngineFuel.get(fluid).powerPerCycle;
		else
			return 0;
	}

	/**
	 *
	 * @param fuelid
	 * @return Duration of burn cycle of one bucket
	 */
	private int determineBurnTime(Fluid item) {
		if (FuelManager.bronzeEngineFuel.containsKey(item))
			return FuelManager.bronzeEngineFuel.get(item).burnDuration;
		else
			return 0;
	}

	// / STATE INFORMATION
	@Override
	public boolean isBurning() {
		return mayBurn() && burnTime > 0;
	}

	@Override
	public int getBurnTimeRemainingScaled(int i) {
		if (totalTime == 0)
			return 0;

		return (burnTime * i) / totalTime;
	}

	public int getOperatingTemperatureScaled(int i) {
		return (int) Math.round((heat * i) / (maxHeat * 0.2));
	}

	public int getFuelScaled(int i) {
		return (fuelTank.getFluidAmount() * i) / Defaults.ENGINE_TANK_CAPACITY;
	}

	public int getHeatingFuelScaled(int i) {
		return (heatingTank.getFluidAmount() * i) / Defaults.ENGINE_TANK_CAPACITY;
	}

	/**
	 * Reads saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		burnTime = nbt.getInteger("EngineBurnTime");
		totalTime = nbt.getInteger("EngineTotalTime");

		if (nbt.hasKey("currentFluid")) {
			Fluid fluid = FluidRegistry.getFluid(nbt.getString("currentFluid"));
			if (fluid != null)
				currentFluidId = fluid.getID();
		}

		fuelTank = new ForestryTank(Defaults.ENGINE_TANK_CAPACITY);
		heatingTank = new ForestryTank(Defaults.ENGINE_TANK_CAPACITY);
		if (nbt.hasKey("FuelSlot")) {
			fuelTank.readFromNBT(nbt.getCompoundTag("FuelSlot"));
			heatingTank.readFromNBT(nbt.getCompoundTag("HeatingSlot"));
		}

		inventory.readFromNBT(nbt);

	}

	/**
	 * Writes data to save
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("EngineBurnTime", burnTime);
		nbt.setInteger("EngineTotalTime", totalTime);

		Fluid fluid = FluidRegistry.getFluid(currentFluidId);
		if (fluid != null)
			nbt.setString("currentFluid", fluid.getName());

		NBTTagCompound nbtFuelSlot = new NBTTagCompound();
		NBTTagCompound nbtHeatingSlot = new NBTTagCompound();

		fuelTank.writeToNBT(nbtFuelSlot);
		heatingTank.writeToNBT(nbtHeatingSlot);

		nbt.setTag("FuelSlot", nbtFuelSlot);
		nbt.setTag("HeatingSlot", nbtHeatingSlot);

		inventory.writeToNBT(nbt);
	}

	/* GUI */
	@Override
	public void getGUINetworkData(int id, int data) {

		switch (id) {
		case 0:
			burnTime = data;
			break;
		case 1:
			totalTime = data;
			break;
		case 2:
			currentOutput = data;
			break;
		case 3:
			storedEnergy = data;
			break;
		case 4:
			heat = data;
			break;
		case 5:
			currentFluidId = data;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(containerEngine, 0, burnTime);
		iCrafting.sendProgressBarUpdate(containerEngine, 1, totalTime);
		iCrafting.sendProgressBarUpdate(containerEngine, 2, currentOutput);
		iCrafting.sendProgressBarUpdate(containerEngine, 3, (int) storedEnergy);
		iCrafting.sendProgressBarUpdate(containerEngine, 4, heat);
		iCrafting.sendProgressBarUpdate(containerEngine, 5, currentFluidId);
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
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
	public String getInventoryName() {
		return super.getInventoryName();
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

		return inventory.addStack(stack, false, doAdd);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		return null;
	}

	// / ITANKCONTAINER
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// We only accept biomass and water as fuel
		int used = 0;

		if (FuelManager.bronzeEngineFuel.containsKey(resource))
			used = fuelTank.fill(resource, doFill);

		if (LiquidHelper.isLiquid(Defaults.LIQUID_LAVA, resource))
			used = heatingTank.fill(resource, doFill);

		return used;
	}

	@Override
	public ForestryTank[] getTanks() {
		return new ForestryTank[] { fuelTank, heatingTank };
	}
}
