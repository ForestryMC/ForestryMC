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
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotEmptyLiquidContainerIn;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.TileUtil;
import forestry.factory.ModuleFactory;
import forestry.factory.inventory.InventoryFermenter;
import forestry.factory.tiles.TileFermenter;

public class ContainerFermenter extends ContainerLiquidTanks<TileFermenter> {

	public static ContainerFermenter fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
		TileFermenter tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileFermenter.class);
		return new ContainerFermenter(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerFermenter(int windowId, PlayerInventory player, TileFermenter tile) {
		super(windowId, ModuleFactory.getContainerTypes().FERMENTER, player, tile, 8, 84);

		this.addSlot(new SlotFiltered(tile, InventoryFermenter.SLOT_RESOURCE, 85, 23));
		this.addSlot(new SlotFiltered(tile, InventoryFermenter.SLOT_FUEL, 75, 57));
		this.addSlot(new SlotOutput(tile, InventoryFermenter.SLOT_CAN_OUTPUT, 150, 58));
		this.addSlot(new SlotEmptyLiquidContainerIn(tile, InventoryFermenter.SLOT_CAN_INPUT, 150, 22));
		this.addSlot(new SlotLiquidIn(tile, InventoryFermenter.SLOT_INPUT, 10, 40));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateProgressBar(int messageId, int data) {
		super.updateProgressBar(messageId, data);

		tile.getGUINetworkData(messageId, data);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (IContainerListener crafter : listeners) {
			tile.sendGUINetworkData(this, crafter);
		}
	}
}
