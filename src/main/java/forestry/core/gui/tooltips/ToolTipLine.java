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

import net.minecraft.util.EnumChatFormatting;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ToolTipLine {

	private final String text;
	private final EnumChatFormatting color;
	private final int spacing;

	public ToolTipLine(String text, EnumChatFormatting color, int spacing) {
		this.text = text;
		this.color = color;
		this.spacing = spacing;
	}

	public ToolTipLine(String text) {
		this(text, null, 0);
	}

	public ToolTipLine() {
		this("", null, 0);
	}

	public int getSpacing() {
		return spacing;
	}

	public String toString() {
		if (color == null) {
			return text;
		}
		return color + text;
	}

}
