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
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.Optional;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.gui.ContainerFermenter;
import forestry.factory.gui.GuiFermenter;
import forestry.factory.inventory.InventoryFermenter;
import forestry.factory.recipes.FermenterRecipeManager;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class TileFermenter extends TilePowered implements ISidedInventory, ILiquidTankTile, IFluidHandler {
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
		super("fermenter", 2000, 8000);
		setEnergyPerWorkCycle(4200);
		setInternalInventory(new InventoryFermenter(this));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FermenterRecipeManager.recipeFluidInputs);
		resourceTank.tankMode = StandardTank.TankMode.INPUT;
		productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FermenterRecipeManager.recipeFluidOutputs);
		productTank.tankMode = StandardTank.TankMode.OUTPUT;
		tankManager = new TankManager(this, resourceTank, productTank);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("FermentationTime", fermentationTime);
		nbttagcompound.setInteger("FermentationTotalTime", fermentationTotalTime);
		nbttagcompound.setInteger("FuelBurnTime", fuelBurnTime);
		nbttagcompound.setInteger("FuelTotalTime", fuelTotalTime);
		nbttagcompound.setInteger("FuelCurrentFerment", fuelCurrentFerment);

		tankManager.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		fermentationTime = nbttagcompound.getInteger("FermentationTime");
		fermentationTotalTime = nbttagcompound.getInteger("FermentationTotalTime");
		fuelBurnTime = nbttagcompound.getInteger("FuelBurnTime");
		fuelTotalTime = nbttagcompound.getInteger("FuelTotalTime");
		fuelCurrentFerment = nbttagcompound.getInteger("FuelCurrentFerment");

		tankManager.readFromNBT(nbttagcompound);
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

	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			FluidHelper.drainContainers(tankManager, this, InventoryFermenter.SLOT_INPUT);

			FluidStack fluidStack = productTank.getFluid();
			if (fluidStack != null) {
				FluidHelper.fillContainers(tankManager, this, InventoryFermenter.SLOT_CAN_INPUT, InventoryFermenter.SLOT_CAN_OUTPUT, fluidStack.getFluid());
			}
		}
	}

	@Override
	public boolean workCycle() {
		int fermented = Math.min(fermentationTime, fuelCurrentFerment);
		int productAmount = Math.round(fermented * currentRecipe.getModifier() * currentResourceModifier);
		productTank.fill(new FluidStack(currentRecipe.getOutput(), productAmount), true);

		fuelBurnTime--;
		resourceTank.drain(fuelCurrentFerment, true);
		fermentationTime -= this.fuelCurrentFerment;

		// Not done yet
		if (fermentationTime > 0) {
			return false;
		}

		currentRecipe = null;
		return true;
	}

	private void checkRecipe() {
		if (currentRecipe != null) {
			return;
		}

		ItemStack resource = getStackInSlot(InventoryFermenter.SLOT_RESOURCE);
		FluidStack fluid = resourceTank.getFluid();

		currentRecipe = FermenterRecipeManager.findMatchingRecipe(resource, fluid);

		fermentationTotalTime = fermentationTime = (currentRecipe == null) ? 0 : currentRecipe.getFermentationValue();

		if (currentRecipe != null) {
			currentResourceModifier = determineResourceMod(resource);
			decrStackSize(InventoryFermenter.SLOT_RESOURCE, 1);
		}
	}

	private void checkFuel() {
		if (fuelBurnTime > 0) {
			return;
		}

		ItemStack fuel = getStackInSlot(InventoryFermenter.SLOT_FUEL);
		if (fuel == null) {
			return;
		}

		FermenterFuel fermenterFuel = FuelManager.fermenterFuel.get(fuel);
		if (fermenterFuel == null) {
			return;
		}

		fuelBurnTime = fuelTotalTime = fermenterFuel.burnDuration;
		fuelCurrentFerment = fermenterFuel.fermentPerCycle;

		decrStackSize(InventoryFermenter.SLOT_FUEL, 1);
	}

	private static float determineResourceMod(ItemStack itemstack) {
		if (!(itemstack.getItem() instanceof IVariableFermentable)) {
			return 1.0f;
		}

		return ((IVariableFermentable) itemstack.getItem()).getFermentationModifier(itemstack);
	}


	@Override
	public boolean hasResourcesMin(float percentage) {
		ItemStack fermentationStack = getStackInSlot(InventoryFermenter.SLOT_RESOURCE);
		if (fermentationStack == null) {
			return false;
		}

		return ((float) fermentationStack.stackSize / (float) fermentationStack.getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		ItemStack fuelStack = getStackInSlot(InventoryFermenter.SLOT_FUEL);
		if (fuelStack == null) {
			return false;
		}

		return ((float) fuelStack.stackSize / (float) fuelStack.getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		checkRecipe();
		checkFuel();

		boolean hasRecipe = (currentRecipe != null);
		boolean hasFuel = fuelBurnTime > 0;
		boolean hasResource = fermentationTime > 0 || getStackInSlot(InventoryFermenter.SLOT_RESOURCE) != null;
		boolean hasFluidResource = resourceTank.canDrain(fuelCurrentFerment);
		boolean hasFluidSpace = true;

		if (hasRecipe) {
			int fermented = Math.min(fermentationTime, fuelCurrentFerment);
			int productAmount = Math.round(fermented * currentRecipe.getModifier() * currentResourceModifier);
			hasFluidSpace = productTank.canFill(currentRecipe.getOutput(), productAmount);
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NO_RECIPE);
		errorLogic.setCondition(!hasFuel, EnumErrorCode.NO_FUEL);
		errorLogic.setCondition(!hasResource, EnumErrorCode.NO_RESOURCE);
		errorLogic.setCondition(!hasFluidResource, EnumErrorCode.NO_RESOURCE_LIQUID);
		errorLogic.setCondition(!hasFluidSpace, EnumErrorCode.NO_SPACE_TANK);

		return hasRecipe && hasFuel && hasResource && hasFluidResource && hasFluidSpace;
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

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return new TankRenderInfo(productTank);
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
			case 0:
				fuelBurnTime = j;
				break;
			case 1:
				fuelTotalTime = j;
				break;
			case 2:
				fermentationTime = j;
				break;
			case 3:
				fermentationTotalTime = j;
				break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, fuelBurnTime);
		iCrafting.sendProgressBarUpdate(container, 1, fuelTotalTime);
		iCrafting.sendProgressBarUpdate(container, 2, fermentationTime);
		iCrafting.sendProgressBarUpdate(container, 3, fermentationTotalTime);
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
		return tankManager.drain(from, quantityMax, doEmpty);
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

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiFermenter(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerFermenter(player.inventory, this);
	}
}
