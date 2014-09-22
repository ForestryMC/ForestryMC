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
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.api.recipes.IFabricatorManager;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ICrafter;
import forestry.core.interfaces.ICraftingPlan;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import net.minecraftforge.fluids.FluidTankInfo;

public class MachineFabricator extends TilePowered implements ICrafter, ISpecialInventory, ILiquidTankContainer {

	/* RECIPE MANAGMENT */
	public static class Recipe {
		ItemStack plan;

		FluidStack molten;
		ShapedRecipeCustom internal;

		public Recipe(ItemStack plan, FluidStack molten, ShapedRecipeCustom internal) {
			this.plan = plan;
			this.molten = molten;
			this.internal = internal;
		}

		public boolean matches(ItemStack plan) {
			if (this.plan == null)
				return true;

			if (plan == null && this.plan == null)
				return true;
			else if (plan == null && this.plan != null)
				return false;

			if (this.plan.getItemDamage() == Defaults.WILDCARD)
				return plan.getItem() == this.plan.getItem();
			else
				return plan.isItemEqual(this.plan);
		}

		public boolean matches(ItemStack plan, ItemStack[][] resources) {
			if (!this.matches(plan))
				return false;

			return internal.matches(resources);
		}

		public boolean hasLiquid(FluidStack resource) {
			if(resource == null)
				return molten == null;

			if (!resource.isFluidEqual(molten))
				return false;

			return molten.amount <= resource.amount;
		}

		public ItemStack getPlan() {
			return plan;
		}

		public FluidStack getLiquid() {
			return molten;
		}

		public IRecipe asIRecipe() {
			return internal;
		}

	}

	public static class Smelting {
		ItemStack resource;
		FluidStack product;
		int meltingPoint;

		public Smelting(ItemStack resource, FluidStack molten, int meltingPoint) {
			if(resource == null)
				throw new IllegalArgumentException("Resource cannot be null");
			if(molten == null)
				throw new IllegalArgumentException("Molten cannot be null");

			this.resource = resource;
			this.product = molten;
			this.meltingPoint = meltingPoint;
		}

		public boolean matches(ItemStack resource) {
			return this.resource.isItemEqual(resource);
		}

		public boolean matches(FluidStack product) {
			return this.product.isFluidEqual(product);
		}

		public ItemStack getResource() {
			return resource;
		}

		public FluidStack getProduct() {
			return product;
		}

		public int getMeltingPoint() {
			return meltingPoint;
		}
	}

	public static class RecipeManager implements IFabricatorManager {
		public static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		public static ArrayList<Smelting> smeltings = new ArrayList<Smelting>();

		@Override
		public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {
			recipes.add(new Recipe(plan, molten, ShapedRecipeCustom.createShapedRecipe(result, pattern)));
		}

		@Override
		public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {
			smeltings.add(new Smelting(resource, molten, meltingPoint));
		}

		public static Recipe findMatchingRecipe(ItemStack plan) {
			for (Recipe recipe : recipes)
				if (recipe.matches(plan))
					return recipe;

			return null;
		}

		public static Recipe findMatchingRecipe(ItemStack plan, FluidStack liquid, ItemStack[] resources) {
			ItemStack[][] gridResources = new ItemStack[3][3];
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					gridResources[j][i] = resources[i * 3 + j];

			for (Recipe recipe : recipes)
				if (recipe.matches(plan, gridResources))
					if (recipe.hasLiquid(liquid))
						return recipe;

			return null;
		}

		public static boolean isResourceLiquid(FluidStack liquid) {
			for (Recipe recipe : recipes)
				if (recipe.hasLiquid(liquid))
					return true;

			return false;
		}

		public static Smelting findMatchingSmelting(ItemStack resource) {
			if (resource == null)
				return null;

			for (Smelting smelting : smeltings)
				if (smelting.matches(resource))
					return smelting;

			return null;
		}

		public static Smelting findMatchingSmelting(FluidStack product) {
			if (product == null)
				return null;

			for (Smelting smelting : smeltings)
				if (smelting.matches(product))
					return smelting;

			return null;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes)
				recipeList.put(recipe.internal.getIngredients(), new Object[] { recipe.internal.getRecipeOutput() });

