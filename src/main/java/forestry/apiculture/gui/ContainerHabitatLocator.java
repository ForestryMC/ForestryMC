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
package forestry.apiculture.gui;

import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBiomefinder;
import forestry.apiculture.items.ItemBiomefinder.BiomefinderInventory;
import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.proxy.Proxies;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHabitatLocator extends ContainerItemInventory {

	public final BiomefinderInventory inventory;

	public ContainerHabitatLocator(InventoryPlayer inventoryplayer, BiomefinderInventory inventory) {
		super(inventory, inventoryplayer.player);

		this.inventory = inventory;

		// Energy
		this.addSlotToContainer(new SlotCustom(inventory, 2, 152, 8, ForestryItem.honeydew, ForestryItem.honeyDrop));

		// Bee to analyze
		this.addSlotToContainer(new SlotCustom(inventory, 0, 152, 32, ItemBeeGE.class));
		// Analyzed bee
		this.addSlotToContainer(new SlotCustom(inventory, 1, 152, 75, ItemBeeGE.class));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++)
			for (int l1 = 0; l1 < 9; l1++)
				addSecuredSlot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 102 + i1 * 18);
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++)
			addSecuredSlot(inventoryplayer, j1, 8 + j1 * 18, 160);
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		((ItemBiomefinder) ForestryItem.biomeFinder.item()).startBiomeSearch(entityplayer.worldObj, entityplayer, inventory.biomesToSearch);

		for (int i = 0; i < inventory.getSizeInventory() - 1; i++) {
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
		return false;
	}

}
