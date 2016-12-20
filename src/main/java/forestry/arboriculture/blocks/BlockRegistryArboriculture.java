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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import forestry.arboriculture.genetics.alleles.AlleleFruits;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.items.ItemBlockDecorativeLeaves;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockPile;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWoodDoor;
import forestry.arboriculture.items.ItemBlockWoodSlab;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.OreDictUtil;

public class BlockRegistryArboriculture extends BlockRegistry {
	public final List<BlockArbLog> logs;
	public final List<BlockArbLog> logsFireproof;
	public final List<BlockFireproofVanillaLog> logsVanillaFireproof;
	public final List<BlockArbPlanks> planks;
	public final List<BlockArbPlanks> planksFireproof;
	public final List<BlockFireproofVanillaPlanks> planksVanillaFireproof;
	public final List<BlockArbSlab> slabs;
	public final List<BlockArbSlab> slabsDouble;
	public final List<BlockArbSlab> slabsFireproof;
	public final List<BlockArbSlab> slabsDoubleFireproof;
	public final List<BlockFireproofVanillaSlab> slabsVanillaFireproof;
	public final List<BlockFireproofVanillaSlab> slabsVanillaDoubleFireproof;
	public final List<BlockArbFence> fences;
	public final List<BlockArbFence> fencesFireproof;
	public final List<BlockFireproofVanillaFence> fencesVanillaFireproof;
	public final List<BlockForestryFenceGate> fenceGates;
	public final List<BlockForestryFenceGate> fenceGatesFireproof;
	public final List<BlockForestryFenceGate> fenceGatesVanillaFireproof;
	public final List<BlockForestryStairs> stairs;
	public final List<BlockForestryStairs> stairsFireproof;
	public final List<BlockForestryStairs> stairsVanillaFireproof;
	public final List<BlockArbDoor> doors;

	public final BlockSapling saplingGE;
	public final BlockForestryLeaves leaves;
	public final List<BlockDecorativeLeaves> leavesDecorative;
	private final Map<String, ItemStack> speciesToLeavesDecorative;
	public final Map<String, BlockFruitPod> podsMap;
	public final Map<EnumPileType, BlockPile> piles;

	public final BlockArboriculture treeChest;

