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

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.recipes.FermenterRecipeManager;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class TileFermenter extends TilePowered implements ISidedInventory, ILiquidTankTile {

	// / CONSTANTS
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_FUEL = 1;
	public static final short SLOT_CAN_OUTPUT = 2;
	public static final short SLOT_CAN_INPUT = 3;
	public static final short SLOT_INPUT = 4;

	private final FilteredTank resourceTank;
	private final FilteredTank productTank;

	private final TankManager tankManager;

	private IFermenterRecipe currentRecipe;
	private float currentResourceModifier;
	private int fermentationTime = 0;
	private int fermentationTotalTime = 0;
	private int fuelBurnTime = 0;
	private int fuelTotalTime = 0;
	private int fuelCurrentFerment = 0;

	public TileFermenter() {
		super(2000, 8000, 600);
		setInternalInventory(new FermenterInventoryAdapter(this));
		setHints(Config.hints.get("fermenter"));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FermenterRecipeManager.recipeFluidInputs);
		resourceTank.tankMode = StandardTank.TankMode.INPUT;
		productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FermenterRecipeManager.recipeFluidOutputs);
		productTank.tankMode = StandardTank.TankMode.OUTPUT;
		tankManager = new TankManager(this, resourceTank, productTank);
	}

	@Override
	public void openGui(EntityPlayer player) {
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
		super.updateServerSide();

		if (!updateOnInterval(20)) {
			return;
		}

		IInventoryAdapter inventory = getInternalInventory();
		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_INPUT) != null) {
			FluidHelper.drainContainers(tankManager, inventory, SLOT_INPUT);
		}

		// Can/capsule input/output needs to be handled here.
		if (inventory.getStackInSlot(SLOT_CAN_INPUT) != null) {
			FluidStack fluidStack = productTank.getFluid();
			if (fluidStack != null) {
				FluidHelper.fillContainers(tankManager, inventory, SLOT_CAN_INPUT, SLOT_CAN_OUTPUT, fluidStack.getFluid());
			}
		}

		IErrorLogic errorLogic = getErrorLogic();

		boolean hasRecipe = FermenterRecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid()) != null;
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NORECIPE);

		boolean hasResource = resourceTank.getFluidAmount() >= fuelCurrentFerment;
		errorLogic.setCondition(!hasResource, EnumErrorCode.NORESOURCE);

		boolean hasFuel = inventory.getStackInSlot(SLOT_FUEL) != null || fuelBurnTime > 0;
		errorLogic.setCondition(!hasFuel, EnumErrorCode.NOFUEL);
	}

	@Override
	public boolean workCycle() {

		if (currentRecipe == null) {
			checkRecipe();
			resetRecipe();

			IInventoryAdapter inventory = getInternalInventory();
			if (currentRecipe != null) {
				currentResourceModifier = determineResourceMod(inventory.getStackInSlot(SLOT_RESOURCE));
				decrStackSize(SLOT_RESOURCE, 1);
				return true;
			} else {
				return false;
			}

			// If we have burnTime left, just decrease it.
		} else if (fuelBurnTime > 0) {
			if (resourceTank.getFluidAmount() < fuelCurrentFerment) {
				return false;
			}

			// Nothing to do, return
			if (fermentationTime <= 0) {
				return false;
			}

			int fermented = Math.min(fermentationTime, this.fuelCurrentFerment);

			// input are checked, add output if possible
			if (!addProduct(new FluidStack(currentRecipe.getOutput(),
					Math.round(fermented * currentRecipe.getModifier() * currentResourceModifier)))) {
				return false; // the output tank is too full, TODO: check/add error code?
			}

			fuelBurnTime--;
			tankManager.drain(resourceTank.getFluidType(), fuelCurrentFerment, true);
			fermentationTime -= this.fuelCurrentFerment;

			// Not done yet
			if (fermentationTime > 0) {
				return true;
			}

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
		IInventoryAdapter inventory = getInternalInventory();
		IFermenterRecipe sameRec = FermenterRecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid());

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
		}
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			fermentationTime = 0;
			fermentationTotalTime = 0;
			return;
		}

		fermentationTime = currentRecipe.getFermentationValue();
		fermentationTotalTime = currentRecipe.getFermentationValue();
	}

	/**
	 * Returns the burnTime an item of the passed ItemStack provides
	 */
	private static int determineFuelValue(ItemStack item) {
		if (item == null) {
			return 0;
		}

		if (FuelManager.fermenterFuel.containsKey(item)) {
			return FuelManager.fermenterFuel.get(item).burnDuration;
		} else {
			return 0;
		}
	}

	private static int determineFermentPerCycle(ItemStack item) {
		if (item == null) {
			return 0;
		}

		if (FuelManager.fermenterFuel.containsKey(item)) {
			return FuelManager.fermenterFuel.get(item).fermentPerCycle;
		} else {
			return 0;
		}
	}

	private static float determineResourceMod(ItemStack itemstack) {
		if (!(itemstack.getItem() instanceof IVariableFermentable)) {
			return 1.0f;
		}

		return ((IVariableFermentable) itemstack.getItem()).getFermentationModifier(itemstack);
	}


	@Override
	public boolean hasResourcesMin(float percentage) {
		if (this.getFermentationStack() == null) {
			return false;
		}

		return ((float) getFermentationStack().stackSize / (float) getFermentationStack().getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		if (this.getFuelStack() == null) {
			return false;
		}

		return ((float) getFuelStack().stackSize / (float) getFuelStack().getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null && FermenterRecipeManager.findMatchingRecipe(getStackInSlot(SLOT_RESOURCE), resourceTank.getFluid()) == null) {
			return false;
		}

		if (fuelBurnTime <= 0 && determineFuelValue(getFuelStack()) <= 0) {
			return false;
		}

		if (fermentationTime <= 0 && this.getFermentationStack() == null) {
			return false;
		}

		if (resourceTank.getFluidAmount() <= fuelCurrentFerment) {
			return false;
		}

		return productTank.getFluidAmount() < productTank.getCapacity();
	}

	public int getBurnTimeRemainingScaled(int i) {
		if (fuelTotalTime == 0) {
			return 0;
		}

		return (fuelBurnTime * i) / fuelTotalTime;
	}

	public int getFermentationProgressScaled(int i) {
		if (fermentationTotalTime == 0) {
			return 0;
		}

		return (fermentationTime * i) / fermentationTotalTime;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Constants.PROCESSOR_TANK_CAPACITY;
	}

	public int getProductScaled(int i) {
		return (productTank.getFluidAmount() * i) / Constants.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return new TankRenderInfo(productTank);
	}

	private ItemStack getFermentationStack() {
		return getInternalInventory().getStackInSlot(SLOT_RESOURCE);
	}

	private ItemStack getFuelStack() {
		return getInternalInventory().getStackInSlot(SLOT_FUEL);
	}

	/* SMP GUI */
	@Override
	public void getGUINetworkData(int i, int j) {
		int firstMessageId = tankManager.maxMessageId() + 1;

		if (i == firstMessageId) {
			fuelBurnTime = j;
		} else if (i == firstMessageId + 1) {
			fuelTotalTime = j;
		} else if (i == firstMessageId + 2) {
			fermentationTime = j;
		} else if (i == firstMessageId + 3) {
			fermentationTotalTime = j;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int firstMessageId = tankManager.maxMessageId() + 1;

		iCrafting.sendProgressBarUpdate(container, firstMessageId, fuelBurnTime);
		iCrafting.sendProgressBarUpdate(container, firstMessageId + 1, fuelTotalTime);
		iCrafting.sendProgressBarUpdate(container, firstMessageId + 2, fermentationTime);
		iCrafting.sendProgressBarUpdate(container, firstMessageId + 3, fermentationTotalTime);
	}

	/* ILiquidTankTile */
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
		return tankManager.drain(productTank.getFluidType(), quantityMax, doEmpty);
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

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return tankManager.getTankInfo(from);
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<>();
		res.add(FactoryTriggers.lowResource25);
		res.add(FactoryTriggers.lowResource10);
		return res;
	}

	private static class FermenterInventoryAdapter extends TileInventoryAdapter<TileFermenter> {
		public FermenterInventoryAdapter(TileFermenter fermenter) {
			super(fermenter, 5, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_RESOURCE) {
				return FermenterRecipeManager.isResource(itemStack);
			} else if (slotIndex == SLOT_INPUT) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.resourceTank.accepts(fluid);
			} else if (slotIndex == SLOT_CAN_INPUT) {
				return FluidHelper.isEmptyContainer(itemStack);
			} else if (slotIndex == SLOT_FUEL) {
				return FuelManager.fermenterFuel.containsKey(itemStack);
			}
			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex == SLOT_CAN_OUTPUT;
		}
	}
}
