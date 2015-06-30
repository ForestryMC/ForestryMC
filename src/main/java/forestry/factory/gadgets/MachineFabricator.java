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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.recipes.IFabricatorManager;
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
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;

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
			if (this.plan != null && !StackUtils.isCraftingEquivalent(this.plan, plan)) {
				return false;
			}

			return internal.matches(resources);
		}

		public boolean hasLiquid(FluidStack resource) {
			if (resource == null) {
				return molten == null;
			}

			return resource.containsFluid(molten);
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
			if (resource == null) {
				throw new IllegalArgumentException("Resource cannot be null");
			}
			if (molten == null) {
				throw new IllegalArgumentException("Molten cannot be null");
			}

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
			if (resource == null || molten == null) {
				return;
			}
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
				if (recipe.matches(plan, gridResources)) {
					if (recipe.hasLiquid(liquid)) {
						return recipe;
					}
				}
			}

			return null;
		}

		public static boolean isPlan(ItemStack plan) {
			for (Recipe recipe : recipes) {
				if (StackUtils.isIdenticalItem(recipe.getPlan(), plan)) {
					return true;
				}
			}

			return false;
		}

		public static Smelting findMatchingSmelting(ItemStack resource) {
			if (resource == null) {
				return null;
			}

			for (Smelting smelting : smeltings) {
				if (StackUtils.isCraftingEquivalent(smelting.resource, resource)) {
					return smelting;
				}
			}

			return null;
		}

		public static Smelting findMatchingSmelting(FluidStack product) {
			if (product == null) {
				return null;
			}

			for (Smelting smelting : smeltings) {
				if (smelting.matches(product)) {
					return smelting;
				}
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
	// FIXME 1.8: change indexes and use correct SLOT_COUNT of 21
	// left this way for now to avoid losing items in existing fabricators
	public static final short SLOT_CRAFTING_LEGACY_1 = 3;
	public static final short SLOT_CRAFTING_LEGACY_COUNT = 9;
	public static final short SLOT_INVENTORY_1 = 12;
	public static final short SLOT_INVENTORY_COUNT = 18;
	public static final short SLOT_COUNT = 30;

	public static final short SLOT_CRAFTING_1 = 0;
	public static final short SLOT_CRAFTING_COUNT = 9;

	/* MEMBER */
	private final TileInventoryAdapter craftingInventory;
	private final TankManager tankManager;
	private final FilteredTank moltenTank;
	private int heat = 0;
	private int guiMeltingPoint = 0;

	public MachineFabricator() {
		super(1100, 50, 3300);
		craftingInventory = new TileInventoryAdapter<MachineFabricator>(this, SLOT_CRAFTING_COUNT, "CraftItems");
		setInternalInventory(new FabricatorInventoryAdapter(this));
		moltenTank = new FilteredTank(2 * Defaults.BUCKET_VOLUME, Fluids.GLASS.getFluid());
		moltenTank.tankMode = StandardTank.TankMode.INTERNAL;
		tankManager = new TankManager(moltenTank);
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

		craftingInventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		heat = nbttagcompound.getInteger("Heat");

		// Tank
		tankManager.readTanksFromNBT(nbttagcompound);

		craftingInventory.readFromNBT(nbttagcompound);

		// FIXME 1.8: wont need this
		// move items from legacy crafting area to the new one
		IInventory inventory = getInternalInventory();
		for (int slot = SLOT_CRAFTING_LEGACY_1; slot < SLOT_CRAFTING_LEGACY_1 + SLOT_CRAFTING_LEGACY_COUNT; slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack != null) {
				inventory.setInventorySlotContents(slot, null);

				int newSlot = slot - SLOT_CRAFTING_LEGACY_1;
				craftingInventory.setInventorySlotContents(newSlot, stack);
			}
		}
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

	/* UPDATING */
	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (!moltenTank.isFull()) {
			trySmelting();
		}

		if (!moltenTank.isEmpty()) {
			// Remove smelt if we have gone below melting point
			Smelting smelt = RecipeManager.findMatchingSmelting(moltenTank.getFluid());
			if (smelt != null && heat < smelt.meltingPoint) {
				moltenTank.drain(5, true);
			}
		}

		this.dissipateHeat();
	}

	private void trySmelting() {
		IInventoryAdapter inventory = getInternalInventory();

		ItemStack smeltResource = inventory.getStackInSlot(SLOT_METAL);
		if (smeltResource == null) {
			return;
		}

		Smelting smelt = RecipeManager.findMatchingSmelting(smeltResource);
		if (smelt == null || smelt.meltingPoint > heat) {
			return;
		}

		FluidStack smeltFluid = smelt.getProduct();
		if (moltenTank.fill(smeltFluid, false) == smeltFluid.amount) {
			this.decrStackSize(SLOT_METAL, 1);
			moltenTank.fill(smeltFluid, true);
		}
	}

	@Override
	public boolean workCycle() {
		craftResult(null);
		return addHeat(25);
	}

	private boolean addHeat(int addition) {
		if (this.heat >= MAX_HEAT) {
			return false;
		}

		this.heat += addition;
		if (this.heat > MAX_HEAT) {
			this.heat = MAX_HEAT;
		}

		return true;
	}

	private void dissipateHeat() {
		if (heat > 2500) {
			this.heat -= 2;
		} else if (heat > 0) {
			this.heat--;
		}
	}

	private Recipe getRecipe() {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack plan = inventory.getStackInSlot(SLOT_PLAN);
		ItemStack[] crafting = InvTools.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
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

		if (myRecipe == null) {
			return null;
		}

		return myRecipe.internal.getRecipeOutput().copy();
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, EntityPlayer player) {
		if (slotIndex != SLOT_RESULT) {
			return null;
		}

		return getInternalInventory().decrStackSize(SLOT_RESULT, 1);
	}

	private void craftResult(EntityPlayer player) {
		Recipe myRecipe = getRecipe();
		if (myRecipe == null) {
			return;
		}

		ItemStack result = getResult();
		if (result == null) {
			return;
		}

		IInventoryAdapter inventory = getInternalInventory();

		if (inventory.getStackInSlot(SLOT_RESULT) != null) {
			return;
		}

		FluidStack liquid = myRecipe.molten;

		// Remove resources
		ItemStack[] crafting = InvTools.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		if (!removeFromInventory(crafting, player, false)) {
			return;
		}

		FluidStack canDrain = moltenTank.drain(liquid.amount, false);
		if (canDrain == null || !canDrain.isFluidStackIdentical(liquid)) {
			return;
		}

		removeFromInventory(crafting, player, true);
		moltenTank.drain(liquid.amount, true);

		// Damage plan
		if (inventory.getStackInSlot(SLOT_PLAN) != null) {
			Item planItem = inventory.getStackInSlot(SLOT_PLAN).getItem();
			if (planItem instanceof ICraftingPlan) {
				inventory.setInventorySlotContents(SLOT_PLAN, ((ICraftingPlan) planItem).planUsed(inventory.getStackInSlot(SLOT_PLAN), result));
			}
		}

		inventory.setInventorySlotContents(SLOT_RESULT, result);
	}

	private boolean removeFromInventory(ItemStack[] set, EntityPlayer player, boolean doRemove) {
		IInventoryAdapter inventory = getInternalInventory();
		if (doRemove) {
			return InvTools.removeSets(inventory, 1, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true, true);
		} else {
			ItemStack[] stock = InvTools.getStacks(inventory, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
			return StackUtils.containsSets(set, stock) >= 1;
		}
	}

	@Override
	public boolean isWorking() {
		return this.heat <= MAX_HEAT;
	}

	@Override
	public boolean hasWork() {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack itemToMelt = inventory.getStackInSlot(SLOT_METAL);
		Smelting smelting = RecipeManager.findMatchingSmelting(itemToMelt);
		if (smelting != null && moltenTank.fill(smelting.getProduct(), false) > 0) {
			return true;
		}

		ItemStack plan = inventory.getStackInSlot(SLOT_PLAN);
		ItemStack[] resources = InvTools.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);

		return RecipeManager.findMatchingRecipe(plan, moltenTank.getFluid(), resources) != null;
	}

	public int getHeatScaled(int i) {
		return (heat * i) / MAX_HEAT;
	}

	public int getMeltingPoint() {
		if (moltenTank.getFluidAmount() > 0) {
			Smelting smelt = RecipeManager.findMatchingSmelting(moltenTank.getFluid());
			if (smelt != null) {
				return smelt.meltingPoint;
			}
		} else if (this.getStackInSlot(SLOT_METAL) != null) {
			Smelting smelt = RecipeManager.findMatchingSmelting(this.getStackInSlot(SLOT_METAL));
			if (smelt != null) {
				return smelt.meltingPoint;
			}
		}

		return 0;
	}

	public int getMeltingPointScaled(int i) {
		// / For SMP clients
		if (guiMeltingPoint > 0) {
			return (guiMeltingPoint * i) / MAX_HEAT;
		}

		int meltingPoint = getMeltingPoint();

		if (meltingPoint <= 0) {
			return 0;
		} else {
			return (meltingPoint * i) / MAX_HEAT;
		}
	}

	/* SMP */
	@Override
	public void getGUINetworkData(int i, int j) {
		int messageId = tankManager.maxMessageId() + 1;

		if (i == messageId) {
			heat = j;
		} else if (i == messageId + 1) {
			guiMeltingPoint = j;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int messageId = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, messageId, heat);
		iCrafting.sendProgressBarUpdate(container, messageId + 1, getMeltingPoint());
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public InventoryAdapter getCraftingInventory() {
		return craftingInventory;
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

	private static class FabricatorInventoryAdapter extends TileInventoryAdapter<MachineFabricator> {
		public FabricatorInventoryAdapter(MachineFabricator fabricator) {
			super(fabricator, MachineFabricator.SLOT_COUNT, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_METAL) {
				return RecipeManager.findMatchingSmelting(itemStack) != null;
			} else if (slotIndex == SLOT_PLAN) {
				return RecipeManager.isPlan(itemStack);
			} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT)) {
				if (RecipeManager.isPlan(itemStack)) {
					return false;
				} else if (RecipeManager.findMatchingSmelting(itemStack) != null) {
					return false;
				}
			}
			return GuiUtil.isIndexInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
			return slotIndex == SLOT_RESULT;
		}
	}
}
