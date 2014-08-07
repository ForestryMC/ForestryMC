/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gadgets;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.interfaces.IPowerHandler;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.network.ClassMap;
import forestry.core.network.IndexInPayload;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.ForestryTank;

public abstract class TilePowered extends TileBase implements IPowerHandler, IRenderableMachine {

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
	private final PowerHandler powerHandler;

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
	}

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

	/* IPOWERRECEPTOR */
	@Override
	public PowerHandler getPowerHandler() {
		return powerHandler;
	}
	private int workCounter;

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote)
			return;

		if (workCounter >= WORK_CYCLES && worldObj.getTotalWorldTime() % 5 == 0) {
			if (workCycle())
				workCounter = 0;
		}
	}

	@Override
	public void doWork(PowerHandler workProvider) {

		if (!Proxies.common.isSimulating(worldObj))
			return;

		// Hard limit to 4 cycles / second.
		if (workCounter < WORK_CYCLES) {
			powerHandler.useEnergy(powerHandler.getActivationEnergy(), powerHandler.getActivationEnergy(), true);
			workCounter++;
		}

		/*
		 // Hard limit to 4 cycles / second.
		 if (worldObj.getTotalWorldTime() % 5 * 10 != 0)
		 return;

		 PluginBuildCraft.instance.invokeUseEnergyMethod(powerHandler, powerHandler.getActivationEnergy(), powerHandler.getActivationEnergy(), false);

		 // Do not consume energy if the boiler didn't do any work.
		 if (!workCycle())
		 return;

		 // Use up energy since we did some work.
		 PluginBuildCraft.instance.invokeUseEnergyMethod(powerHandler, powerHandler.getActivationEnergy(), powerHandler.getActivationEnergy(), true);
		 */
	}

	public abstract boolean workCycle();

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerHandler.getPowerReceiver();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		powerHandler.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		powerHandler.readFromNBT(nbt);
	}

	/*
	 @Override
	 public int powerRequest(ForgeDirection direction) {
	 float needed = powerProvider.getMaxEnergyStored() - powerProvider.getEnergyStored();
	 return (int) Math.ceil(Math.min(powerProvider.getMaxEnergyReceived(), needed));
	 } */

	/* LIQUID CONTAINER HANDLING */
	protected ItemStack bottleIntoContainer(ItemStack canStack, ItemStack outputStack, FluidContainerData container, ForestryTank tank) {
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
}
