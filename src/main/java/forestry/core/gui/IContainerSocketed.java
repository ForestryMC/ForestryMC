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
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IContainerSocketed {
	@SideOnly(Side.CLIENT)
	void handleChipsetClick(int slot);

	void handleChipsetClickServer(int slot, EntityPlayerMP player, ItemStack itemstack);

	@SideOnly(Side.CLIENT)
	void handleSolderingIronClick(int slot);

	void handleSolderingIronClickServer(int slot, EntityPlayerMP player, ItemStack itemstack);
}
