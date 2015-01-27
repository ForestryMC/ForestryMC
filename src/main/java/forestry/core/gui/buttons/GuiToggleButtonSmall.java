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
package forestry.core.gui.buttons;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiToggleButtonSmall extends GuiToggleButton {

	public GuiToggleButtonSmall(int i, int j, int k, String s, boolean active) {
		this(i, j, k, 200, s, active);
	}

	public GuiToggleButtonSmall(int i, int x, int y, int w, String s, boolean active) {
		super(i, x, y, w, StandardButtonTextureSets.SMALL_BUTTON, s, active);
		this.active = active;
	}

}
