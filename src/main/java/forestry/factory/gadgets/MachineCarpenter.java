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
import forestry.api.recipes.ICarpenterManager;
import forestry.api.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Utils;
import forestry.factory.gui.ContainerCarpenter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MachineCarpenter extends TilePowered implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_BOX = 9;
	public final static int SLOT_PRODUCT = 10;
	public final static int SLOT_CAN_INPUT = 11;
	public final static short SLOT_INVENTORY_1 = 12;
	public final static short SLOT_INVENTORY_COUNT = 18;

	/* RECIPE MANAGMENT */
	public static class Recipe {

		private final int packagingTime;
		private final FluidStack liquid;
		private final ItemStack box;
		private final ShapedRecipeCustom internal;

		public Recipe(int packagingTime, FluidStack liquid, ItemStack box, ShapedRecipeCustom internal) {
			this.packagingTime = packagingTime;
			this.liquid = liquid;
			this.box = box;
			this.internal = internal;
		}

		public ItemStack getCraftingResult() {
			return internal.getRecipeOutput();
		}

		public boolean matches(FluidStack resource, ItemStack item, InventoryCrafting inventorycrafting, World world) {

			// Check liquid
			if (liquid != null && resource == null)
				return false;
			if (liquid != null && !liquid.isFluidEqual(resource))
				return false;

			// Check box
			if (box != null && item == null)
				return false;
			if (box != null && !box.isItemEqual(item))
				return false;

			return internal.matches(inventorycrafting, world);
		}

		public boolean hasLiquid(FluidStack resource) {
			if (liquid != null && resource != null)
				return liquid.isFluidEqual(resource);
			else
				return false;
		}

		public boolean hasBox(ItemStack resource) {
			if (box == null && resource == null)
				return true;
			else if (box == null)
				return true;

			if (box.getItemDamage() > 0)
				return box.isItemEqual(resource);
			else
				return box.getItem() == resource.getItem();
		}

		public boolean isIngredient(ItemStack resource) {
			return internal.isIngredient(resource);
		}

		public ItemStack getBox() {
			return box;
		}

		public FluidStack getLiquid() {
			return liquid;
		}

		public IRecipe asIRecipe() {
			return internal;
		}
	}

	public static class RecipeManager implements ICarpenterManager {

		public static final ArrayList<MachineCarpenter.Recipe> recipes = new ArrayList<MachineCarpenter.Recipe>();
		public static final HashSet<Fluid> recipeFluids = new HashSet<Fluid>();

		@Override
		public void addCrating(ItemStack itemStack) {
			ItemStack uncrated = ((forestry.core.items.ItemCrated) itemStack.getItem()).getContained(itemStack);
			addRecipe(Defaults.CARPENTER_CRATING_CYCLES, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, Defaults.CARPENTER_CRATING_LIQUID_QUANTITY),
					ForestryItem.crate.getItemStack(), itemStack, new Object[] { "###", "###", "###", '#', uncrated });
			addRecipe(null, new ItemStack(uncrated.getItem(), 9, uncrated.getItemDamage()), new Object[] { "#", Character.valueOf('#'), itemStack });
		}

		@Override
		public void addCrating(String toCrate, ItemStack unpack, ItemStack crated) {
			addRecipe(Defaults.CARPENTER_CRATING_CYCLES, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, Defaults.CARPENTER_CRATING_LIQUID_QUANTITY),
					ForestryItem.crate.getItemStack(), crated, new Object[] { "###", "###", "###", '#', toCrate });
			addRecipe(null, new ItemStack(unpack.getItem(), 9, unpack.getItemDamage()), new Object[] { "#", '#', crated });
		}

		@Override
		public void addRecipe(ItemStack box, ItemStack product, Object materials[]) {
			addRecipe(5, null, box, product, materials);
		}

		@Override
		public void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object materials[]) {
			addRecipe(packagingTime, null, box, product, materials);
		}

		@Override
		public void addRecipe(int packagingTime, FluidStack liquid, ItemStack box, ItemStack product, Object materials[]) {
			recipes.add(new Recipe(packagingTime, liquid, box, ShapedRecipeCustom.createShapedRecipe(product, materials)));
			if (liquid != null)
				recipeFluids.add(liquid.getFluid());
		}

		public static Recipe findMatchingRecipe(FluidStack liquid, ItemStack item, InventoryCrafting inventorycrafting, World world) {
			for (Recipe recipe : recipes) {
				if (recipe.matches(liquid, item, inventorycrafting, world))
					return recipe;
			}
			return null;
		}

		public static boolean isResourceLiquid(FluidStack liquid) {
			return recipeFluids.contains(liquid.getFluid());
		}

		public static boolean isBox(ItemStack resource) {
			for (Recipe recipe : recipes) {
				if (StackUtils.isIdenticalItem(recipe.getBox(), resource))
					return true;
			}

			return false;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {

			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(recipe.internal.getIngredients(), new Object[] { recipe.getCraftingResult() });
			}

			return recipeList;
		}
	}

	/* MEMBER */
	@EntityNetData
	public final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final TileInventoryAdapter craftingInventory;
	private final TileInventoryAdapter accessibleInventory;
	public MachineCarpenter.Recipe currentRecipe;
	public MachineCarpenter.Recipe lastRecipe;
	public ContainerCarpenter activeContainer;
	private int packageTime;
	private int totalTime;
	private ItemStack currentProduct;
	private ItemStack pendingProduct;

	public ItemStack getBoxStack() {
		return accessibleInventory.getStackInSlot(SLOT_BOX);
	}

	public MachineCarpenter() {
		super(1100, 50, 4000);
		setHints(Config.hints.get("carpenter"));
		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluids);
		craftingInventory = new TileInventoryAdapter(this, 10, "CraftItems");
		accessibleInventory = (TileInventoryAdapter) new TileInventoryAdapter(this, 30, "Items").configureSided(Defaults.FACINGS, SLOT_BOX, 20);

		tankManager = new TankManager(resourceTank);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.CarpenterGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("PackageTime", packageTime);
		nbttagcompound.setInteger("PackageTotalTime", totalTime);

		tankManager.writeTanksToNBT(nbttagcompound);

		craftingInventory.writeToNBT(nbttagcompound);
		accessibleInventory.writeToNBT(nbttagcompound);

		// Write pending product
		if (pendingProduct != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			pendingProduct.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("PendingProduct", nbttagcompoundP);
		}
		// Write current product
		if (currentProduct != null) {
			NBTTagCompound nbttagcompoundC = new NBTTagCompound();
			currentProduct.writeToNBT(nbttagcompoundC);
			nbttagcompound.setTag("CurrentProduct", nbttagcompoundC);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		packageTime = nbttagcompound.getInteger("PackageTime");
		totalTime = nbttagcompound.getInteger("PackageTotalTime");

		tankManager.readTanksFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("CraftItems"))
			craftingInventory.readFromNBT(nbttagcompound);
		else {
			// FIXME: Legacy conversion for carpenters <= 2.2.2.2
			NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);

			for (int j = 0; j < nbttaglist.tagCount(); ++j) {
				NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
				int index = nbttagcompound2.getByte("Slot");
				if (index < 9)
					craftingInventory.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(nbttagcompound2));
			}

		}
		accessibleInventory.readFromNBT(nbttagcompound);

		// Load pending product
		if (nbttagcompound.hasKey("PendingProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("PendingProduct");
			pendingProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}
		// Load current product
		if (nbttagcompound.hasKey("CurrentProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("CurrentProduct");
			currentProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}

		// Reset recipe according to contents
		ContainerCarpenter container = new ContainerCarpenter(this);
		setCurrentRecipe(RecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), container.craftMatrix, worldObj));
	}

	public void setCurrentRecipe(MachineCarpenter.Recipe currentRecipe) {
		this.currentRecipe = currentRecipe;
		if(currentRecipe != null)
			lastRecipe = currentRecipe;
	}

	@Override
	public void updateServerSide() {

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		// Check if we have suitable items waiting in the item slot
		if (accessibleInventory.getStackInSlot(SLOT_CAN_INPUT) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(accessibleInventory.getStackInSlot(SLOT_CAN_INPUT));
			if (container != null && RecipeManager.isResourceLiquid(container.fluid)) {

				accessibleInventory.setInventorySlotContents(SLOT_CAN_INPUT,
						StackUtils.replenishByContainer(this, accessibleInventory.getStackInSlot(SLOT_CAN_INPUT), container, resourceTank));
				if (accessibleInventory.getStackInSlot(SLOT_CAN_INPUT).stackSize <= 0)
					accessibleInventory.setInventorySlotContents(SLOT_CAN_INPUT, null);
			}
		}

		if (worldObj.getTotalWorldTime() % 40 * 10 != 0)
			return;

		if (currentRecipe == null) {
			ContainerCarpenter container = new ContainerCarpenter(this);
			setCurrentRecipe(MachineCarpenter.RecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), container.craftMatrix, worldObj));
		}

		if (currentRecipe == null)
			setErrorState(EnumErrorCode.NORECIPE);
		else if (!validateResources())
			setErrorState(EnumErrorCode.NORESOURCE);
		else if (energyManager.getTotalEnergyStored() == 0)
			setErrorState(EnumErrorCode.NOPOWER);
		else
			setErrorState(EnumErrorCode.OK);

	}

	@Override
	public boolean workCycle() {

		if (packageTime > 0) {
			packageTime--;

			// Check whether we have become invalid and need to abort production
			if (currentRecipe == null || !currentProduct.isItemEqual(currentRecipe.getCraftingResult()) || !validateResources()) {
				currentProduct = null;
				packageTime = totalTime = 0;
				return false;
			}

			if (packageTime <= 0) {

				pendingProduct = currentProduct;
				currentProduct = null;
				totalTime = 0;

				// Remove resources
				if (!removeResources(currentRecipe))
					return false;

				// Update product display
				if (activeContainer != null)
					activeContainer.updateProductDisplay();

				return tryAddPending();
			}
			return true;
		} else if (pendingProduct != null)
			return tryAddPending();
		else {

			if (currentRecipe != null) {

				if (!validateResources())
					return false;

				// Enough items available, start the process
				packageTime = totalTime = currentRecipe.packagingTime;
				currentProduct = currentRecipe.getCraftingResult();

				// Update product display
				if (activeContainer != null)
					activeContainer.updateProductDisplay();

				return true;
			}

			return false;
		}
	}

	private boolean validateResources() {
		// Check whether liquid is needed and if there is enough available
		if (currentRecipe.liquid != null)
			if (resourceTank.getFluidAmount() < currentRecipe.liquid.amount)
				return false;

		// Check whether boxes are available
		if (currentRecipe.box != null)
			if (accessibleInventory.getStackInSlot(SLOT_BOX) == null)
				return false;

		// Need at least one matched set
		ItemStack[] set = craftingInventory.getStacks(SLOT_CRAFTING_1, 9);
		ItemStack[] stock = accessibleInventory.getStacks(SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		return StackUtils.containsSets(set, stock, true, false) > 0;
	}

	private boolean removeResources(Recipe recipe) {

		// Remove resources
		if (recipe.liquid != null) {
			FluidStack amountDrained = resourceTank.drain(recipe.liquid.amount, false);
			if (amountDrained != null && amountDrained.amount == recipe.liquid.amount)
				resourceTank.drain(recipe.liquid.amount, true);
			else
				return false;
		}
		// Remove boxes
		if (recipe.box != null) {
			ItemStack removed = accessibleInventory.decrStackSize(SLOT_BOX, 1);
			if (removed == null || removed.stackSize == 0)
				return false;
		}

		return removeSets(1, craftingInventory.getStacks(SLOT_CRAFTING_1, 9));
	}

	private boolean removeSets(int count, ItemStack[] set) {
		EntityPlayer player = Proxies.common.getPlayer(worldObj, owner);
		return accessibleInventory.removeSets(count, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true, true);
	}

	private boolean tryAddPending() {
		if (accessibleInventory.getStackInSlot(SLOT_PRODUCT) == null) {
			accessibleInventory.setInventorySlotContents(SLOT_PRODUCT, pendingProduct.copy());
			pendingProduct = null;
			return true;
		}

		if (accessibleInventory.getStackInSlot(SLOT_PRODUCT).isItemEqual(pendingProduct)
				&& accessibleInventory.getStackInSlot(SLOT_PRODUCT).stackSize <= accessibleInventory.getStackInSlot(SLOT_PRODUCT).getMaxStackSize() - pendingProduct.stackSize) {
			accessibleInventory.getStackInSlot(SLOT_PRODUCT).stackSize += pendingProduct.stackSize;
			pendingProduct = null;
			return true;
		}

		setErrorState(EnumErrorCode.NOSPACE);
		return false;
	}

	/* STATE INFORMATION */
	@Override
	public boolean isWorking() {
		return packageTime > 0 || pendingProduct != null || currentRecipe != null && validateResources();
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null)
			return false;

		// Stop working if the output slot cannot take more
		if (accessibleInventory.getStackInSlot(SLOT_PRODUCT) != null
				&& accessibleInventory.getStackInSlot(SLOT_PRODUCT).getMaxStackSize() - accessibleInventory.getStackInSlot(SLOT_PRODUCT).stackSize < currentRecipe
				.getCraftingResult().stackSize)
			return false;

		return validateResources();
	}

	public int getCraftingProgressScaled(int i) {
		if (totalTime == 0)
			return 0;

		return (packageTime * i) / totalTime;

	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return accessibleInventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return accessibleInventory.getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		accessibleInventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return accessibleInventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return accessibleInventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return accessibleInventory.getInventoryStackLimit();
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

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
		case 0:
			packageTime = j;
			break;
		case 1:
			totalTime = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, packageTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, totalTime);
	}

	/* ISIDEDINVENTORY */
	@Override
	public InventoryAdapter getInternalInventory() {
		return accessibleInventory;
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public InventoryAdapter getCraftingInventory() {
		return craftingInventory;
	}

	@Override
	protected boolean canTakeStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if (!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		if (slotIndex == SLOT_PRODUCT)
			return true;

		return false;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if (!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		if (slotIndex == SLOT_PRODUCT)
			return false;

		if (slotIndex == SLOT_BOX)
			return RecipeManager.isBox(itemstack);

		if (slotIndex == SLOT_CAN_INPUT)
			return FluidContainerRegistry.isContainer(itemstack);

		if (slotIndex >= SLOT_INVENTORY_1 && slotIndex < SLOT_INVENTORY_1 + SLOT_INVENTORY_COUNT) {
			if (lastRecipe == null)
				return true;
			return lastRecipe.isIngredient(itemstack);
		}

		return false;
	}

	// ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection direction, FluidStack resource, boolean doFill) {
		return tankManager.fill(direction, resource, doFill);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
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

	public void resetProductDisplay(InventoryCrafting craftMatrix) {
		setCurrentRecipe(RecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftMatrix, getWorldObj()));
	}

}
