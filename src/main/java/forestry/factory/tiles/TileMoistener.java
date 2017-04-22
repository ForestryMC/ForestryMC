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
package forestry.factory.tiles;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.gui.ContainerMoistener;
import forestry.factory.gui.GuiMoistener;
import forestry.factory.inventory.InventoryMoistener;
import forestry.factory.recipes.MoistenerRecipeManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileMoistener extends TileBase implements ISidedInventory, ILiquidTankTile, IRenderableTile {
	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	@Nullable
	private IMoistenerRecipe currentRecipe;

	private int burnTime = 0;
	private int totalTime = 0;
	private int productionTime = 0;
	private int timePerItem = 0;
	@Nullable
	private ItemStack currentProduct;
	@Nullable
	private ItemStack pendingProduct;

	public TileMoistener() {
		setInternalInventory(new InventoryMoistener(this));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(FluidRegistry.WATER);
		tankManager = new TankManager(this, resourceTank);
	}

	/* LOADING & SAVING */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("BurnTime", burnTime);
		nbttagcompound.setInteger("TotalTime", totalTime);
		nbttagcompound.setInteger("ProductionTime", productionTime);

		tankManager.writeToNBT(nbttagcompound);

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
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		burnTime = nbttagcompound.getInteger("BurnTime");
		totalTime = nbttagcompound.getInteger("TotalTime");
		productionTime = nbttagcompound.getInteger("ProductionTime");

		tankManager.readFromNBT(nbttagcompound);

		// Load pending product
		if (nbttagcompound.hasKey("PendingProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("PendingProduct");
			pendingProduct = new ItemStack(nbttagcompoundP);
		}
		if (nbttagcompound.hasKey("CurrentProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("CurrentProduct");
			currentProduct = new ItemStack(nbttagcompoundP);
		}

		checkRecipe();
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
	}

	@Override
	public void updateServerSide() {

		if (updateOnInterval(20)) {
			// Check if we have suitable water container waiting in the item slot
			FluidHelper.drainContainers(tankManager, this, InventoryMoistener.SLOT_PRODUCT);
		}

		// Let's get to work
		int lightvalue = world.getLightFromNeighbors(getPos().up());

		IErrorLogic errorLogic = getErrorLogic();

		// Not working in broad daylight
		boolean gloomy = lightvalue <= 11;
		if (errorLogic.setCondition(!gloomy, EnumErrorCode.NOT_DARK)) {
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
			if (errorLogic.setCondition(!hasLiquid, EnumErrorCode.NO_RESOURCE_LIQUID)) {
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
				decrStackSize(InventoryMoistener.SLOT_RESOURCE, 1);
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
				if (getStackInSlot(InventoryMoistener.SLOT_WORKING).isEmpty()) {
					return;
				}

				if (FuelManager.moistenerResource.containsKey(getStackInSlot(InventoryMoistener.SLOT_WORKING))) {
					MoistenerFuel res = FuelManager.moistenerResource.get(getStackInSlot(InventoryMoistener.SLOT_WORKING));
					burnTime = totalTime = res.getMoistenerValue();
				}
			} else {
				rotateReservoir();
			}
		}

		errorLogic.setCondition(currentRecipe == null, EnumErrorCode.NO_RECIPE);
	}

	private boolean tryAddPending() {
		if (pendingProduct == null) {
			return false;
		}

		boolean added = InventoryUtil.tryAddStack(this, pendingProduct, InventoryMoistener.SLOT_PRODUCT, 1, true);
		getErrorLogic().setCondition(!added, EnumErrorCode.NO_SPACE_INVENTORY);

		if (added) {
			pendingProduct = null;
		}

		return added;
	}

	public void checkRecipe() {
		IMoistenerRecipe sameRec = MoistenerRecipeManager.findMatchingRecipe(getInternalInventory().getStackInSlot(InventoryMoistener.SLOT_RESOURCE));
		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}

		getErrorLogic().setCondition(currentRecipe == null, EnumErrorCode.NO_RECIPE);
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			currentProduct = null;
			productionTime = 0;
			timePerItem = 0;
		} else {
			currentProduct = currentRecipe.getProduct();
			productionTime = currentRecipe.getTimePerItem();
			timePerItem = currentRecipe.getTimePerItem();
		}
	}

	private int getFreeSlot(ItemStack deposit, int startSlot, int endSlot, boolean emptyOnly) {
		int slot = -1;

		for (int i = startSlot; i < endSlot; i++) {
			ItemStack slotStack = getStackInSlot(i);
			// Empty slots are okay.
			if (slotStack.isEmpty()) {
				if (slot < 0) {
					slot = i;
				}
				continue;
			}

			if (emptyOnly) {
				continue;
			}

			// Wrong item or full
			if (!slotStack.isItemEqual(deposit) || slotStack.getCount() >= slotStack.getMaxStackSize()) {
				continue;
			}

			slot = i;
		}

		return slot;
	}

	private int getFreeStashSlot(ItemStack deposit, boolean emptyOnly) {
		return getFreeSlot(deposit, 0, InventoryMoistener.SLOT_RESERVOIR_1, emptyOnly);
	}

	private int getFreeReservoirSlot(ItemStack deposit) {
		return getFreeSlot(deposit, InventoryMoistener.SLOT_RESERVOIR_1, InventoryMoistener.SLOT_RESERVOIR_1 + InventoryMoistener.SLOT_RESERVOIR_COUNT, false);

	}

	private int getNextResourceSlot(int startSlot, int endSlot) {
		// Let's look for a new resource to put into the working slot.
		int stage = -1;
		int resourceSlot = -1;

		IInventoryAdapter inventory = getInternalInventory();
		for (int i = startSlot; i < endSlot; i++) {
			ItemStack slotStack = inventory.getStackInSlot(i);
			if (slotStack.isEmpty()) {
				continue;
			}

			if (!FuelManager.moistenerResource.containsKey(slotStack)) {
				continue;
			}

			MoistenerFuel res = FuelManager.moistenerResource.get(slotStack);
			if (stage < 0 || res.getStage() < stage) {
				stage = res.getStage();
				resourceSlot = i;
			}
		}

		return resourceSlot;
	}

	private boolean rotateWorkingSlot() {
		IErrorLogic errorLogic = getErrorLogic();

		// Put working slot contents into inventory if space is available
		if (!getStackInSlot(InventoryMoistener.SLOT_WORKING).isEmpty()) {
			// Get the result of the consumed item in the working slot
			ItemStack deposit;
			if (FuelManager.moistenerResource.containsKey(getStackInSlot(InventoryMoistener.SLOT_WORKING))) {
				MoistenerFuel res = FuelManager.moistenerResource.get(getStackInSlot(InventoryMoistener.SLOT_WORKING));
				deposit = res.getProduct().copy();
			} else {
				deposit = getStackInSlot(InventoryMoistener.SLOT_WORKING).copy();
			}

			int targetSlot = getFreeReservoirSlot(deposit);
			// We stop the whole thing, if we don't have any room anymore.
			if (errorLogic.setCondition(targetSlot < 0, EnumErrorCode.NO_SPACE_INVENTORY)) {
				return false;
			}

			if (getStackInSlot(targetSlot).isEmpty()) {
				setInventorySlotContents(targetSlot, deposit);
			} else {
				getStackInSlot(targetSlot).grow(1);
			}

			decrStackSize(InventoryMoistener.SLOT_WORKING, 1);
		}

		if (!getStackInSlot(InventoryMoistener.SLOT_WORKING).isEmpty()) {
			return true;
		}

		// Let's look for a new resource to put into the working slot.
		int resourceSlot = getNextResourceSlot(InventoryMoistener.SLOT_RESERVOIR_1, InventoryMoistener.SLOT_RESERVOIR_1 + InventoryMoistener.SLOT_RESERVOIR_COUNT);
		// Nothing found, stop.
		if (errorLogic.setCondition(resourceSlot < 0, EnumErrorCode.NO_RESOURCE)) {
			return false;
		}

		setInventorySlotContents(InventoryMoistener.SLOT_WORKING, decrStackSize(resourceSlot, 1));
		return true;
	}

	private void rotateReservoir() {
		ArrayList<Integer> slotsToShift = new ArrayList<>();

		for (int i = InventoryMoistener.SLOT_RESERVOIR_1; i < InventoryMoistener.SLOT_RESERVOIR_1 + InventoryMoistener.SLOT_RESERVOIR_COUNT; i++) {
			if (getStackInSlot(i).isEmpty()) {
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
			setInventorySlotContents(slot, ItemStack.EMPTY);
			shiftedSlots++;
		}

		// Grab new items from stash
		for (int i = 0; i < (!slotsToShift.isEmpty() ? shiftedSlots : 2); i++) {
			int resourceSlot = getNextResourceSlot(0, InventoryMoistener.SLOT_RESERVOIR_1);
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
			if (getStackInSlot(targetSlot).isEmpty()) {
				setInventorySlotContents(targetSlot, getStackInSlot(resourceSlot));
				setInventorySlotContents(resourceSlot, ItemStack.EMPTY);
			} else {
				ItemStackUtil.mergeStacks(getStackInSlot(resourceSlot), getStackInSlot(targetSlot));
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

		for (int i = InventoryMoistener.SLOT_STASH_1; i < InventoryMoistener.SLOT_RESERVOIR_1; i++) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				max += 64;
				continue;
			}
			if (FuelManager.moistenerResource.containsKey(inventory.getStackInSlot(i))) {
				MoistenerFuel res = FuelManager.moistenerResource.get(inventory.getStackInSlot(i));
				if (res.getItem().isItemEqual(inventory.getStackInSlot(i))) {
					max += 64;
					avail += inventory.getStackInSlot(i).getCount();
				}
			}
		}

		return (float) avail / (float) max > percentage;
	}

	public boolean hasResourcesMin(float percentage) {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(InventoryMoistener.SLOT_RESOURCE).isEmpty()) {
			return false;
		}

		return (float) inventory.getStackInSlot(InventoryMoistener.SLOT_RESOURCE).getCount() / (float) inventory.getStackInSlot(InventoryMoistener.SLOT_RESOURCE).getMaxStackSize() > percentage;
	}

	public boolean isProducing() {
		return productionTime > 0;
	}

	public int getProductionProgressScaled(int i) {
		if (timePerItem == 0) {
			return 0;
		}

		return productionTime * i / timePerItem;

	}

	public int getConsumptionProgressScaled(int i) {
		if (totalTime == 0) {
			return 0;
		}

		return burnTime * i / totalTime;

	}

	public int getResourceScaled(int i) {
		return resourceTank.getFluidAmount() * i / Constants.PROCESSOR_TANK_CAPACITY;
	}

	/* IRenderableTile */
	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return TankRenderInfo.EMPTY;
	}


	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
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

	public void sendGUINetworkData(Container container, IContainerListener iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, burnTime);
		iCrafting.sendProgressBarUpdate(container, 1, totalTime);
		iCrafting.sendProgressBarUpdate(container, 2, productionTime);
		iCrafting.sendProgressBarUpdate(container, 3, timePerItem);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiMoistener(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerMoistener(player.inventory, this);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return super.getCapability(capability, facing);
	}
}
