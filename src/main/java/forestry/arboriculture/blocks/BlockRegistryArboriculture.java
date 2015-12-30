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
import forestry.arboriculture.items.ItemBlockWoodSlab;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryArboriculture extends BlockRegistry {
	public final BlockLog logs;
	public final BlockPlanks planks;
	public final BlockSlab slabs;
	public final BlockSlab slabsDouble;
	public final BlockArbFence fences;
	public final BlockArbStairs stairs;
	public final BlockLog logsFireproof;
	public final BlockPlanks planksFireproof;
	public final BlockSlab slabsFireproof;
	public final BlockSlab slabsDoubleFireproof;
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

		BlockSlab blockSlab = new BlockSlab(false, false);
		BlockSlab blockSlabDouble = new BlockSlab(true, false);
		slabs = registerBlock(blockSlab, ItemBlockWoodSlab.class, "slabs", blockSlabDouble, blockSlab);
		registerOreDictWildcard("slabWood", slabs);

		slabsDouble = registerBlock(blockSlabDouble, ItemBlockWoodSlab.class, "slabsDouble", blockSlabDouble, blockSlab);
		
		fences = registerBlock(new BlockArbFence(false), ItemBlockWood.class, "fences");
		registerOreDictWildcard("fenceWood", fences);
		
		stairs = registerBlock(new BlockArbStairs(planks, false), ItemBlockWood.class, "stairs");
		registerOreDictWildcard("stairWood", stairs);
		
		logsFireproof = registerBlock(new BlockLog(true), ItemBlockWood.class, "logsFireproof");
		registerOreDictWildcard("logWood", logsFireproof);
		
		planksFireproof = registerBlock(new BlockPlanks(true), ItemBlockWood.class, "planksFireproof");
		registerOreDictWildcard("plankWood", planksFireproof);

		BlockSlab blockSlabFireproof = new BlockSlab(false, true);
		BlockSlab blockSlabDoubleFireproof = new BlockSlab(true, true);
		slabsFireproof = registerBlock(blockSlabFireproof, ItemBlockWoodSlab.class, "slabsFireproof", blockSlabDoubleFireproof, blockSlabFireproof);
		registerOreDictWildcard("slabWood", slabsFireproof);

		slabsDoubleFireproof = registerBlock(blockSlabDoubleFireproof, ItemBlockWoodSlab.class, "slabsDoubleFireproof", blockSlabDoubleFireproof, blockSlabFireproof);
		
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
