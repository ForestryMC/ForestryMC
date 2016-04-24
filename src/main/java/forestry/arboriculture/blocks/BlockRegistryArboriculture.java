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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.arboriculture.items.ItemBlockDecorativeLeaves;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWoodDoor;
import forestry.arboriculture.items.ItemBlockWoodSlab;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryArboriculture extends BlockRegistry {
	public final List<BlockArbLog> logs;
	public final List<BlockArbLog> logsFireproof;
	public final List<BlockArbPlanks> planks;
	public final List<BlockArbPlanks> planksFireproof;
	public final List<BlockArbSlab> slabs;
	public final List<BlockArbSlab> slabsDouble;
	public final List<BlockArbSlab> slabsFireproof;
	public final List<BlockArbSlab> slabsDoubleFireproof;
	public final List<BlockArbFence> fences;
	public final List<BlockArbFence> fencesFireproof;
	public final List<BlockArbFenceGate> fenceGates;
	public final List<BlockArbFenceGate> fenceGatesFireproof;
	public final List<BlockArbStairs> stairs;
	public final List<BlockArbStairs> stairsFireproof;
	public final List<BlockArbDoor> doors;

	public final BlockSapling saplingGE;
	public final BlockForestryLeaves leaves;
	public final List<BlockDecorativeLeaves> leavesDecorative;
	private final Map<String, ItemStack> speciesToLeavesDecorative;
	public final Map<String, BlockFruitPod> podsMap;

	public final BlockArboriculture arboriculture;

	public BlockRegistryArboriculture() {
		// Wood blocks
		logs = BlockArbLog.create(false);
		for (BlockArbLog block : logs) {
			registerBlock(block, ItemBlockWood.class, "logs." + block.getBlockNumber());
			registerOreDictWildcard("logWood", block);
		}

		logsFireproof = BlockArbLog.create(true);
		for (BlockArbLog block : logsFireproof) {
			registerBlock(block, ItemBlockWood.class, "logs.fireproof." + block.getBlockNumber());
			registerOreDictWildcard("logWood", block);
		}

		planks = BlockArbPlanks.create(false);
		for (BlockArbPlanks block : planks) {
			registerBlock(block, ItemBlockWood.class, "planks." + block.getBlockNumber());
			registerOreDictWildcard("plankWood", block);
		}

		planksFireproof = BlockArbPlanks.create(true);
		for (BlockArbPlanks block : planksFireproof) {
			registerBlock(block, ItemBlockWood.class, "planks.fireproof." + block.getBlockNumber());
			registerOreDictWildcard("plankWood", block);
		}

		slabs = BlockArbSlab.create(false, false);
		slabsDouble = BlockArbSlab.create(false, true);
		for (int i = 0; i < slabs.size(); i++) {
			BlockArbSlab slab = slabs.get(i);
			BlockArbSlab slabDouble = slabsDouble.get(i);
			registerBlock(slab, new ItemBlockWoodSlab(slab, slab, slabDouble), "slabs." + slab.getBlockNumber());
			registerBlock(slabDouble, new ItemBlockWoodSlab(slabDouble, slab, slabDouble), "slabs.double." + slabDouble.getBlockNumber());
			registerOreDictWildcard("slabWood", slab);
		}

		slabsFireproof = BlockArbSlab.create(true, false);
		slabsDoubleFireproof = BlockArbSlab.create(true, true);
		for (int i = 0; i < slabsFireproof.size(); i++) {
			BlockArbSlab slab = slabsFireproof.get(i);
			BlockArbSlab slabDouble = slabsDoubleFireproof.get(i);
			registerBlock(slab, new ItemBlockWoodSlab(slab, slab, slabDouble), "slabs.fireproof." + slab.getBlockNumber());
			registerBlock(slabDouble, new ItemBlockWoodSlab(slabDouble, slab, slabDouble), "slabs.double.fireproof." + slabDouble.getBlockNumber());
			registerOreDictWildcard("slabWood", slab);
		}

		fences = BlockArbFence.create(false);
		for (BlockArbFence block : fences) {
			registerBlock(block, ItemBlockWood.class, "fences." + block.getBlockNumber());
			registerOreDictWildcard("fenceWood", block);
		}

		fencesFireproof = BlockArbFence.create(true);
		for (BlockArbFence block : fencesFireproof) {
			registerBlock(block, ItemBlockWood.class, "fences.fireproof." + block.getBlockNumber());
			registerOreDictWildcard("fenceWood", block);
		}
		
		fenceGates = new ArrayList<>();
		fenceGatesFireproof = new ArrayList<>();
		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			BlockArbFenceGate fence = registerBlock(new BlockArbFenceGate(false, woodType), ItemBlockWood.class, "fence.gates." + woodType);
			registerOreDictWildcard("fenceGateWood", fence);
			fenceGates.add(fence);

			BlockArbFenceGate fenceFireproof = registerBlock(new BlockArbFenceGate(true, woodType), ItemBlockWood.class, "fence.gates.fireproof." + woodType);
			registerOreDictWildcard("fenceGateWood", fenceFireproof);
			fenceGatesFireproof.add(fenceFireproof);
		}

		stairs = new ArrayList<>();
		for (BlockArbPlanks plank : planks) {
			for (IBlockState blockState : plank.getBlockState().getValidStates()) {
				int meta = plank.getMetaFromState(blockState);
				EnumWoodType woodType = plank.getWoodType(meta);

				BlockArbStairs stair = new BlockArbStairs(false, blockState, woodType);
				stair = registerBlock(stair, ItemBlockWood.class, "stairs." + woodType);
				registerOreDictWildcard("stairWood", stair);
				stairs.add(stair);
			}
		}

		stairsFireproof = new ArrayList<>();
		for (BlockArbPlanks plank : planksFireproof) {
			for (IBlockState blockState : plank.getBlockState().getValidStates()) {
				int meta = plank.getMetaFromState(blockState);
				EnumWoodType woodType = plank.getWoodType(meta);

				BlockArbStairs stair = new BlockArbStairs(true, blockState, woodType);
				stair = registerBlock(stair, ItemBlockWood.class, "stairs.fireproof." + woodType);
				registerOreDictWildcard("stairWood", stair);
				stairsFireproof.add(stair);
			}
		}

		doors = new ArrayList<>();
		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			BlockArbDoor door = registerBlock(new BlockArbDoor(woodType), ItemBlockWoodDoor.class, "doors." + woodType);
			registerOreDictWildcard("doorWood", door);
			doors.add(door);
		}
		
		// Saplings
		TreeDefinition.preInit();
		saplingGE = registerBlock(new BlockSapling(), ItemBlockForestry.class, "saplingGE");
		registerOreDictWildcard("treeSapling", saplingGE);
		
		// Leaves
		leaves = registerBlock(new BlockForestryLeaves(), ItemBlockLeaves.class, "leaves");
		registerOreDictWildcard("treeLeaves", leaves);

		leavesDecorative = BlockDecorativeLeaves.create();
		speciesToLeavesDecorative = new HashMap<>();
		for (BlockDecorativeLeaves leaves : leavesDecorative) {
			registerBlock(leaves, new ItemBlockDecorativeLeaves(leaves), "leaves.decorative." + leaves.getBlockNumber());
			registerOreDictWildcard("treeLeaves", leaves);

			for (IBlockState state : leaves.getBlockState().getValidStates()) {
				TreeDefinition treeDefinition = state.getValue(leaves.getVariant());
				String speciesUid = treeDefinition.getUID();
				int meta = leaves.getMetaFromState(state);
				speciesToLeavesDecorative.put(speciesUid, new ItemStack(leaves, 1, meta));
			}
		}
		
		// Pods
		AlleleFruit.createAlleles();
		podsMap = new HashMap<>();
		for (BlockFruitPod pod : BlockFruitPod.create()) {
			IAlleleFruit fruit = pod.getFruit();
			registerBlock(pod, "pods." + fruit.getModelName());
			podsMap.put(fruit.getUID(), pod);
		}

		// Machines
		arboriculture = registerBlock(new BlockArboriculture(), ItemBlockForestry.class, "arboriculture");
	}

	public ItemStack getDecorativeLeaves(String speciesUid) {
		return speciesToLeavesDecorative.get(speciesUid);
	}

	public BlockFruitPod getFruitPod(IAlleleFruit fruit) {
		return podsMap.get(fruit.getUID());
	}
}
