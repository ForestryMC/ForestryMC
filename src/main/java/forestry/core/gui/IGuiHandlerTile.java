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

import javax.annotation.Nullable;

import forestry.api.core.ILocatable;
import net.minecraft.entity.player.EntityPlayer;

public interface IGuiHandlerTile extends IGuiHandlerForestry, ILocatable {
	@Nullable
	Object getGui(EntityPlayer player, int data);

	@Nullable
	Object getContainer(EntityPlayer player, int data);
}
