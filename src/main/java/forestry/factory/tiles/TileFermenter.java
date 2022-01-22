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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.features.FactoryTiles;
import forestry.factory.gui.ContainerFermenter;
import forestry.factory.inventory.InventoryFermenter;

public class TileFermenter extends TilePowered implements WorldlyContainer, ILiquidTankTile {
	private final FilteredTank resourceTank;
	private final FilteredTank productTank;
	private final TankManager tankManager;

	@Nullable
	private IFermenterRecipe currentRecipe;
	private float currentResourceModifier;
	private int fermentationTime = 0;
	private int fermentationTotalTime = 0;
	private int fuelBurnTime = 0;
	private int fuelTotalTime = 0;
	private int fuelCurrentFerment = 0;

	public TileFermenter(BlockPos pos, BlockState state) {
		super(FactoryTiles.FERMENTER.tileType(), pos, state, 2000, 8000);
		setEnergyPerWorkCycle(4200);
		setInternalInventory(new InventoryFermenter(this));

		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, true, true);
		resourceTank.setFilters(() -> RecipeManagers.fermenterManager.getRecipeFluidInputs(level.getRecipeManager()));

		productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, false, true);
		resourceTank.setFilters(() -> RecipeManagers.fermenterManager.getRecipeFluidOutputs(level.getRecipeManager()));

		tankManager = new TankManager(this, resourceTank, productTank);
	}

	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);

		compoundNBT.putInt("FermentationTime", fermentationTime);
		compoundNBT.putInt("FermentationTotalTime", fermentationTotalTime);
		compoundNBT.putInt("FuelBurnTime", fuelBurnTime);
		compoundNBT.putInt("FuelTotalTime", fuelTotalTime);
		compoundNBT.putInt("FuelCurrentFerment", fuelCurrentFerment);

		tankManager.write(compoundNBT);
	}

	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);

		fermentationTime = compoundNBT.getInt("FermentationTime");
		fermentationTotalTime = compoundNBT.getInt("FermentationTotalTime");
		fuelBurnTime = compoundNBT.getInt("FuelBurnTime");
		fuelTotalTime = compoundNBT.getInt("FuelTotalTime");
		fuelCurrentFerment = compoundNBT.getInt("FuelCurrentFerment");

		tankManager.read(compoundNBT);
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
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
				FluidHelper.fillContainers(tankManager, this, InventoryFermenter.SLOT_CAN_INPUT, InventoryFermenter.SLOT_CAN_OUTPUT, fluidStack.getFluid(), true);
			}
		}
	}

	@Override
	public boolean workCycle() {
		if (currentRecipe == null) {
			return false;
		}

		int fermented = Math.min(fermentationTime, fuelCurrentFerment);
		int productAmount = Math.round(fermented * currentRecipe.getModifier() * currentResourceModifier);
		productTank.fillInternal(new FluidStack(currentRecipe.getOutput(), productAmount), IFluidHandler.FluidAction.EXECUTE);

		fuelBurnTime--;
		resourceTank.drain(fermented, IFluidHandler.FluidAction.EXECUTE);
		fermentationTime -= fermented;

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

		ItemStack resource = getItem(InventoryFermenter.SLOT_RESOURCE);
		FluidStack fluid = resourceTank.getFluid();

		if (!fluid.isEmpty()) {
			currentRecipe = RecipeManagers.fermenterManager.findMatchingRecipe(getLevel().getRecipeManager(), resource, fluid);
		}

		fermentationTotalTime = fermentationTime = currentRecipe == null ? 0 : currentRecipe.getFermentationValue();

		if (currentRecipe != null) {
			currentResourceModifier = determineResourceMod(resource);
			removeItem(InventoryFermenter.SLOT_RESOURCE, 1);
		}
	}

	private void checkFuel() {
		if (fuelBurnTime <= 0) {
			ItemStack fuel = getItem(InventoryFermenter.SLOT_FUEL);
			if (!fuel.isEmpty()) {
				FermenterFuel fermenterFuel = FuelManager.fermenterFuel.get(fuel);
				if (fermenterFuel != null) {
					fuelBurnTime = fuelTotalTime = fermenterFuel.getBurnDuration();
					fuelCurrentFerment = fermenterFuel.getFermentPerCycle();

					removeItem(InventoryFermenter.SLOT_FUEL, 1);
				}
			}
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
		ItemStack fermentationStack = getItem(InventoryFermenter.SLOT_RESOURCE);
		if (fermentationStack.isEmpty()) {
			return false;
		}

		return (float) fermentationStack.getCount() / (float) fermentationStack.getMaxStackSize() > percentage;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		ItemStack fuelStack = getItem(InventoryFermenter.SLOT_FUEL);
		if (fuelStack.isEmpty()) {
			return false;
		}

		return (float) fuelStack.getCount() / (float) fuelStack.getMaxStackSize() > percentage;
	}

	@Override
	public boolean hasWork() {
		checkRecipe();
		checkFuel();

		int fermented = Math.min(fermentationTime, fuelCurrentFerment);

		boolean hasRecipe = currentRecipe != null;
		boolean hasFuel = fuelBurnTime > 0;
		boolean hasResource = fermentationTime > 0 || !getItem(InventoryFermenter.SLOT_RESOURCE).isEmpty();
		FluidStack drained = resourceTank.drain(fermented, IFluidHandler.FluidAction.SIMULATE);
		boolean hasFluidResource = !drained.isEmpty() && drained.getAmount() == fermented;
		boolean hasFluidSpace = true;

		if (hasRecipe) {
			int productAmount = Math.round(fermented * currentRecipe.getModifier() * currentResourceModifier);
			Fluid output = currentRecipe.getOutput();
			FluidStack fluidStack = new FluidStack(output, productAmount);
			hasFluidSpace = productTank.fillInternal(fluidStack, IFluidHandler.FluidAction.SIMULATE) == fluidStack.getAmount();
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

		return fuelBurnTime * i / fuelTotalTime;
	}

	public int getFermentationProgressScaled(int i) {
		if (fermentationTotalTime == 0) {
			return 0;
		}

		return fermentationTime * i / fermentationTotalTime;
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
			case 0 -> fuelBurnTime = j;
			case 1 -> fuelTotalTime = j;
			case 2 -> fermentationTime = j;
			case 3 -> fermentationTotalTime = j;
		}
	}

	public void sendGUINetworkData(AbstractContainerMenu container, ContainerListener iCrafting) {
		iCrafting.dataChanged(container, 0, fuelBurnTime);
		iCrafting.dataChanged(container, 1, fuelTotalTime);
		iCrafting.dataChanged(container, 2, fermentationTime);
		iCrafting.dataChanged(container, 3, fermentationTotalTime);
	}


	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerFermenter(windowId, inv, this);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> tankManager).cast();
		}
		return super.getCapability(capability, facing);
	}
}
