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

import forestry.api.core.ForestryAPI;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.items.ICraftingPlan;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.tiles.ICrafter;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.SlotUtil;
import forestry.factory.recipes.FabricatorRecipeManager;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;

public class TileFabricator extends TilePowered implements ICrafter, ILiquidTankTile, ISidedInventory {

	/* CONSTANTS */
	private static final int MAX_HEAT = 5000;

	public static final short SLOT_METAL = 0;
	public static final short SLOT_PLAN = 1;
	public static final short SLOT_RESULT = 2;
	// FIXME 1.8: change indexes and use correct SLOT_COUNT of 21
	// left this way for now to avoid losing items in existing fabricators
	public static final short SLOT_CRAFTING_LEGACY_1 = 3;
	public static final short SLOT_CRAFTING_LEGACY_COUNT = 9;
	public static final short SLOT_INVENTORY_1 = 12;
	public static final short SLOT_INVENTORY_COUNT = 18;
	public static final short SLOT_COUNT = 30;

	public static final short SLOT_CRAFTING_1 = 0;
	public static final short SLOT_CRAFTING_COUNT = 9;

	/* MEMBER */
	private final TileInventoryAdapter craftingInventory;
	private final TankManager tankManager;
	private final FilteredTank moltenTank;
	private int heat = 0;
	private int guiMeltingPoint = 0;

	public TileFabricator() {
		super(1100, 3300, 200);
		craftingInventory = new TileInventoryAdapter<>(this, SLOT_CRAFTING_COUNT, "CraftItems");
		setInternalInventory(new FabricatorInventoryAdapter(this));
		moltenTank = new FilteredTank(2 * Constants.BUCKET_VOLUME, Fluids.GLASS.getFluid());
		moltenTank.tankMode = StandardTank.TankMode.INTERNAL;
		tankManager = new TankManager(moltenTank);
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.FabricatorGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Heat", heat);

		// Tank
		tankManager.writeTanksToNBT(nbttagcompound);

		craftingInventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		heat = nbttagcompound.getInteger("Heat");

		// Tank
		tankManager.readTanksFromNBT(nbttagcompound);

		craftingInventory.readFromNBT(nbttagcompound);

		// FIXME 1.8: wont need this
		// move items from legacy crafting area to the new one
		IInventory inventory = getInternalInventory();
		for (int slot = SLOT_CRAFTING_LEGACY_1; slot < SLOT_CRAFTING_LEGACY_1 + SLOT_CRAFTING_LEGACY_COUNT; slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack != null) {
				inventory.setInventorySlotContents(slot, null);

				int newSlot = slot - SLOT_CRAFTING_LEGACY_1;
				craftingInventory.setInventorySlotContents(newSlot, stack);
			}
		}
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

	/* UPDATING */
	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (!moltenTank.isFull()) {
			trySmelting();
		}

		if (!moltenTank.isEmpty()) {
			// Remove smelt if we have gone below melting point
			IFabricatorSmeltingRecipe smelt = FabricatorSmeltingRecipeManager.findMatchingSmelting(moltenTank.getFluid());
			if (smelt != null && heat < smelt.getMeltingPoint()) {
				moltenTank.drain(5, true);
			}
		}

