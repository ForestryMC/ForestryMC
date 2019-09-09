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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.InventoryGhostCrafting;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.items.ICraftingPlan;
import forestry.core.network.PacketBufferForestry;
import forestry.core.recipes.RecipePair;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.factory.ModuleFactory;
import forestry.factory.gui.ContainerFabricator;
import forestry.factory.inventory.InventoryFabricator;
import forestry.factory.recipes.FabricatorRecipeManager;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;

public class TileFabricator extends TilePowered implements ISlotPickupWatcher, ILiquidTankTile, ISidedInventory {
	private static final int MAX_HEAT = 5000;

	private final InventoryAdapterTile craftingInventory;
	private final TankManager tankManager;
	private final FilteredTank moltenTank;
	private int heat = 0;
	private int meltingPoint = 0;

	public TileFabricator() {
		super(ModuleFactory.getTiles().fabricator, 1100, 3300);
		setEnergyPerWorkCycle(200);
		craftingInventory = new InventoryGhostCrafting<>(this, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
		setInternalInventory(new InventoryFabricator(this));

		moltenTank = new FilteredTank(8 * FluidAttributes.BUCKET_VOLUME, false, false).setFilters(FabricatorSmeltingRecipeManager.getRecipeFluids());

		tankManager = new TankManager(this, moltenTank);
	}

	/* SAVING & LOADING */

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound = super.write(compound);

		compound.putInt("Heat", heat);
		tankManager.write(compound);
		craftingInventory.write(compound);
		return compound;
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		heat = compound.getInt("Heat");
		tankManager.read(compound);
		craftingInventory.read(compound);
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

	/* UPDATING */
	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (!moltenTank.isFull()) {
			trySmelting();
		}

		if (!moltenTank.isEmpty()) {
			// Remove smelt if we have gone below melting point
			if (heat < getMeltingPoint() - 100) {
				moltenTank.drain(5, IFluidHandler.FluidAction.EXECUTE);
			}
		}

		if (heat > 2500) {
			this.heat -= 2;
		} else if (heat > 0) {
			this.heat--;
		}
	}

	private void trySmelting() {
		IInventoryAdapter inventory = getInternalInventory();

		ItemStack smeltResource = inventory.getStackInSlot(InventoryFabricator.SLOT_METAL);
		if (smeltResource.isEmpty()) {
			return;
		}

		IFabricatorSmeltingRecipe smelt = FabricatorSmeltingRecipeManager.findMatchingSmelting(smeltResource);
		if (smelt == null || smelt.getMeltingPoint() > heat) {
			return;
		}

		FluidStack smeltFluid = smelt.getProduct();
		if (moltenTank.fillInternal(smeltFluid, IFluidHandler.FluidAction.SIMULATE) == smeltFluid.getAmount()) {
			this.decrStackSize(InventoryFabricator.SLOT_METAL, 1);
			moltenTank.fillInternal(smeltFluid, IFluidHandler.FluidAction.EXECUTE);
			meltingPoint = smelt.getMeltingPoint();
		}
	}

	@Override
	public boolean workCycle() {
		this.heat += 100;
		if (this.heat > MAX_HEAT) {
			this.heat = MAX_HEAT;
		}

		craftResult();

		return true;
	}

	private RecipePair<IFabricatorRecipe> getRecipe() {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack plan = inventory.getStackInSlot(InventoryFabricator.SLOT_PLAN);
		FluidStack liquid = moltenTank.getFluid();
		RecipePair<IFabricatorRecipe> recipePair = FabricatorRecipeManager.findMatchingRecipe(plan, craftingInventory);
		IFabricatorRecipe recipe = recipePair.getRecipe();
		if (!liquid.isEmpty() && recipe != null && !liquid.containsFluid(recipe.getLiquid())) {
			return RecipePair.EMPTY;
		}
		return recipePair;
	}

	public ItemStack getResult(RecipePair<IFabricatorRecipe> myRecipePair) {
		IFabricatorRecipe myRecipe = myRecipePair.getRecipe();
		if (myRecipe == null) {
			return ItemStack.EMPTY;
		}

		return myRecipe.getRecipeOutput().copy();
	}

	/* ISlotPickupWatcher */
	@Override
	public void onTake(int slotIndex, PlayerEntity player) {
		if (slotIndex == InventoryFabricator.SLOT_RESULT) {
			decrStackSize(InventoryFabricator.SLOT_RESULT, 1);
		}
	}

