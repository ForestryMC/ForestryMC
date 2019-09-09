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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IToolTipProvider {
	@Nullable
	@OnlyIn(Dist.CLIENT)
	ToolTip getToolTip(int mouseX, int mouseY);

	@OnlyIn(Dist.CLIENT)
	boolean isToolTipVisible();

	@OnlyIn(Dist.CLIENT)
	boolean isMouseOver(double mouseX, double mouseY);

	@OnlyIn(Dist.CLIENT)
	default boolean isRelativeToGui() {
		return true;
	}
}
