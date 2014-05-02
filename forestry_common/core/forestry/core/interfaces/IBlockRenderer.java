/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.interfaces;

import net.minecraft.tileentity.TileEntity;

public interface IBlockRenderer {
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f);

	public void inventoryRender(double x, double y, double z, float f, float f1);
}
