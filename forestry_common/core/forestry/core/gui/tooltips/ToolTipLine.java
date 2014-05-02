/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.tooltips;

import net.minecraft.util.EnumChatFormatting;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ToolTipLine {

	public String text;
	public final EnumChatFormatting color;
	public int spacing;

	public ToolTipLine(String text, EnumChatFormatting color) {
		this.text = text;
		this.color = color;
	}

	public ToolTipLine(String text) {
		this(text, null);
	}

	public ToolTipLine() {
		this("", null);
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	public int getSpacing() {
		return spacing;
	}

}
