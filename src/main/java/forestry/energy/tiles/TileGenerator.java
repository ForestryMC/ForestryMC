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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;
import forestry.energy.gui.ContainerGenerator;
import forestry.energy.gui.GuiGenerator;
import forestry.energy.inventory.InventoryGenerator;
import forestry.plugins.compat.PluginIC2;

import ic2.api.energy.prefab.BasicSource;

public class TileGenerator extends TileBase implements ISidedInventory, ILiquidTankTile, IFluidHandler, IRenderableTile {
	private static final int maxEnergy = 30000;

	private final TankManager tankManager;
	private final FilteredTank resourceTank;

	private int tickCount = 0;

	private BasicSource ic2EnergySource;

	public TileGenerator() {
		super("generator");

		setInternalInventory(new InventoryGenerator(this));

		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FuelManager.generatorFuel.keySet());
		tankManager = new TankManager(this, resourceTank);

		if (PluginIC2.instance.isAvailable()) {
			ic2EnergySource = new BasicSource(this, maxEnergy, 1);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (ic2EnergySource != null) {
			ic2EnergySource.writeToNBT(nbttagcompound);
		}

		tankManager.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (ic2EnergySource != null) {
			ic2EnergySource.readFromNBT(nbttagcompound);
		}

		tankManager.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
	}

	@Override
	public void onChunkUnload() {
		if (ic2EnergySource != null) {
			ic2EnergySource.onChunkUnload();
		}

		super.onChunkUnload();
	}

	@Override
	public void invalidate() {
		if (ic2EnergySource != null) {
			ic2EnergySource.invalidate();
		}

		super.invalidate();
	}

	@Override
	public void updateServerSide() {
		if (updateOnInterval(20)) {
			FluidHelper.drainContainers(tankManager, this, InventoryGenerator.SLOT_CAN);
		}

		IErrorLogic errorLogic = getErrorLogic();

		// No work to be done if IC2 is unavailable.
		if (errorLogic.setCondition(ic2EnergySource == null, EnumErrorCode.NO_ENERGY_NET)) {
			return;
		}

		ic2EnergySource.updateEntity();

		if (resourceTank.getFluidAmount() > 0) {
			GeneratorFuel fuel = FuelManager.generatorFuel.get(resourceTank.getFluid().getFluid());
			if (resourceTank.canDrain(fuel.fuelConsumed) && ic2EnergySource.getFreeCapacity() >= fuel.eu) {
				ic2EnergySource.addEnergy(fuel.eu);
				this.tickCount++;

				if (tickCount >= fuel.rate) {
					tickCount = 0;
					resourceTank.drain(fuel.fuelConsumed.amount, true);
				}
			}

		}

		boolean hasFuel = resourceTank.getFluidAmount() > 0;
		errorLogic.setCondition(!hasFuel, EnumErrorCode.NO_FUEL);
	}

	public boolean isWorking() {
		return resourceTank.getFluidAmount() > 0;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Constants.PROCESSOR_TANK_CAPACITY;
	}

	public int getStoredScaled(int i) {
		if (ic2EnergySource == null) {
			return 0;
		}

		return (int) (ic2EnergySource.getEnergyStored() * i) / maxEnergy;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return TankRenderInfo.EMPTY;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		if (i == 0) {
			if (ic2EnergySource != null) {
				ic2EnergySource.setEnergyStored(j);
			}
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		if (ic2EnergySource != null) {
			iCrafting.sendProgressBarUpdate(container, 0, (short) ic2EnergySource.getEnergyStored());
		}
	}

	/* ILiquidTankTile */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
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
		return new GuiGenerator(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerGenerator(player.inventory, this);
	}
}
