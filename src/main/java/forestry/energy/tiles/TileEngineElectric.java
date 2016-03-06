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

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.core.circuits.ISocketable;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.TemperatureState;
import forestry.core.tiles.TileEngine;
import forestry.energy.gui.ContainerEngineElectric;
import forestry.energy.gui.GuiEngineElectric;
import forestry.plugins.compat.PluginIC2;
import ic2.api.energy.prefab.BasicSink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class TileEngineElectric extends TileEngine implements ISocketable, IInventory, IStreamableGui {
	protected static class EuConfig {

		public int euForCycle;
		public int rfPerCycle;
		public int euStorage;

		public EuConfig() {
			this.euForCycle = Constants.ENGINE_TIN_EU_FOR_CYCLE;
			this.rfPerCycle = Constants.ENGINE_TIN_ENERGY_PER_CYCLE;
			this.euStorage = Constants.ENGINE_TIN_MAX_EU_STORED;
		}
	}

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");
	private final EuConfig euConfig = new EuConfig();

	private BasicSink ic2EnergySink;

	public TileEngineElectric() {
		super("engine.tin", Constants.ENGINE_ELECTRIC_HEAT_MAX, 100000);

		setInternalInventory(new InventoryEngineElectric(this));

		if (PluginIC2.instance.isAvailable()) {
			ic2EnergySink = new BasicSink(this, euConfig.euStorage, 3);
		}
	}

	// / SAVING / LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (ic2EnergySink != null) {
			ic2EnergySink.readFromNBT(nbttagcompound);
		}
		sockets.readFromNBT(nbttagcompound);

		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (ic2EnergySink != null) {
			ic2EnergySink.writeToNBT(nbttagcompound);
		}
		sockets.writeToNBT(nbttagcompound);
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(currentOutput);
		data.writeVarInt(heat);
		sockets.writeData(data);
		energyManager.writeData(data);
		final boolean hasIc2EnergySink = (ic2EnergySink != null);
		data.writeBoolean(hasIc2EnergySink);
		if (hasIc2EnergySink) {
			data.writeVarInt((int) ic2EnergySink.getEnergyStored());
		}
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		currentOutput = data.readVarInt();
		heat = data.readVarInt();
		sockets.readData(data);
		energyManager.readData(data);
		final boolean hasIc2EnergySink = data.readBoolean();
		if (hasIc2EnergySink) {
			final int energyStored = data.readVarInt();
			ic2EnergySink.setEnergyStored(energyStored);
		}
	}

	@Override
	public void onChunkUnload() {
		if (ic2EnergySink != null) {
			ic2EnergySink.onChunkUnload();
		}

		super.onChunkUnload();
	}

	@Override
	public void invalidate() {
		if (ic2EnergySink != null) {
			ic2EnergySink.invalidate();
		}

		super.invalidate();
	}

	// / HEAT MANAGEMENT
	@Override
	public int dissipateHeat() {
		if (heat <= 0) {
			return 0;
		}

		int loss = 0;

		if (!isBurning() || !isRedstoneActivated()) {
			loss += 1;
		}

		TemperatureState tempState = getTemperatureState();
		if (tempState == TemperatureState.OVERHEATING || tempState == TemperatureState.OPERATING_TEMPERATURE) {
			loss += 1;
		}

		heat -= loss;
		return loss;
	}

	@Override
	public int generateHeat() {

		int gain = 0;
		if (isRedstoneActivated() && isBurning()) {
			gain++;
			if (((double) energyManager.getTotalEnergyStored() / (double) energyManager.getMaxEnergyStored()) > 0.5) {
				gain++;
			}
		}

		addHeat(gain);
		return gain;
	}

	// / WORK
	@Override
	public void updateServerSide() {
		IErrorLogic errorLogic = getErrorLogic();

		// No work to be done if IC2 is unavailable.
		if (errorLogic.setCondition(ic2EnergySink == null, EnumErrorCode.NO_ENERGY_NET)) {
			return;
		}

		ic2EnergySink.updateEntity();

		super.updateServerSide();

		if (forceCooldown) {
			return;
		}

		if (getStackInSlot(InventoryEngineElectric.SLOT_BATTERY) != null) {
			replenishFromBattery(InventoryEngineElectric.SLOT_BATTERY);
		}

		// Updating of gui delayed to prevent it from going crazy
		if (!updateOnInterval(80)) {
			return;
		}

		boolean canUseEnergy = ic2EnergySink.canUseEnergy(euConfig.euForCycle);
		errorLogic.setCondition(!canUseEnergy, EnumErrorCode.NO_FUEL);
	}

	@Override
	public void burn() {

		currentOutput = 0;

		if (!isRedstoneActivated()) {
			return;
		}

		if (ic2EnergySink.useEnergy(euConfig.euForCycle)) {
			currentOutput = euConfig.rfPerCycle;
			energyManager.generateEnergy(euConfig.rfPerCycle);
		}

	}

	private void replenishFromBattery(int slot) {
		if (!isRedstoneActivated()) {
			return;
		}

		ic2EnergySink.discharge(getStackInSlot(slot), euConfig.euForCycle * 3);
	}

	// / STATE INFORMATION
	@Override
	protected boolean isBurning() {
		return mayBurn() && ic2EnergySink != null && ic2EnergySink.canUseEnergy(euConfig.euForCycle);
	}

	public int getStorageScaled(int i) {
		if (ic2EnergySink == null) {
			return 0;
		}

		return Math.min(i, (int) (ic2EnergySink.getEnergyStored() * i) / ic2EnergySink.getCapacity());
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {
		// handled by IStreamableGui
	}

	@Override
	public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
		// handled by IStreamableGui
	}

	// / ENERGY CONFIG CHANGE
	public void changeEnergyConfig(int euChange, int rfChange, int storageChange) {
		euConfig.euForCycle += euChange;
		euConfig.rfPerCycle += rfChange;
		euConfig.euStorage += storageChange;

		if (ic2EnergySink != null) {
			ic2EnergySink.setCapacity(euConfig.euStorage);
		}
	}

	/* ISocketable */
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

		if (stack != null && !ChipsetManager.circuitRegistry.isChipset(stack)) {
			return;
		}

		// Dispose correctly of old chipsets
		if (sockets.getStackInSlot(slot) != null) {
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null) {
					chipset.onRemoval(this);
				}
			}
		}

		sockets.setInventorySlotContents(slot, stack);
		if (stack == null) {
			return;
		}

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(stack);
		if (chipset != null) {
			chipset.onInsertion(this);
		}
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return CircuitSocketType.ELECTRIC_ENGINE;
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiEngineElectric(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerEngineElectric(player.inventory, this);
	}
}
