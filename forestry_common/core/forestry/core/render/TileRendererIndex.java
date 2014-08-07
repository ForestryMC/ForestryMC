/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.render;

import net.minecraft.block.Block;

public class TileRendererIndex {
	private Block block;
	private int meta;

	public TileRendererIndex(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}

	public int hashCode() {
		return block.hashCode() + meta;
	}

	public boolean equals(Object aobj) {
		if (!(aobj instanceof TileRendererIndex))
			return false;

		TileRendererIndex index = (TileRendererIndex) aobj;

		return index.block == block && index.meta == meta;
	}
}
