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
package forestry.mail.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;

public class ItemCatalogue extends ItemForestry {

	public ItemCatalogue() {
		super();
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.CatalogueGUI.ordinal(), world, (int) entityplayer.posX,
					(int) entityplayer.posY, (int) entityplayer.posZ);
		}

		return itemstack;
	}

}
