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

import net.minecraft.item.ItemStack;
import forestry.core.utils.ItemStackUtil;

public class TileGreenhouseDoor extends TileGreenhouse {

	public TileGreenhouseDoor() {
	}
	
	@Override
	public void setCamouflageBlock(String type, ItemStack camouflageBlock) {
		if(!ItemStackUtil.isIdenticalItem(camouflageBlock, this.camouflageBlock)){
			super.setCamouflageBlock(type, camouflageBlock);
			TileGreenhouseDoor otherDoorTile = null;
			if(worldObj.getTileEntity(pos.up()) instanceof TileGreenhouseDoor){
				otherDoorTile = (TileGreenhouseDoor) worldObj.getTileEntity(pos.up());
			}else if(worldObj.getTileEntity(pos.down()) instanceof TileGreenhouseDoor){
				otherDoorTile = (TileGreenhouseDoor) worldObj.getTileEntity(pos.down());
			}
			if(otherDoorTile != null){
				otherDoorTile.setCamouflageBlock(type, camouflageBlock);
			}
		}
	}

}
