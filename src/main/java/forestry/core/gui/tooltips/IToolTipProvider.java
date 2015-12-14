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

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IToolTipProvider {

	ToolTip getToolTip(int mouseX, int mouseY);

	boolean isToolTipVisible();

	boolean isMouseOver(int mouseX, int mouseY);
}
