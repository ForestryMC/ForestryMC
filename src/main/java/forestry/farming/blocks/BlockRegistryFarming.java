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
package forestry.farming.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockTyped;
import forestry.farming.items.ItemBlockFarm;

public class BlockRegistryFarming extends BlockRegistry {
	public final BlockMushroom mushroom;
	public final BlockFarm farm;

	public BlockRegistryFarming() {
		mushroom = registerBlock(new BlockMushroom(), ItemBlockTyped.class, "mushroom");
		farm = registerBlock(new BlockFarm(), ItemBlockFarm.class, "ffarm");
	}
}
