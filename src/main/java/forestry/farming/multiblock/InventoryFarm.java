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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapterRestricted;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.SlotUtil;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

import ic2.api.item.IC2Items;

public class InventoryFarm extends InventoryAdapterRestricted implements IFarmInventory {
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

	private final FarmController farmController;

	private final IInventory resourcesInventory;
	private final IInventory germlingsInventory;
	private final IInventory productInventory;
	private final IInventory fertilizerInventory;

	public InventoryFarm(FarmController farmController) {
		super(SLOT_COUNT, "Items", farmController.getAccessHandler());
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
			Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
			return farmController.getTankManager().accepts(fluid);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT);
	}

	@Override
	public boolean hasResources(ItemStack[] resources) {
		return InventoryUtil.contains(resourcesInventory, resources);
	}

	@Override
	public void removeResources(ItemStack[] resources) {
		EntityPlayer player = PlayerUtil.getPlayer(farmController.getWorld(), farmController.getAccessHandler().getOwner());
		InventoryUtil.removeSets(resourcesInventory, 1, resources, player, false, true, false, true);
	}

	@Override
	public boolean acceptsAsGermling(ItemStack itemstack) {
		if (itemstack == null) {
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
		if (itemstack == null) {
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
		if (itemstack == null) {
			return false;
		}
		Item item = itemstack.getItem();
		if (PluginManager.Module.INDUSTRIALCRAFT.isEnabled()) {
			Item ic2fert = IC2Items.getItem("fertilizer").getItem();
			if (ic2fert == item) {
					return true;
			}
		}

		return PluginCore.items.fertilizerCompound == item;
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

	public boolean plantGermling(IFarmable germling, EntityPlayer player, int x, int y, int z) {
		for (int i = 0; i < germlingsInventory.getSizeInventory(); i++) {
			ItemStack germlingStack = germlingsInventory.getStackInSlot(i);
			if (germlingStack == null || !germling.isGermling(germlingStack)) {
				continue;
			}

			if (germling.plantSaplingAt(player, germlingStack, player.worldObj, x, y, z)) {
				germlingsInventory.decrStackSize(i, 1);
				return true;
			}
		}
		return false;
	}

	public void addProduce(ItemStack produce) {

		if (acceptsAsGermling(produce)) {
			produce.stackSize -= InventoryUtil.addStack(germlingsInventory, produce, true);
		}

		if (produce.stackSize <= 0) {
			return;
		}

		if (acceptsAsResource(produce)) {
			produce.stackSize -= InventoryUtil.addStack(resourcesInventory, produce, true);
		}

		if (produce.stackSize <= 0) {
			return;
		}

		produce.stackSize -= InventoryUtil.addStack(productInventory, produce, true);
	}

	public void stowHarvest(Iterable<ItemStack> harvested, Stack<ItemStack> pendingProduce) {
		for (ItemStack harvest : harvested) {

			if (acceptsAsGermling(harvest)) {
				harvest.stackSize -= InventoryUtil.addStack(germlingsInventory, harvest, true);
			}

			if (harvest.stackSize <= 0) {
				continue;
			}

			harvest.stackSize -= InventoryUtil.addStack(productInventory, harvest, true);

			if (harvest.stackSize <= 0) {
				continue;
			}

			pendingProduce.push(harvest);
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

	public boolean useFertilizer() {
		ItemStack fertilizer = getStackInSlot(SLOT_FERTILIZER);
		if (fertilizer == null || fertilizer.stackSize <= 0) {
			return false;
		}

		if (!acceptsAsFertilizer(fertilizer)) {
			return false;
		}

		decrStackSize(SLOT_FERTILIZER, 1);
		return true;
	}
}