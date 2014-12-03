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
package forestry.apiculture.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.core.ForestryAPI;
import forestry.core.config.Defaults;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;

public class TileAlvearyHygroregulator extends TileAlveary implements IInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final int BLOCK_META = 5;

	/* RECIPE MANAGMENT */
	private static class HygroregulatorRecipe {
		public final FluidStack liquid;
		public final int transferTime;
		public final float humidChange;
		public final float tempChange;

		public HygroregulatorRecipe(FluidStack liquid, int transferTime, float humidChange, float tempChange) {
			this.liquid = liquid;
			this.transferTime = transferTime;
			this.humidChange = humidChange;
			this.tempChange = tempChange;
		}
	}

	private final HygroregulatorRecipe[] recipes;

	/* MEMBERS */
	private final InventoryAdapter canInventory = new InventoryAdapter(1, "CanInv");
	private final TankManager tankManager;
	private final FilteredTank liquidTank;

	private HygroregulatorRecipe currentRecipe;
	private int transferTime;

	public TileAlvearyHygroregulator() {
		super(BLOCK_META);

		Fluid water = LiquidHelper.getFluid(Defaults.LIQUID_WATER);
		Fluid lava = LiquidHelper.getFluid(Defaults.LIQUID_LAVA);
		Fluid liquidIce = LiquidHelper.getFluid(Defaults.LIQUID_ICE);

		liquidTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, water, lava, liquidIce);
		tankManager = new TankManager(liquidTank);

		recipes = new HygroregulatorRecipe[] { new HygroregulatorRecipe(new FluidStack(water, 1), 1, 0.01f, -0.005f),
				new HygroregulatorRecipe(new FluidStack(lava, 1), 10, -0.01f, +0.005f),
				new HygroregulatorRecipe(new FluidStack(liquidIce, 1), 10, 0.02f, -0.01f) };
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.HygroregulatorGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	/* UPDATING */
	private HygroregulatorRecipe getRecipe(FluidStack liquid) {
		HygroregulatorRecipe recipe = null;
		for (HygroregulatorRecipe rec : recipes)
			if (rec.liquid.isFluidEqual(liquid)) {
				recipe = rec;
				break;
			}
		return recipe;
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (transferTime <= 0 && liquidTank.getFluidAmount() > 0) {
			currentRecipe = getRecipe(liquidTank.getFluid());

			if (currentRecipe != null) {
				liquidTank.drain(currentRecipe.liquid.amount, true);
				transferTime = currentRecipe.transferTime;
			}
		}

		if (transferTime > 0) {

			transferTime--;
			if (currentRecipe != null) {
				IAlvearyComponent component = (IAlvearyComponent) this.getCentralTE();
				if (component != null) {
					component.addHumidityChange(currentRecipe.humidChange, 0.0f, 1.0f);
					component.addTemperatureChange(currentRecipe.tempChange, 0.0f, 2.0f);
				}
			} else
				transferTime = 0;
		}

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		// Check if we have suitable items waiting in the item slot
		if (canInventory.getStackInSlot(0) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(canInventory.getStackInSlot(0));
			if (container != null &&
					(container.fluid.getFluid() == FluidRegistry.WATER || container.fluid.getFluid() == FluidRegistry.LAVA)) {

				canInventory.setInventorySlotContents(0, StackUtils.replenishByContainer(this, canInventory.getStackInSlot(0), container, liquidTank));
				if (canInventory.getStackInSlot(0).stackSize <= 0)
					canInventory.setInventorySlotContents(0, null);
			}
		}

	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		canInventory.readFromNBT(nbttagcompound);

		tankManager.readTanksFromNBT(nbttagcompound);

		transferTime = nbttagcompound.getInteger("TransferTime");

		if (nbttagcompound.hasKey("CurrentLiquid")) {
			FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbttagcompound.getCompoundTag("CurrentLiquid"));
			currentRecipe = getRecipe(liquid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		canInventory.writeToNBT(nbttagcompound);

		tankManager.writeTanksToNBT(nbttagcompound);

		nbttagcompound.setInteger("TransferTime", transferTime);
		if (currentRecipe != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			currentRecipe.liquid.writeToNBT(subcompound);
			nbttagcompound.setTag("CurrentLiquid", subcompound);
		}

	}

	/* TEXTURES */
	@Override
	public int getIcon(int side, int metadata) {
		return BlockAlveary.TX_73_VLVE;
	}

	@Override
	public IInventory getInventory() {
		return canInventory;
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		if (canInventory != null)
			return canInventory.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if (canInventory != null)
			return canInventory.getStackInSlot(slotIndex);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if (canInventory != null)
			return canInventory.decrStackSize(slotIndex, amount);
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if (canInventory != null)
			return canInventory.getStackInSlotOnClosing(slotIndex);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		// Client side handling for container synch
		if (canInventory == null && !Proxies.common.isSimulating(worldObj))
			createInventory();

		if (canInventory != null)
			canInventory.setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		if (canInventory != null)
			return canInventory.getInventoryStackLimit();
		else
			return 0;
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
		if (slotIndex == 0) {
			FluidStack fluid = LiquidHelper.getFluidStackInContainer(itemstack);
			if (fluid == null || fluid.amount <= 0)
				return false;
			return liquidTank.accepts(fluid.getFluid());
		}

		return super.isItemValidForSlot(slotIndex, itemstack);
	}

	/* ILIQUIDTANKCONTAINER */
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

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
	public void getGUINetworkData(int messageId, int data) {}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {}
}
