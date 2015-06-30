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

import net.minecraftforge.fluids.Fluid;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapterRestricted;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginFarming;

public class FarmInventory extends InventoryAdapterRestricted implements IFarmInventory {
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

	public FarmInventory(FarmController farmController) {
		super(SLOT_COUNT, "Items", farmController.getAccessHandler());
		this.farmController = farmController;

		this.resourcesInventory = new InventoryMapper(this, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT);
		this.germlingsInventory = new InventoryMapper(this, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT);
		this.productInventory = new InventoryMapper(this, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT);
		this.fertilizerInventory = new InventoryMapper(this, SLOT_FERTILIZER, SLOT_FERTILIZER_COUNT);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (GuiUtil.isIndexInRange(slotIndex, SLOT_FERTILIZER, SLOT_FERTILIZER_COUNT)) {
			return acceptsAsFertilizer(itemStack);
		} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT)) {
			return acceptsAsGermling(itemStack);
		} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT)) {
			return acceptsAsResource(itemStack);
		} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_CAN, SLOT_CAN_COUNT)) {
			Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
			return farmController.getTankManager().accepts(fluid);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
		return GuiUtil.isIndexInRange(slotIndex, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT);
	}

	@Override
	public boolean hasResources(ItemStack[] resources) {
		return InvTools.contains(resourcesInventory, resources);
	}

	@Override
	public void removeResources(ItemStack[] resources) {
		EntityPlayer player = Proxies.common.getPlayer(farmController.getWorld(), farmController.getAccessHandler().getOwner());
		InvTools.removeSets(resourcesInventory, 1, resources, player, false, true, true);
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

		return StackUtils.isIdenticalItem(PluginFarming.farmFertilizer, itemstack);
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
		if (getStackInSlot(SLOT_CAN) != null) {
			FluidHelper.drainContainers(tankManager, this, SLOT_CAN);
		}
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
			produce.stackSize -= InvTools.addStack(germlingsInventory, produce, true);
		}

		if (produce.stackSize <= 0) {
			return;
		}

		if (acceptsAsResource(produce)) {
			produce.stackSize -= InvTools.addStack(resourcesInventory, produce, true);
		}

		if (produce.stackSize <= 0) {
			return;
		}

		produce.stackSize -= InvTools.addStack(productInventory, produce, true);
	}

	public void stowHarvest(Iterable<ItemStack> harvested, Stack<ItemStack> pendingProduce) {
		for (ItemStack harvest : harvested) {

			if (acceptsAsGermling(harvest)) {
				harvest.stackSize -= InvTools.addStack(germlingsInventory, harvest, true);
			}

			if (harvest.stackSize <= 0) {
				continue;
			}

			harvest.stackSize -= InvTools.addStack(productInventory, harvest, true);

			if (harvest.stackSize <= 0) {
				continue;
			}

			pendingProduce.push(harvest);
		}
	}

	public boolean tryAddPendingProduce(Stack<ItemStack> pendingProduce) {
		IInventory productInventory = getProductInventory();

		ItemStack next = pendingProduce.peek();
		boolean added = InvTools.tryAddStack(productInventory, next, true, true);

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