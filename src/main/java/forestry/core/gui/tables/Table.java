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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Table {
	private final List<TableEntry> lines = new ArrayList<>();
	
	public Table() {
	}
	
	public Table(String title) {
		lines.add(new TableTitle(title));
	}

	public Table clear(){
		lines.clear();
		return this;
	}

	public Table addValueEntry(String textKey, String value) {
		lines.add(new TableValueText(textKey + ": ", value));
		return this;
	}

	public Table addCenteredEntry(String textKey) {
		lines.add(new TableTextCentered(textKey));
		return this;
	}

	public Table addEmptyEntry() {
		lines.add(new TableEmptyLine());
		return this;
	}

	public Table addEntry(TableEntry entry) {
		lines.add(entry);
		return this;
	}

	public void draw(int x, int y, int fontColor, boolean drawBackground){
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int lineWidth = getLineWidth();
		int lineStart = x + 2;
		int lineEnd = lineStart + lineWidth;
		int rowTopY = y + 1;

		for (TableEntry entry : lines) {
			int rowBottomY = rowTopY + entry.getHeight(fontRenderer) - 1;
			entry.draw(fontRenderer, x, y, lineWidth, lineStart, lineEnd, rowTopY, rowBottomY, fontColor, drawBackground);
			rowTopY += + entry.getHeight(fontRenderer) - 1;
		}
	}

	public Collection<TableEntry> getEntrys() {
		return lines;
	}

	public int getHeight() {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int height = 0;
		for (TableEntry entry : lines) {
			height += entry.getHeight(fontRenderer);
		}
		return height;
	}

	public int getLineWidth() {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int lineWidth = 0;
		
		for (TableEntry entry : lines) {
			lineWidth = Math.max(entry.getLineWidth(fontRenderer), lineWidth);
		}
		
		return lineWidth;
	}
}