	public BlockRegistryArboriculture() {
		// Wood blocks
		logs = BlockArbLog.create(false);
		for (BlockArbLog block : logs) {
			registerBlock(block, new ItemBlockWood(block), "logs." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.LOG_WOOD, block);
		}

		logsFireproof = BlockArbLog.create(true);
		for (BlockArbLog block : logsFireproof) {
			registerBlock(block, new ItemBlockWood(block), "logs.fireproof." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.LOG_WOOD, block);
		}

		logsVanillaFireproof = BlockFireproofVanillaLog.create();
		for (BlockFireproofVanillaLog block : logsVanillaFireproof) {
			registerBlock(block, new ItemBlockWood(block), "logs.vanilla.fireproof." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.LOG_WOOD, block);
		}

		planks = BlockArbPlanks.create(false);
		for (BlockArbPlanks block : planks) {
			registerBlock(block, new ItemBlockWood(block), "planks." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.PLANK_WOOD, block);
		}

		planksFireproof = BlockArbPlanks.create(true);
		for (BlockArbPlanks block : planksFireproof) {
			registerBlock(block, new ItemBlockWood(block), "planks.fireproof." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.PLANK_WOOD, block);
		}

		planksVanillaFireproof = BlockFireproofVanillaPlanks.create();
		for (BlockFireproofVanillaPlanks block : planksVanillaFireproof) {
			registerBlock(block, new ItemBlockWood(block), "planks.vanilla.fireproof." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.PLANK_WOOD, block);
		}

		slabs = BlockArbSlab.create(false, false);
		slabsDouble = BlockArbSlab.create(false, true);
		for (int i = 0; i < slabs.size(); i++) {
			BlockArbSlab slab = slabs.get(i);
			BlockArbSlab slabDouble = slabsDouble.get(i);
			registerBlock(slab, new ItemBlockWoodSlab(slab, slab, slabDouble), "slabs." + slab.getBlockNumber());
			registerBlock(slabDouble, new ItemBlockWoodSlab(slabDouble, slab, slabDouble), "slabs.double." + slabDouble.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.SLAB_WOOD, slab);
		}

		slabsFireproof = BlockArbSlab.create(true, false);
		slabsDoubleFireproof = BlockArbSlab.create(true, true);
		for (int i = 0; i < slabsFireproof.size(); i++) {
			BlockArbSlab slab = slabsFireproof.get(i);
			BlockArbSlab slabDouble = slabsDoubleFireproof.get(i);
			registerBlock(slab, new ItemBlockWoodSlab(slab, slab, slabDouble), "slabs.fireproof." + slab.getBlockNumber());
			registerBlock(slabDouble, new ItemBlockWoodSlab(slabDouble, slab, slabDouble), "slabs.double.fireproof." + slabDouble.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.SLAB_WOOD, slab);
		}

		slabsVanillaFireproof = BlockFireproofVanillaSlab.create(false);
		slabsVanillaDoubleFireproof = BlockFireproofVanillaSlab.create(true);
		for (int i = 0; i < slabsVanillaFireproof.size(); i++) {
			BlockForestrySlab slab = slabsVanillaFireproof.get(i);
			BlockForestrySlab slabDouble = slabsVanillaDoubleFireproof.get(i);
			registerBlock(slab, new ItemBlockWoodSlab(slab, slab, slabDouble), "slabs.vanilla.fireproof." + slab.getBlockNumber());
			registerBlock(slabDouble, new ItemBlockWoodSlab(slabDouble, slab, slabDouble), "slabs.vanilla.double.fireproof." + slabDouble.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.SLAB_WOOD, slab);
		}

		fences = BlockArbFence.create(false);
		for (BlockArbFence block : fences) {
			registerBlock(block, new ItemBlockWood(block), "fences." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.FENCE_WOOD, block);
		}

		fencesFireproof = BlockArbFence.create(true);
		for (BlockArbFence block : fencesFireproof) {
			registerBlock(block, new ItemBlockWood(block), "fences.fireproof." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.FENCE_WOOD, block);
		}

		fencesVanillaFireproof = BlockFireproofVanillaFence.create();
		for (BlockFireproofVanillaFence block : fencesVanillaFireproof) {
			registerBlock(block, new ItemBlockWood(block), "fences.vanilla.fireproof." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.FENCE_WOOD, block);
		}
		
		fenceGates = new ArrayList<>();
		fenceGatesFireproof = new ArrayList<>();
		for (EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			BlockForestryFenceGate fenceGate = new BlockForestryFenceGate<>(false, woodType);
			registerBlock(fenceGate, new ItemBlockWood(fenceGate), "fence.gates." + woodType);
			registerOreDictWildcard(OreDictUtil.FENCE_GATE_WOOD, fenceGate);
			fenceGates.add(fenceGate);

			BlockForestryFenceGate fenceGateFireproof = new BlockForestryFenceGate<>(true, woodType);
			registerBlock(fenceGateFireproof, new ItemBlockWood(fenceGateFireproof), "fence.gates.fireproof." + woodType);
			registerOreDictWildcard(OreDictUtil.FENCE_GATE_WOOD, fenceGateFireproof);
			fenceGatesFireproof.add(fenceGateFireproof);
		}

		fenceGatesVanillaFireproof = new ArrayList<>();
		for (EnumVanillaWoodType woodType : EnumVanillaWoodType.VALUES) {
			BlockForestryFenceGate fenceGateFireproof = new BlockForestryFenceGate<>(true, woodType);
			registerBlock(fenceGateFireproof, new ItemBlockWood(fenceGateFireproof), "fence.gates.vanilla.fireproof." + woodType);
			registerOreDictWildcard(OreDictUtil.FENCE_GATE_WOOD, fenceGateFireproof);
			fenceGatesFireproof.add(fenceGateFireproof);
		}

		stairs = new ArrayList<>();
		for (BlockArbPlanks plank : planks) {
			for (IBlockState blockState : plank.getBlockState().getValidStates()) {
				int meta = plank.getMetaFromState(blockState);
				EnumForestryWoodType woodType = plank.getWoodType(meta);

				BlockForestryStairs stair = new BlockForestryStairs<>(false, blockState, woodType);
				registerBlock(stair, new ItemBlockWood(stair), "stairs." + woodType);
				registerOreDictWildcard(OreDictUtil.STAIR_WOOD, stair);
				stairs.add(stair);
			}
		}

		stairsFireproof = new ArrayList<>();
		for (BlockArbPlanks plank : planksFireproof) {
			for (IBlockState blockState : plank.getBlockState().getValidStates()) {
				int meta = plank.getMetaFromState(blockState);
				EnumForestryWoodType woodType = plank.getWoodType(meta);

				BlockForestryStairs stair = new BlockForestryStairs<>(true, blockState, woodType);
				registerBlock(stair, new ItemBlockWood(stair), "stairs.fireproof." + woodType);
				registerOreDictWildcard(OreDictUtil.STAIR_WOOD, stair);
				stairsFireproof.add(stair);
			}
		}

		stairsVanillaFireproof = new ArrayList<>();
		for (BlockFireproofVanillaPlanks plank : planksVanillaFireproof) {
			for (IBlockState blockState : plank.getBlockState().getValidStates()) {
				int meta = plank.getMetaFromState(blockState);
				EnumVanillaWoodType woodType = plank.getWoodType(meta);

				BlockForestryStairs stair = new BlockForestryStairs<>(true, blockState, woodType);
				registerBlock(stair, new ItemBlockWood(stair), "stairs.vanilla.fireproof." + woodType);
				registerOreDictWildcard(OreDictUtil.STAIR_WOOD, stair);
				stairsVanillaFireproof.add(stair);
			}
		}

		doors = new ArrayList<>();
		for (EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			BlockArbDoor door = new BlockArbDoor(woodType);
			registerBlock(door, new ItemBlockWoodDoor(door), "doors." + woodType);
			registerOreDictWildcard(OreDictUtil.DOOR_WOOD, door);
			doors.add(door);
		}
		
		// Saplings
		TreeDefinition.preInit();
		saplingGE = new BlockSapling();
		registerBlock(saplingGE, new ItemBlockForestry(saplingGE), "saplingGE");
		registerOreDictWildcard(OreDictUtil.TREE_SAPLING, saplingGE);
		
		// Leaves
		leaves = new BlockForestryLeaves();
		registerBlock(leaves, new ItemBlockLeaves(leaves), "leaves");
		registerOreDictWildcard(OreDictUtil.TREE_LEAVES, leaves);

		leavesDecorative = BlockDecorativeLeaves.create();
		speciesToLeavesDecorative = new HashMap<>();
		for (BlockDecorativeLeaves leaves : leavesDecorative) {
			registerBlock(leaves, new ItemBlockDecorativeLeaves(leaves), "leaves.decorative." + leaves.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.TREE_LEAVES, leaves);

			for (IBlockState state : leaves.getBlockState().getValidStates()) {
				TreeDefinition treeDefinition = state.getValue(leaves.getVariant());
				String speciesUid = treeDefinition.getUID();
				int meta = leaves.getMetaFromState(state);
				speciesToLeavesDecorative.put(speciesUid, new ItemStack(leaves, 1, meta));
			}
		}
		
		piles = BlockPile.create();
		for(Entry<EnumPileType, BlockPile> pile : piles.entrySet()){
			registerBlock(pile.getValue(), new ItemBlockPile(pile.getValue()), "pile_" + pile.getKey().name().toLowerCase(Locale.ENGLISH));
		}
		
		// Pods
		AlleleFruits.registerAlleles();
		podsMap = new HashMap<>();
		for (BlockFruitPod pod : BlockFruitPod.create()) {
			IAlleleFruit fruit = pod.getFruit();
			registerBlock(pod, "pods." + fruit.getModelName());
			podsMap.put(fruit.getUID(), pod);
		}

		// Machines
		treeChest = new BlockArboriculture(BlockTypeArboricultureTesr.ARB_CHEST);
		registerBlock(treeChest, new ItemBlockForestry(treeChest), "tree_chest");
	}

	public ItemStack getDecorativeLeaves(String speciesUid) {
		ItemStack itemStack = speciesToLeavesDecorative.get(speciesUid);
		if (itemStack == null) {
			return ItemStack.EMPTY;
		}
		return itemStack.copy();
	}

	@Nullable
	public BlockFruitPod getFruitPod(IAlleleFruit fruit) {
		return podsMap.get(fruit.getUID());
	}
}
