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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.IItemStackDisplay;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.SlotUtil;
import forestry.factory.recipes.CarpenterRecipeManager;

public class TileCarpenter extends TilePowered implements ISidedInventory, ILiquidTankTile, IItemStackDisplay {

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
	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final TileInventoryAdapter craftingInventory;
	private final InventoryCraftResult craftPreviewInventory;

	@Nullable
	private ICarpenterRecipe currentRecipe;
	private int packageTime;
	private int totalTime;
	private ItemStack pendingProduct;

	private ItemStack getBoxStack() {
		return getInternalInventory().getStackInSlot(SLOT_BOX);
	}

	public TileCarpenter() {
		super(1100, 4000, 200);
		setHints(Config.hints.get("carpenter"));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, CarpenterRecipeManager.recipeFluids);

		craftingInventory = new TileInventoryAdapter<>(this, 10, "CraftItems");
		craftPreviewInventory = new InventoryCraftResult();
		setInternalInventory(new CarpenterInventoryAdapter(this));

		tankManager = new TankManager(this, resourceTank);
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.CarpenterGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("PackageTime", packageTime);
		nbttagcompound.setInteger("PackageTotalTime", totalTime);

		tankManager.writeToNBT(nbttagcompound);

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

		tankManager.readFromNBT(nbttagcompound);

		craftingInventory.readFromNBT(nbttagcompound);

		// Load pending product
		if (nbttagcompound.hasKey("PendingProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("PendingProduct");
			pendingProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}

		// Reset recipe according to contents
		setCurrentRecipe(CarpenterRecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftingInventory, worldObj));
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
	}

	public void resetRecipe() {
		if (worldObj.isRemote) {
			return;
		}
		setCurrentRecipe(CarpenterRecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftingInventory, getWorldObj()));
	}

	private void setCurrentRecipe(@Nullable ICarpenterRecipe currentRecipe) {
		this.currentRecipe = currentRecipe;

		final ItemStack craftingResult;

		if (currentRecipe != null) {
			craftingResult = currentRecipe.getCraftingGridRecipe().getRecipeOutput();
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
			ICarpenterRecipe recipe = CarpenterRecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftingInventory, worldObj);
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

				pendingProduct = currentRecipe.getCraftingGridRecipe().getRecipeOutput();
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
			packageTime = totalTime = currentRecipe.getPackagingTime();

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
		FluidStack fluid = currentRecipe.getFluidResource();
		if (fluid != null) {
			if (resourceTank.getFluidAmount() < fluid.amount) {
				return false;
			}
		}

		IInventoryAdapter accessibleInventory = getInternalInventory();
		// Check whether boxes are available
		if (currentRecipe.getBox() != null) {
			if (accessibleInventory.getStackInSlot(SLOT_BOX) == null) {
				return false;
			}
		}

		// Need at least one matched set
		ItemStack[] set = InventoryUtil.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		ItemStack[] stock = InventoryUtil.getStacks(accessibleInventory, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);

		return ItemStackUtil.containsSets(set, stock, true, false) > 0;
	}

	private boolean removeResources(ICarpenterRecipe recipe) {

		// Remove resources
		FluidStack fluid = recipe.getFluidResource();
		if (fluid != null) {
			FluidStack amountDrained = tankManager.drain(fluid, false);
			if (amountDrained != null && amountDrained.amount == fluid.amount) {
				tankManager.drain(fluid, true);
			} else {
				return false;
			}
		}
		// Remove boxes
		if (recipe.getBox() != null) {
			ItemStack removed = getInternalInventory().decrStackSize(SLOT_BOX, 1);
			if (removed == null || removed.stackSize == 0) {
				return false;
			}
		}
		return removeSets(1, InventoryUtil.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT));
	}

	private boolean removeSets(int count, ItemStack[] set) {
		EntityPlayer player = PlayerUtil.getPlayer(worldObj, getAccessHandler().getOwner());
		return InventoryUtil.removeSets(getInternalInventory(), count, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true);
	}

	private boolean tryAddPending() {
		if (pendingProduct == null) {
			return false;
		}

		boolean added = InventoryUtil.tryAddStack(this, pendingProduct, SLOT_PRODUCT, SLOT_PRODUCT_COUNT, true);

		if (added) {
			pendingProduct = null;
		}

		getErrorLogic().setCondition(!added, EnumErrorCode.NOSPACE);
		return added;
	}

	/* STATE INFORMATION */
	@Override
	public boolean hasWork() {
		if (currentRecipe == null) {
			return false;
		}

		// Stop working if the output slot cannot take more
		ItemStack product = getStackInSlot(SLOT_PRODUCT);
		if (product != null && product.getMaxStackSize() - product.stackSize < currentRecipe.getCraftingGridRecipe().getRecipeOutput().stackSize) {
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
		return (resourceTank.getFluidAmount() * i) / Constants.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
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

	private static class CarpenterInventoryAdapter extends TileInventoryAdapter<TileCarpenter> {
		public CarpenterInventoryAdapter(TileCarpenter carpenter) {
			super(carpenter, 30, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_CAN_INPUT) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.tankManager.accepts(fluid);
			} else if (slotIndex == SLOT_BOX) {
				return CarpenterRecipeManager.isBox(itemStack);
			} else if (canSlotAccept(SLOT_CAN_INPUT, itemStack) || canSlotAccept(SLOT_BOX, itemStack)) {
				return false;
			}

			return SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex == SLOT_PRODUCT;
		}
	}

}
