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
package forestry.factory.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockNBT;

public class BlockRegistryFactory extends BlockRegistry {
	public final BlockFactoryTESR factoryTESR;
	public final BlockFactoryPlain factoryPlain;

	public BlockRegistryFactory() {
		factoryTESR = registerBlock(new BlockFactoryTESR(), ItemBlockForestry.class, "factory");
		factoryPlain = registerBlock(new BlockFactoryPlain(), ItemBlockNBT.class, "factory2");
	}
}
