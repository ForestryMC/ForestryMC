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

import javax.annotation.Nonnull;

import net.minecraft.util.EnumChatFormatting;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ToolTipLine {
	@Nonnull
	private final String text;
	@Nonnull
	private final EnumChatFormatting color;
	private final int spacing;

	public ToolTipLine(@Nonnull String text, @Nonnull EnumChatFormatting color, int spacing) {
		this.text = text;
		this.color = color;
		this.spacing = spacing;
	}

	public ToolTipLine(@Nonnull String text) {
		this(text, null, 0);
	}

	public int getSpacing() {
		return spacing;
	}

	@Nonnull
	@Override
	public String toString() {
		if (color == null) {
			return text;
		}
		return color + text;
	}

}
