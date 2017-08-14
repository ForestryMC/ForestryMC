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
package forestry.core.gui.tables;

import net.minecraft.client.gui.FontRenderer;

abstract class TableText extends TableEntry {
	String text;
	
	public TableText(String text) {
		this.text = text;
	}
	
	@Override
	public int getLineWidth(FontRenderer fontRenderer) {
		return fontRenderer.getStringWidth(text);
	}
}
