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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ic2.api.energy.prefab.BasicSink;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.TemperatureState;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ISocketable;
import forestry.core.network.GuiId;
import forestry.core.utils.DelayTimer;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.InventoryAdapter;
import forestry.plugins.PluginIC2;

public class EngineTin extends Engine implements ISocketable, IInventory {

	protected static class EuConfig {
		public int euForCycle;
		public int rfPerCycle;
		public int euStorage;
		public int euMaxAccept = 512;

		public EuConfig(int euForCycle, int rfPerCycle, int euStorage) {
			this.euForCycle = euForCycle;
			this.rfPerCycle = rfPerCycle;
			this.euStorage = euStorage;
		}
	}

	private final short batterySlot = 0;
	private final InventoryAdapter inventory = new InventoryAdapter(1, "electrical");
	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");
	private final EuConfig euConfig = new EuConfig(Defaults.ENGINE_TIN_EU_FOR_CYCLE, Defaults.ENGINE_TIN_ENERGY_PER_CYCLE, Defaults.ENGINE_TIN_MAX_EU_STORED);

	protected BasicSink ic2EnergySink;

	private final DelayTimer delayUpdateTimer = new DelayTimer();

	public EngineTin() {
		super(Defaults.ENGINE_TIN_HEAT_MAX, 100000, 4000);
		setHints(Config.hints.get("engine.tin"));

		if (PluginIC2.instance.isAvailable()) ic2EnergySink = new BasicSink(this, euConfig.euStorage, 3);
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.EngineTinGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	// / SAVING / LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (ic2EnergySink != null) ic2EnergySink.readFromNBT(nbttagcompound);
		inventory.readFromNBT(nbttagcompound);
		sockets.readFromNBT(nbttagcompound);

		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(chip);
			if (chipset != null)
				chipset.onLoad(this);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (ic2EnergySink != null) ic2EnergySink.writeToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);
		sockets.writeToNBT(nbttagcompound);
	}

	@Override
	public void onChunkUnload() {
		if (ic2EnergySink != null) ic2EnergySink.onChunkUnload();

		super.onChunkUnload();
	}

	@Override
	public void invalidate() {
		if (ic2EnergySink != null) ic2EnergySink.invalidate();

		super.invalidate();
	}

	// / HEAT MANAGMENT
	@Override
	public int dissipateHeat() {
		if (heat <= 0)
			return 0;

		int loss = 0;

		if (!isBurning() || !isActivated())
			loss += 1;

		TemperatureState tempState = getTemperatureState();
		if (tempState == TemperatureState.OVERHEATING || tempState == TemperatureState.OPERATING_TEMPERATURE)
			loss += 1;

		heat -= loss;
		return loss;
	}

	@Override
	public int generateHeat() {

		int gain = 0;
		if (isActivated() && isBurning()) {
			gain++;
			if (((double) energyStorage.getEnergyStored() / (double) maxEnergy) > 0.5)
				gain++;
		}

		addHeat(gain);
		return gain;
	}

	// / WORK
	@Override
	public void updateServerSide() {
		// No work to be done if IC2 is unavailable.
		if (ic2EnergySink == null) {
			setErrorState(EnumErrorCode.NOENERGYNET);
			return;
		}

		ic2EnergySink.updateEntity();

		super.updateServerSide();

		if (forceCooldown) {
			setErrorState(EnumErrorCode.FORCEDCOOLDOWN);
			return;
		}

		if (inventory.getStackInSlot(batterySlot) != null)
			replenishFromBattery(batterySlot);

		// Updating of gui delayed to prevent it from going crazy
		if (!delayUpdateTimer.delayPassed(worldObj, 80))
			return;

		if (currentOutput <= 0 && getErrorState() == EnumErrorCode.OK)
			setErrorState(EnumErrorCode.NOFUEL);
		else
			setErrorState(EnumErrorCode.OK);
	}

	@Override
	public void burn() {

		currentOutput = 0;

		if (!isActivated())
			return;

		if (ic2EnergySink.useEnergy(euConfig.euForCycle)) {
			currentOutput = euConfig.rfPerCycle;
			energyStorage.modifyEnergyStored(euConfig.rfPerCycle);
		}

	}

	private void replenishFromBattery(int slot) {
		if (!isActivated())
			return;

		ic2EnergySink.discharge(inventory.getStackInSlot(slot), euConfig.euForCycle * 3);
	}

	// / STATE INFORMATION
	@Override
	public boolean isBurning() {
		return mayBurn() && ic2EnergySink != null && ic2EnergySink.canUseEnergy(euConfig.euForCycle);
	}

	public int getStorageScaled(int i) {
		if (ic2EnergySink == null) return 0;

		return Math.min(i, (int) (ic2EnergySink.getEnergyStored() * i) / ic2EnergySink.getCapacity());
	}

	public EnumTankLevel rateLevel(int scaled) {

		if (scaled < 5)
			return EnumTankLevel.EMPTY;
		else if (scaled < 30)
			return EnumTankLevel.LOW;
		else if (scaled < 60)
			return EnumTankLevel.MEDIUM;
		else if (scaled < 90)
			return EnumTankLevel.HIGH;
		else
			return EnumTankLevel.MAXIMUM;
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {

		case 0:
			currentOutput = j;
			break;
		case 1:
			energyStorage.setEnergyStored(j);
			break;
		case 2:
			heat = j;
			break;
		case 3:
			if (ic2EnergySink != null) ic2EnergySink.setEnergyStored(j);
			break;
		}

	}

	@Override
	public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(containerEngine, 0, currentOutput);
		iCrafting.sendProgressBarUpdate(containerEngine, 1, energyStorage.getEnergyStored());
		iCrafting.sendProgressBarUpdate(containerEngine, 2, heat);
		if (ic2EnergySink != null) {
			iCrafting.sendProgressBarUpdate(containerEngine, 3, (short) ic2EnergySink.getEnergyStored());
		}
	}

	// / ENERGY CONFIG CHANGE
	public void changeEnergyConfig(int euChange, int mjChange, int storageChange) {
		euConfig.euForCycle += euChange;
		euConfig.rfPerCycle += mjChange;
		euConfig.euStorage += storageChange;

		if (ic2EnergySink != null) ic2EnergySink.setCapacity(euConfig.euStorage);
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
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
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

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
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

	/* ISOCKETABLE */
	@Override
	public int getSocketCount() {
		return sockets.getSizeInventory();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return sockets.getStackInSlot(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {

		if (stack != null && !ChipsetManager.circuitRegistry.isChipset(stack))
			return;

		// Dispose correctly of old chipsets
		if (sockets.getStackInSlot(slot) != null)
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null)
					chipset.onRemoval(this);
			}

		if (stack == null) {
			sockets.setInventorySlotContents(slot, stack);
			return;
		}

		sockets.setInventorySlotContents(slot, stack);

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(stack);
		if (chipset != null)
			chipset.onInsertion(this);
	}

}
