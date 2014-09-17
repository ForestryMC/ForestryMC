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
package forestry.factory.gadgets;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.gates.ITrigger;

import forestry.api.core.EnumHumidity;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.Fluids;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class MachineRaintank extends TileBase implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_PRODUCT = 1;
	private static final FluidStack STACK_WATER = LiquidHelper.getLiquid(Defaults.LIQUID_WATER, Defaults.RAINTANK_AMOUNT_PER_UPDATE);

	/* MEMBER */
	public StandardTank resourceTank = new StandardTank(Defaults.RAINTANK_TANK_CAPACITY);
	private final InventoryAdapter inventory = new InventoryAdapter(3, "Items");
	private boolean isValidBiome = true;
	private int fillingTime;
	private ItemStack usedEmpty;

	public MachineRaintank() {
		setHints(Config.hints.get("raintank"));

		// Raintanks in desert and snow biomes are useless
		if (worldObj != null) {
			BiomeGenBase biome = Utils.getBiomeAt(worldObj, xCoord, zCoord);
			if (EnumHumidity.getFromValue(biome.rainfall) == EnumHumidity.ARID) {
				setErrorState(EnumErrorCode.INVALIDBIOME);
				isValidBiome = false;
			}
		}
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.RaintankGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsValidBiome", isValidBiome);

		NBTTagCompound NBTresourceSlot = new NBTTagCompound();
		resourceTank.writeToNBT(NBTresourceSlot);
		nbttagcompound.setTag("ResourceTank", NBTresourceSlot);

		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		isValidBiome = nbttagcompound.getBoolean("IsValidBiome");

		resourceTank = new StandardTank(Defaults.RAINTANK_TANK_CAPACITY);
		if (nbttagcompound.hasKey("ResourceTank"))
			resourceTank.readFromNBT(nbttagcompound.getCompoundTag("ResourceTank"));

		inventory.readFromNBT(nbttagcompound);
	}

	@Override
	public void updateServerSide() {
		if (!isValidBiome)
			setErrorState(EnumErrorCode.INVALIDBIOME);
		else if (!worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord))
			setErrorState(EnumErrorCode.NOSKY);
		else if (!worldObj.isRaining())
			setErrorState(EnumErrorCode.NOTRAINING);
		else {
			resourceTank.fill(STACK_WATER, true);
			setErrorState(EnumErrorCode.OK);
		}

		if (worldObj.getTotalWorldTime() % 16 != 0)
			return;

		if (!StackUtils.isIdenticalItem(usedEmpty, inventory.getStackInSlot(SLOT_RESOURCE))) {
			fillingTime = 0;
			usedEmpty = null;
		}

		if (usedEmpty == null)
			usedEmpty = inventory.getStackInSlot(SLOT_RESOURCE);

		if (!isFilling())
			tryToStartFillling();
		else {
			fillingTime--;
			if (fillingTime <= 0 && LiquidHelper.fillContainers(this, inventory, SLOT_RESOURCE, SLOT_PRODUCT, Fluids.WATER.get()))
				fillingTime = 0;
		}
	}

	public boolean isFilling() {
		return fillingTime > 0;
	}

	private void tryToStartFillling() {
		// Nothing to do if no empty cans are available
		if (!LiquidHelper.fillContainers(this, inventory, SLOT_RESOURCE, SLOT_PRODUCT, Fluids.WATER.get(), false))
			return;

		fillingTime = Defaults.RAINTANK_FILLING_TIME;
	}

	public int getFillProgressScaled(int i) {
		return (fillingTime * i) / Defaults.RAINTANK_FILLING_TIME;
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
		if (!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		return slotIndex == SLOT_PRODUCT;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {
		if (!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		if (slotIndex != SLOT_RESOURCE)
			return false;

		return LiquidHelper.getEmptyContainer(itemstack, STACK_WATER) != null;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			fillingTime = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, fillingTime);
	}

	// / ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// We only accept water
		if (!resource.isFluidEqual(STACK_WATER))
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0)
			// updateNetworkTime.markTime(worldObj);
			sendNetworkUpdate();

		return used;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int quantityMax, boolean doEmpty) {
		return resourceTank.drain(quantityMax, doEmpty);
	}

	@Override
	public StandardTank[] getTanks() {
		return new StandardTank[] { resourceTank };
	}

	/* ITRIGGERPROVIDER */
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowResource25);
		res.add(ForestryTrigger.lowResource10);
		res.add(ForestryTrigger.hasWork);
		return res;
	}
}
