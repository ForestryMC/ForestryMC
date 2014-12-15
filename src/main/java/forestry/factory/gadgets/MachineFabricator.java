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
import forestry.api.recipes.IFabricatorManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ICrafter;
import forestry.core.interfaces.ICraftingPlan;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.GuiId;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class MachineFabricator extends TilePowered implements ICrafter, ILiquidTankContainer, ISidedInventory {

	/* RECIPE MANAGMENT */
	public static class Recipe {

		private final ItemStack plan;
		private final FluidStack molten;
		private final ShapedRecipeCustom internal;

		public Recipe(ItemStack plan, FluidStack molten, ShapedRecipeCustom internal) {
			this.plan = plan;
			this.molten = molten;
			this.internal = internal;
		}

		public boolean matches(ItemStack plan, ItemStack[][] resources) {
			if (this.plan != null && !StackUtils.isCraftingEquivalent(this.plan, plan))
				return false;

			return internal.matches(resources);
		}

		public boolean hasLiquid(FluidStack resource) {
			if (resource == null)
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

		private final ItemStack resource;
		private final FluidStack product;
		private final int meltingPoint;

		public Smelting(ItemStack resource, FluidStack molten, int meltingPoint) {
			if (resource == null)
				throw new IllegalArgumentException("Resource cannot be null");
			if (molten == null)
				throw new IllegalArgumentException("Molten cannot be null");

			this.resource = resource;
			this.product = molten;
			this.meltingPoint = meltingPoint;
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

		public static final ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		public static final ArrayList<Smelting> smeltings = new ArrayList<Smelting>();

		@Override
		public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {
			recipes.add(new Recipe(plan, molten, ShapedRecipeCustom.createShapedRecipe(result, pattern)));
		}

		@Override
		public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {
			smeltings.add(new Smelting(resource, molten, meltingPoint));
		}

		public static Recipe findMatchingRecipe(ItemStack plan, FluidStack liquid, ItemStack[] resources) {
			ItemStack[][] gridResources = new ItemStack[3][3];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					gridResources[j][i] = resources[i * 3 + j];
				}
			}

			for (Recipe recipe : recipes) {
				if (recipe.matches(plan, gridResources))
					if (recipe.hasLiquid(liquid))
						return recipe;
			}

			return null;
		}

		public static boolean isResourceLiquid(FluidStack liquid) {
			for (Recipe recipe : recipes) {
				if (recipe.hasLiquid(liquid))
					return true;
			}

			return false;
		}

		public static Smelting findMatchingSmelting(ItemStack resource) {
			if (resource == null)
				return null;

			for (Smelting smelting : smeltings) {
				if (StackUtils.isCraftingEquivalent(smelting.resource, resource))
					return smelting;
			}

			return null;
		}

		public static Smelting findMatchingSmelting(FluidStack product) {
			if (product == null)
				return null;

			for (Smelting smelting : smeltings) {
				if (smelting.matches(product))
					return smelting;
			}

			return null;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(recipe.internal.getIngredients(), new Object[]{recipe.internal.getRecipeOutput()});
			}

			return recipeList;
		}
	}

	/* CONSTANTS */
	private static final int MAX_HEAT = 5000;

	public static final short SLOT_METAL = 0;
	public static final short SLOT_PLAN = 1;
	public static final short SLOT_RESULT = 2;
	public static final short SLOT_CRAFTING_1 = 3;
	public static final short SLOT_CRAFTING_COUNT = 9;
	public static final short SLOT_INVENTORY_1 = 12;
	public static final short SLOT_INVENTORY_COUNT = 18;

	/* MEMBER */
	private final InventoryMapper invCrafting;
	private final TankManager tankManager;
	private final FilteredTank moltenTank;
	private int heat = 0;
	private int guiMeltingPoint = 0;

	private FluidStack pendingSmelt;

	public MachineFabricator() {
		super(1100, 50, 3300);
		setInternalInventory(new TileInventoryAdapter(this, 30, "Items"));
		invCrafting = new InventoryMapper(getInternalInventory(), SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		moltenTank = new FilteredTank(2 * Defaults.BUCKET_VOLUME, Fluids.GLASS.get());
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

	}

	/* UPDATING */
	@Override
	public void updateServerSide() {
		TileInventoryAdapter inventory = getInternalInventory();
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

		if (energyManager.getTotalEnergyStored() == 0 && heat == 0)
			setErrorState(EnumErrorCode.NOPOWER);
		else if (getErrorState() == EnumErrorCode.NOPOWER)
			setErrorState(EnumErrorCode.OK);
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

	private Recipe getRecipe() {
		TileInventoryAdapter inventory = getInternalInventory();
		ItemStack plan = inventory.getStackInSlot(SLOT_PLAN);
		ItemStack[] crafting = inventory.getStacks(SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		return RecipeManager.findMatchingRecipe(plan, moltenTank.getFluid(), crafting);
	}

	/* ICRAFTER */
	@Override
	public boolean canTakeStack(int slotIndex) {
		return true;
	}

	@Override
	public ItemStack getResult() {
		Recipe myRecipe = getRecipe();

		if (myRecipe == null)
			return null;

		return myRecipe.internal.getRecipeOutput().copy();
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, boolean consumeRecipe, EntityPlayer player) {
		if (slotIndex != SLOT_RESULT)
			return null;

		Recipe myRecipe = getRecipe();
		if (myRecipe == null)
			return null;

		FluidStack liquid = myRecipe.molten;

		TileInventoryAdapter inventory = getInternalInventory();
		// Remove resources
		if (removeFromInventory(1, inventory.getStacks(SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT), player, false)) {
			removeFromInventory(1, inventory.getStacks(SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT), player, true);
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
		for (IInvSlot slot : InventoryIterator.getIterable(invCrafting)) {
			if (slot.getStackInSlot() == null)
				continue;
			slot.decreaseStackInSlot();
		}

	}

	private boolean removeFromInventory(int count, ItemStack[] set, EntityPlayer player, boolean doRemove) {
		TileInventoryAdapter inventory = getInternalInventory();
		if (doRemove)
			return inventory.removeSets(count, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true, true);
		else {
			ItemStack[] stock = inventory.getStacks(SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
			return StackUtils.containsSets(set, stock) >= count;
		}
	}

	@Override
	public boolean isWorking() {
		return this.heat <= MAX_HEAT;
	}

	@Override
	public boolean hasWork() {
		TileInventoryAdapter inventory = getInternalInventory();
		ItemStack itemToMelt = inventory.getStackInSlot(SLOT_METAL);
		Smelting smelting = RecipeManager.findMatchingSmelting(itemToMelt);
		if (smelting != null && moltenTank.fill(smelting.getProduct(), false) > 0)
			return true;

		ItemStack plan = inventory.getStackInSlot(SLOT_PLAN);
		ItemStack[] resources = inventory.getStacks(SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);

		return RecipeManager.findMatchingRecipe(plan, moltenTank.getFluid(), resources) != null;
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
	@Override
	public void getGUINetworkData(int i, int j) {
		int messageId = tankManager.maxMessageId() + 1;

		if (i == messageId)
			heat = j;
		else if (i == messageId + 1)
			guiMeltingPoint = j;
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int messageId = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, messageId, heat);
		iCrafting.sendProgressBarUpdate(container, messageId + 1, getMeltingPoint());
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
	public int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInternalInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return Utils.isUseableByPlayer(player, this);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack stack) {
		if (!getInternalInventory().isItemValidForSlot(slotIndex, stack))
			return false;
		if (slotIndex == SLOT_METAL && RecipeManager.findMatchingSmelting(stack) == null)
			return false;
		return slotIndex >= SLOT_INVENTORY_1 && slotIndex < SLOT_INVENTORY_1 + SLOT_INVENTORY_COUNT;
	}

	/* ISIDEDINVENTORY */
	@Override
	public boolean canInsertItem(int slotIndex, ItemStack stack, int side) {
		return isItemValidForSlot(slotIndex, stack);
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
		if (!getInternalInventory().canExtractItem(slotIndex, stack, side))
			return false;
		return slotIndex == SLOT_RESULT;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return getInternalInventory().getAccessibleSlotsFromSide(side);
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
