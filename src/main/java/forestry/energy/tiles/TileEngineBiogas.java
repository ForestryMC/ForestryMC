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
package forestry.energy.tiles;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TileEngine;
import forestry.energy.gui.ContainerEngineBiogas;
import forestry.energy.gui.GuiEngineBiogas;
import forestry.energy.inventory.InventoryEngineBiogas;

public class TileEngineBiogas extends TileEngine implements ISidedInventory, ILiquidTankTile, IFluidHandler {
	private final FilteredTank fuelTank;
	private final FilteredTank heatingTank;
	private final StandardTank burnTank;
	private final TankManager tankManager;

	private boolean shutdown; // true if the engine is too cold and needs to warm itself up.

	public TileEngineBiogas() {
		super("engine.bronze", Constants.ENGINE_BRONZE_HEAT_MAX, 300000);

		setInternalInventory(new InventoryEngineBiogas(this));

		fuelTank = new FilteredTank(Constants.ENGINE_TANK_CAPACITY, FuelManager.bronzeEngineFuel.keySet());
		fuelTank.tankMode = StandardTank.TankMode.DEFAULT;
		heatingTank = new FilteredTank(Constants.ENGINE_TANK_CAPACITY, FluidRegistry.LAVA);
		heatingTank.tankMode = StandardTank.TankMode.INPUT;
		burnTank = new StandardTank(Constants.BUCKET_VOLUME);
		burnTank.tankMode = StandardTank.TankMode.INTERNAL;
		this.tankManager = new TankManager(this, fuelTank, heatingTank, burnTank);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	public Fluid getBurnTankFluidType() {
		return burnTank.getFluidType();
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();
		if (!updateOnInterval(20)) {
			return;
		}

		// Check if we have suitable items waiting in the item slot
		FluidHelper.drainContainers(tankManager, this, InventoryEngineBiogas.SLOT_CAN);

		IErrorLogic errorLogic = getErrorLogic();

		boolean hasHeat = getHeatLevel() > 0.2 || heatingTank.getFluidAmount() > 0;
		errorLogic.setCondition(!hasHeat, EnumErrorCode.NO_HEAT);

		boolean hasFuel = burnTank.getFluidAmount() > 0 || fuelTank.getFluidAmount() > 0;
		errorLogic.setCondition(!hasFuel, EnumErrorCode.NO_FUEL);
	}

	/**
	 * Burns fuel increasing stored energy
	 */
	@Override
	public void burn() {

		currentOutput = 0;

		if (isRedstoneActivated() && (fuelTank.getFluidAmount() >= Constants.BUCKET_VOLUME || burnTank.getFluidAmount() > 0)) {

			double heatStage = getHeatLevel();

			// If we have reached a safe temperature, enable energy transfer
			if (heatStage > 0.25 && shutdown) {
				shutdown(false);
			} else if (shutdown)

			{
				if (heatingTank.getFluidAmount() > 0 && Fluids.LAVA.is(heatingTank.getFluid())) {
					addHeat(Constants.ENGINE_HEAT_VALUE_LAVA);
					heatingTank.drain(1, true);
				}
			}

			// We need a minimum temperature to generate energy
			if (heatStage > 0.2) {
				if (burnTank.getFluidAmount() > 0) {
					FluidStack drained = burnTank.drain(1, true);
					currentOutput = determineFuelValue(drained.getFluid());
					energyManager.generateEnergy(currentOutput);
				} else {
					FluidStack fuel = fuelTank.drain(Constants.BUCKET_VOLUME, true);
					int burnTime = determineBurnTime(fuel.getFluid());
					fuel.amount = burnTime;
					burnTank.setCapacity(burnTime);
					burnTank.setFluid(fuel);
				}
			} else {
				shutdown(true);
			}
		}
	}

	private void shutdown(boolean val) {
		shutdown = val;
	}

	@Override
	public int dissipateHeat() {
		if (heat <= 0) {
			return 0;
		}

		int loss = 1; // Basic loss even when running

		if (!isBurning()) {
			loss++;
		}

		double heatStage = getHeatLevel();
		if (heatStage > 0.55) {
			loss++;
		}

		// Lose extra heat when using water as fuel.
		if (fuelTank.getFluidAmount() > 0) {
			FluidStack fuelFluidStack = fuelTank.getFluid();
			EngineBronzeFuel fuel = FuelManager.bronzeEngineFuel.get(fuelFluidStack.getFluid());
			if (fuel != null) {
				loss = loss * fuel.dissipationMultiplier;
			}
		}

		heat -= loss;
		return loss;
	}

	@Override
	public int generateHeat() {

		int generate = 0;

		if (isRedstoneActivated() && burnTank.getFluidAmount() > 0) {
			double heatStage = getHeatLevel();
			if (heatStage >= 0.75) {
				generate += Constants.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 3;
			} else if (heatStage > 0.24) {
				generate += Constants.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 2;
			} else if (heatStage > 0.2) {
				generate += Constants.ENGINE_BRONZE_HEAT_GENERATION_ENERGY;
			}
		}

		heat += generate;
		return generate;

	}

	/**
	 * Returns the fuel value (power per cycle) an item of the passed fluid
	 */
	private static int determineFuelValue(Fluid fluid) {
		if (FuelManager.bronzeEngineFuel.containsKey(fluid)) {
			return FuelManager.bronzeEngineFuel.get(fluid).powerPerCycle;
		} else {
			return 0;
		}
	}

	/**
	 * @return Duration of burn cycle of one bucket
	 */
	private static int determineBurnTime(Fluid fluid) {
		if (FuelManager.bronzeEngineFuel.containsKey(fluid)) {
			return FuelManager.bronzeEngineFuel.get(fluid).burnDuration;
		} else {
			return 0;
		}
	}

	// / STATE INFORMATION
	@Override
	protected boolean isBurning() {
		return mayBurn() && burnTank.getFluidAmount() > 0;
	}

	@Override
	public int getBurnTimeRemainingScaled(int i) {
		if (burnTank.getCapacity() == 0) {
			return 0;
		}

		return (burnTank.getFluidAmount() * i) / burnTank.getCapacity();
	}

	public int getOperatingTemperatureScaled(int i) {
		return (int) Math.round((heat * i) / (maxHeat * 0.2));
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		//legacy
		{
			if (nbt.hasKey("currentFluid")) {
				Fluid fluid = FluidRegistry.getFluid(nbt.getString("currentFluid"));
				if (fluid != null) {
					int burnTime = nbt.getInteger("EngineBurnTime");
					int totalTime = nbt.getInteger("EngineTotalTime");
					burnTank.setCapacity(totalTime);
					burnTank.setFluid(new FluidStack(fluid, burnTime));
				}
			}
		}

		if (nbt.hasKey("shutdown")) {
			shutdown = nbt.getBoolean("shutdown");
		}
		tankManager.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("shutdown", shutdown);
		tankManager.writeToNBT(nbt);
	}

	/* NETWORK */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeBoolean(shutdown);
		tankManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		shutdown = data.readBoolean();
		tankManager.readData(data);
	}

	/* GUI */
	@Override
	public void getGUINetworkData(int id, int data) {
		switch (id) {
			case 0:
				currentOutput = data;
				break;
			case 1:
				energyManager.fromGuiInt(data);
				break;
			case 2:
				heat = data;
				break;
			case 3:
				burnTank.setCapacity(data);
				break;
		}
	}

	@Override
	public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(containerEngine, 0, currentOutput);
		iCrafting.sendProgressBarUpdate(containerEngine, 1, energyManager.toGuiInt());
		iCrafting.sendProgressBarUpdate(containerEngine, 2, heat);
		iCrafting.sendProgressBarUpdate(containerEngine, 3, burnTank.getCapacity());
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

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiEngineBiogas(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerEngineBiogas(player.inventory, this);
	}
}
