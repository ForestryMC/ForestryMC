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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.items.ICraftingPlan;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.ICrafter;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.factory.inventory.InventoryFabricator;
import forestry.factory.recipes.FabricatorRecipeManager;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;

public class TileFabricator extends TilePowered implements ICrafter, ILiquidTankTile, IFluidHandler, ISidedInventory {
	private static final int MAX_HEAT = 5000;

	private final InventoryAdapterTile craftingInventory;
	private final TankManager tankManager;
	private final FilteredTank moltenTank;
	private int heat = 0;
	private int meltingPoint = 0;

	public TileFabricator() {
		super(GuiId.FabricatorGUI, "fabricator", 1100, 3300);
		setEnergyPerWorkCycle(200);
		craftingInventory = new InventoryAdapterTile<>(this, InventoryFabricator.SLOT_CRAFTING_COUNT, "CraftItems");
		setInternalInventory(new InventoryFabricator(this));
		moltenTank = new FilteredTank(2 * Constants.BUCKET_VOLUME, Fluids.GLASS.getFluid());
		moltenTank.tankMode = StandardTank.TankMode.INTERNAL;
		tankManager = new TankManager(this, moltenTank);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Heat", heat);
		tankManager.writeToNBT(nbttagcompound);
		craftingInventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		heat = nbttagcompound.getInteger("Heat");
		tankManager.readFromNBT(nbttagcompound);
		craftingInventory.readFromNBT(nbttagcompound);

		// FIXME 1.8: wont need this
		// move items from legacy crafting area to the new one
		IInventory inventory = getInternalInventory();
		for (int slot = InventoryFabricator.SLOT_CRAFTING_LEGACY_1; slot < InventoryFabricator.SLOT_CRAFTING_LEGACY_1 + InventoryFabricator.SLOT_CRAFTING_LEGACY_COUNT; slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack != null) {
				inventory.setInventorySlotContents(slot, null);

				int newSlot = slot - InventoryFabricator.SLOT_CRAFTING_LEGACY_1;
				craftingInventory.setInventorySlotContents(newSlot, stack);
			}
		}
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

	/* UPDATING */
	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (!moltenTank.isFull()) {
			trySmelting();
		}

		if (!moltenTank.isEmpty()) {
			// Remove smelt if we have gone below melting point
			if (heat < (getMeltingPoint() - 100)) {
				moltenTank.drain(5, true);
			}
		}

