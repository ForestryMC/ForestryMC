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
package forestry.core.interfaces;

import net.minecraft.tileentity.TileEntity;

public interface IBlockRenderer {
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f);

	public void inventoryRender(double x, double y, double z, float f, float f1);
}
