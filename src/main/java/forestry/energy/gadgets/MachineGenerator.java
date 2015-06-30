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

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.Utils;
import forestry.plugins.PluginIC2;

import ic2.api.energy.prefab.BasicSource;

public class MachineGenerator extends TileBase implements ISidedInventory, ILiquidTankContainer, IRenderableMachine {

	// / CONSTANTS
	public static final short SLOT_CAN = 0;
	public static final int maxEnergy = 30000;

	public final FilteredTank resourceTank;
	private final TankManager tankManager;
	private int tickCount = 0;

	protected BasicSource ic2EnergySource;

	public MachineGenerator() {
		setHints(Config.hints.get("generator"));

		setInternalInventory(new GeneratorInventoryAdapter(this));

		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, FuelManager.generatorFuel.keySet());
		tankManager = new TankManager(resourceTank);

		if (PluginIC2.instance.isAvailable()) {
			ic2EnergySource = new BasicSource(this, maxEnergy, 1);
		}
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.GeneratorGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (ic2EnergySource != null) {
			ic2EnergySource.writeToNBT(nbttagcompound);
		}

		tankManager.writeTanksToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (ic2EnergySource != null) {
			ic2EnergySource.readFromNBT(nbttagcompound);
		}

		tankManager.readTanksFromNBT(nbttagcompound);
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writePacketData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readPacketData(data);
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
			// Check inventory slots for fuel
			// Check if we have suitable items waiting in the item slot
			IInventoryAdapter inventory = getInternalInventory();
			if (inventory.getStackInSlot(SLOT_CAN) != null) {
				FluidHelper.drainContainers(tankManager, inventory, SLOT_CAN);
			}
		}

		IErrorLogic errorLogic = getErrorLogic();

		// No work to be done if IC2 is unavailable.
		if (errorLogic.setCondition(ic2EnergySource == null, EnumErrorCode.NOENERGYNET)) {
			return;
		}

		ic2EnergySource.updateEntity();

		if (resourceTank.getFluidAmount() > 0) {
			GeneratorFuel fuel = FuelManager.generatorFuel.get(resourceTank.getFluid().getFluid());

			if (resourceTank.getFluidAmount() >= fuel.fuelConsumed.amount
					&& ic2EnergySource.getFreeCapacity() >= fuel.eu) {
				ic2EnergySource.addEnergy(fuel.eu);
				this.tickCount++;

				if (tickCount >= fuel.rate) {
					tickCount = 0;
					resourceTank.drain(fuel.fuelConsumed.amount, true);
				}
			}

		}

		boolean hasFuel = resourceTank.getFluidAmount() > 0;
		errorLogic.setCondition(!hasFuel, EnumErrorCode.NOFUEL);
	}

	public boolean isWorking() {
		return resourceTank.getFluidAmount() > 0;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public int getStoredScaled(int i) {
		if (ic2EnergySource == null) {
			return 0;
		}

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
	@Override
	public void getGUINetworkData(int i, int j) {
		int firstMessageId = tankManager.maxMessageId() + 1;
		if (i == firstMessageId) {
			if (ic2EnergySource != null) {
				ic2EnergySource.setEnergyStored(j);
			}
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int firstMessageId = tankManager.maxMessageId() + 1;
		if (ic2EnergySource != null) {
			iCrafting.sendProgressBarUpdate(container, firstMessageId, (short) ic2EnergySource.getEnergyStored());
		}
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

	private static class GeneratorInventoryAdapter extends TileInventoryAdapter<MachineGenerator> {
		public GeneratorInventoryAdapter(MachineGenerator generator) {
			super(generator, 1, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_CAN) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.tankManager.accepts(fluid);
			}

			return false;
		}
	}
}
