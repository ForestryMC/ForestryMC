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

import forestry.core.gui.ContainerAlyzer;
import forestry.core.config.ForestryItem;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBeealyzer.BeealyzerInventory;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerBeealyzer extends ContainerAlyzer {

	public ContainerBeealyzer(InventoryPlayer inventoryplayer, BeealyzerInventory inventory) {
		super(inventoryplayer, inventory, new Object[] { ForestryItem.honeydew, ForestryItem.honeyDrop }, new Object[] { ItemBeeGE.class });
	}

}