	private void craftResult() {
		RecipePair<IFabricatorRecipe> myRecipePair = getRecipe();
		ItemStack craftResult = getResult(myRecipePair);
		IFabricatorRecipe myRecipe = myRecipePair.getRecipe();
		if (myRecipe != null && !craftResult.isEmpty() && getStackInSlot(InventoryFabricator.SLOT_RESULT).isEmpty()) {
			FluidStack liquid = myRecipe.getLiquid();

			// Remove resources
			NonNullList<ItemStack> crafting = InventoryUtil.getStacks(craftingInventory, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
			if (removeFromInventory(crafting, myRecipePair, false)) {
				FluidStack drained = moltenTank.drainInternal(liquid, IFluidHandler.FluidAction.SIMULATE);
				if (!drained.isEmpty() && drained.isFluidStackIdentical(liquid)) {
					removeFromInventory(crafting, myRecipePair, true);
					moltenTank.drain(liquid.getAmount(), IFluidHandler.FluidAction.EXECUTE);

					// Damage plan
					if (!getStackInSlot(InventoryFabricator.SLOT_PLAN).isEmpty()) {
						Item planItem = getStackInSlot(InventoryFabricator.SLOT_PLAN).getItem();
						if (planItem instanceof ICraftingPlan) {
							ItemStack planUsed = ((ICraftingPlan) planItem).planUsed(getStackInSlot(InventoryFabricator.SLOT_PLAN), craftResult);
							setInventorySlotContents(InventoryFabricator.SLOT_PLAN, planUsed);
						}
					}

					setInventorySlotContents(InventoryFabricator.SLOT_RESULT, craftResult);
				}
			}
		}
	}

	private boolean removeFromInventory(NonNullList<ItemStack> set, RecipePair<IFabricatorRecipe> recipePair, boolean doRemove) {
		IInventory inventory = new InventoryMapper(this, InventoryFabricator.SLOT_INVENTORY_1, InventoryFabricator.SLOT_INVENTORY_COUNT);
		return InventoryUtil.removeSets(inventory, 1, set, recipePair.getOreDictEntries(), null, true, false, doRemove);
	}

	@Override
	public boolean hasWork() {
		boolean hasRecipe = true;
		boolean hasLiquidResources = true;
		boolean hasResources = true;

		ItemStack plan = getStackInSlot(InventoryFabricator.SLOT_PLAN);
		RecipePair<IFabricatorRecipe> recipePair = FabricatorRecipeManager.findMatchingRecipe(plan, craftingInventory);
		if (!recipePair.isEmpty()) {
			IFabricatorRecipe recipe = recipePair.getRecipe();
			if (recipe == null) {
				return false;
			}
			NonNullList<ItemStack> crafting = InventoryUtil.getStacks(craftingInventory, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
			hasResources = removeFromInventory(crafting, recipePair, false);
			FluidStack toDrain = recipe.getLiquid();
			FluidStack drained = moltenTank.drainInternal(toDrain, IFluidHandler.FluidAction.SIMULATE);
			hasLiquidResources = !drained.isEmpty() && drained.isFluidStackIdentical(toDrain);
		} else {
			hasRecipe = false;
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NO_RECIPE);
		errorLogic.setCondition(!hasLiquidResources, EnumErrorCode.NO_RESOURCE_LIQUID);
		errorLogic.setCondition(!hasResources, EnumErrorCode.NO_RESOURCE_INVENTORY);

		return hasRecipe;
	}

	public int getHeatScaled(int i) {
		return heat * i / MAX_HEAT;
	}

	private int getMeltingPoint() {
		if (!this.getStackInSlot(InventoryFabricator.SLOT_METAL).isEmpty()) {
			IFabricatorSmeltingRecipe smelt = FabricatorSmeltingRecipeManager.findMatchingSmelting(this.getStackInSlot(InventoryFabricator.SLOT_METAL));
			if (smelt != null) {
				return smelt.getMeltingPoint();
			}
		} else if (moltenTank.getFluidAmount() > 0) {
			return meltingPoint;
		}

		return 0;
	}

	public int getMeltingPointScaled(int i) {
		int meltingPoint = getMeltingPoint();

		if (meltingPoint <= 0) {
			return 0;
		} else {
			return meltingPoint * i / MAX_HEAT;
		}
	}

	/* SMP */
	public void getGUINetworkData(int i, int j) {
		if (i == 0) {
			heat = j;
		} else if (i == 1) {
			meltingPoint = j;
		}
	}

	public void sendGUINetworkData(Container container, IContainerListener iCrafting) {
		iCrafting.sendWindowProperty(container, 0, heat);
		iCrafting.sendWindowProperty(container, 1, getMeltingPoint());
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public InventoryAdapter getCraftingInventory() {
		return craftingInventory;
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
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerFabricator(windowId, player.inventory, this);
	}
}
