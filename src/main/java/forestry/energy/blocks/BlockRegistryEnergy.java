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
package forestry.energy.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.energy.items.ItemEngine;

public class BlockRegistryEnergy extends BlockRegistry {
	public final BlockEngine peatEngine;
	public final BlockEngine biogasEngine;
	public final BlockEngine clockworkEngine;

	public BlockRegistryEnergy() {
		peatEngine = new BlockEngine(BlockTypeEngine.PEAT);
		registerBlock(peatEngine, new ItemEngine(peatEngine), "engine.peat");

		biogasEngine = new BlockEngine(BlockTypeEngine.BIOGAS);
		registerBlock(biogasEngine, new ItemEngine(biogasEngine), "engine.biogas");

		clockworkEngine = new BlockEngine(BlockTypeEngine.CLOCKWORK);
		registerBlock(clockworkEngine, new ItemEngine(clockworkEngine), "engine.clockwork");
	}
}
