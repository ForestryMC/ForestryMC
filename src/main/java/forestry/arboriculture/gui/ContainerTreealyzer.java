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
package forestry.arboriculture.gui;

import forestry.api.genetics.AlleleManager;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.config.ForestryItem;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemTreealyzer.TreealyzerInventory;

import net.minecraft.entity.player.InventoryPlayer;
import org.apache.commons.lang3.ArrayUtils;

public class ContainerTreealyzer extends ContainerAlyzer {

	TreealyzerInventory inventory;
	private static Object[] acceptedEnergy = new Object[] {ForestryItem.honeydew, ForestryItem.honeyDrop};
	private static Object[] acceptedSpecimens = ArrayUtils.addAll(AlleleManager.ersatzSaplings.keySet().toArray(), ItemGermlingGE.class);

	public ContainerTreealyzer(InventoryPlayer inventoryplayer, TreealyzerInventory inventory) {
		super(inventoryplayer, inventory, acceptedEnergy, acceptedSpecimens);
	}

}
