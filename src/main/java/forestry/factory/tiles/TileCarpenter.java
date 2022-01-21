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
import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.InventoryGhostCrafting;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.IItemStackDisplay;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.factory.features.FactoryTiles;
import forestry.factory.gui.ContainerCarpenter;
import forestry.factory.inventory.InventoryCarpenter;

public class TileCarpenter extends TilePowered implements WorldlyContainer, ILiquidTankTile, IItemStackDisplay {

	private static final int TICKS_PER_RECIPE_TIME = 1;
	private static final int ENERGY_PER_WORK_CYCLE = 2040;
	private static final int ENERGY_PER_RECIPE_TIME = ENERGY_PER_WORK_CYCLE / 10;

	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final InventoryAdapterTile craftingInventory;
	private final ResultContainer craftPreviewInventory;

	@Nullable
	private ICarpenterRecipe currentRecipe;

	private ItemStack getBoxStack() {
		return getInternalInventory().getItem(InventoryCarpenter.SLOT_BOX);
	}

	public TileCarpenter() {
		super(FactoryTiles.CARPENTER.tileType(), 1100, 4000);
		setEnergyPerWorkCycle(ENERGY_PER_WORK_CYCLE);
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(() -> RecipeManagers.carpenterManager.getRecipeFluids(getLevel().getRecipeManager()));

		craftingInventory = new InventoryGhostCrafting<>(this, 10);
		craftPreviewInventory = new ResultContainer();
		setInternalInventory(new InventoryCarpenter(this));

		tankManager = new TankManager(this, resourceTank);
	}

	/* LOADING & SAVING */

	@Override
	public CompoundTag save(CompoundTag compoundNBT) {
		compoundNBT = super.save(compoundNBT);

		tankManager.write(compoundNBT);
		craftingInventory.write(compoundNBT);
		return compoundNBT;
	}

	@Override
	public void load(BlockState state, CompoundTag compoundNBT) {
		super.load(state, compoundNBT);
		tankManager.read(compoundNBT);
		craftingInventory.read(compoundNBT);
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

	public void checkRecipe() {
		if (level.isClientSide) {
			return;
		}

		//TODO optional could work quite well here
		if (!RecipeManagers.carpenterManager.matches(currentRecipe, resourceTank.getFluid(), getBoxStack(), craftingInventory, level)) {
			Optional<ICarpenterRecipe> optional = RecipeManagers.carpenterManager.findMatchingRecipe(level.getRecipeManager(), resourceTank.getFluid(), getBoxStack(), craftingInventory, level);
			currentRecipe = optional.orElse(null);

			if (optional.isPresent()) {
				int recipeTime = currentRecipe.getPackagingTime();
				setTicksPerWorkCycle(recipeTime * TICKS_PER_RECIPE_TIME);
				setEnergyPerWorkCycle(recipeTime * ENERGY_PER_RECIPE_TIME);

				ItemStack craftingResult = currentRecipe.getResult();
				craftPreviewInventory.setItem(0, craftingResult);
			} else {
				craftPreviewInventory.setItem(0, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			FluidHelper.drainContainers(tankManager, this, InventoryCarpenter.SLOT_CAN_INPUT);
		}
	}

	@Override
	public boolean workCycle() {
		if (!removeLiquidResources(true)) {
			return false;
		}
		if (!removeItemResources(true)) {
			return false;
		}

		if (currentRecipe != null) {
			ItemStack pendingProduct = currentRecipe.getResult();
			InventoryUtil.tryAddStack(this, pendingProduct, InventoryCarpenter.SLOT_PRODUCT, InventoryCarpenter.SLOT_PRODUCT_COUNT, true);
		}
		return true;
	}

	private boolean removeLiquidResources(boolean doRemove) {
		if (currentRecipe == null) {
			return true;
		}

		FluidStack fluid = currentRecipe.getFluidResource();
		if (fluid != null && !fluid.isEmpty()) {
			FluidStack drained = resourceTank.drainInternal(fluid, IFluidHandler.FluidAction.SIMULATE);
			if (!fluid.isFluidStackIdentical(drained)) {
				return false;
			}
			if (doRemove) {
				resourceTank.drainInternal(fluid, IFluidHandler.FluidAction.EXECUTE);
			}
		}

		return true;
	}

	private boolean removeItemResources(boolean doRemove) {
		if (currentRecipe == null) {
			return true;
		}

		if (!currentRecipe.getBox().isEmpty()) {
			ItemStack box = getItem(InventoryCarpenter.SLOT_BOX);
			if (box.isEmpty()) {
				return false;
			}
			if (doRemove) {
				removeItem(InventoryCarpenter.SLOT_BOX, 1);
			}
		}

		Container inventory = new InventoryMapper(getInternalInventory(), InventoryCarpenter.SLOT_INVENTORY_1, InventoryCarpenter.SLOT_INVENTORY_COUNT);
		return InventoryUtil.consumeIngredients(inventory, currentRecipe.getCraftingGridRecipe().getIngredients(), null, true, false, doRemove);
	}

	/* STATE INFORMATION */
	@Override
	public boolean hasWork() {
		if (updateOnInterval(20)) {
			checkRecipe();
		}

		boolean hasRecipe = currentRecipe != null;
		boolean hasLiquidResources = true;
		boolean hasItemResources = true;
		boolean canAdd = true;

		if (hasRecipe) {
			hasLiquidResources = removeLiquidResources(false);
			hasItemResources = removeItemResources(false);

			ItemStack pendingProduct = currentRecipe.getResult();
			canAdd = InventoryUtil.tryAddStack(this, pendingProduct, InventoryCarpenter.SLOT_PRODUCT, InventoryCarpenter.SLOT_PRODUCT_COUNT, true, false);
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NO_RECIPE);
		errorLogic.setCondition(!hasLiquidResources, EnumErrorCode.NO_RESOURCE_LIQUID);
		errorLogic.setCondition(!hasItemResources, EnumErrorCode.NO_RESOURCE_INVENTORY);
		errorLogic.setCondition(!canAdd, EnumErrorCode.NO_SPACE_INVENTORY);

		return hasRecipe && hasItemResources && hasLiquidResources && canAdd;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public Container getCraftingInventory() {
		return craftingInventory;
	}

	public Container getCraftPreviewInventory() {
		return craftPreviewInventory;
	}

	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		craftPreviewInventory.setItem(0, itemStack);
	}


	@Override
	public TankManager getTankManager() {
		return tankManager;
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> tankManager).cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerCarpenter(windowId, player.inventory, this);
	}
}
