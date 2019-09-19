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
package forestry.core.models;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.BlockItem;

public class BlockModelEntry {

	public final IBakedModel model;
	public final Block block;
	public final Collection<BlockState> states;
	@Nullable
	public final BlockItem item;

	public BlockModelEntry(IBakedModel model, Block block, @Nullable BlockItem item, Collection<BlockState> states) {
		this.model = model;
		this.block = block;
		this.item = item;
		this.states = states;
	}

}