			return recipeList;
		}

	}

	/* CONSTANTS */
	private static final int MAX_HEAT = 5000;

	public static final short SLOT_METAL = 0;
	public static final short SLOT_PLAN = 1;
	public static final short SLOT_RESULT = 2;
	public static final short SLOT_CRAFTING_1 = 3;
	public static final short SLOT_INVENTORY_1 = 12;
	public static final short SLOT_INVENTORY_COUNT = 18;

	/* MEMBER */
	private final TankManager tankManager;
	private FilteredTank moltenTank;
	private final InventoryAdapter inventory = new InventoryAdapter(30, "Items");
	private int heat = 0;
	private int guiMeltingPoint = 0;

	private FluidStack pendingSmelt;

	public MachineFabricator() {
		super(3300, 50, 1100);
		Fluid liquidGlass = FluidRegistry.getFluid(Defaults.LIQUID_GLASS);
		moltenTank = new FilteredTank(2 * Defaults.BUCKET_VOLUME, liquidGlass);
		moltenTank.tankMode = StandardTank.TankMode.INTERNAL;
		tankManager = new TankManager(moltenTank);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.FabricatorGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Heat", heat);

		// Tank
		tankManager.writeTanksToNBT(nbttagcompound);

		// Pending Smelt
		if (pendingSmelt != null) {
			NBTTagCompound smelt = new NBTTagCompound();
			pendingSmelt.writeToNBT(smelt);
			nbttagcompound.setTag("PendingSmelt", smelt);
		}

		// / Inventory
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		heat = nbttagcompound.getInteger("Heat");

		// Tank
		tankManager.readTanksFromNBT(nbttagcompound);

		// Pending Smelt
		if (nbttagcompound.hasKey("PendingSmelt")) {
			NBTTagCompound smelt = nbttagcompound.getCompoundTag("PendingSmelt");
			pendingSmelt = FluidStack.loadFluidStackFromNBT(smelt);
		}

		// / Inventory
		inventory.readFromNBT(nbttagcompound);

	}

	/* UPDATING */
	@Override
	public void updateServerSide() {

		// Add pending smelt
		if (pendingSmelt != null) {

			int filled = moltenTank.fill(pendingSmelt, true);
			pendingSmelt.amount -= filled;

			if (pendingSmelt.amount <= 0)
				pendingSmelt = null;
			// Smelt if necessary and possible
		} else if (moltenTank.getFluidAmount() < moltenTank.getCapacity() && inventory.getStackInSlot(SLOT_METAL) != null) {
			Smelting smelt = RecipeManager.findMatchingSmelting(inventory.getStackInSlot(SLOT_METAL));
			if (smelt != null && smelt.meltingPoint <= heat) {

				this.decrStackSize(SLOT_METAL, 1);
				pendingSmelt = smelt.product.copy();
			}
		} else if (moltenTank.getFluidAmount() > 0) {
			// Remove smelt if we have gone below melting point
			Smelting smelt = RecipeManager.findMatchingSmelting(moltenTank.getFluid());
			if (smelt != null && heat < smelt.meltingPoint)
				moltenTank.drain(5, true);
		}

		this.dissipateHeat();
	}

	@Override
	public boolean workCycle() {
		return addHeat(25);
	}

	private boolean addHeat(int addition) {
		if (this.heat >= MAX_HEAT)
			return false;

		this.heat += addition;
		if (this.heat > MAX_HEAT)
			this.heat = MAX_HEAT;

		return true;
	}

	private void dissipateHeat() {
		if (heat > 2500)
			this.heat -= 2;
		else if (heat > 0)
			this.heat--;
	}

	public Object[] getPlan() {
		if (inventory.getStackInSlot(SLOT_PLAN) == null)
			return null;

		Recipe myRecipe = RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_PLAN));
		if (myRecipe == null)
			return null;

		return myRecipe.internal.getIngredients();
	}

	/* ICRAFTER */
	@Override
	public boolean canTakeStack(int slotIndex) {
		return true;
	}

	@Override
	public ItemStack getResult() {
		Recipe myRecipe = RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_PLAN), moltenTank.getFluid(),
				inventory.getStacks(SLOT_CRAFTING_1, 9));

		if (myRecipe == null)
			return null;

		return myRecipe.internal.getRecipeOutput().copy();
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, boolean consumeRecipe, EntityPlayer player) {
		if(slotIndex != SLOT_RESULT)
			return null;

		Recipe myRecipe = RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_PLAN), moltenTank.getFluid(),
				inventory.getStacks(SLOT_CRAFTING_1, 9));
		if (myRecipe == null)
			return null;

		FluidStack liquid = myRecipe.molten;

		// Remove resources
		if (removeFromInventory(1, inventory.getStacks(SLOT_CRAFTING_1, 9), player, false)) {
			removeFromInventory(1, inventory.getStacks(SLOT_CRAFTING_1, 9), player, true);
			moltenTank.drain(liquid.amount, true);
		} else if (consumeRecipe) {
			removeFromCraftMatrix(myRecipe);
			moltenTank.drain(liquid.amount, true);
		} else
			return null;

		ItemStack result = myRecipe.internal.getRecipeOutput().copy();
		// Damage plan
		if (inventory.getStackInSlot(SLOT_PLAN) != null) {
			Item planItem = inventory.getStackInSlot(SLOT_PLAN).getItem();
			if (planItem instanceof ICraftingPlan)
				inventory.setInventorySlotContents(SLOT_PLAN, ((ICraftingPlan) planItem).planUsed(inventory.getStackInSlot(SLOT_PLAN), result));
		}

		// Return result
		return result;
	}

	private void removeFromCraftMatrix(Recipe recipe) {

		for (int i = 0; i < 9; i++) {
			if (inventory.getStackInSlot(SLOT_CRAFTING_1 + i) == null)
				continue;
			inventory.decrStackSize(SLOT_CRAFTING_1 + i, 1);
		}

	}

	private boolean removeFromInventory(int count, ItemStack[] set, EntityPlayer player, boolean doRemove) {
		if (doRemove) {
			return inventory.removeSets(count, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true, true);
		} else {
			ItemStack[] stock = inventory.getStacks(SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
			return StackUtils.containsSets(set, stock) >= count;
		}
	}

	@Override
	public boolean isWorking() {
		return this.heat <= MAX_HEAT;
	}

	public int getHeatScaled(int i) {
		return (heat * i) / MAX_HEAT;
	}

	public int getMeltingPoint() {
		if (moltenTank.getFluidAmount() > 0) {
			Smelting smelt = RecipeManager.findMatchingSmelting(moltenTank.getFluid());
			if (smelt != null)
				return smelt.meltingPoint;
		} else if (this.getStackInSlot(SLOT_METAL) != null) {
			Smelting smelt = RecipeManager.findMatchingSmelting(this.getStackInSlot(SLOT_METAL));
			if (smelt != null)
				return smelt.meltingPoint;
		}

		return 0;
	}

	public int getMeltingPointScaled(int i) {
		// / For SMP clients
		if (guiMeltingPoint > 0)
			return (guiMeltingPoint * i) / MAX_HEAT;

		int meltingPoint = getMeltingPoint();

		if (meltingPoint <= 0)
			return 0;
		else
			return (meltingPoint * i) / MAX_HEAT;
	}

	/* SMP */
	public void getGUINetworkData(int i, int j) {
		int messageId = tankManager.maxMessageId() + 1;

		if (i == messageId)
			heat = j;
		else if (i == messageId + 1)
			guiMeltingPoint = j;
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int messageId = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, messageId, heat);
		iCrafting.sendProgressBarUpdate(container, messageId + 1, getMeltingPoint());
	}

	// / ISPECIALINVENTORY
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (from == ForgeDirection.UP && RecipeManager.findMatchingSmelting(stack) != null)
			return inventory.addStack(stack, SLOT_METAL, 1, false, doAdd);
		return inventory.addStack(stack, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, false, doAdd);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack taken;
		if (doRemove) {
			EntityPlayer player = Proxies.common.getPlayer(worldObj, owner);
			taken = this.takenFromSlot(SLOT_RESULT, false, player);
		} else {
			taken = this.getResult();
		}

		if (taken != null)
			return new ItemStack[] { taken };
		else
			return new ItemStack[0];
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
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
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

	/* ILIQUIDCONTAINER */
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
