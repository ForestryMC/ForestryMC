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

import javax.annotation.Nonnull;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.ITankManager;
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

public class TileEuGenerator extends TileBase implements ISidedInventory, ILiquidTankTile, IRenderableTile {
	private static final int maxEnergy = 30000;

	private final TankManager tankManager;
	private final FilteredTank resourceTank;

	private int tickCount = 0;

	private BasicSource ic2EnergySource;

	public TileEuGenerator() {
		super("generator");

		setInternalInventory(new InventoryGenerator(this));

		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY);
		resourceTank.setFilters(FuelManager.generatorFuel.keySet());

		tankManager = new TankManager(this, resourceTank);

		if (PluginIC2.instance.isAvailable()) {
			ic2EnergySource = new BasicSource(this, maxEnergy, 1);
		}
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		if (ic2EnergySource != null) {
			ic2EnergySource.writeToNBT(nbttagcompound);
		}

		tankManager.writeToNBT(nbttagcompound);
		return nbttagcompound;
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

		ic2EnergySource.update();

		if (resourceTank.getFluidAmount() > 0) {
			GeneratorFuel fuel = FuelManager.generatorFuel.get(resourceTank.getFluid().getFluid());
			if (resourceTank.canDrainFluidType(fuel.fuelConsumed) && ic2EnergySource.getFreeCapacity() >= fuel.eu) {
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

	public void sendGUINetworkData(Container container, IContainerListener iCrafting) {
		if (ic2EnergySource != null) {
			iCrafting.sendProgressBarUpdate(container, 0, (short) ic2EnergySource.getEnergyStored());
		}
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiGenerator(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerGenerator(player.inventory, this);
	}

	@Nonnull
	@Override
	public ITankManager getTankManager() {
		return tankManager;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return true;
		}
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return super.getCapability(capability, facing);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return null;
	}
}
