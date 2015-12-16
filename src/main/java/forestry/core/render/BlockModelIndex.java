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
package forestry.core.render;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class BlockModelIndex {

	public final ModelResourceLocation blockModelLocation;
	public final ModelResourceLocation itemModelLocation;
	public final IBakedModel model;
	public final Block block;

	public BlockModelIndex(ModelResourceLocation blockModelLocation, ModelResourceLocation itemModelLocation,
			IBakedModel model, Block block) {
		this.blockModelLocation = blockModelLocation;
		this.itemModelLocation = itemModelLocation;
		this.model = model;
		this.block = block;
	}

}