		this.dissipateHeat();
	}

	private void trySmelting() {
		IInventoryAdapter inventory = getInternalInventory();

		ItemStack smeltResource = inventory.getStackInSlot(InventoryFabricator.SLOT_METAL);
		if (smeltResource == null) {
			return;
		}

		IFabricatorSmeltingRecipe smelt = FabricatorSmeltingRecipeManager.findMatchingSmelting(smeltResource);
		if (smelt == null || smelt.getMeltingPoint() > heat) {
			return;
		}

		FluidStack smeltFluid = smelt.getProduct();
		if (moltenTank.canFill(smeltFluid)) {
			this.decrStackSize(InventoryFabricator.SLOT_METAL, 1);
			moltenTank.fill(smeltFluid, true);
			meltingPoint = smelt.getMeltingPoint();
		}
	}

	@Override
	public boolean workCycle() {
		craftResult(null);
		return addHeat(100);
	}

	private boolean addHeat(int addition) {
		if (this.heat >= MAX_HEAT) {
			return false;
		}

		this.heat += addition;
		if (this.heat > MAX_HEAT) {
			this.heat = MAX_HEAT;
		}

		return true;
	}

	private void dissipateHeat() {
		if (heat > 2500) {
			this.heat -= 2;
		} else if (heat > 0) {
			this.heat--;
		}
	}

	private IFabricatorRecipe getRecipe() {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack plan = inventory.getStackInSlot(InventoryFabricator.SLOT_PLAN);
		ItemStack[] crafting = InventoryUtil.getStacks(craftingInventory, InventoryFabricator.SLOT_CRAFTING_1, InventoryFabricator.SLOT_CRAFTING_COUNT);
		return FabricatorRecipeManager.findMatchingRecipe(plan, moltenTank.getFluid(), crafting);
	}

	public ItemStack getResult() {
		IFabricatorRecipe myRecipe = getRecipe();

		if (myRecipe == null) {
			return null;
		}

		return RecipeUtil.getCraftingResult(myRecipe.getRecipeOutput(), myRecipe.preservesNbt(), craftingInventory);
	}

	/* ICrafter */
	@Override
	public ItemStack takenFromSlot(int slotIndex, EntityPlayer player) {
		if (slotIndex != InventoryFabricator.SLOT_RESULT) {
			return null;
		}

		return getInternalInventory().decrStackSize(InventoryFabricator.SLOT_RESULT, 1);
	}

	private void craftResult(EntityPlayer player) {
		IFabricatorRecipe myRecipe = getRecipe();
		if (myRecipe == null) {
			return;
		}

		ItemStack result = getResult();
		if (result == null) {
			return;
		}

		IInventoryAdapter inventory = getInternalInventory();

		if (inventory.getStackInSlot(InventoryFabricator.SLOT_RESULT) != null) {
			return;
		}

		FluidStack liquid = myRecipe.getLiquid();

		// Remove resources
		ItemStack[] crafting = InventoryUtil.getStacks(craftingInventory, InventoryFabricator.SLOT_CRAFTING_1, InventoryFabricator.SLOT_CRAFTING_COUNT);
		if (!removeFromInventory(crafting, player, false)) {
			return;
		}

		if (!moltenTank.canDrain(liquid)) {
			return;
		}

		removeFromInventory(crafting, player, true);
		moltenTank.drain(liquid.amount, true);

		// Damage plan
		if (inventory.getStackInSlot(InventoryFabricator.SLOT_PLAN) != null) {
			Item planItem = inventory.getStackInSlot(InventoryFabricator.SLOT_PLAN).getItem();
			if (planItem instanceof ICraftingPlan) {
				inventory.setInventorySlotContents(InventoryFabricator.SLOT_PLAN, ((ICraftingPlan) planItem).planUsed(inventory.getStackInSlot(InventoryFabricator.SLOT_PLAN), result));
			}
		}

		inventory.setInventorySlotContents(InventoryFabricator.SLOT_RESULT, result);
	}

	private boolean removeFromInventory(ItemStack[] set, EntityPlayer player, boolean doRemove) {
		IInventory inventory = new InventoryMapper(this, InventoryFabricator.SLOT_INVENTORY_1, InventoryFabricator.SLOT_INVENTORY_COUNT);
		return InventoryUtil.removeSets(inventory, 1, set, player, true, true, false, doRemove);
	}

	@Override
	public boolean hasWork() {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack itemToMelt = inventory.getStackInSlot(InventoryFabricator.SLOT_METAL);
		IFabricatorSmeltingRecipe smelting = FabricatorSmeltingRecipeManager.findMatchingSmelting(itemToMelt);
		if (smelting != null && moltenTank.fill(smelting.getProduct(), false) > 0) {
			return true;
		}

		ItemStack plan = inventory.getStackInSlot(InventoryFabricator.SLOT_PLAN);
		ItemStack[] resources = InventoryUtil.getStacks(craftingInventory, InventoryFabricator.SLOT_CRAFTING_1, InventoryFabricator.SLOT_CRAFTING_COUNT);

		return FabricatorRecipeManager.findMatchingRecipe(plan, moltenTank.getFluid(), resources) != null;
	}

	public int getHeatScaled(int i) {
		return (heat * i) / MAX_HEAT;
	}

	private int getMeltingPoint() {
		if (this.getStackInSlot(InventoryFabricator.SLOT_METAL) != null) {
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
			return (meltingPoint * i) / MAX_HEAT;
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

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, heat);
		iCrafting.sendProgressBarUpdate(container, 1, getMeltingPoint());
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public InventoryAdapter getCraftingInventory() {
		return craftingInventory;
	}

	/* ILIQUIDCONTAINER */
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
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
}
