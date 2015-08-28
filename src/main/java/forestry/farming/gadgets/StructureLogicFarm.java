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
package forestry.farming.gadgets;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class StructureLogicFarm {

	public static final ImmutableSet<Block> bricks = ImmutableSet.of(
			Blocks.brick_block,
			Blocks.stonebrick,
			Blocks.sandstone,
			Blocks.nether_brick,
			Blocks.quartz_block
	);

	private StructureLogicFarm() {

	}

}
