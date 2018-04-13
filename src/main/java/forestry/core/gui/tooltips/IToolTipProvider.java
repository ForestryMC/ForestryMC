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
package forestry.core.gui.tooltips;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IToolTipProvider {
	@Nullable
	@SideOnly(Side.CLIENT)
	ToolTip getToolTip(int mouseX, int mouseY);

	@SideOnly(Side.CLIENT)
	boolean isToolTipVisible();

	@SideOnly(Side.CLIENT)
	boolean isMouseOver(int mouseX, int mouseY);

	@SideOnly(Side.CLIENT)
	default boolean isRelativeToGui(){
		return true;
	}
}
