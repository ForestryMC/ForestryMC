/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.client.gui.FontRenderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IClimateTableEntry {

	void draw(FontRenderer fontRenderer, int x, int y, int lineWidth, int lineStart, int lineEnd, int rowTopY, int rowBottomY, int fontColor, boolean drawBackground);

	int getHeight(FontRenderer fontRenderer);
	
	int getLineWidth(FontRenderer fontRenderer);
	
}
