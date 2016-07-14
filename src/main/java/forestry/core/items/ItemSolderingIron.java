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

import java.util.List;

import forestry.core.utils.Translator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.circuits.GuiSolderingIron;
import forestry.core.circuits.ISolderingIron;
import forestry.core.inventory.ItemInventorySolderingIron;

public class ItemSolderingIron extends ItemWithGui implements ISolderingIron {

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(Translator.translateToLocal("item.for.solderingIron.tooltip"));
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiSolderingIron(player, new ItemInventorySolderingIron(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerSolderingIron(player, new ItemInventorySolderingIron(player, heldItem));
	}
}
