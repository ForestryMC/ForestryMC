/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.gui;


import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.farming.IFarmLogic;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.gui.slots.SlotForestry;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.PacketTankUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ForestryTank;
import forestry.core.utils.TileInventoryAdapter;
import forestry.farming.gadgets.TileFarmPlain;
import forestry.plugins.PluginFarming;

public class ContainerFarm extends ContainerSocketed {

	private class SlotResources extends SlotForestry {

		private final IFarmLogic[] logics;

		public SlotResources(IInventory inventory, IFarmLogic[] logics, int slotIndex, int xPos, int yPos) {
			super(inventory, slotIndex, xPos, yPos);
			this.logics = logics;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			for (IFarmLogic logic : logics) {
				if (logic != null && logic.isAcceptedResource(stack))
					return true;
			}
			return false;
		}
	}

	private class SlotGermlings extends SlotForestry {

		private final IFarmLogic[] logics;

		public SlotGermlings(IInventory inventory, IFarmLogic[] logics, int slotIndex, int xPos, int yPos) {
			super(inventory, slotIndex, xPos, yPos);
			this.logics = logics;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			for (IFarmLogic logic : logics) {
				if (logic != null && logic.isAcceptedGermling(stack))
					return true;
			}
			return false;
		}
	}
	TileFarmPlain tile;

	public ContainerFarm(InventoryPlayer playerinventory, TileFarmPlain tile) {
		super(playerinventory, tile);

		this.tile = tile;

		IInventory inventory = tile.getInventory();
		// Tile will not have an inventory client side.
		if (inventory == null)
			inventory = new TileInventoryAdapter(tile, TileFarmPlain.SLOT_COUNT, "Items");

		// Resources
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new SlotResources(inventory, tile.getFarmLogics(), TileFarmPlain.SLOT_RESOURCES_1 + j + i * 2, 123 + j * 18, 22 + i * 18));
			}
		}

		// Germlings
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new SlotGermlings(inventory, tile.getFarmLogics(), TileFarmPlain.SLOT_GERMLINGS_1 + j + i * 2, 164 + j * 18, 22 + i * 18));
			}
		}

		// Production 1
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new SlotOutput(inventory, TileFarmPlain.SLOT_PRODUCTION_1 + j + i * 2, 123 + j * 18, 86 + i * 18));
			}
		}

		// Production 2
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new SlotOutput(inventory, TileFarmPlain.SLOT_PRODUCTION_1 + 4 + j + i * 2, 164 + j * 18, 86 + i * 18));
			}
		}

		// Fertilizer
		addSlot(new SlotCustom(inventory, TileFarmPlain.SLOT_FERTILIZER, 63, 95, PluginFarming.farmFertilizer));
		// Can Slot
		addSlot(new SlotLiquidContainer(inventory, TileFarmPlain.SLOT_CAN, 15, 95));

		// Player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerinventory, j + i * 9 + 9, 28 + j * 18, 138 + i * 18));
			}
		}
		// Player hotbar
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerinventory, i, 28 + i * 18, 196));
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		tile.getGUINetworkData(i, j);
	}
	private Map<Integer, ForestryTank> syncedFluids = new HashMap<Integer, ForestryTank>();

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			tile.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}

		ForestryTank tank = tile.getTank();

		// If null has been synced
		if (tank.getFluid() == null && getTank(0).getFluidAmount() <= 0)
			return;
		// If fluid has been synced
		if (tank.getFluid() != null && tank.getFluid().isFluidStackIdentical(getTank(0).getFluid()))
			return;

		for (int j = 0; j < this.crafters.size(); ++j) {
			if (this.crafters.get(j) instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) this.crafters.get(j);
				Proxies.net.sendToPlayer(new PacketTankUpdate(0, tank), player);
			}
		}
		syncedFluids.put(0, new ForestryTank(tank.getFluid() == null ? null : tank.getFluid().copy(), tank.getCapacity()));

	}

	@Override
	public void onTankUpdate(NBTTagCompound nbt) {
		int tankID = nbt.getByte("tank");
		int capacity = nbt.getShort("capacity");
		ForestryTank tank = new ForestryTank(capacity);
		tank.readFromNBT(nbt);
		syncedFluids.put(tankID, tank);
	}

	@Override
	public ForestryTank getTank(int slot) {
		return syncedFluids.get(slot) == null ? ForestryTank.FAKETANK : syncedFluids.get(slot);
	}
}
