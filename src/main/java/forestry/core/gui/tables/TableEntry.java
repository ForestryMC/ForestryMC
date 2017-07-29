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

import forestry.api.climate.IClimateTableEntry;

public abstract class TableEntry implements IClimateTableEntry {
	public static final int TITLE_BACKGROUND = 1610612736;
	public static final int DEFAULT_BACKGROUND = 1342177280;
	public static final int DEFAULT_FONT = 553648127;
	
	@Override
	public int getHeight(FontRenderer fontRenderer) {
		return fontRenderer.FONT_HEIGHT + 1;
	}
}
