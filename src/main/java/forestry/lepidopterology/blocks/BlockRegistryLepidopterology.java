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
package forestry.lepidopterology.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryLepidopterology extends BlockRegistry {
	public final BlockLepidopterology lepidopterology;

	public BlockRegistryLepidopterology() {
		lepidopterology = registerBlock(new BlockLepidopterology(), ItemBlockForestry.class, "lepidopterology");
	}
}