		this.dissipateHeat();
	}

	private void trySmelting() {
		IInventoryAdapter inventory = getInternalInventory();

		ItemStack smeltResource = inventory.getStackInSlot(SLOT_METAL);
		if (smeltResource == null) {
			return;
		}

		IFabricatorSmeltingRecipe smelt = FabricatorSmeltingRecipeManager.findMatchingSmelting(smeltResource);
		if (smelt == null || smelt.getMeltingPoint() > heat) {
			return;
		}

		FluidStack smeltFluid = smelt.getProduct();
		if (moltenTank.fill(smeltFluid, false) == smeltFluid.amount) {
			this.decrStackSize(SLOT_METAL, 1);
			moltenTank.fill(smeltFluid, true);
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
		ItemStack plan = inventory.getStackInSlot(SLOT_PLAN);
		ItemStack[] crafting = InventoryUtil.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		return FabricatorRecipeManager.findMatchingRecipe(plan, moltenTank.getFluid(), crafting);
	}

	/* ICRAFTER */
	@Override
	public boolean canTakeStack(int slotIndex) {
		return true;
	}

	@Override
	public ItemStack getResult() {
		IFabricatorRecipe myRecipe = getRecipe();

		if (myRecipe == null) {
			return null;
		}

		return myRecipe.getCraftingResult(craftingInventory);
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, EntityPlayer player) {
		if (slotIndex != SLOT_RESULT) {
			return null;
		}

		return getInternalInventory().decrStackSize(SLOT_RESULT, 1);
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

		if (inventory.getStackInSlot(SLOT_RESULT) != null) {
			return;
		}

		FluidStack liquid = myRecipe.getLiquid();

		// Remove resources
		ItemStack[] crafting = InventoryUtil.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		if (!removeFromInventory(crafting, player, false)) {
			return;
		}

		FluidStack canDrain = moltenTank.drain(liquid.amount, false);
		if (canDrain == null || !canDrain.isFluidStackIdentical(liquid)) {
			return;
		}

		removeFromInventory(crafting, player, true);
		moltenTank.drain(liquid.amount, true);

		// Damage plan
		if (inventory.getStackInSlot(SLOT_PLAN) != null) {
			Item planItem = inventory.getStackInSlot(SLOT_PLAN).getItem();
			if (planItem instanceof ICraftingPlan) {
				inventory.setInventorySlotContents(SLOT_PLAN, ((ICraftingPlan) planItem).planUsed(inventory.getStackInSlot(SLOT_PLAN), result));
			}
		}

		inventory.setInventorySlotContents(SLOT_RESULT, result);
	}

	private boolean removeFromInventory(ItemStack[] set, EntityPlayer player, boolean doRemove) {
		IInventoryAdapter inventory = getInternalInventory();
		if (doRemove) {
			return InventoryUtil.removeSets(inventory, 1, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true);
		} else {
			ItemStack[] stock = InventoryUtil.getStacks(inventory, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
			return ItemStackUtil.containsSets(set, stock) >= 1;
		}
	}

	@Override
	public boolean hasWork() {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack itemToMelt = inventory.getStackInSlot(SLOT_METAL);
		IFabricatorSmeltingRecipe smelting = FabricatorSmeltingRecipeManager.findMatchingSmelting(itemToMelt);
		if (smelting != null && moltenTank.fill(smelting.getProduct(), false) > 0) {
			return true;
		}

		ItemStack plan = inventory.getStackInSlot(SLOT_PLAN);
		ItemStack[] resources = InventoryUtil.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);

		return FabricatorRecipeManager.findMatchingRecipe(plan, moltenTank.getFluid(), resources) != null;
	}

	public int getHeatScaled(int i) {
		return (heat * i) / MAX_HEAT;
	}

	private int getMeltingPoint() {
		if (moltenTank.getFluidAmount() > 0) {
			IFabricatorSmeltingRecipe smelt = FabricatorSmeltingRecipeManager.findMatchingSmelting(moltenTank.getFluid());
			if (smelt != null) {
				return smelt.getMeltingPoint();
			}
		} else if (this.getStackInSlot(SLOT_METAL) != null) {
			IFabricatorSmeltingRecipe smelt = FabricatorSmeltingRecipeManager.findMatchingSmelting(this.getStackInSlot(SLOT_METAL));
			if (smelt != null) {
				return smelt.getMeltingPoint();
			}
		}

		return 0;
	}

	public int getMeltingPointScaled(int i) {
		// / For SMP clients
		if (guiMeltingPoint > 0) {
			return (guiMeltingPoint * i) / MAX_HEAT;
		}

		int meltingPoint = getMeltingPoint();

		if (meltingPoint <= 0) {
			return 0;
		} else {
			return (meltingPoint * i) / MAX_HEAT;
		}
	}

	/* SMP */
	@Override
	public void getGUINetworkData(int i, int j) {
		int messageId = tankManager.maxMessageId() + 1;

		if (i == messageId) {
			heat = j;
		} else if (i == messageId + 1) {
			guiMeltingPoint = j;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int messageId = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, messageId, heat);
		iCrafting.sendProgressBarUpdate(container, messageId + 1, getMeltingPoint());
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

	private static class FabricatorInventoryAdapter extends TileInventoryAdapter<TileFabricator> {
		public FabricatorInventoryAdapter(TileFabricator fabricator) {
			super(fabricator, TileFabricator.SLOT_COUNT, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_METAL) {
				return FabricatorSmeltingRecipeManager.findMatchingSmelting(itemStack) != null;
			} else if (slotIndex == SLOT_PLAN) {
				return FabricatorRecipeManager.isPlan(itemStack);
			} else if (SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT)) {
				if (FabricatorRecipeManager.isPlan(itemStack)) {
					return false;
				} else if (FabricatorSmeltingRecipeManager.findMatchingSmelting(itemStack) != null) {
					return false;
				}
			}
			return SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
			return slotIndex == SLOT_RESULT;
		}
	}

}
