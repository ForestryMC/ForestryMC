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
package forestry.apiculture.multiblock;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.climate.IClimateControlled;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlvearyHygroregulator;
import forestry.apiculture.inventory.InventoryHygroregulator;
import forestry.core.config.Constants;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.ILiquidTankTile;

public class TileAlvearyHygroregulator extends TileAlveary implements Container, ILiquidTankTile, IAlvearyComponent.Climatiser {
	private final TankManager tankManager;
	private final FilteredTank liquidTank;
	private final IInventoryAdapter inventory;

	@Nullable
	private IHygroregulatorRecipe currentRecipe;
	private int transferTime;

	public TileAlvearyHygroregulator(BlockPos pos, BlockState state) {
		super(BlockAlvearyType.HYGRO, pos, state);

		this.inventory = new InventoryHygroregulator(this);

		this.liquidTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(() -> RecipeManagers.hygroregulatorManager.getRecipeFluids(level.getRecipeManager()));

		this.tankManager = new TankManager(this, liquidTank);
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	/* UPDATING */
	@Override
	public void changeClimate(int tickCount, IClimateControlled climateControlled) {
		if (transferTime <= 0) {
			FluidStack fluid = liquidTank.getFluid();
			if (!fluid.isEmpty()) {
				currentRecipe = RecipeManagers.hygroregulatorManager.findMatchingRecipe(level.getRecipeManager(), fluid);

				if (currentRecipe != null) {
					liquidTank.drainInternal(currentRecipe.getResource().getAmount(), IFluidHandler.FluidAction.EXECUTE);
					transferTime = currentRecipe.getTransferTime();
				}
			}
		}

		if (transferTime > 0) {

			transferTime--;
			if (currentRecipe != null) {
				climateControlled.addHumidityChange(currentRecipe.getHumidChange(), 0.0f, 1.0f);
				climateControlled.addTemperatureChange(currentRecipe.getTempChange(), 0.0f, 2.0f);
			} else {
				transferTime = 0;
			}
		}

		if (tickCount % 20 == 0) {
			// Check if we have suitable items waiting in the item slot
			FluidHelper.drainContainers(tankManager, this, 0);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);
		tankManager.read(compoundNBT);

		transferTime = compoundNBT.getInt("TransferTime");

		if (compoundNBT.contains("CurrentLiquid")) {
			FluidStack liquid = FluidStack.loadFluidStackFromNBT(compoundNBT.getCompound("CurrentLiquid"));
			currentRecipe = RecipeManagers.hygroregulatorManager.findMatchingRecipe(level.getRecipeManager(), liquid);
		}
	}


	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);
		tankManager.write(compoundNBT);

		compoundNBT.putInt("TransferTime", transferTime);
		if (currentRecipe != null) {
			CompoundTag subcompound = new CompoundTag();
			currentRecipe.getResource().writeToNBT(subcompound);
			compoundNBT.put("CurrentLiquid", subcompound);
		}
	}

	/* ILIQUIDTANKCONTAINER */

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		LazyOptional<T> superCap = super.getCapability(capability, facing);
		if (superCap.isPresent()) {
			return superCap;
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> tankManager).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerAlvearyHygroregulator(windowId, inv, this);
	}
}
