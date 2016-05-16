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
package forestry.core.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import forestry.api.apiculture.BeeManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlyzer;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.inventory.ItemInventoryAlyzer;

public class ItemAlyzer extends ItemWithGui {
	public ItemAlyzer() {
		setCreativeTab(Tabs.tabApiculture);
	}
	
	@Override
	public void openGui(EntityPlayer entityplayer) {
		super.openGui(entityplayer);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		IAlyzer alyzer =BeeManager.beeRoot.getAlyzer();
		ItemInventoryAlyzer inventory = new ItemInventoryAlyzer(player, heldItem, null);
		if(inventory.getSpecimen() != null){
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(inventory.getSpecimen());
			alyzer = speciesRoot.getAlyzer();
		}
		return new GuiAlyzer(player, alyzer, new ItemInventoryAlyzer(player, heldItem, alyzer));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		ItemInventoryAlyzer inventory = new ItemInventoryAlyzer(player, heldItem, null);
		if(inventory.getSpecimen() != null){
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(inventory.getSpecimen());
			return new ContainerAlyzer(new ItemInventoryAlyzer(player, heldItem, speciesRoot.getAlyzer()), player);
		}
		return new ContainerAlyzer(new ItemInventoryAlyzer(player, heldItem, BeeManager.beeRoot.getAlyzer()), player);
	}
}
