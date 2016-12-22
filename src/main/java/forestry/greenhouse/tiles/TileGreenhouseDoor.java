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
package forestry.greenhouse.tiles;

import forestry.core.utils.ItemStackUtil;
import net.minecraft.item.ItemStack;

public class TileGreenhouseDoor extends TileGreenhouse {

	public TileGreenhouseDoor() {
	}

	@Override
	public boolean setCamouflageBlock(String type, ItemStack camouflageBlock, boolean sendClientUpdate) {
		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, this.camouflageBlock)) {
			super.setCamouflageBlock(type, camouflageBlock, sendClientUpdate);
			TileGreenhouseDoor otherDoorTile = null;
			if (world.getTileEntity(pos.up()) instanceof TileGreenhouseDoor) {
				otherDoorTile = (TileGreenhouseDoor) world.getTileEntity(pos.up());
			} else if (world.getTileEntity(pos.down()) instanceof TileGreenhouseDoor) {
				otherDoorTile = (TileGreenhouseDoor) world.getTileEntity(pos.down());
			}
			if (otherDoorTile != null) {
				return otherDoorTile.setCamouflageBlock(type, camouflageBlock, sendClientUpdate);
			}
		}
		return false;
	}

}
