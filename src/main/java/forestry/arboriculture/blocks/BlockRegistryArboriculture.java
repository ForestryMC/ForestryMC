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
package forestry.arboriculture.blocks;

import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryArboriculture extends BlockRegistry {
	public final BlockLog logs;
	public final BlockPlanks planks;
	public final BlockSlab slabs;
	public final BlockArbFence fences;
	public final BlockArbStairs stairs;
	public final BlockLog logsFireproof;
	public final BlockPlanks planksFireproof;
	public final BlockSlab slabsFireproof;
	public final BlockArbFence fencesFireproof;
	public final BlockArbStairs stairsFireproof;

	public final BlockSapling saplingGE;
	public final BlockForestryLeaves leaves;
	public final BlockFruitPod pods;
	
	public final BlockArboriculture arboriculture;

	public BlockRegistryArboriculture() {
		// Wood blocks
		logs = registerBlock(new BlockLog(false), ItemBlockWood.class, "logs");
		registerOreDictWildcard("logWood", logs);
		
		planks = registerBlock(new BlockPlanks(false), ItemBlockWood.class, "planks");
		registerOreDictWildcard("plankWood", planks);
		
		slabs = registerBlock(new BlockSlab(false), ItemBlockWood.class, "slabs");
		registerOreDictWildcard("slabWood", slabs);
		
		fences = registerBlock(new BlockArbFence(false), ItemBlockWood.class, "fences");
		registerOreDictWildcard("fenceWood", fences);
		
		stairs = registerBlock(new BlockArbStairs(planks, false), ItemBlockWood.class, "stairs");
		registerOreDictWildcard("stairWood", stairs);
		
		logsFireproof = registerBlock(new BlockLog(true), ItemBlockWood.class, "logsFireproof");
		registerOreDictWildcard("logWood", logsFireproof);
		
		planksFireproof = registerBlock(new BlockPlanks(true), ItemBlockWood.class, "planksFireproof");
		registerOreDictWildcard("plankWood", planksFireproof);
		
		slabsFireproof = registerBlock(new BlockSlab(true), ItemBlockWood.class, "slabsFireproof");
		registerOreDictWildcard("slabWood", slabsFireproof);
		
		fencesFireproof = registerBlock(new BlockArbFence(true), ItemBlockWood.class, "fencesFireproof");
		registerOreDictWildcard("fenceWood", fencesFireproof);
		
		stairsFireproof = registerBlock(new BlockArbStairs(planksFireproof, true), ItemBlockWood.class, "stairsFireproof");
		registerOreDictWildcard("stairWood", stairsFireproof);
		
		// Saplings
		saplingGE = registerBlock(new BlockSapling(), ItemBlockForestry.class, "saplingGE");
		registerOreDictWildcard("treeSapling", saplingGE);
		
		// Leaves
		leaves = registerBlock(new BlockForestryLeaves(), ItemBlockLeaves.class, "leaves");
		registerOreDictWildcard("treeLeaves", leaves);
		
		// Pods
		pods = registerBlock(new BlockFruitPod(), ItemBlockForestry.class, "pods");
		
		// Machines
		arboriculture = registerBlock(new BlockArboriculture(), ItemBlockForestry.class, "arboriculture");
	}
}
