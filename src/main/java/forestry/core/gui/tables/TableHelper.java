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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import forestry.api.climate.IClimateTable;
import forestry.api.climate.IClimateTableEntry;
import forestry.api.climate.IClimateTableHelper;

public class TableHelper implements IClimateTableHelper {
	
	public static final TableHelper INSTANCE = new TableHelper();
	
	@Override
	public IClimateTable createTable(String titleKey) {
		return new Table(titleKey);
	}
	
	@Override
	public void drawTable(IClimateTable table, int x, int y, int fontColor, boolean drawBackground) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		int lineWidth = table.getLineWidth();
		int lineStart = x + 2;
		int lineEnd = lineStart + lineWidth;
		int rowTopY = y + 1;
		
		for (IClimateTableEntry entry : table.getEntrys()) {
			int rowBottomY = rowTopY + entry.getHeight(fontRenderer) - 1;
			entry.draw(fontRenderer, x, y, lineWidth, lineStart, lineEnd, rowTopY, rowBottomY, fontColor, drawBackground);
			rowTopY += + entry.getHeight(fontRenderer) - 1;
		}
	}
}
