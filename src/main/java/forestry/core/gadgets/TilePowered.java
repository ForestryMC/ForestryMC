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

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

import forestry.core.GameMode;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.network.ClassMap;
import forestry.core.network.IndexInPayload;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.fluids.tanks.StandardTank;

public abstract class TilePowered extends TileBase implements IRenderableMachine, IEnergyHandler {

	public static int WORK_CYCLES = 4;

	@Override
	public PacketPayload getPacketPayload() {
		if (!ClassMap.classMappers.containsKey(this.getClass()))
			ClassMap.classMappers.put(this.getClass(), new ClassMap(this.getClass()));

		ClassMap classmap = ClassMap.classMappers.get(this.getClass());
		PacketPayload payload = new PacketPayload(classmap.intSize, classmap.floatSize, classmap.stringSize);

		try {
			classmap.setData(this, payload.intPayload, payload.floatPayload, payload.stringPayload, new IndexInPayload(0, 0, 0));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {

		if (payload.isEmpty())
			return;

		if (!ClassMap.classMappers.containsKey(this.getClass()))
			ClassMap.classMappers.put(this.getClass(), new ClassMap(this.getClass()));

		ClassMap classmap = ClassMap.classMappers.get(this.getClass());

		try {
			classmap.fromData(this, payload.intPayload, payload.floatPayload, payload.stringPayload, new IndexInPayload(0, 0, 0));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    protected EnergyStorage energyStorage;
	public int energyPerUse;

	public TilePowered() {
		this(5000, 150);
    }

	public TilePowered(int defaultCapacity, int defaultEnergyPerUse) {
		this(defaultCapacity, defaultEnergyPerUse, defaultCapacity);
	}

	public TilePowered(int defaultCapacity, int defaultEnergyPerUse, int defaultMaxTransfer) {
		this.energyPerUse = scaleEnergyByDifficulty(defaultEnergyPerUse);
		int capacity = scaleEnergyByDifficulty(defaultCapacity);
		int maxTransfer = scaleEnergyByDifficulty(defaultMaxTransfer);
		energyStorage = new EnergyStorage(capacity, maxTransfer);
	}

	private static int scaleEnergyByDifficulty(int energyPerUse) {
		return Math.round(energyPerUse * GameMode.getGameMode().getFloatSetting("energy.demand.modifier"));
	}

	/*private final PowerHandler powerHandler;

	public TilePowered() {
		powerHandler = new PowerHandler(this, Type.MACHINE);
		configurePowerProvider(powerHandler);
		adjustPowerProvider(powerHandler);
	}

	protected void configurePowerProvider(PowerHandler provider) {
		provider.configure(Defaults.MACHINE_MIN_ENERGY_RECEIVED, Defaults.MACHINE_MAX_ENERGY_RECEIVED,
				Defaults.MACHINE_MIN_ACTIVATION_ENERGY, Defaults.MACHINE_MAX_ENERGY);
	}

	private void adjustPowerProvider(PowerHandler provider) {
		provider.configure(
				provider.getMinEnergyReceived(),
				Math.round(provider.getMaxEnergyReceived() * GameMode.getGameMode().getFloatSetting("energy.demand.modifier")),
				Math.round(provider.getActivationEnergy() * GameMode.getGameMode().getFloatSetting("energy.demand.modifier")),
				Math.round(provider.getMaxEnergyStored() * GameMode.getGameMode().getFloatSetting("energy.demand.modifier")));
	}*/

	/* STATE INFORMATION */
	public abstract boolean isWorking();

	public boolean hasResourcesMin(float percentage) {
		return false;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public boolean hasWork() {
		return false;
	}

	private int workCounter;

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!Proxies.common.isSimulating(worldObj))
			return;

        if (workCounter < WORK_CYCLES && energyStorage.getEnergyStored() >= energyPerUse) {
            energyStorage.modifyEnergyStored(-energyPerUse); //TODO Make it continuous usage while doing work
            workCounter++;
        }

		if (workCounter >= WORK_CYCLES && worldObj.getTotalWorldTime() % 5 == 0) {
			if (workCycle())
				workCounter = 0;
		}
	}

	public abstract boolean workCycle();

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		energyStorage.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energyStorage.readFromNBT(nbt);
	}

	/* LIQUID CONTAINER HANDLING */
	protected ItemStack bottleIntoContainer(ItemStack canStack, ItemStack outputStack, FluidContainerData container, StandardTank tank) {
		if (tank.getFluidAmount() < container.fluid.amount)
			return outputStack;
		if (canStack.stackSize <= 0)
			return outputStack;
		if (outputStack != null && !outputStack.isItemEqual(container.filledContainer))
			return outputStack;
		if (outputStack != null && outputStack.stackSize >= outputStack.getMaxStackSize())
			return outputStack;

		tank.drain(container.fluid.amount, true);
		canStack.stackSize--;

		if (outputStack == null)
			outputStack = container.filledContainer.copy();
		else
			outputStack.stackSize++;

		return outputStack;
	}

	// / ADDITIONAL LIQUID HANDLING
	@Override
	public EnumTankLevel getPrimaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}
}
