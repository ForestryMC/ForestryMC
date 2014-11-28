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
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterManager;
import forestry.api.recipes.IVariableFermentable;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class MachineFermenter extends TilePowered implements ISidedInventory, ILiquidTankContainer {

	// / CONSTANTS
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_FUEL = 1;
	public static final short SLOT_CAN_OUTPUT = 2;
	public static final short SLOT_CAN_INPUT = 3;
	public static final short SLOT_INPUT = 4;

	// / RECIPE MANAGMENT
	public static class Recipe {

		public final ItemStack resource;
		public final int fermentationValue;
		public final float modifier;
		public final FluidStack output;
		public final FluidStack liquid;

		public Recipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
			this.resource = resource;
			this.fermentationValue = fermentationValue;
			this.modifier = modifier;
			this.output = output;
			this.liquid = liquid;

			if (resource == null)
				throw new NullPointerException("Fermenter Resource cannot be null!");

			if (output == null)
				throw new NullPointerException("Fermenter Output cannot be null!");

			if (liquid == null)
				throw new NullPointerException("Fermenter Liquid cannot be null!");
		}

		public boolean matches(ItemStack res, FluidStack liqu) {
			// No recipe without resource!
			if (res == null)
				return false;

			if (resource.getItem() != res.getItem())
				return false;
			if (resource.getItemDamage() != Defaults.WILDCARD && resource.getItemDamage() != res.getItemDamage())
				return false;

			// No liquid required
			if (liquid == null)
				return true;

			// Liquid required but none given
			if (liquid != null && liqu == null)
				return false;

			// Wrong liquid
			if (!liquid.isFluidEqual(liqu))
				return false;

			// Enough liquid
			if (liquid.amount <= liqu.amount)
				return true;

			return false;
		}
	}

	public static class RecipeManager implements IFermenterManager {

		public static ArrayList<MachineFermenter.Recipe> recipes = new ArrayList<MachineFermenter.Recipe>();
		public static HashSet<Fluid> recipeFluidInputs = new HashSet<Fluid>();
		public static HashSet<Fluid> recipeFluidOutputs = new HashSet<Fluid>();

		@Override
		public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
			recipes.add(new Recipe(resource, fermentationValue, modifier, output, liquid));
			if (liquid != null)
				recipeFluidInputs.add(liquid.getFluid());
			if (output != null)
				recipeFluidOutputs.add(output.getFluid());
		}

		@Override
		public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output) {
			addRecipe(resource, fermentationValue, modifier, output, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, 1000));
		}

		public static Recipe findMatchingRecipe(ItemStack res, FluidStack liqu) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(res, liqu))
					return recipe;
			}
			return null;
		}

		public static boolean isResource(ItemStack resource) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.resource.getItemDamage() == Defaults.WILDCARD
						&& recipe.resource.getItem() == resource.getItem()) {
					return true;
				} else if (recipe.resource.isItemEqual(resource)) {
					return true;
				}
			}
			return false;
		}

		public static boolean isLiquidResource(FluidStack liquid) {
			return recipeFluidInputs.contains(liquid.getFluid());
		}

		public static boolean isLiquidProduct(FluidStack liquid) {
			return recipeFluidOutputs.contains(liquid.getFluid());
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(new Object[] { recipe.resource, recipe.liquid }, new Object[] { recipe.output });
			}

			return recipeList;
		}
	}
	@EntityNetData
	public FilteredTank resourceTank;
	@EntityNetData
	public FilteredTank productTank;

	private final TankManager tankManager;

	private final InventoryAdapter inventory = new InventoryAdapter(5, "Items");
	private Recipe currentRecipe;
	private float currentResourceModifier;
	public int fermentationTime = 0;
	public int fermentationTotalTime = 0;
	public int fuelBurnTime = 0;
	public int fuelTotalTime = 0;
	public int fuelCurrentFerment = 0;

	public MachineFermenter() {
		super(2000, 150, 8000);
		setHints(Config.hints.get("fermenter"));
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
		player.openGui(ForestryAPI.instance, GuiId.FermenterGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("FermentationTime", fermentationTime);
		nbttagcompound.setInteger("FermentationTotalTime", fermentationTotalTime);
		nbttagcompound.setInteger("FuelBurnTime", fuelBurnTime);
		nbttagcompound.setInteger("FuelTotalTime", fuelTotalTime);
		nbttagcompound.setInteger("FuelCurrentFerment", fuelCurrentFerment);

		tankManager.writeTanksToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);

	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		fermentationTime = nbttagcompound.getInteger("FermentationTime");
		fermentationTotalTime = nbttagcompound.getInteger("FermentationTotalTime");
		fuelBurnTime = nbttagcompound.getInteger("FuelBurnTime");
		fuelTotalTime = nbttagcompound.getInteger("FuelTotalTime");
		fuelCurrentFerment = nbttagcompound.getInteger("FuelCurrentFerment");

		tankManager.readTanksFromNBT(nbttagcompound);
		inventory.readFromNBT(nbttagcompound);
	}

	@Override
	public void updateServerSide() {

		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_INPUT) != null) {

			FluidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_INPUT));
			if (container != null && RecipeManager.isLiquidResource(container.fluid)) {

				inventory.setInventorySlotContents(SLOT_INPUT, StackUtils.replenishByContainer(this, inventory.getStackInSlot(SLOT_INPUT), container, resourceTank));
				if (inventory.getStackInSlot(SLOT_INPUT).stackSize <= 0)
					inventory.setInventorySlotContents(SLOT_INPUT, null);

			}
		}
		// Can/capsule input/output needs to be handled here.
		if (inventory.getStackInSlot(SLOT_CAN_INPUT) != null) {
			FluidContainerData container = LiquidHelper.getEmptyContainer(inventory.getStackInSlot(SLOT_CAN_INPUT), productTank.getFluid());

			if (container != null) {
				inventory.setInventorySlotContents(SLOT_CAN_OUTPUT, bottleIntoContainer(inventory.getStackInSlot(SLOT_CAN_INPUT), inventory.getStackInSlot(SLOT_CAN_OUTPUT), container,
						productTank));
				if (inventory.getStackInSlot(SLOT_CAN_INPUT).stackSize <= 0)
					inventory.setInventorySlotContents(SLOT_CAN_INPUT, null);
			}
		}

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;


		if (RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid()) != null) {
			if (resourceTank.getFluidAmount() < fuelCurrentFerment)
				setErrorState(EnumErrorCode.NORESOURCE);
			else
				setErrorState(EnumErrorCode.OK);
		} else if (inventory.getStackInSlot(SLOT_FUEL) == null && fuelBurnTime <= 0)
			setErrorState(EnumErrorCode.NOFUEL);
		else if (energyManager.getTotalEnergyStored() == 0)
			setErrorState(EnumErrorCode.NOPOWER);
		else
			setErrorState(EnumErrorCode.NORECIPE);
	}

	@Override
	public boolean workCycle() {

		if (currentRecipe == null) {
			checkRecipe();
			resetRecipe();

			if (currentRecipe != null) {
				currentResourceModifier = determineResourceMod(inventory.getStackInSlot(SLOT_RESOURCE));
				decrStackSize(SLOT_RESOURCE, 1);
				return true;
			} else
				return false;

			// If we have burnTime left, just decrease it.
		} else if (fuelBurnTime > 0) {
			if (resourceTank.getFluidAmount() < fuelCurrentFerment)
				return false;

			// Nothing to do, return
			if (fermentationTime <= 0)
				return false;

			int fermented = Math.min(fermentationTime, this.fuelCurrentFerment);

			// input are checked, add output if possible
			if (!addProduct(new FluidStack(currentRecipe.output,
					Math.round(fermented * currentRecipe.modifier * currentResourceModifier)))) {
				return false; // the output tank is too full, TODO: check/add error code?
			}

			fuelBurnTime--;
			resourceTank.drain(fuelCurrentFerment, true);
			fermentationTime -= this.fuelCurrentFerment;

			// Not done yet
			if (fermentationTime > 0)
				return true;

			currentRecipe = null;
			return true;

		} else { // out of fuel

			// Use only fuel that provides value
			fuelBurnTime = fuelTotalTime = determineFuelValue(getFuelStack());
			if (fuelBurnTime > 0) {
				this.fuelCurrentFerment = determineFermentPerCycle(getFuelStack());
				decrStackSize(1, 1);
				return true;
			} else {
				this.fuelCurrentFerment = 0;
				return false;
			}
		}
	}

	private boolean addProduct(FluidStack output) {
		int amount = productTank.fill(output, false);

		if (amount == output.amount) {
			productTank.fill(output, true);

			return true;
		} else {
			return false;
		}
	}

	private void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid());

		if (currentRecipe != sameRec)
			currentRecipe = sameRec;
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			fermentationTime = 0;
			fermentationTotalTime = 0;
			return;
		}

		fermentationTime = currentRecipe.fermentationValue;
		fermentationTotalTime = currentRecipe.fermentationValue;
	}

	/**
	 * Returns the burnTime an item of the passed ItemStack provides
	 *
	 * @param item
	 * @return
	 */
	private int determineFuelValue(ItemStack item) {
		if (item == null)
			return 0;

		if (FuelManager.fermenterFuel.containsKey(item))
			return FuelManager.fermenterFuel.get(item).burnDuration;
		else
			return 0;
	}

	private int determineFermentPerCycle(ItemStack item) {
		if (item == null)
			return 0;

		if (FuelManager.fermenterFuel.containsKey(item))
			return FuelManager.fermenterFuel.get(item).fermentPerCycle;
		else
			return 0;
	}

	private float determineResourceMod(ItemStack itemstack) {
		if (!(itemstack.getItem() instanceof IVariableFermentable))
			return 1.0f;

		return ((IVariableFermentable) itemstack.getItem()).getFermentationModifier(itemstack);
	}

	@Override
	public boolean isWorking() {
		if (currentRecipe == null
				&& RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid()) == null)
			return false;
		if (fuelBurnTime > 0)
			return resourceTank.getFluidAmount() > 0 && productTank.getFluidAmount() < Defaults.PROCESSOR_TANK_CAPACITY;
			else
				return determineFuelValue(getFuelStack()) > 0;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		if (this.getFermentationStack() == null)
			return false;

		return ((float) getFermentationStack().stackSize / (float) getFermentationStack().getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		if (this.getFuelStack() == null)
			return false;

		return ((float) getFuelStack().stackSize / (float) getFuelStack().getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		if (this.getFuelStack() == null && fuelBurnTime <= 0)
			return false;
		else if (fuelBurnTime <= 0)
			if (RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid()) == null)
				return false;

		if (this.getFermentationStack() == null && fermentationTime <= 0)
			return false;
		else if (fermentationTime <= 0)
			if (RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid()) == null)
				return false;

		if (resourceTank.getFluidAmount() <= fuelCurrentFerment)
			return false;

		if (productTank.getFluidAmount() >= productTank.getCapacity())
			return false;

		return true;
	}

	public int getBurnTimeRemainingScaled(int i) {
		if (fuelTotalTime == 0)
			return 0;

		return (fuelBurnTime * i) / fuelTotalTime;
	}

	public int getFermentationProgressScaled(int i) {
		if (fermentationTotalTime == 0)
			return 0;

		return (fermentationTime * i) / fermentationTotalTime;
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

	public ItemStack getFermentationStack() {
		return inventory.getStackInSlot(SLOT_RESOURCE);
	}

	public ItemStack getFuelStack() {
		return inventory.getStackInSlot(SLOT_FUEL);
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		int firstMessageId = tankManager.maxMessageId() + 1;

		if (i == firstMessageId)
			fuelBurnTime = j;
		else if (i == firstMessageId + 1)
			fuelTotalTime = j;
		else if (i == firstMessageId + 2)
			fermentationTime = j;
		else  if (i == firstMessageId + 3)
			fermentationTotalTime = j;
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int firstMessageId = tankManager.maxMessageId() + 1;

		iCrafting.sendProgressBarUpdate(container, firstMessageId, fuelBurnTime);
		iCrafting.sendProgressBarUpdate(container, firstMessageId + 1, fuelTotalTime);
		iCrafting.sendProgressBarUpdate(container, firstMessageId + 2, fermentationTime);
		iCrafting.sendProgressBarUpdate(container, firstMessageId + 3, fermentationTotalTime);
	}

	/* INVENTORY */
	@Override
	public InventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	protected boolean canTakeStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if (!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		if (slotIndex == SLOT_CAN_OUTPUT)
			return true;

		return false;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if (!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		if (slotIndex == SLOT_RESOURCE && RecipeManager.isResource(itemstack))
			return true;


		if (slotIndex == SLOT_INPUT) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(itemstack);
			return container != null && RecipeManager.isLiquidResource(container.fluid);
		}

		if (slotIndex == SLOT_CAN_INPUT && LiquidHelper.isEmptyContainer(itemstack)) {
			return true;
		}
		if (slotIndex == SLOT_FUEL && FuelManager.fermenterFuel.containsKey(itemstack))
			return true;

		return false;
	}

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
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
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

	/* ILiquidTankContainer */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return resourceTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int quantityMax, boolean doEmpty) {
		return productTank.drain(quantityMax, doEmpty);
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
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(FactoryTriggers.lowResource25);
		res.add(FactoryTriggers.lowResource10);
		return res;
	}
}
