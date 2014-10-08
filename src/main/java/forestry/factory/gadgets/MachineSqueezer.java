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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.gates.ITrigger;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.api.recipes.ISqueezerManager;
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
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import net.minecraftforge.fluids.FluidTankInfo;

public class MachineSqueezer extends TilePowered implements ISpecialInventory, ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	private static final short SLOT_RESOURCE_1 = 0;
	private static final short SLOTS_RESOURCE_COUNT = 9;
	private static final short SLOT_REMNANT = 9;
	private static final short SLOT_CAN_INPUT = 10;
	private static final short SLOT_CAN_OUTPUT = 11;

	/* RECIPE MANAGMENT */
	public static class Recipe {
		public final int timePerItem;
		public final ItemStack[] resources;
		public final FluidStack liquid;
		public final ItemStack remnants;
		public final int chance;

		public Recipe(int timePerItem, ItemStack[] resources, FluidStack liquid, ItemStack remnants, int chance) {
			this.timePerItem = timePerItem;
			this.resources = resources;
			this.liquid = liquid;
			this.remnants = remnants;
			this.chance = chance;
		}

		public boolean matches(ItemStack[] res) {
			return StackUtils.containsSets(resources, res, true, true) > 0;
		}
	}

	public static class RecipeManager implements ISqueezerManager {
		public static ArrayList<MachineSqueezer.Recipe> recipes = new ArrayList<MachineSqueezer.Recipe>();
		public static HashSet<Fluid> recipeFluids = new HashSet<Fluid>();
		public static HashSet<ItemStack> recipeInputs = new HashSet<ItemStack>();

		@Override
		public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid, ItemStack remnants, int chance) {
			recipes.add(new MachineSqueezer.Recipe(timePerItem, resources, liquid, remnants, chance));
			if (liquid != null)
				recipeFluids.add(liquid.getFluid());
			if (resources != null)
				recipeInputs.addAll(Arrays.asList(resources));
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid) {
			addRecipe(timePerItem, resources, liquid, null, 0);
		}

		public static Recipe findMatchingRecipe(ItemStack[] items) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(items))
					return recipe;
			}

			return null;
		}

		public static boolean canUse(ItemStack itemStack) {
			if (recipeInputs.contains(itemStack))
				return true;
			for (ItemStack recipeInput : recipeInputs)
				if (StackUtils.isCraftingEquivalent(recipeInput, itemStack))
					return true;
			return false;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes)
				recipeList.put(recipe.resources, new Object[] { recipe.remnants, recipe.liquid });

			return recipeList;
		}
	}

	/* MEMBER */
	private final TankManager tankManager;
	@EntityNetData
	public FilteredTank productTank;

	private final InventoryAdapter inventory = new InventoryAdapter(12, "Items");
	private Recipe currentRecipe;

	private final Stack<FluidStack> pendingLiquids = new Stack<FluidStack>();
	private final Stack<ItemStack> pendingRemnants = new Stack<ItemStack>();
	private int productionTime;
	private int timePerItem;

	public MachineSqueezer() {
		super(1100, 50, 4000);
		setHints(Config.hints.get("squeezer"));
		productTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluids);
		productTank.tankMode = StandardTank.TankMode.OUTPUT;
		tankManager = new TankManager(productTank);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.SqueezerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("ProductionTime", productionTime);
		nbttagcompound.setInteger("TimePerItem", timePerItem);

		// Inventory
		inventory.writeToNBT(nbttagcompound);

		// Pending remnants
		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] remnants = pendingRemnants.toArray(new ItemStack[pendingRemnants.size()]);
		for (int i = 0; i < remnants.length; i++)
			if (remnants[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				remnants[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingRemnants", nbttaglist);

		// Pending liquids
		nbttaglist = new NBTTagList();
		FluidStack[] liquids = pendingLiquids.toArray(new FluidStack[pendingLiquids.size()]);
		for (int i = 0; i < liquids.length; i++)
			if (liquids[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				liquids[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingLiquids", nbttaglist);

		tankManager.writeTanksToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		productionTime = nbttagcompound.getInteger("ProductionTime");
		timePerItem = nbttagcompound.getInteger("TimePerItem");

		// Inventory
		inventory.readFromNBT(nbttagcompound);

		// Pending remnants
		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingRemnants", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingRemnants.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

		// Pending liquids
		nbttaglist = nbttagcompound.getTagList("PendingLiquids", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingLiquids.add(FluidStack.loadFluidStackFromNBT(nbttagcompound1));
		}

		tankManager.readTanksFromNBT(nbttagcompound);

		checkRecipe();
	}

	// / WORKING
	@Override
	public void updateServerSide() {

		// Can/capsule input/output needs to be handled here.
		if (inventory.getStackInSlot(SLOT_CAN_INPUT) != null) {

			FluidContainerData container = LiquidHelper.getEmptyContainer(inventory.getStackInSlot(SLOT_CAN_INPUT), productTank.getFluid());
			if (container != null) {
				inventory.setInventorySlotContents(SLOT_CAN_OUTPUT, bottleIntoContainer(inventory.getStackInSlot(SLOT_CAN_INPUT), inventory.getStackInSlot(SLOT_CAN_OUTPUT), container, productTank));
				if (inventory.getStackInSlot(SLOT_CAN_INPUT).stackSize <= 0)
					inventory.setInventorySlotContents(SLOT_CAN_INPUT, null);
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

		// If we add pending products, we skip to the next work cycle.
		tryAddPending();

		if (!pendingLiquids.isEmpty() || !pendingRemnants.isEmpty())
			return false;

		// Continue work if nothing needs to be added
		if (productionTime <= 0)
			return false;

		if (currentRecipe == null)
			return false;

		productionTime--;
		// Still not done, return
		if (productionTime > 0) {
			setErrorState(EnumErrorCode.OK);
			return true;
		}

		if (!removeResources(currentRecipe.resources))
			return false;

		// We are done, add products to queue
		pendingLiquids.push(currentRecipe.liquid.copy());
		if (currentRecipe.remnants != null && worldObj.rand.nextInt(100) < currentRecipe.chance)
			pendingRemnants.push(currentRecipe.remnants.copy());

		checkRecipe();
		resetRecipe();

		tryAddPending();
		setErrorState(EnumErrorCode.OK);

		return true;
	}

	private void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(inventory.getStacks(SLOT_RESOURCE_1, 9));

		if (sameRec == null)
			setErrorState(EnumErrorCode.NORECIPE);

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			productionTime = 0;
			timePerItem = 0;
			return;
		}

		productionTime = currentRecipe.timePerItem;
		timePerItem = currentRecipe.timePerItem;
	}

	private boolean tryAddPending() {

		if (!pendingLiquids.isEmpty()) {
			FluidStack next = pendingLiquids.peek();
			if (addProduct(next)) {
				pendingLiquids.pop();
				return true;
			}
		}

		if (!pendingRemnants.isEmpty()) {
			ItemStack next = pendingRemnants.peek();
			if (addRemnant(next)) {
				pendingRemnants.pop();
				return true;
			}
		}

		if (!pendingLiquids.isEmpty() || !pendingRemnants.isEmpty())
			setErrorState(EnumErrorCode.NOSPACE);
		return false;
	}

	private boolean addProduct(FluidStack stack) {
		stack.amount -= productTank.fill(stack, true);

		if (stack.amount <= 0)
			return true;
		else
			return false;
	}

	private boolean addRemnant(ItemStack stack) {
		return inventory.tryAddStack(stack, SLOT_REMNANT, 1, true);
	}

	private boolean removeResources(ItemStack[] stacks) {
		EntityPlayer player = Proxies.common.getPlayer(worldObj, owner);
		return inventory.removeSets(1, stacks, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT, player, false, true, true);
	}

	@Override
	public boolean isWorking() {
		return currentRecipe != null && productTank.getFluidAmount() < productTank.getCapacity();
	}

	@Override
	public boolean hasWork() {
		return currentRecipe != null && productTank.getFluidAmount() < productTank.getCapacity();
	}

	public int getProgressScaled(int i) {
		if (timePerItem == 0)
			return i;

		return (productionTime * i) / timePerItem;
	}

	public int getResourceScaled(int i) {
		return (productTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
		case 0:
			productionTime = j;
			break;
		case 1:
			timePerItem = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, productionTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, timePerItem);
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

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {

		if(slotIndex == SLOT_CAN_INPUT) {
			return LiquidHelper.isEmptyContainer(itemstack);
		}

		if(slotIndex >= SLOT_RESOURCE_1 && slotIndex < SLOT_RESOURCE_1 + SLOTS_RESOURCE_COUNT) {
			if (LiquidHelper.isEmptyContainer(itemstack))
				return false;

			if (RecipeManager.canUse(itemstack))
				return true;
		}

		return false;
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

		return slotIndex == SLOT_REMNANT || slotIndex == SLOT_CAN_OUTPUT;
	}


	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if(!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		return slotIndex == SLOT_CAN_INPUT || (slotIndex >= SLOT_RESOURCE_1 && slotIndex < SLOT_RESOURCE_1 + SLOTS_RESOURCE_COUNT);
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		// Try to add to can slot if input is from top or bottom.
		if (LiquidHelper.isEmptyContainer(stack)) {
			return inventory.addStack(stack, SLOT_CAN_INPUT, 1, false, doAdd);
		} else {
			return inventory.addStack(stack, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT, false, doAdd);
		}
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product;

		if (inventory.getStackInSlot(SLOT_CAN_OUTPUT) != null) {

			product = new ItemStack(inventory.getStackInSlot(SLOT_CAN_OUTPUT).getItem(), 1, inventory.getStackInSlot(SLOT_CAN_OUTPUT).getItemDamage());
			if (doRemove) {
				inventory.decrStackSize(SLOT_CAN_OUTPUT, 1);
			}
			return new ItemStack[] { product };
		} else {

			if (inventory.getStackInSlot(SLOT_REMNANT) == null)
				return StackUtils.EMPTY_STACK_ARRAY;

			product = new ItemStack(inventory.getStackInSlot(SLOT_REMNANT).getItem(), 1, inventory.getStackInSlot(SLOT_REMNANT).getItemDamage());
			if (doRemove) {
				inventory.decrStackSize(SLOT_REMNANT, 1);
			}
			return new ItemStack[] { product };
		}

	}

	/* ILIQUIDCONTAINER IMPLEMENTATION */
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
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.hasWork);
		return res;
	}

}
