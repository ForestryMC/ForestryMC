/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
