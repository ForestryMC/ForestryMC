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
package forestry.lepidopterology.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.proxy.Proxies;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.items.ItemFlutterlyzer.FlutterlyzerInventory;

public class ContainerFlutterlyzer extends ContainerItemInventory {

	FlutterlyzerInventory inventory;

	public ContainerFlutterlyzer(InventoryPlayer inventoryplayer, FlutterlyzerInventory inventory) {
		super(inventory, inventoryplayer.player);

		this.inventory = inventory;

		// Energy
		this.addSlot(new SlotCustom(inventory, FlutterlyzerInventory.SLOT_ENERGY, 172, 8, new Object[] { ForestryItem.honeydew, ForestryItem.honeyDrop }));

		// Bee to analyze
		this.addSlot(new SlotCustom(inventory, FlutterlyzerInventory.SLOT_SPECIMEN, 172, 26, new Object[] { ItemButterflyGE.class, Blocks.sapling }));

		// Analyzed bee
		this.addSlot(new SlotCustom(inventory, FlutterlyzerInventory.SLOT_ANALYZE_1, 172, 57, new Object[] { ItemButterflyGE.class }));
		this.addSlot(new SlotCustom(inventory, FlutterlyzerInventory.SLOT_ANALYZE_2, 172, 75, new Object[] { ItemButterflyGE.class }));
		this.addSlot(new SlotCustom(inventory, FlutterlyzerInventory.SLOT_ANALYZE_3, 172, 93, new Object[] { ItemButterflyGE.class }));
		this.addSlot(new SlotCustom(inventory, FlutterlyzerInventory.SLOT_ANALYZE_4, 172, 111, new Object[] { ItemButterflyGE.class }));
		this.addSlot(new SlotCustom(inventory, FlutterlyzerInventory.SLOT_ANALYZE_5, 172, 129, new Object[] { ItemButterflyGE.class }));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++)
			for (int l1 = 0; l1 < 9; l1++)
				addSecuredSlot(inventoryplayer, l1 + i1 * 9 + 9, 18 + l1 * 18, 156 + i1 * 18);
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++)
			addSecuredSlot(inventoryplayer, j1, 18 + j1 * 18, 214);

	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		// Last slot is the energy slot, so we don't save that one.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (i == FlutterlyzerInventory.SLOT_ENERGY)
				continue;
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}

		inventory.onGuiSaved(entityplayer);

	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		return true;
	}

}
