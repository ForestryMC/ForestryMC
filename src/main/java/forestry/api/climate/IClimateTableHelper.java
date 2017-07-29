/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IClimateTableHelper {

	IClimateTable createTable(String titleKey);
	
	void drawTable(IClimateTable table, int x, int y, int fontColor, boolean drawBackground);
}
