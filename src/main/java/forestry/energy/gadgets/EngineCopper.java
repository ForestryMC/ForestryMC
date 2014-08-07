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

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.registry.GameData;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITrigger;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.api.fuels.FuelManager;
import forestry.core.EnumErrorCode;
import forestry.core.TemperatureState;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.TileInventoryAdapter;

public class EngineCopper extends Engine implements ISpecialInventory, ISidedInventory {

	/* CONSTANTS */
	public static final short SLOT_FUEL = 0;
	public static final short SLOT_WASTE_1 = 1;
	public static final short SLOT_WASTE_COUNT = 4;

	/* MEMBERS */
	private Item fuelItem;
	private int fuelItemMeta;
	private int burnTime;
	private int totalBurnTime;
	private int ashProduction;
	private final int ashForItem;

	private final TileInventoryAdapter inventory;

	public EngineCopper() {
		super(Defaults.ENGINE_COPPER_HEAT_MAX, 20000, 400);
		setHints(Config.hints.get("engine.copper"));

		ashForItem = Defaults.ENGINE_COPPER_ASH_FOR_ITEM;
		inventory = new TileInventoryAdapter(this, 5, "Items");
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.EngineCopperGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	private int getFuelSlot() {

		if (inventory.getStackInSlot(SLOT_FUEL) == null)
			return -1;

		if (determineFuelValue(inventory.getStackInSlot(SLOT_FUEL)) > 0)
			return SLOT_FUEL;

		return -1;
	}

	private int getFreeWasteSlot() {

		for (int i = SLOT_WASTE_1; i <= SLOT_WASTE_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				return i;
			if (!ForestryItem.ash.isItemEqual(inventory.getStackInSlot(i)))
				continue;

			if (inventory.getStackInSlot(i).stackSize < 64)
				return i;
		}

		return -1;
	}

	@Override
	public void updateServerSide() {

		super.updateServerSide();

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		if (mayBurn() && burnTime > 0) {
			setErrorState(EnumErrorCode.OK);
			return;
		} else if (forceCooldown) {
			setErrorState(EnumErrorCode.FORCEDCOOLDOWN);
			return;
		}

		int fuelSlot = getFuelSlot();
		if (fuelSlot >= 0 && determineBurnDuration(inventory.getStackInSlot(fuelSlot)) > 0)
			setErrorState(EnumErrorCode.OK);
		else
			setErrorState(EnumErrorCode.NOFUEL);
	}

	@Override
	public void burn() {

		currentOutput = 0;

		if (burnTime > 0) {
			burnTime--;
			addAsh(1);

			if (isActivated()) {
				currentOutput = determineFuelValue(new ItemStack(fuelItem, 1, fuelItemMeta));
				addEnergy(currentOutput);
			}
		} else if (isActivated()) {
			int fuelslot = getFuelSlot();
			int wasteslot = getFreeWasteSlot();

			if (fuelslot >= 0 && wasteslot >= 0) {
				burnTime = totalBurnTime = determineBurnDuration(inventory.getStackInSlot(fuelslot));
				if (burnTime > 0) {
					fuelItem = inventory.getStackInSlot(fuelslot).getItem();
					decrStackSize(fuelslot, 1);
				}
			}
		}
	}

	@Override
	public int dissipateHeat() {
		if (heat <= 0)
			return 0;

		int loss = 0;

		if (!isBurning())
			loss += 1;

		TemperatureState tempState = getTemperatureState();
		if (tempState == TemperatureState.OVERHEATING || tempState == TemperatureState.OPERATING_TEMPERATURE)
			loss += 1;

		heat -= loss;
		return loss;
	}

	@Override
	public int generateHeat() {

		int heat = 0;

		if (isBurning()) {
			heat++;
			if (((double) storedEnergy / (double) maxEnergy) > 0.5)
				heat++;
		}

		addHeat(heat);
		return heat;
	}

	private void addAsh(int amount) {

		ashProduction += amount;
		if (ashProduction < ashForItem)
			return;

		// If we have reached the necessary amount, we need to add ash
		int wasteslot = getFreeWasteSlot();
		if (wasteslot >= 0)
			if (inventory.getStackInSlot(wasteslot) == null)
				inventory.setInventorySlotContents(wasteslot, ForestryItem.ash.getItemStack());
			else
				inventory.getStackInSlot(wasteslot).stackSize++;
		// Reset
		ashProduction = 0;
		// try to dump stash
		dumpStash();
	}

	/**
	 * Returns the fuel value (power per cycle) an item of the passed ItemStack provides
	 * 
	 * @param item
	 * @return
	 */
	private int determineFuelValue(ItemStack fuel) {
		if (FuelManager.copperEngineFuel.containsKey(fuel))
			return FuelManager.copperEngineFuel.get(fuel).powerPerCycle;
		else
			return 0;
	}

	/**
	 * Returns the fuel value (power per cycle) an item of the passed ItemStack provides
	 * 
	 * @param item
	 * @return
	 */
	private int determineBurnDuration(ItemStack fuel) {
		if (FuelManager.copperEngineFuel.containsKey(fuel))
			return FuelManager.copperEngineFuel.get(fuel).burnDuration;
		else
			return 0;
	}

	private void dumpStash() {
		ForgeDirection[] pipes = BlockUtil.getPipeDirections(worldObj, Coords(), ForgeDirection.UNKNOWN);

		if (pipes.length > 0)
			dumpToPipe(pipes);
	}

	private void dumpToPipe(ForgeDirection[] pipes) {

		for (int i = SLOT_WASTE_1; i < SLOT_WASTE_1 + SLOT_WASTE_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).stackSize <= 0)
				continue;

			ArrayList<ForgeDirection> filtered;
			filtered = BlockUtil.filterPipeDirections(pipes, new ForgeDirection[] { getOrientation() });

			while (inventory.getStackInSlot(i).stackSize > 0 && filtered.size() > 0)
				BlockUtil.putFromStackIntoPipe(this, filtered, inventory.getStackInSlot(i));

			if (inventory.getStackInSlot(i).stackSize <= 0)
				inventory.setInventorySlotContents(i, null);
		}
	}

	// / STATE INFORMATION

	@Override
	public boolean isBurning() {
		return mayBurn() && burnTime > 0;
	}

	@Override
	public int getBurnTimeRemainingScaled(int i) {
		if (totalBurnTime == 0)
			return 0;

		return (burnTime * i) / totalBurnTime;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		int fuelSlot = this.getFuelSlot();
		if (fuelSlot < 0)
			return false;

		return ((float) inventory.getStackInSlot(fuelSlot).stackSize / (float) inventory.getStackInSlot(fuelSlot).getMaxStackSize()) > percentage;
	}

	// / LOADING AND SAVING
	/**
	 * Reads saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		String fuelItemName = nbttagcompound.getString("EngineFuelItem");

		if (!fuelItemName.isEmpty()) fuelItem = GameData.getItemRegistry().getRaw(fuelItemName);

		fuelItemMeta = nbttagcompound.getInteger("EngineFuelMeta");
		burnTime = nbttagcompound.getInteger("EngineBurnTime");
		totalBurnTime = nbttagcompound.getInteger("EngineTotalTime");
		if (nbttagcompound.hasKey("AshProduction"))
			ashProduction = nbttagcompound.getInteger("AshProduction");

		// Fuel
		inventory.readFromNBT(nbttagcompound);
	}

	/**
	 * Writes data to save
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (fuelItem != null) {
			nbttagcompound.setString("EngineFuelItem", GameData.getItemRegistry().getNameForObject(fuelItem));
		}

		nbttagcompound.setInteger("EngineFuelMeta", fuelItemMeta);
		nbttagcompound.setInteger("EngineBurnTime", burnTime);
		nbttagcompound.setInteger("EngineTotalTime", totalBurnTime);
		nbttagcompound.setInteger("AshProduction", ashProduction);

		inventory.writeToNBT(nbttagcompound);
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			burnTime = j;
			break;
		case 1:
			totalBurnTime = j;
			break;
		case 2:
			currentOutput = j;
			break;
		case 3:
			storedEnergy = j;
			break;
		case 4:
			heat = j;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(containerEngine, 0, burnTime);
		iCrafting.sendProgressBarUpdate(containerEngine, 1, totalBurnTime);
		iCrafting.sendProgressBarUpdate(containerEngine, 2, currentOutput);
		iCrafting.sendProgressBarUpdate(containerEngine, 3, (int)storedEnergy);
		iCrafting.sendProgressBarUpdate(containerEngine, 4, heat);
	}

	// / IINVENTORY
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

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return super.canInsertItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return super.canExtractItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return super.getAccessibleSlotsFromSide(side);
	}

	/* ISIDEDINVENTORY */
	@Override
	public InventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	protected boolean canTakeStackFromSide(int slotIndex, ItemStack itemstack, int side) {
		if(!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		if(slotIndex >= SLOT_WASTE_1 && slotIndex < SLOT_WASTE_1 + SLOT_WASTE_COUNT)
			return true;

		return false;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {
		if(!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		if(slotIndex == SLOT_FUEL && FuelManager.copperEngineFuel.containsKey(itemstack))
			return true;

		return false;

	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		// Peat
		if (FuelManager.copperEngineFuel.containsKey(stack))
			return inventory.addStack(stack, SLOT_FUEL, 1, false, doAdd);

		return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		for (int i = SLOT_WASTE_1; i < SLOT_WASTE_1 + SLOT_WASTE_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;

			// Only ash can be extracted
			if (!ForestryItem.ash.isItemEqual(inventory.getStackInSlot(i)))
				continue;

			ItemStack product = ForestryItem.ash.getItemStack();
			if (doRemove)
				decrStackSize(i, 1);
			return new ItemStack[] { product };
		}

		return new ItemStack[0];
	}

	/* ITRIGGERPROVIDER */
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowFuel25);
		return res;
	}

}
