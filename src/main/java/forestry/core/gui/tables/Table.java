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

import forestry.api.climate.IClimateTable;
import forestry.api.climate.IClimateTableEntry;

public class Table implements IClimateTable {
	private final List<IClimateTableEntry> lines = new ArrayList<>();
	
	public Table() {
	}
	
	public Table(String title) {
		lines.add(new TableTitle(title));
	}
	
	@Override
	public IClimateTable addValueEntry(String textKey, String value) {
		lines.add(new TableValueText(textKey + ": ", value));
		return this;
	}
	
	@Override
	public IClimateTable addCenteredEntry(String textKey) {
		lines.add(new TableTextCentered(textKey));
		return this;
	}
	
	@Override
	public IClimateTable addEmptyEntry() {
		lines.add(new TableEmptyLine());
		return this;
	}
	
	@Override
	public IClimateTable addEntry(IClimateTableEntry entry) {
		lines.add(entry);
		return this;
	}
	
	@Override
	public Collection<IClimateTableEntry> getEntrys() {
		return lines;
	}
	
	@Override
	public int getHeight() {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		int height = 0;
		for (IClimateTableEntry entry : lines) {
			height += entry.getHeight(fontRenderer);
		}
		return height;
	}
	
	@Override
	public int getLineWidth() {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		int lineWidth = 0;
		
		for (IClimateTableEntry entry : lines) {
			lineWidth = Math.max(entry.getLineWidth(fontRenderer), lineWidth);
		}
		
		return lineWidth;
	}
}
