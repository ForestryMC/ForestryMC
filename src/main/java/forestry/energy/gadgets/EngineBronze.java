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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;

public class EngineBronze extends Engine implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_CAN = 0;

	/* NETWORK */
	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = super.getPacketPayload();

		if (shutdown)
			payload.append(new int[]{1});
		else
			payload.append(new int[]{0});

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		super.fromPacketPayload(payload);

		shutdown = payload.intPayload[3] > 0;
	}
	private final FilteredTank fuelTank;
	private final FilteredTank heatingTank;
	private final TankManager tankManager;
	public int burnTime;
	public int totalTime;
	public int currentFluidId = -1;
	/**
	 * true if the engine is too cold and needs to warm itself up.
	 */
	private boolean shutdown;

	public EngineBronze() {
		super(Defaults.ENGINE_BRONZE_HEAT_MAX, 300000, 5000);
		setHints(Config.hints.get("engine.bronze"));

		setInternalInventory(new TileInventoryAdapter(this, 1, "Items") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				if (slotIndex == SLOT_CAN) {
					Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
					return tankManager.accepts(fluid);
				}

				return false;
			}
		});

		fuelTank = new FilteredTank(Defaults.ENGINE_TANK_CAPACITY, FuelManager.bronzeEngineFuel.keySet());
		fuelTank.tankMode = StandardTank.TankMode.INPUT;
		heatingTank = new FilteredTank(Defaults.ENGINE_TANK_CAPACITY, FluidRegistry.LAVA);
		heatingTank.tankMode = StandardTank.TankMode.INPUT;
		this.tankManager = new TankManager(fuelTank, heatingTank);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.EngineBronzeGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();
		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		IInventoryAdapter inventory = getInternalInventory();
		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null)
			FluidHelper.drainContainers(tankManager, inventory, SLOT_CAN);

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

				if (heatingTank.getFluidAmount() > 0 && Fluids.LAVA.is(heatingTank.getFluid())) {
					addHeat(Defaults.ENGINE_HEAT_VALUE_LAVA);
					heatingTank.drain(1, true);
				}

			// We need a minimum temperature to generate energy
			if (heatStage > 0.2)

				if (burnTime > 0) {
					burnTime--;
					currentOutput = determineFuelValue(FluidRegistry.getFluid(currentFluidId));
					energyManager.generateEnergy(currentOutput);
				} else {
					burnTime = totalTime = this.determineBurnTime(fuelTank.getFluid().getFluid());
					currentFluidId = fuelTank.getFluid().getFluid().getID();
					fuelTank.drain(Defaults.BUCKET_VOLUME, true);
				}
			else
				shutdown(true);
		}

		if (burnTime <= 0)
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
		if (fuelTank.getFluidAmount() > 0) {
			FluidStack fuelFluidStack = fuelTank.getFluid();
			EngineBronzeFuel fuel = FuelManager.bronzeEngineFuel.get(fuelFluidStack.getFluid());
			if (fuel != null)
				loss = loss * fuel.dissipationMultiplier;
		}

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
	 * Returns the fuel value (power per cycle) an item of the passed fluid
	 */
	private int determineFuelValue(Fluid fluid) {
		if (FuelManager.bronzeEngineFuel.containsKey(fluid))
			return FuelManager.bronzeEngineFuel.get(fluid).powerPerCycle;
		else
			return 0;
	}

	/**
	 * @return Duration of burn cycle of one bucket
	 */
	private int determineBurnTime(Fluid fluid) {
		if (FuelManager.bronzeEngineFuel.containsKey(fluid))
			return FuelManager.bronzeEngineFuel.get(fluid).burnDuration;
		else
			return 0;
	}

	protected ItemStack replenishByContainer(ItemStack inventoryStack, FluidContainerData container, StandardTank tank) {
		if (container == null)
			return inventoryStack;

		if (tank.fill(container.fluid, false) >= container.fluid.amount) {
			tank.fill(container.fluid, true);
			if (container.filledContainer != null && container.filledContainer.getItem().hasContainerItem(container.filledContainer))
				inventoryStack = container.emptyContainer.copy();
			else
				inventoryStack.stackSize--;
		}

		return inventoryStack;
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

		tankManager.readTanksFromNBT(nbt);

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("EngineBurnTime", burnTime);
		nbt.setInteger("EngineTotalTime", totalTime);

		Fluid fluid = FluidRegistry.getFluid(currentFluidId);
		if (fluid != null)
			nbt.setString("currentFluid", fluid.getName());

		tankManager.writeTanksToNBT(nbt);
	}

	/* GUI */
	@Override
	public void getGUINetworkData(int id, int data) {
		id -= tankManager.maxMessageId() + 1;

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
			energyManager.fromPacketInt(data);
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
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(containerEngine, i, burnTime);
		iCrafting.sendProgressBarUpdate(containerEngine, i + 1, totalTime);
		iCrafting.sendProgressBarUpdate(containerEngine, i + 2, currentOutput);
		iCrafting.sendProgressBarUpdate(containerEngine, i + 3, energyManager.toPacketInt());
		iCrafting.sendProgressBarUpdate(containerEngine, i + 4, heat);
		iCrafting.sendProgressBarUpdate(containerEngine, i + 5, currentFluidId);
	}

	// IFluidHandler
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
