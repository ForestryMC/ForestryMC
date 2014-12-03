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

import buildcraft.api.statements.ITriggerExternal;
import cpw.mods.fml.common.Optional;
import forestry.api.core.EnumErrorCode;
import forestry.api.core.ForestryAPI;
import forestry.api.recipes.ICraftingProvider;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.factory.triggers.FactoryTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class MachineBottler extends TilePowered implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_PRODUCT = 1;
	public static final short SLOT_CAN = 2;

	public static final short CYCLES_FILLING_DEFAULT = 5;

	/* RECIPE MANAGMENT */
	public static class Recipe {
		public final int cyclesPerUnit;
		public final FluidStack input;
		public final ItemStack can;
		public final ItemStack bottled;

		public Recipe(int cyclesPerUnit, FluidStack input, ItemStack can, ItemStack bottled) {
			this.cyclesPerUnit = cyclesPerUnit;
			this.input = input;
			this.can = can;
			this.bottled = bottled;
		}

		public boolean matches(FluidStack res, ItemStack empty) {
			return input.isFluidEqual(res) && res.amount >= input.amount && can.isItemEqual(empty);
		}

		public boolean hasInput(FluidStack res) {
			return input.isFluidEqual(res);
		}
	}

	public static class RecipeManager implements ICraftingProvider {
		public static final ArrayList<MachineBottler.Recipe> recipes = new ArrayList<MachineBottler.Recipe>();

		/**
		 * 
		 * @param res
		 * @param empty
		 * @return Recipe matching both res and empty, null if none
		 */
		public static Recipe findMatchingRecipe(FluidStack res, ItemStack empty) {
			// We need both ingredients
			if (res == null || empty == null)
				return null;

			for (Recipe recipe : recipes) {
				if (recipe.matches(res, empty))
					return recipe;
			}

			// No recipe matched. See if the liquid dictionary has anything.
			if(FluidContainerRegistry.isEmptyContainer(empty)) {
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(res, empty);
				if(filled != null) {
					FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(filled);
					Recipe recipe = new Recipe(CYCLES_FILLING_DEFAULT, fluidStack, empty, filled);
					recipes.add(recipe);
					return recipe;
				}
			}

			return null;
		}

		/**
		 * 
		 * @param res
		 * @return true if any recipe has a matching input
		 */
		public static boolean isInput(FluidStack res) {
			if (res == null)
				return false;
			return FluidRegistry.isFluidRegistered(res.getFluid());
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes)
				recipeList.put(new Object[] { recipe.input, recipe.can }, new Object[] { recipe.bottled });

			return recipeList;
		}
	}

	@EntityNetData
	public final StandardTank resourceTank;
	private final TankManager tankManager;

	private final InventoryAdapter inventory = new InventoryAdapter(3, "Items");

	private boolean productPending = false;

	private Recipe currentRecipe;
	private final Stack<ItemStack> pendingProducts = new Stack<ItemStack>();
	private int fillingTime;
	private int fillingTotalTime;

	public MachineBottler() {
		super(1100, 50, 4000);
		setHints(Config.hints.get("bottler"));
		resourceTank = new StandardTank(Defaults.PROCESSOR_TANK_CAPACITY);
		tankManager = new TankManager(resourceTank);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.BottlerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("FillingTime", fillingTime);
		nbttagcompound.setInteger("FillingTotalTime", fillingTotalTime);
		nbttagcompound.setBoolean("ProductPending", productPending);

		tankManager.writeTanksToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);

		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] offspring = pendingProducts.toArray(new ItemStack[pendingProducts.size()]);
		for (int i = 0; i < offspring.length; i++)
			if (offspring[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				offspring[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingProducts", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		fillingTime = nbttagcompound.getInteger("FillingTime");
		fillingTotalTime = nbttagcompound.getInteger("FillingTotalTime");
		productPending = nbttagcompound.getBoolean("ProductPending");

		tankManager.readTanksFromNBT(nbttagcompound);

		inventory.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingProducts", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingProducts.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

		checkRecipe();
	}

	@Override
	public void updateServerSide() {
		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_CAN));
			if (container != null && RecipeManager.isInput(container.fluid)) {

				inventory.setInventorySlotContents(SLOT_CAN, StackUtils.replenishByContainer(this, inventory.getStackInSlot(SLOT_CAN), container, resourceTank));
				if (inventory.getStackInSlot(SLOT_CAN).stackSize <= 0)
					inventory.setInventorySlotContents(SLOT_CAN, null);
			}
		}

		checkRecipe();
		if (getErrorState() == EnumErrorCode.NORECIPE && currentRecipe != null)
			setErrorState(EnumErrorCode.OK);

		if (energyManager.getTotalEnergyStored() == 0) {
			setErrorState(EnumErrorCode.NOPOWER);
			return;
		}
	}

	@Override
	public boolean workCycle() {

		checkRecipe();

		// If we add pending products, we skip to the next work cycle.
		if (tryAddPending())
			return false;

		if (!pendingProducts.isEmpty())
			return false;

		// Continue work if nothing needs to be added
		if (fillingTime <= 0)
			return false;

		if (currentRecipe == null) {
			setErrorState(EnumErrorCode.NORECIPE);
			return false;
		}

		fillingTime--;
		// Still not done, return
		if (fillingTime > 0) {
			setErrorState(EnumErrorCode.OK);
			return true;
		}

		// We are done, add products to queue and remove resources
		pendingProducts.push(currentRecipe.bottled.copy());

		inventory.decrStackSize(SLOT_RESOURCE, 1);
		resourceTank.drain(currentRecipe.input.amount, true);
		checkRecipe();
		resetRecipe();

		while (tryAddPending())
			;
		return true;
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(resourceTank.getFluid(), inventory.getStackInSlot(SLOT_RESOURCE));

		if (sameRec == null)
			setErrorState(EnumErrorCode.NORECIPE);

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			fillingTime = 0;
			fillingTotalTime = 0;
			return;
		}

		fillingTime = currentRecipe.cyclesPerUnit;
		fillingTotalTime = currentRecipe.cyclesPerUnit;
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty())
			return false;

		ItemStack next = pendingProducts.peek();
		if (addProduct(next, true)) {
			pendingProducts.pop();
			return true;
		}

		setErrorState(EnumErrorCode.NOSPACE);
		return false;
	}

	private boolean addProduct(ItemStack product, boolean all) {
		return inventory.tryAddStack(product, SLOT_PRODUCT, 1, all);
	}

	// / STATE INFORMATION
	@Override
	public boolean isWorking() {
		return fillingTime > 0;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		if (inventory.getStackInSlot(SLOT_RESOURCE) == null)
			return false;

		return ((float) inventory.getStackInSlot(SLOT_RESOURCE).stackSize / (float) inventory.getStackInSlot(SLOT_RESOURCE).getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		return currentRecipe != null;
	}

	public int getFillProgressScaled(int i) {
		if (fillingTotalTime == 0)
			return 0;

		return (fillingTime * i) / fillingTotalTime;

	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
		case 0:
			fillingTime = j;
			break;
		case 1:
			fillingTotalTime = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, fillingTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, fillingTotalTime);
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
			return FluidContainerRegistry.isEmptyContainer(itemstack);

		if(slotIndex == SLOT_CAN) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(itemstack);
			return container != null && RecipeManager.isInput(container.fluid);
		}

		return false;
	}

	/* ILIQUIDCONTAINER */
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

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(FactoryTriggers.lowResource25);
		res.add(FactoryTriggers.lowResource10);
		return res;
	}

}
