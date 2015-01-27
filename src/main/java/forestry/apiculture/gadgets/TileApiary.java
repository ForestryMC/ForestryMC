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
package forestry.apiculture.gadgets;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.common.Optional;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.ForestryAPI;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.core.gadgets.TileBase;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.plugins.PluginApiculture;

import buildcraft.api.statements.ITriggerExternal;

public class TileApiary extends TileBeehouse {

	public TileApiary() {
		setInternalInventory(new BeehouseInventoryAdapter(this, 12, "Items"));
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.ApiaryGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* IBEEHOUSING */
	@Override
	public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
		IInventoryAdapter inventory = getInternalInventory();
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getTerritoryModifier(genome, mod);
			}
		}
		return mod;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome, float currentModifier) {
		IInventoryAdapter inventory = getInternalInventory();
		float mod = 0.1f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getProductionModifier(genome, mod);
			}
		}
		return mod;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		IInventoryAdapter inventory = getInternalInventory();
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getMutationModifier(genome, mate, mod);
			}
		}
		return mod;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		IInventoryAdapter inventory = getInternalInventory();
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getLifespanModifier(genome, mate, mod);
			}
		}
		return mod;
	}

	@Override
	public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
		IInventoryAdapter inventory = getInternalInventory();
		float mod = 1f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getFloweringModifier(genome, mod);
			}
		}
		return mod;
	}

	@Override
	public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
		IInventoryAdapter inventory = getInternalInventory();
		float mod = 1f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getGeneticDecay(genome, mod);
			}
		}
		return mod;
	}

	@Override
	public void wearOutEquipment(int amount) {
		IInventoryAdapter inventory = getInternalInventory();
		int wear = Math.round(amount * PluginApiculture.beeInterface.getBeekeepingMode(worldObj).getWearModifier());

		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (!(inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)) {
				continue;
			}

			inventory.setInventorySlotContents(
					i,
					((IHiveFrame) inventory.getStackInSlot(i).getItem()).frameUsed(this, inventory.getStackInSlot(i),
							PluginApiculture.beeInterface.getMember(inventory.getStackInSlot(SLOT_QUEEN)), wear));
		}
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
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(ApicultureTriggers.missingQueen);
		res.add(ApicultureTriggers.missingDrone);
		res.add(ApicultureTriggers.noFrames);
		return res;
	}
}
