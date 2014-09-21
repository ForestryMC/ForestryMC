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
package forestry.farming.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmLogic;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.gui.slots.SlotForestry;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.core.gui.slots.SlotOutput;
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
		TankManager tankManager = tile.getTankManager();
		if (tankManager != null)
			tankManager.processGuiUpdate(i, j);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		TankManager tankManager = tile.getTankManager();
		if (tankManager != null)
			tankManager.updateGuiData(this, crafters);

		for (int i = 0; i < crafters.size(); i++) {
			tile.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting iCrafting) {
		super.addCraftingToCrafters(iCrafting);
		TankManager tankManager = tile.getTankManager();
		if (tankManager != null)
			tankManager.initGuiData(this, iCrafting);
	}

	public StandardTank getTank(int slot) {
		return tile.getTankManager().get(slot);
	}
}
