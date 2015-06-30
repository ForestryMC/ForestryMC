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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ICarpenterManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.IItemStackDisplay;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class MachineCarpenter extends TilePowered implements ISidedInventory, ILiquidTankContainer, IItemStackDisplay {

	/* CONSTANTS */
	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_CRAFTING_COUNT = 9;
	public final static int SLOT_BOX = 9;
	public final static int SLOT_PRODUCT = 10;
	public final static int SLOT_PRODUCT_COUNT = 1;
	public final static int SLOT_CAN_INPUT = 11;
	public final static short SLOT_INVENTORY_1 = 12;
	public final static short SLOT_INVENTORY_COUNT = 18;

	/* MEMBER */
	public final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final TileInventoryAdapter craftingInventory;
	private final InventoryCraftResult craftPreviewInventory;

	@Nullable
	public MachineCarpenter.Recipe currentRecipe;
	private int packageTime;
	private int totalTime;
	private ItemStack pendingProduct;

	public ItemStack getBoxStack() {
		return getInternalInventory().getStackInSlot(SLOT_BOX);
	}

	public MachineCarpenter() {
		super(1100, 50, 4000);
		setHints(Config.hints.get("carpenter"));
		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluids);

		craftingInventory = new TileInventoryAdapter<MachineCarpenter>(this, 10, "CraftItems");
		craftPreviewInventory = new InventoryCraftResult();
		setInternalInventory(new CarpenterInventoryAdapter(this));

		InvTools.configureSided(getInternalInventory(), Defaults.FACINGS, SLOT_BOX, 21);

		tankManager = new TankManager(resourceTank);
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

		// Write pending product
		if (pendingProduct != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			pendingProduct.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("PendingProduct", nbttagcompoundP);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		packageTime = nbttagcompound.getInteger("PackageTime");
		totalTime = nbttagcompound.getInteger("PackageTotalTime");

		tankManager.readTanksFromNBT(nbttagcompound);

		craftingInventory.readFromNBT(nbttagcompound);

		// Load pending product
		if (nbttagcompound.hasKey("PendingProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("PendingProduct");
			pendingProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}

		// Reset recipe according to contents
		setCurrentRecipe(RecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftingInventory, worldObj));
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writePacketData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readPacketData(data);
	}

	public void resetRecipe() {
		if (worldObj.isRemote) {
			return;
		}
		setCurrentRecipe(RecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftingInventory, getWorldObj()));
	}

	private void setCurrentRecipe(@Nullable MachineCarpenter.Recipe currentRecipe) {
		this.currentRecipe = currentRecipe;

		final ItemStack craftingResult;

		if (currentRecipe != null) {
			craftingResult = currentRecipe.getCraftingResult();
		} else {
			craftingResult = null;
		}

		craftPreviewInventory.setInventorySlotContents(0, craftingResult);
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (!updateOnInterval(20)) {
			return;
		}
		IInventoryAdapter accessibleInventory = getInternalInventory();
		// Check if we have suitable items waiting in the item slot
		if (accessibleInventory.getStackInSlot(SLOT_CAN_INPUT) != null) {
			FluidHelper.drainContainers(tankManager, accessibleInventory, SLOT_CAN_INPUT);
		}

		if (!updateOnInterval(40)) {
			return;
		}

		if (currentRecipe == null) {
			Recipe recipe = MachineCarpenter.RecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftingInventory, worldObj);
			if (recipe != null) {
				setCurrentRecipe(recipe);
			}
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(currentRecipe == null, EnumErrorCode.NORECIPE);
		errorLogic.setCondition(!validateResources(), EnumErrorCode.NORESOURCE);
	}

	@Override
	public boolean workCycle() {

		if (packageTime > 0) {
			packageTime--;

			// Check whether we have become invalid and need to abort production
			if (currentRecipe == null || !validateResources()) {
				packageTime = totalTime = 0;
				return false;
			}

			if (packageTime <= 0) {

				pendingProduct = currentRecipe.getCraftingResult();
				totalTime = 0;

				// Remove resources
				if (!removeResources(currentRecipe)) {
					return false;
				}

				// Update product display
				resetRecipe();

				return tryAddPending();
			}
			return true;
		} else if (pendingProduct != null) {
			return tryAddPending();
		} else if (currentRecipe != null) {

			if (!validateResources()) {
				return false;
			}

			// Enough items available, start the process
			packageTime = totalTime = currentRecipe.packagingTime;

			// Update product display
			resetRecipe();

			return true;
		} else {

			return false;
		}
	}

	private boolean validateResources() {
		if (currentRecipe == null) {
			return true;
		}
		// Check whether liquid is needed and if there is enough available
		if (currentRecipe.liquid != null) {
			if (resourceTank.getFluidAmount() < currentRecipe.liquid.amount) {
				return false;
			}
		}

		IInventoryAdapter accessibleInventory = getInternalInventory();
		// Check whether boxes are available
		if (currentRecipe.box != null) {
			if (accessibleInventory.getStackInSlot(SLOT_BOX) == null) {
				return false;
			}
		}

		// Need at least one matched set
		ItemStack[] set = InvTools.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		ItemStack[] stock = InvTools.getStacks(accessibleInventory, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);

		return StackUtils.containsSets(set, stock, true, false) > 0;
	}

	private boolean removeResources(Recipe recipe) {

		// Remove resources
		if (recipe.liquid != null) {
			FluidStack amountDrained = resourceTank.drain(recipe.liquid.amount, false);
			if (amountDrained != null && amountDrained.amount == recipe.liquid.amount) {
				resourceTank.drain(recipe.liquid.amount, true);
			} else {
				return false;
			}
		}
		// Remove boxes
		if (recipe.box != null) {
			ItemStack removed = getInternalInventory().decrStackSize(SLOT_BOX, 1);
			if (removed == null || removed.stackSize == 0) {
				return false;
			}
		}
		return removeSets(1, InvTools.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT));
	}

	private boolean removeSets(int count, ItemStack[] set) {
		EntityPlayer player = Proxies.common.getPlayer(worldObj, getAccessHandler().getOwner());
		return InvTools.removeSets(getInternalInventory(), count, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true, true);
	}

	private boolean tryAddPending() {
		if (pendingProduct == null) {
			return false;
		}

		boolean added = InvTools.tryAddStack(this, pendingProduct, SLOT_PRODUCT, SLOT_PRODUCT_COUNT, true);

		if (added) {
			pendingProduct = null;
		}

		getErrorLogic().setCondition(!added, EnumErrorCode.NOSPACE);
		return added;
	}

	/* STATE INFORMATION */
	@Override
	public boolean isWorking() {
		return packageTime > 0 || pendingProduct != null || currentRecipe != null && validateResources();
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null) {
			return false;
		}

		IInventoryAdapter accessibleInventory = getInternalInventory();
		// Stop working if the output slot cannot take more
		if (accessibleInventory.getStackInSlot(SLOT_PRODUCT) != null
				&& accessibleInventory.getStackInSlot(SLOT_PRODUCT).getMaxStackSize() - accessibleInventory.getStackInSlot(SLOT_PRODUCT).stackSize < currentRecipe
				.getCraftingResult().stackSize) {
			return false;
		}

		return validateResources();
	}

	public int getCraftingProgressScaled(int i) {
		if (totalTime == 0) {
			return 0;
		}

		return ((totalTime - packageTime) * i) / totalTime;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	/* SMP GUI */
	@Override
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

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, packageTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, totalTime);
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public IInventory getCraftingInventory() {
		return craftingInventory;
	}

	public IInventory getCraftPreviewInventory() {
		return craftPreviewInventory;
	}

	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		craftPreviewInventory.setInventorySlotContents(0, itemStack);
	}

	// IFLUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection direction, FluidStack resource, boolean doFill) {
		return tankManager.fill(direction, resource, doFill);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
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

	private static class CarpenterInventoryAdapter extends TileInventoryAdapter<MachineCarpenter> {
		public CarpenterInventoryAdapter(MachineCarpenter carpenter) {
			super(carpenter, 30, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_CAN_INPUT) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.tankManager.accepts(fluid);
			} else if (slotIndex == SLOT_BOX) {
				return RecipeManager.isBox(itemStack);
			} else if (canSlotAccept(SLOT_CAN_INPUT, itemStack) || canSlotAccept(SLOT_BOX, itemStack)) {
				return false;
			}

			return GuiUtil.isIndexInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex == SLOT_PRODUCT;
		}
	}

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

		public boolean matches(FluidStack resource, ItemStack item, IInventory inventorycrafting, World world) {

			if (liquid != null) {
				if (resource == null || !resource.containsFluid(liquid)) {
					return false;
				}
			}

			// Check box
			if (box != null && !StackUtils.isCraftingEquivalent(box, item)) {
				return false;
			}

			return internal.matches(inventorycrafting, world);
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
		private static final Set<Fluid> recipeFluids = new HashSet<Fluid>();
		private static final List<ItemStack> boxes = new ArrayList<ItemStack>();

		public void addCrating(ItemStack itemStack) {
			ItemStack uncrated = ((forestry.core.items.ItemCrated) itemStack.getItem()).getContained();
			addRecipe(Defaults.CARPENTER_CRATING_CYCLES, Fluids.WATER.getFluid(Defaults.CARPENTER_CRATING_LIQUID_QUANTITY),
					ForestryItem.crate.getItemStack(), itemStack, new Object[]{"###", "###", "###", '#', uncrated});
			addRecipe(null, new ItemStack(uncrated.getItem(), 9, uncrated.getItemDamage()), new Object[]{"#", '#', itemStack});
		}

		public void addCratingWithOreDict(ItemStack itemStack) {
			ItemStack uncrated = ((forestry.core.items.ItemCrated) itemStack.getItem()).getContained();
			int[] oreIds = OreDictionary.getOreIDs(uncrated);
			for (int oreId : oreIds) {
				String oreName = OreDictionary.getOreName(oreId);
				addCrating(oreName, uncrated, itemStack);
			}
		}

		public void addCrating(String toCrate, ItemStack unpack, ItemStack crated) {
			addRecipe(Defaults.CARPENTER_CRATING_CYCLES, Fluids.WATER.getFluid(Defaults.CARPENTER_CRATING_LIQUID_QUANTITY),
					ForestryItem.crate.getItemStack(), crated, new Object[]{"###", "###", "###", '#', toCrate});
			addRecipe(null, new ItemStack(unpack.getItem(), 9, unpack.getItemDamage()), new Object[]{"#", '#', crated});
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
			if (liquid != null) {
				recipeFluids.add(liquid.getFluid());
			}
			if (box != null && !isBox(box)) {
				boxes.add(box);
			}
		}

		public static Recipe findMatchingRecipe(FluidStack liquid, ItemStack item, IInventory inventorycrafting, World world) {
			for (Recipe recipe : recipes) {
				if (recipe.matches(liquid, item, inventorycrafting, world)) {
					return recipe;
				}
			}
			return null;
		}

		public static boolean isBox(ItemStack resource) {
			if (resource == null) {
				return false;
			}

			for (ItemStack box : boxes) {
				if (StackUtils.isIdenticalItem(box, resource)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {

			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(recipe.internal.getIngredients(), new Object[]{recipe.getCraftingResult()});
			}

			return recipeList;
		}
	}

}
