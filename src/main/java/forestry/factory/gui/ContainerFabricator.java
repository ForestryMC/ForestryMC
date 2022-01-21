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
package forestry.factory.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.SimpleContainerData;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.IContainerCrafting;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.inventory.InventoryGhostCrafting;
import forestry.core.tiles.TileUtil;
import forestry.factory.features.FactoryContainers;
import forestry.factory.inventory.InventoryFabricator;
import forestry.factory.tiles.TileFabricator;

public class ContainerFabricator extends ContainerLiquidTanks<TileFabricator> implements IContainerCrafting {

	public static ContainerFabricator fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		TileFabricator tile = TileUtil.getTile(inv.player.level, data.readBlockPos(), TileFabricator.class);
		return new ContainerFabricator(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerFabricator(int windowId, Inventory playerInventory, TileFabricator tile) {
		super(windowId, FactoryContainers.FABRICATOR.containerType(), playerInventory, tile, 8, 129);
		addDataSlots(new SimpleContainerData(4));

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(this.tile, InventoryFabricator.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 84 + i * 18));
			}
		}

		// Molten resource
		this.addSlot(new SlotFiltered(this.tile, InventoryFabricator.SLOT_METAL, 26, 21));

		// Plan
		this.addSlot(new SlotFiltered(this.tile, InventoryFabricator.SLOT_PLAN, 139, 17));

		// Result
		this.addSlot(new SlotOutput(this.tile, InventoryFabricator.SLOT_RESULT, 139, 53));

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 3; k++) {
				this.addSlot(new SlotCraftMatrix(this, this.tile.getCraftingInventory(), InventoryGhostCrafting.SLOT_CRAFTING_1 + k + l * 3, 67 + k * 18, 17 + l * 18));
			}
		}
	}

	@Override
	public void onCraftMatrixChanged(Container iinventory, int slot) {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setData(int messageId, int data) {
		super.setData(messageId, data);

		tile.getGUINetworkData(messageId, data);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		for (ContainerListener crafter : containerListeners) {
			tile.sendGUINetworkData(this, crafter);
		}
	}

	public TileFabricator getFabricator() {
		return tile;
	}
}
