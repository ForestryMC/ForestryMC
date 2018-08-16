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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.gui.ContainerFermenter;
import forestry.factory.gui.GuiFermenter;
import forestry.factory.inventory.InventoryFermenter;
import forestry.factory.recipes.FermenterRecipeManager;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class TileFermenter extends TilePowered implements ISidedInventory, ILiquidTankTile {
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

	public TileFermenter() {
		super(2000, 8000);
		setEnergyPerWorkCycle(4200);
		setInternalInventory(new InventoryFermenter(this));

		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, true, false);
		resourceTank.setFilters(FermenterRecipeManager.recipeFluidInputs);

		productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, false, true);
		productTank.setFilters(FermenterRecipeManager.recipeFluidOutputs);

		tankManager = new TankManager(this, resourceTank, productTank);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("FermentationTime", fermentationTime);
		nbttagcompound.setInteger("FermentationTotalTime", fermentationTotalTime);
		nbttagcompound.setInteger("FuelBurnTime", fuelBurnTime);
		nbttagcompound.setInteger("FuelTotalTime", fuelTotalTime);
		nbttagcompound.setInteger("FuelCurrentFerment", fuelCurrentFerment);

		tankManager.writeToNBT(nbttagcompound);
		return nbttagcompound;
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
		productTank.fillInternal(new FluidStack(currentRecipe.getOutput(), productAmount), true);

		fuelBurnTime--;
		resourceTank.drain(fermented, true);
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

		ItemStack resource = getStackInSlot(InventoryFermenter.SLOT_RESOURCE);
		FluidStack fluid = resourceTank.getFluid();

		if (fluid != null) {
			currentRecipe = FermenterRecipeManager.findMatchingRecipe(resource, fluid);
		}

		fermentationTotalTime = fermentationTime = currentRecipe == null ? 0 : currentRecipe.getFermentationValue();

		if (currentRecipe != null) {
			currentResourceModifier = determineResourceMod(resource);
			decrStackSize(InventoryFermenter.SLOT_RESOURCE, 1);
		}
	}

	private void checkFuel() {
		if (fuelBurnTime <= 0) {
			ItemStack fuel = getStackInSlot(InventoryFermenter.SLOT_FUEL);
			if (!fuel.isEmpty()) {
				FermenterFuel fermenterFuel = FuelManager.fermenterFuel.get(fuel);
				if (fermenterFuel != null) {
					fuelBurnTime = fuelTotalTime = fermenterFuel.getBurnDuration();
					fuelCurrentFerment = fermenterFuel.getFermentPerCycle();

					decrStackSize(InventoryFermenter.SLOT_FUEL, 1);
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
		ItemStack fermentationStack = getStackInSlot(InventoryFermenter.SLOT_RESOURCE);
		if (fermentationStack.isEmpty()) {
			return false;
		}

		return (float) fermentationStack.getCount() / (float) fermentationStack.getMaxStackSize() > percentage;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		ItemStack fuelStack = getStackInSlot(InventoryFermenter.SLOT_FUEL);
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
		boolean hasResource = fermentationTime > 0 || !getStackInSlot(InventoryFermenter.SLOT_RESOURCE).isEmpty();
		FluidStack drained = resourceTank.drain(fermented, false);
		boolean hasFluidResource = drained != null && drained.amount == fermented;
		boolean hasFluidSpace = true;

		if (hasRecipe) {
			int productAmount = Math.round(fermented * currentRecipe.getModifier() * currentResourceModifier);
			Fluid output = currentRecipe.getOutput();
			FluidStack fluidStack = new FluidStack(output, productAmount);
			hasFluidSpace = productTank.fillInternal(fluidStack, false) == fluidStack.amount;
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

	public void sendGUINetworkData(Container container, IContainerListener iCrafting) {
		iCrafting.sendWindowProperty(container, 0, fuelBurnTime);
		iCrafting.sendWindowProperty(container, 1, fuelTotalTime);
		iCrafting.sendWindowProperty(container, 2, fermentationTime);
		iCrafting.sendWindowProperty(container, 3, fermentationTotalTime);
	}


	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	@Override
	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull EnumFacing side, TileEntity tile) {
		super.addExternalTriggers(triggers, side, tile);
		triggers.add(FactoryTriggers.lowResource25);
		triggers.add(FactoryTriggers.lowResource10);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiFermenter(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerFermenter(player.inventory, this);
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
