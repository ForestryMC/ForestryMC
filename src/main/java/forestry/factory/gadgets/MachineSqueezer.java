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

import forestry.api.core.ForestryAPI;
import forestry.api.recipes.ISqueezerManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class MachineSqueezer extends TilePowered implements ISidedInventory, ILiquidTankContainer {

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

		public static final ArrayList<MachineSqueezer.Recipe> recipes = new ArrayList<MachineSqueezer.Recipe>();
		public static final HashSet<Fluid> recipeFluids = new HashSet<Fluid>();
		public static final HashSet<ItemStack> recipeInputs = new HashSet<ItemStack>();

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
			for (Recipe recipe : recipes) {
				if (recipe.matches(items))
					return recipe;
			}

			return null;
		}

		public static boolean canUse(ItemStack itemStack) {
			if (recipeInputs.contains(itemStack))
				return true;
			for (ItemStack recipeInput : recipeInputs) {
				if (StackUtils.isCraftingEquivalent(recipeInput, itemStack))
					return true;
			}
			return false;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(recipe.resources, new Object[]{recipe.remnants, recipe.liquid});
			}

			return recipeList;
		}
	}

	/* MEMBER */
	private final TankManager tankManager;
	public final FilteredTank productTank;

	private Recipe currentRecipe;

	private final Stack<FluidStack> pendingLiquids = new Stack<FluidStack>();
	private final Stack<ItemStack> pendingRemnants = new Stack<ItemStack>();
	private int productionTime;
	private int timePerItem;

	public MachineSqueezer() {
		super(1100, 50, 4000);
		setInternalInventory(new TileInventoryAdapter(this, 12, "Items"));
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

		// Pending remnants
		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] remnants = pendingRemnants.toArray(new ItemStack[pendingRemnants.size()]);
		for (int i = 0; i < remnants.length; i++) {
			if (remnants[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				remnants[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("PendingRemnants", nbttaglist);

		// Pending liquids
		nbttaglist = new NBTTagList();
		FluidStack[] liquids = pendingLiquids.toArray(new FluidStack[pendingLiquids.size()]);
		for (int i = 0; i < liquids.length; i++) {
			if (liquids[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				liquids[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("PendingLiquids", nbttaglist);

		tankManager.writeTanksToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		productionTime = nbttagcompound.getInteger("ProductionTime");
		timePerItem = nbttagcompound.getInteger("TimePerItem");

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

		if (worldObj.getTotalWorldTime() % 20 != 0)
			return;

		TileInventoryAdapter inventory = getInternalInventory();
		// Can/capsule input/output needs to be handled here.
		if (inventory.getStackInSlot(SLOT_CAN_INPUT) != null) {
			FluidStack fluidStack = productTank.getFluid();
			if (fluidStack != null)
				FluidHelper.fillContainers(tankManager, inventory, SLOT_CAN_INPUT, SLOT_CAN_OUTPUT, fluidStack.getFluid());
		}

		checkRecipe();
		if (getErrorState() == EnumErrorCode.NORECIPE && currentRecipe != null)
			setErrorState(EnumErrorCode.OK);

		if (energyManager.getTotalEnergyStored() == 0) {
			setErrorState(EnumErrorCode.NOPOWER);
		}
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
		Recipe sameRec = RecipeManager.findMatchingRecipe(getInternalInventory().getStacks(SLOT_RESOURCE_1, 9));

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

		return stack.amount <= 0;
	}

	private boolean addRemnant(ItemStack stack) {
		return getInternalInventory().tryAddStack(stack, SLOT_REMNANT, 1, true);
	}

	private boolean removeResources(ItemStack[] stacks) {
		EntityPlayer player = Proxies.common.getPlayer(worldObj, getOwnerProfile());
		return getInternalInventory().removeSets(1, stacks, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT, player, false, true, true);
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
	@Override
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

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, productionTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, timePerItem);
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getInternalInventory().getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getInternalInventory().decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInternalInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		if (!getInternalInventory().isItemValidForSlot(slotIndex, itemstack))
			return false;

		if (slotIndex == SLOT_CAN_INPUT)
			return FluidHelper.isEmptyContainer(itemstack);

		if (slotIndex >= SLOT_RESOURCE_1 && slotIndex < SLOT_RESOURCE_1 + SLOTS_RESOURCE_COUNT) {
			if (FluidHelper.isEmptyContainer(itemstack))
				return false;

			if (RecipeManager.canUse(itemstack))
				return true;
		}

		return false;
	}

	/* ISIDEDINVENTORY */
	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemstack, int side) {
		return isItemValidForSlot(slotIndex, itemstack);
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		if (!getInternalInventory().canExtractItem(slotIndex, itemstack, side))
			return false;

		return slotIndex == SLOT_REMNANT || slotIndex == SLOT_CAN_OUTPUT;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return getInternalInventory().getAccessibleSlotsFromSide(side);
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

	@Override
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
}
