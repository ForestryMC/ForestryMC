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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotEmptyLiquidContainerIn;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.TileUtil;
import forestry.factory.ModuleFactory;
import forestry.factory.inventory.InventoryStill;
import forestry.factory.tiles.TileStill;

public class ContainerStill extends ContainerLiquidTanks<TileStill> {

	//TODO work out if there is a good way to make this generic
	public static ContainerStill fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
		TileStill tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileStill.class);
		return new ContainerStill(windowId, inv, tile);
	}

	public ContainerStill(int windowId, PlayerInventory player, TileStill tile) {
		super(windowId, ModuleFactory.getContainerTypes().STILL, player, tile, 8, 84);

		this.addSlot(new SlotOutput(tile, InventoryStill.SLOT_PRODUCT, 150, 54));
		this.addSlot(new SlotEmptyLiquidContainerIn(tile, InventoryStill.SLOT_RESOURCE, 150, 18));
		this.addSlot(new SlotLiquidIn(tile, InventoryStill.SLOT_CAN, 10, 36));
	}
}
