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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.PowerHandler;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.api.recipes.IStillManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.EnumTankLevel;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.TankManager;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import net.minecraftforge.fluids.FluidTankInfo;

public class MachineStill extends TilePowered implements ISpecialInventory, ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_PRODUCT = 0;
	public static final short SLOT_RESOURCE = 1;
	public static final short SLOT_CAN = 2;

	public static class Recipe {
		public final int timePerUnit;
		public final FluidStack input;
		public final FluidStack output;

		public Recipe(int timePerUnit, FluidStack input, FluidStack output) {
			this.timePerUnit = timePerUnit;
			if(input == null)
				throw new IllegalArgumentException("Still recipes need an input. Input was null.");
			if(output == null)
				throw new IllegalArgumentException("Still recipes need an output. Output was null.");
			this.input = input;
			this.output = output;
		}

		public boolean matches(FluidStack res) {
			if (res == null && input == null)
				return true;
			else if (res == null && input != null)
				return false;
			else if (res != null && input == null)
				return false;
			else
				return input.isFluidEqual(res);
		}
	}

	public static class RecipeManager implements IStillManager {
		public static ArrayList<MachineStill.Recipe> recipes = new ArrayList<MachineStill.Recipe>();
		public static HashSet<Fluid> recipeFluidInputs = new HashSet<Fluid>();
		public static HashSet<Fluid> recipeFluidOutputs = new HashSet<Fluid>();

		@Override
		public void addRecipe(int timePerUnit, FluidStack input, FluidStack output) {
			recipes.add(new MachineStill.Recipe(timePerUnit, input, output));
			if (input != null)
				recipeFluidInputs.add(input.getFluid());
			if (output != null)
				recipeFluidOutputs.add(output.getFluid());
		}

		public static Recipe findMatchingRecipe(FluidStack item) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(item))
					return recipe;
			}
			return null;
		}

		public static boolean isInput(FluidStack res) {
			return recipeFluidInputs.contains(res.getFluid());
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes)
				recipeList.put(new Object[] { recipe.input }, new Object[] { recipe.output });

			return recipeList;
		}
	}

	/* MEMBER */
	@EntityNetData
	public FilteredTank resourceTank;
	@EntityNetData
	public FilteredTank productTank;
	private final TankManager tankManager;

	private final InventoryAdapter inventory = new InventoryAdapter(3, "Items");

	private Recipe currentRecipe;
	private FluidStack bufferedLiquid;
	public int distillationTime = 0;
	public int distillationTotalTime = 0;

	public MachineStill() {
		setHints(Config.hints.get("still"));
		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluidInputs);
		resourceTank.tankMode = StandardTank.TankMode.INPUT;
		productTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluidOutputs);
		productTank.tankMode = StandardTank.TankMode.OUTPUT;
		tankManager = new TankManager(resourceTank, productTank);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.StillGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	protected void configurePowerProvider(PowerHandler provider) {
		provider.configure(50, 110, 5, 800);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("DistillationTime", distillationTime);
		nbttagcompound.setInteger("DistillationTotalTime", distillationTotalTime);

		tankManager.writeTanksToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		distillationTime = nbttagcompound.getInteger("DistillationTime");
		distillationTotalTime = nbttagcompound.getInteger("DistillationTotalTime");

		tankManager.readTanksFromNBT(nbttagcompound);
		inventory.readFromNBT(nbttagcompound);

		checkRecipe();
	}

	@Override
	public void updateServerSide() {

		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_CAN));

			if (container != null && resourceTank.accepts(container.fluid.getFluid())) {

				inventory.setInventorySlotContents(SLOT_CAN, StackUtils.replenishByContainer(this, inventory.getStackInSlot(SLOT_CAN), container, resourceTank));
				if (inventory.getStackInSlot(SLOT_CAN).stackSize <= 0)
					inventory.setInventorySlotContents(SLOT_CAN, null);

			}
		}

		// Can product liquid if possible
		if (inventory.getStackInSlot(SLOT_RESOURCE) != null) {
			FluidContainerData container = LiquidHelper.getEmptyContainer(inventory.getStackInSlot(SLOT_RESOURCE), productTank.getFluid());
			if (container != null) {
				inventory.setInventorySlotContents(SLOT_PRODUCT, bottleIntoContainer(inventory.getStackInSlot(SLOT_RESOURCE), inventory.getStackInSlot(SLOT_PRODUCT), container, productTank));
				if (inventory.getStackInSlot(SLOT_RESOURCE).stackSize <= 0)
					inventory.setInventorySlotContents(SLOT_RESOURCE, null);
			}
		}

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		checkRecipe();
		if (getErrorState() == EnumErrorCode.NORECIPE && currentRecipe != null)
			setErrorState(EnumErrorCode.OK);

	}

	@Override
	public boolean workCycle() {

		checkRecipe();

		// Ongoing process
		if (distillationTime > 0 && currentRecipe != null) {

			distillationTime -= currentRecipe.input.amount;
			addProduct(currentRecipe.output.fluidID, currentRecipe.output.amount);

			setErrorState(EnumErrorCode.OK);
			return true;

		} else if (currentRecipe != null && productTank.getFluidAmount() + currentRecipe.output.amount <= Defaults.PROCESSOR_TANK_CAPACITY) {

			int resReq = currentRecipe.timePerUnit * currentRecipe.input.amount;
			// Start next cycle if enough bio mass is available
			if (resourceTank.getFluidAmount() >= resReq) {

				distillationTime = distillationTotalTime = resReq;
				resourceTank.drain(resReq, true);
				bufferedLiquid = new FluidStack(currentRecipe.input.fluidID, resReq);

				setErrorState(EnumErrorCode.OK);
				return true;

			} else
				setErrorState(EnumErrorCode.NORESOURCE);

		}

		bufferedLiquid = null;
		return false;
	}

	private void addProduct(int id, int amount) {

		productTank.fill(new FluidStack(id, amount), true);
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(resourceTank.getFluid());

		if (sameRec == null && bufferedLiquid != null && distillationTime > 0)
			sameRec = RecipeManager.findMatchingRecipe(new FluidStack(bufferedLiquid.fluidID, distillationTime));

		if (sameRec == null)
			setErrorState(EnumErrorCode.NORECIPE);

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
	}

	@Override
	public boolean isWorking() {
		return distillationTime > 0 || currentRecipe != null && productTank.getFluidAmount() + currentRecipe.output.amount <= Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null)
			return false;

		return (distillationTime > 0 || resourceTank.getFluidAmount() >= currentRecipe.timePerUnit * currentRecipe.input.amount)
				&& productTank.getFluidAmount() <= productTank.getCapacity() - currentRecipe.output.amount;
	}

	public int getDistillationProgressScaled(int i) {
		if (distillationTotalTime == 0)
			return i;

		return (distillationTime * i) / distillationTotalTime;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public int getProductScaled(int i) {
		return (productTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return Utils.rateTankLevel(getProductScaled(100));
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
		case 0:
			distillationTime = j;
			break;
		case 1:
			distillationTotalTime = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, distillationTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, distillationTotalTime);
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		int inventorySlot;

		FluidContainerData container = LiquidHelper.getLiquidContainer(stack);
		if (container != null && resourceTank.accepts(container.fluid.getFluid()))
			inventorySlot = SLOT_CAN;
		else if (LiquidHelper.isEmptyContainer(stack))
			inventorySlot = SLOT_RESOURCE;
		else
			return 0;

		return inventory.addStack(stack, inventorySlot, 1, false, doAdd);
	}

	/**
	 * Transport pipes cannot extract items from the still.
	 */
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product = null;

		if (inventory.getStackInSlot(SLOT_PRODUCT) != null) {

			product = new ItemStack(inventory.getStackInSlot(SLOT_PRODUCT).getItem(), 1, inventory.getStackInSlot(SLOT_PRODUCT).getItemDamage());
			if (doRemove) {
				inventory.decrStackSize(SLOT_PRODUCT, 1);
			}
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return StackUtils.EMPTY_STACK_ARRAY;
	}

	/* IINVENTORY */
	@Override public int getSizeInventory() { return inventory.getSizeInventory(); }
	@Override public ItemStack getStackInSlot(int i) { return inventory.getStackInSlot(i); }
	@Override public ItemStack decrStackSize(int i, int j) { return inventory.decrStackSize(i, j); }
	@Override public void setInventorySlotContents(int i, ItemStack itemstack) { inventory.setInventorySlotContents(i, itemstack); }
	@Override public ItemStack getStackInSlotOnClosing(int slot) { return inventory.getStackInSlotOnClosing(slot); }
	@Override public int getInventoryStackLimit() { return inventory.getInventoryStackLimit(); }
	@Override public void openInventory() {}
	@Override public void closeInventory() {}

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

		if(!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		return slotIndex == SLOT_PRODUCT;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if(!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		if(slotIndex == SLOT_RESOURCE)
			return LiquidHelper.isEmptyContainer(itemstack);

		if(slotIndex == SLOT_CAN) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(itemstack);
			return container != null && resourceTank.accepts(container.fluid.getFluid());
		}

		return false;
	}

	/* ILiquidTankContainer */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int quantityMax, boolean doEmpty) {
		return tankManager.drain(from, quantityMax, doEmpty);
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
	public TankManager getTankManager() {
		return tankManager;
	}

	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return tankManager.getTankInfo(from);
	}

	/* ITRIGGERPROVIDER */
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.hasWork);
		return res;
	}

}
