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

public abstract class TableEntry {
	public static final int TITLE_BACKGROUND = 1610612736;
	public static final int DEFAULT_BACKGROUND = 1342177280;
	public static final int DEFAULT_FONT = 553648127;

	public int getHeight(FontRenderer fontRenderer) {
		return fontRenderer.FONT_HEIGHT + 1;
	}

	public abstract int getLineWidth(FontRenderer fontRenderer);

	public abstract void draw(FontRenderer fontRenderer, int x, int y, int lineWidth, int lineStart, int lineEnd, int rowTopY, int rowBottomY, int fontColor, boolean drawBackground);
}
