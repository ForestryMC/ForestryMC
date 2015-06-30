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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class MachineMoistener extends TileBase implements ISidedInventory, ILiquidTankContainer, IRenderableMachine {

	/* CONSTANTS */
	private static final short SLOT_STASH_1 = 0;
	private static final short SLOT_STASH_COUNT = 6;
	private static final short SLOT_RESERVOIR_1 = 6;
	private static final short SLOT_RESERVOIR_COUNT = 3;
	private static final short SLOT_WORKING = 9;
	private static final short SLOT_PRODUCT = 10;
	private static final short SLOT_RESOURCE = 11;

	/* RECIPE MANAGMENT */
	public static class Recipe {

		public final int timePerItem;
		public final ItemStack resource;
		public final ItemStack product;

		public Recipe(ItemStack resource, ItemStack product, int timePerItem) {
			this.timePerItem = timePerItem;
			this.resource = resource;
			this.product = product;
		}

		public boolean matches(ItemStack res) {
			return StackUtils.isCraftingEquivalent(resource, res);
		}
	}

	public static class RecipeManager implements IMoistenerManager {

		public static final ArrayList<MachineMoistener.Recipe> recipes = new ArrayList<MachineMoistener.Recipe>();

		@Override
		public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {
			recipes.add(new MachineMoistener.Recipe(resource, product, timePerItem));
		}

		public static boolean isResource(ItemStack resource) {
			if (resource == null) {
				return false;
			}

			for (Recipe rec : recipes) {
				if (StackUtils.isIdenticalItem(resource, rec.resource)) {
					return true;
				}
			}

			return false;
		}

		public static Recipe findMatchingRecipe(ItemStack item) {
			for (Recipe recipe : recipes) {
				if (recipe.matches(item)) {
					return recipe;
				}
			}
			return null;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(new ItemStack[]{recipe.resource}, new ItemStack[]{recipe.product});
			}

			return recipeList;
		}
	}

	public final FilteredTank resourceTank;
	private final TankManager tankManager;
	public MachineMoistener.Recipe currentRecipe;

	public int burnTime = 0;
	public int totalTime = 0;
	public int productionTime = 0;
	private int timePerItem = 0;
	private ItemStack currentProduct;
	private ItemStack pendingProduct;

	public MachineMoistener() {
		setInternalInventory(new MoistenerInventoryAdapter(this));
		setHints(Config.hints.get("moistener"));
		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, FluidRegistry.WATER);
		tankManager = new TankManager(resourceTank);
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.MoistenerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("BurnTime", burnTime);
		nbttagcompound.setInteger("TotalTime", totalTime);
		nbttagcompound.setInteger("ProductionTime", productionTime);

		tankManager.writeTanksToNBT(nbttagcompound);

		// Write pending product
		if (pendingProduct != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			pendingProduct.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("PendingProduct", nbttagcompoundP);
		}
		if (currentProduct != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			currentProduct.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("CurrentProduct", nbttagcompoundP);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		burnTime = nbttagcompound.getInteger("BurnTime");
		totalTime = nbttagcompound.getInteger("TotalTime");
		productionTime = nbttagcompound.getInteger("ProductionTime");

		tankManager.readTanksFromNBT(nbttagcompound);

		// Load pending product
		if (nbttagcompound.hasKey("PendingProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("PendingProduct");
			pendingProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}
		if (nbttagcompound.hasKey("CurrentProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("CurrentProduct");
			currentProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}

		checkRecipe();
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

	@Override
	public void updateServerSide() {

		IInventoryAdapter inventory = getInternalInventory();
		// Check if we have suitable water container waiting in the item slot
		if (inventory.getStackInSlot(SLOT_PRODUCT) != null) {
			FluidHelper.drainContainers(tankManager, inventory, SLOT_PRODUCT);
		}

		// Let's get to work
		int lightvalue = worldObj.getBlockLightValue(xCoord, yCoord + 1, zCoord);

		IErrorLogic errorLogic = getErrorLogic();

		// Not working in broad daylight
		boolean gloomy = lightvalue <= 11;
		if (errorLogic.setCondition(!gloomy, EnumErrorCode.NOTGLOOMY)) {
			return;
		}

		// The darker, the better
		int speed;
		if (lightvalue >= 9) {
			speed = 1;
		} else if (lightvalue >= 7) {
			speed = 2;
		} else if (lightvalue >= 5) {
			speed = 3;
		} else {
			speed = 4;
		}

		// Already running
		if (burnTime > 0 && pendingProduct == null) {
			// Not working if there is no water available.
			boolean hasLiquid = resourceTank.getFluidAmount() > 0;
			if (errorLogic.setCondition(!hasLiquid, EnumErrorCode.NORESOURCE)) {
				return;
			}

			checkRecipe();

			if (currentRecipe == null) {
				return;
			}

			resourceTank.drain(1, true);
			burnTime -= speed;
			productionTime -= speed;

			if (productionTime <= 0) {
				pendingProduct = currentProduct;
				decrStackSize(SLOT_RESOURCE, 1);
				resetRecipe();
				tryAddPending();
			}

		} else if (pendingProduct != null) {
			tryAddPending();
		}
		// Try to start process
		else { // Make sure we have a new item in the working slot.
			if (rotateWorkingSlot()) {
				checkRecipe();

				// Let's see if we have a valid resource in the working slot
				if (inventory.getStackInSlot(SLOT_WORKING) == null) {
					return;
				}

				if (FuelManager.moistenerResource.containsKey(inventory.getStackInSlot(SLOT_WORKING))) {
					MoistenerFuel res = FuelManager.moistenerResource.get(inventory.getStackInSlot(SLOT_WORKING));
					burnTime = totalTime = res.moistenerValue;
				}
			} else {
				rotateReservoir();
			}
		}

		errorLogic.setCondition(currentRecipe == null, EnumErrorCode.NORECIPE);
	}

	private boolean tryAddPending() {
		if (pendingProduct == null) {
			return false;
		}

		boolean added = InvTools.tryAddStack(this, pendingProduct, SLOT_PRODUCT, 1, true);
		getErrorLogic().setCondition(!added, EnumErrorCode.NOSPACE);

		if (added) {
			pendingProduct = null;
		}

		return added;
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(getInternalInventory().getStackInSlot(SLOT_RESOURCE));
		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}

		getErrorLogic().setCondition(currentRecipe == null, EnumErrorCode.NORECIPE);
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			currentProduct = null;
			productionTime = 0;
			timePerItem = 0;
		} else {
			currentProduct = currentRecipe.product;
			productionTime = currentRecipe.timePerItem;
			timePerItem = currentRecipe.timePerItem;
		}
	}

	private int getFreeSlot(ItemStack deposit, int startSlot, int endSlot, boolean emptyOnly) {
		int slot = -1;

		for (int i = startSlot; i < endSlot; i++) {
			ItemStack slotStack = getStackInSlot(i);
			// Empty slots are okay.
			if (slotStack == null) {
				if (slot < 0) {
					slot = i;
				}
				continue;
			}

			if (emptyOnly) {
				continue;
			}

			// Wrong item or full
			if (!slotStack.isItemEqual(deposit) || slotStack.stackSize >= slotStack.getMaxStackSize()) {
				continue;
			}

			slot = i;
		}

		return slot;
	}

	private int getFreeStashSlot(ItemStack deposit, boolean emptyOnly) {
		return getFreeSlot(deposit, 0, SLOT_RESERVOIR_1, emptyOnly);
	}

	private int getFreeReservoirSlot(ItemStack deposit) {
		return getFreeSlot(deposit, SLOT_RESERVOIR_1, SLOT_RESERVOIR_1 + SLOT_RESERVOIR_COUNT, false);

	}

	private int getNextResourceSlot(int startSlot, int endSlot) {
		// Let's look for a new resource to put into the working slot.
		int stage = -1;
		int resourceSlot = -1;

		IInventoryAdapter inventory = getInternalInventory();
		for (int i = startSlot; i < endSlot; i++) {
			ItemStack slotStack = inventory.getStackInSlot(i);
			if (slotStack == null) {
				continue;
			}

			if (!FuelManager.moistenerResource.containsKey(slotStack)) {
				continue;
			}

			MoistenerFuel res = FuelManager.moistenerResource.get(slotStack);
			if (stage < 0 || res.stage < stage) {
				stage = res.stage;
				resourceSlot = i;
			}
		}

		return resourceSlot;
	}

	private boolean rotateWorkingSlot() {
		IErrorLogic errorLogic = getErrorLogic();

		// Put working slot contents into inventory if space is available
		if (getStackInSlot(SLOT_WORKING) != null) {
			// Get the result of the consumed item in the working slot
			ItemStack deposit;
			if (FuelManager.moistenerResource.containsKey(getStackInSlot(SLOT_WORKING))) {
				MoistenerFuel res = FuelManager.moistenerResource.get(getStackInSlot(SLOT_WORKING));
				deposit = res.product.copy();
			} else {
				deposit = getStackInSlot(SLOT_WORKING).copy();
			}

			int targetSlot = getFreeReservoirSlot(deposit);
			// We stop the whole thing, if we don't have any room anymore.
			if (errorLogic.setCondition(targetSlot < 0, EnumErrorCode.NOSPACE)) {
				return false;
			}

			if (getStackInSlot(targetSlot) == null) {
				setInventorySlotContents(targetSlot, deposit);
			} else {
				getStackInSlot(targetSlot).stackSize++;
			}

			decrStackSize(SLOT_WORKING, 1);
		}

		if (getStackInSlot(SLOT_WORKING) != null) {
			return true;
		}

		// Let's look for a new resource to put into the working slot.
		int resourceSlot = getNextResourceSlot(SLOT_RESERVOIR_1, SLOT_RESERVOIR_1 + SLOT_RESERVOIR_COUNT);
		// Nothing found, stop.
		if (errorLogic.setCondition(resourceSlot < 0, EnumErrorCode.NORESOURCE)) {
			return false;
		}

		setInventorySlotContents(SLOT_WORKING, decrStackSize(resourceSlot, 1));
		return true;
	}

	private void rotateReservoir() {
		ArrayList<Integer> slotsToShift = new ArrayList<Integer>();

		for (int i = SLOT_RESERVOIR_1; i < SLOT_RESERVOIR_1 + SLOT_RESERVOIR_COUNT; i++) {
			if (getStackInSlot(i) == null) {
				continue;
			}

			if (!FuelManager.moistenerResource.containsKey(getStackInSlot(i))) {
				slotsToShift.add(i);
			}
		}

		// Move consumed items back to stash
		int shiftedSlots = 0;
		for (int slot : slotsToShift) {
			ItemStack slotStack = getStackInSlot(slot);
			int targetSlot = getFreeStashSlot(slotStack, true);
			if (targetSlot < 0) {
				continue;
			}

			setInventorySlotContents(targetSlot, slotStack);
			setInventorySlotContents(slot, null);
			shiftedSlots++;
		}

		// Grab new items from stash
		for (int i = 0; i < (slotsToShift.size() > 0 ? shiftedSlots : 2); i++) {
			int resourceSlot = getNextResourceSlot(0, SLOT_RESERVOIR_1);
			// Stop if no resources are available
			if (resourceSlot < 0) {
				break;
			}
			int targetSlot = getFreeReservoirSlot(getStackInSlot(resourceSlot));
			// No free target slot, stop
			if (targetSlot < 0) {
				break;
			}
			// Else shift
			if (getStackInSlot(targetSlot) == null) {
				setInventorySlotContents(targetSlot, getStackInSlot(resourceSlot));
				setInventorySlotContents(resourceSlot, null);
			} else {
				StackUtils.mergeStacks(getStackInSlot(resourceSlot), getStackInSlot(targetSlot));
				if (getStackInSlot(resourceSlot) != null && getStackInSlot(resourceSlot).stackSize <= 0) {
					setInventorySlotContents(resourceSlot, null);
				}
			}
		}
	}

	public boolean isWorking() {
		return burnTime > 0 && resourceTank.getFluidAmount() > 0;
	}

	public boolean hasFuelMin(float percentage) {
		int max = 0;
		int avail = 0;
		IInventoryAdapter inventory = getInternalInventory();

		for (int i = SLOT_STASH_1; i < SLOT_RESERVOIR_1; i++) {
			if (inventory.getStackInSlot(i) == null) {
				max += 64;
				continue;
			}
			if (FuelManager.moistenerResource.containsKey(inventory.getStackInSlot(i))) {
				MoistenerFuel res = FuelManager.moistenerResource.get(inventory.getStackInSlot(i));
				if (res.item.isItemEqual(inventory.getStackInSlot(i))) {
					max += 64;
					avail += inventory.getStackInSlot(i).stackSize;
				}
			}
		}

		return ((float) avail / (float) max) > percentage;
	}

	public boolean hasResourcesMin(float percentage) {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(SLOT_RESOURCE) == null) {
			return false;
		}

		return ((float) inventory.getStackInSlot(SLOT_RESOURCE).stackSize / (float) inventory.getStackInSlot(SLOT_RESOURCE).getMaxStackSize()) > percentage;
	}

	public boolean isProducing() {
		return productionTime > 0;
	}

	public int getProductionProgressScaled(int i) {
		if (timePerItem == 0) {
			return 0;
		}

		return (productionTime * i) / timePerItem;

	}

	public int getConsumptionProgressScaled(int i) {
		if (totalTime == 0) {
			return 0;
		}

		return (burnTime * i) / totalTime;

	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	/* IRenderableMachine */
	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	/* ILiquidTankContainer */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
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

	/* SMP GUI */
	@Override
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;

		switch (i) {
			case 0:
				burnTime = j;
				break;
			case 1:
				totalTime = j;
				break;
			case 2:
				productionTime = j;
				break;
			case 3:
				timePerItem = j;
				break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, burnTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, totalTime);
		iCrafting.sendProgressBarUpdate(container, i + 2, productionTime);
		iCrafting.sendProgressBarUpdate(container, i + 3, timePerItem);
	}

	private static class MoistenerInventoryAdapter extends TileInventoryAdapter<MachineMoistener> {
		public MoistenerInventoryAdapter(MachineMoistener moistener) {
			super(moistener, 12, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_RESOURCE) {
				return RecipeManager.isResource(itemStack);
			}

			if (GuiUtil.isIndexInRange(slotIndex, SLOT_STASH_1, SLOT_STASH_COUNT)) {
				return FuelManager.moistenerResource.containsKey(itemStack);
			}

			if (slotIndex == SLOT_PRODUCT) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.resourceTank.accepts(fluid);
			}

			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			if (slotIndex == SLOT_PRODUCT) {
				return true;
			}

			if (GuiUtil.isIndexInRange(slotIndex, SLOT_STASH_1, (SLOT_STASH_COUNT + SLOT_RESERVOIR_COUNT))) {
				return !FuelManager.moistenerResource.containsKey(itemstack);
			}

			return false;
		}
	}
}
