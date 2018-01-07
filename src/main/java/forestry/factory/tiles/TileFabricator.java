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

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
import forestry.factory.gui.ContainerFabricator;
import forestry.factory.gui.GuiFabricator;
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
		super(1100, 3300);
		setEnergyPerWorkCycle(200);
		craftingInventory = new InventoryGhostCrafting<>(this, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
		setInternalInventory(new InventoryFabricator(this));

		moltenTank = new FilteredTank(8 * Fluid.BUCKET_VOLUME, false, false).setFilters(FabricatorSmeltingRecipeManager.getRecipeFluids());

		tankManager = new TankManager(this, moltenTank);
	}

	/* SAVING & LOADING */

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Heat", heat);
		tankManager.writeToNBT(nbttagcompound);
		craftingInventory.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		heat = nbttagcompound.getInteger("Heat");
		tankManager.readFromNBT(nbttagcompound);
		craftingInventory.readFromNBT(nbttagcompound);
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
				moltenTank.drain(5, true);
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
		if (moltenTank.fillInternal(smeltFluid, false) == smeltFluid.amount) {
			this.decrStackSize(InventoryFabricator.SLOT_METAL, 1);
			moltenTank.fillInternal(smeltFluid, true);
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

	@Nullable
	private RecipePair getRecipe() {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack plan = inventory.getStackInSlot(InventoryFabricator.SLOT_PLAN);
		FluidStack liquid = moltenTank.getFluid();
		RecipePair<IFabricatorRecipe> recipePair = FabricatorRecipeManager.findMatchingRecipe(plan, craftingInventory);
		IFabricatorRecipe recipe = recipePair.getRecipe();
		if (liquid != null && recipe != null && !liquid.containsFluid(recipe.getLiquid())) {
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
	public void onTake(int slotIndex, EntityPlayer player) {
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
				FluidStack drained = moltenTank.drainInternal(liquid, false);
				if (drained != null && drained.isFluidStackIdentical(liquid)) {
					removeFromInventory(crafting, myRecipePair, true);
					moltenTank.drain(liquid.amount, true);

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
			NonNullList<ItemStack> crafting = InventoryUtil.getStacks(craftingInventory, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
			hasResources = removeFromInventory(crafting, recipePair, false);
			FluidStack toDrain = recipe.getLiquid();
			FluidStack drained = moltenTank.drainInternal(toDrain, false);
			hasLiquidResources = drained != null && drained.isFluidStackIdentical(toDrain);
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

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiFabricator(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerFabricator(player.inventory, this);
	}
}
