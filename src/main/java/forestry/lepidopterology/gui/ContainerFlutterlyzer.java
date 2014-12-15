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

import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerAlyzer;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.items.ItemFlutterlyzer.FlutterlyzerInventory;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerFlutterlyzer extends ContainerAlyzer {

	FlutterlyzerInventory inventory;

	public ContainerFlutterlyzer(InventoryPlayer inventoryplayer, FlutterlyzerInventory inventory) {
		super(inventoryplayer, inventory, new Object[] { ForestryItem.honeydew, ForestryItem.honeyDrop }, ItemButterflyGE.class);
	}

}
