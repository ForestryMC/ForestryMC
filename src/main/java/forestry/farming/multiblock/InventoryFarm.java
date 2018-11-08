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
package forestry.farming.multiblock;

import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapterRestricted;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.SlotUtil;

public class InventoryFarm extends InventoryAdapterRestricted implements IFarmInventoryInternal {
	public static final int SLOT_RESOURCES_1 = 0;
	public static final int SLOT_RESOURCES_COUNT = 6;
	public static final int SLOT_GERMLINGS_1 = 6;
	public static final int SLOT_GERMLINGS_COUNT = 6;
	public static final int SLOT_PRODUCTION_1 = 12;
	public static final int SLOT_PRODUCTION_COUNT = 8;

	public static final int SLOT_FERTILIZER = 20;
	public static final int SLOT_FERTILIZER_COUNT = 1;
	public static final int SLOT_CAN = 21;
	public static final int SLOT_CAN_COUNT = 1;

	public static final int SLOT_COUNT = SLOT_RESOURCES_COUNT + SLOT_GERMLINGS_COUNT + SLOT_PRODUCTION_COUNT + SLOT_FERTILIZER_COUNT + SLOT_CAN_COUNT;

	private static final int FERTILIZER_MODIFIER = ForestryAPI.activeMode.getIntegerSetting("farms.fertilizer.modifier");

	private final FarmController farmController;

	private final IInventory resourcesInventory;
	private final IInventory germlingsInventory;
	private final IInventory productInventory;
	private final IInventory fertilizerInventory;

	public InventoryFarm(FarmController farmController) {
		super(SLOT_COUNT, "Items");
		this.farmController = farmController;

		this.resourcesInventory = new InventoryMapper(this, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT);
		this.germlingsInventory = new InventoryMapper(this, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT);
		this.productInventory = new InventoryMapper(this, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT);
		this.fertilizerInventory = new InventoryMapper(this, SLOT_FERTILIZER, SLOT_FERTILIZER_COUNT);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (SlotUtil.isSlotInRange(slotIndex, SLOT_FERTILIZER, SLOT_FERTILIZER_COUNT)) {
			return acceptsAsFertilizer(itemStack);
		} else if (SlotUtil.isSlotInRange(slotIndex, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT)) {
			return acceptsAsGermling(itemStack);
		} else if (SlotUtil.isSlotInRange(slotIndex, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT)) {
			return acceptsAsResource(itemStack);
		} else if (SlotUtil.isSlotInRange(slotIndex, SLOT_CAN, SLOT_CAN_COUNT)) {
			FluidStack fluid = FluidUtil.getFluidContained(itemStack);
			return fluid != null && farmController.getTankManager().canFillFluidType(fluid);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, EnumFacing side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT);
	}

	@Override
	public boolean hasResources(NonNullList<ItemStack> resources) {
		return InventoryUtil.contains(resourcesInventory, resources);
	}

	@Override
	public void removeResources(NonNullList<ItemStack> resources) {
		InventoryUtil.removeSets(resourcesInventory, 1, resources, null, false, true, false, true);
	}

	@Override
	public boolean acceptsAsGermling(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}

		for (FarmDirection farmDirection : FarmDirection.values()) {
			IFarmLogic logic = farmController.getFarmLogic(farmDirection);
			if (logic.isAcceptedGermling(itemstack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean acceptsAsResource(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}

		for (FarmDirection farmDirection : FarmDirection.values()) {
			IFarmLogic logic = farmController.getFarmLogic(farmDirection);
			if (logic.isAcceptedResource(itemstack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean acceptsAsFertilizer(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}

		return ForestryAPI.farmRegistry.getFertilizeValue(itemstack) > 0;
	}

	@Override
	public IInventory getProductInventory() {
		return productInventory;
	}

	@Override
	public IInventory getGermlingsInventory() {
		return germlingsInventory;
	}

	@Override
	public IInventory getResourcesInventory() {
		return resourcesInventory;
	}

	@Override
	public IInventory getFertilizerInventory() {
		return fertilizerInventory;
	}

	public void drainCan(TankManager tankManager) {
		FluidHelper.drainContainers(tankManager, this, SLOT_CAN);
	}

	public boolean plantGermling(IFarmable germling, EntityPlayer player, BlockPos pos) {
		for (int i = 0; i < germlingsInventory.getSizeInventory(); i++) {
			ItemStack germlingStack = germlingsInventory.getStackInSlot(i);
			if (germlingStack.isEmpty() || !germling.isGermling(germlingStack)) {
				continue;
			}

			if (germling.plantSaplingAt(player, germlingStack, player.world, pos)) {
				germlingsInventory.decrStackSize(i, 1);
				return true;
			}
		}
		return false;
	}

	public void addProduce(ItemStack produce) {
		int added = InventoryUtil.addStack(productInventory, produce, true);
		produce.shrink(added);
	}

	public void stowHarvest(Iterable<ItemStack> harvested, Stack<ItemStack> pendingProduce) {
		for (ItemStack harvest : harvested) {
			int added = InventoryUtil.addStack(productInventory, harvest, true);
			harvest.shrink(added);
			if (!harvest.isEmpty()) {
				pendingProduce.push(harvest);
			}
		}
	}

	public boolean tryAddPendingProduce(Stack<ItemStack> pendingProduce) {
		IInventory productInventory = getProductInventory();

		ItemStack next = pendingProduce.peek();
		boolean added = InventoryUtil.tryAddStack(productInventory, next, true, true);

		if (added) {
			pendingProduce.pop();
		}

		return added;
	}

	public int getFertilizerValue() {
		ItemStack fertilizerStack = getStackInSlot(SLOT_FERTILIZER);
		if (fertilizerStack.isEmpty()) {
			return 0;
		}

		int fertilizerValue = ForestryAPI.farmRegistry.getFertilizeValue(fertilizerStack);
		if (fertilizerValue > 0) {
			return fertilizerValue * FERTILIZER_MODIFIER;
		}
		return 0;
	}

	public boolean useFertilizer() {
		ItemStack fertilizer = getStackInSlot(SLOT_FERTILIZER);
		if (acceptsAsFertilizer(fertilizer)) {
			decrStackSize(SLOT_FERTILIZER, 1);
			return true;
		}
		return false;
	}
}