/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.gadgets;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import buildcraft.api.gates.ITrigger;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.ForestryAPI;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.plugins.PluginApiculture;

public class TileApiary extends TileBeehouse implements ISidedInventory {

	@Override
	public String getInventoryName() {
		return "apiculture.0";
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.ApiaryGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* IBEEHOUSING */
	@Override
	public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getTerritoryModifier(genome, mod);
		}
		return mod;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome, float currentModifier) {
		float mod = 0.1f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getProductionModifier(genome, mod);
		}
		return mod;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getMutationModifier(genome, mate, mod);
		}
		return mod;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getLifespanModifier(genome, mate, mod);
		}
		return mod;
	}

	@Override
	public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
		float mod = 1f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getFloweringModifier(genome, mod);
		}
		return mod;
	}

	@Override
	public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
		float mod = 1f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getGeneticDecay(genome, mod);
		}
		return mod;
	}

	@Override
	public void wearOutEquipment(int amount) {
		int wear = Math.round(amount * PluginApiculture.beeInterface.getBeekeepingMode(worldObj).getWearModifier());

		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (!(inventory.getStackInSlot(i).getItem() instanceof IHiveFrame))
				continue;

			inventory.setInventorySlotContents(
					i,
					((IHiveFrame) inventory.getStackInSlot(i).getItem()).frameUsed(this, inventory.getStackInSlot(i),
							PluginApiculture.beeInterface.getMember(inventory.getStackInSlot(SLOT_QUEEN)), wear));
		}
	}

	/* IINVENTORY */
	@Override
	protected boolean canTakeStackFromSide(int slotIndex, ItemStack itemstack, int side) {
		if (!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		switch (slotIndex) {
		case SLOT_QUEEN:
		case SLOT_DRONE:
		case SLOT_FRAMES_1:
		case SLOT_FRAMES_2:
		case SLOT_FRAMES_3:
			return false;
		default:
			return true;
		}
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {
		if (!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		if (slotIndex == SLOT_QUEEN && PluginApiculture.beeInterface.isMember(itemstack)
				&& !PluginApiculture.beeInterface.isDrone(itemstack))
			return true;

		if (slotIndex == SLOT_DRONE && PluginApiculture.beeInterface.isDrone(itemstack))
			return true;

		return false;
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public ItemStack getStackInSlot(int i) {
		return super.getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		super.setSlotContents(i, itemstack);
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.isUseableByPlayer(player);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return super.hasCustomInventoryName();
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return super.isItemValidForSlot(slotIndex, itemstack);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return super.canInsertItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return super.canExtractItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return super.getAccessibleSlotsFromSide(side);
	}

	/* ISPECIALINVENTORY */
	//	@Override
	//	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
	//		ItemStack product = null;
	//
	//		for (int i = SLOT_PRODUCT_1; i < SLOT_PRODUCT_1 + SLOT_PRODUCT_COUNT; i++) {
	//			if (inventory.getStackInSlot(i) == null)
	//				continue;
	//
	//			// Princesses can only be extracted from top.
	//			if (ForestryItem.beePrincessGE.isItemEqual(inventory.getStackInSlot(i))) {
	//				if (PluginApiculture.apiarySideSensitive && from != ForgeDirection.UP)
	//					continue;
	//
	//				product = inventory.getStackInSlot(i).copy();
	//				if (doRemove) {
	//					inventory.getStackInSlot(i).stackSize = 0;
	//					inventory.setInventorySlotContents(i, null);
	//				}
	//				break;
	//
	//				// Drones can only be extracted from the bottom.
	//			} else if (ForestryItem.beeDroneGE.isItemEqual(inventory.getStackInSlot(i))) {
	//				if (PluginApiculture.apiarySideSensitive && from != ForgeDirection.DOWN)
	//					continue;
	//
	//				product = StackUtils.createSplitStack(inventory.getStackInSlot(i), 1);
	//				product.stackSize = 1;
	//				if (doRemove) {
	//					inventory.getStackInSlot(i).stackSize--;
	//					if (inventory.getStackInSlot(i).stackSize <= 0)
	//						inventory.setInventorySlotContents(i, null);
	//				}
	//				break;
	//
	//				// Everything else to be extracted from the sides
	//			} else {
	//				if (PluginApiculture.apiarySideSensitive && (from == ForgeDirection.UP || from == ForgeDirection.DOWN))
	//					continue;
	//
	//				product = StackUtils.createSplitStack(inventory.getStackInSlot(i), 1);
	//				if (doRemove) {
	//					inventory.getStackInSlot(i).stackSize--;
	//					if (inventory.getStackInSlot(i).stackSize <= 0)
	//						inventory.setInventorySlotContents(i, null);
	//				}
	//				break;
	//			}
	//		}
	//
	//		return new ItemStack[] { product };
	//	}
	//
	//	@Override
	//	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
	//		// Princesses && Queens
	//		if (ForestryItem.beePrincessGE.isItemEqual(stack) || ForestryItem.beeQueenGE.isItemEqual(stack))
	//			if (inventory.getStackInSlot(SLOT_QUEEN) == null) {
	//				if (doAdd) {
	//					inventory.setInventorySlotContents(SLOT_QUEEN, stack.copy());
	//					inventory.getStackInSlot(SLOT_QUEEN).stackSize = 1;
	//				}
	//
	//				return 1;
	//			}
	//
	//		// Drones
	//		if (ForestryItem.beeDroneGE.isItemEqual(stack)) {
	//
	//			ItemStack droneStack = inventory.getStackInSlot(SLOT_DRONE);
	//			if (droneStack == null) {
	//				if (doAdd)
	//					inventory.setInventorySlotContents(SLOT_DRONE, stack.copy());
	//				return stack.stackSize;
	//			} else {
	//				if (!droneStack.isItemEqual(stack))
	//					return 0;
	//				if (!ItemStack.areItemStackTagsEqual(droneStack, stack))
	//					return 0;
	//				int space = droneStack.getMaxStackSize() - droneStack.stackSize;
	//				if (space <= 0)
	//					return 0;
	//
	//				int added = space > stack.stackSize ? stack.stackSize : space;
	//				if (doAdd)
	//					droneStack.stackSize += added;
	//				return added;
	//			}
	//		}
	//
	//		return 0;
	//	}

	/* ITRIGGERPROVIDER */
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.missingQueen);
		res.add(ForestryTrigger.missingDrone);
		res.add(PluginApiculture.triggerNoFrames);
		return res;
	}
}
