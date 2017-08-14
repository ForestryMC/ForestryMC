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

class TableEmptyLine extends TableEntry {
	@Override
	public void draw(FontRenderer fontRenderer, int x, int y, int lineWidth, int lineStart, int lineEnd, int rowTopY, int rowBottomY, int fontColor, boolean drawBackground) {
	}
	
	@Override
	public int getLineWidth(FontRenderer fontRenderer) {
		return 0;
	}
}
