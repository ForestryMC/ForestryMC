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
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.core.ItemGroups;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.items.ItemBlockDecorativeLeaves;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWoodDoor;
import forestry.arboriculture.items.ItemBlockWoodSlab;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockBase;

public class BlockRegistryArboriculture extends BlockRegistry {
	//TODO mega table with WoodBlockKind and IWoodType?

	public final Map<EnumForestryWoodType, BlockForestryLog> logs = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryLog> logsFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryLog> logsVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryPlank> planks = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryPlank> planksFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryPlank> planksVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestrySlab> slabs = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestrySlab> slabsFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestrySlab> slabsVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryFence> fences = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryFence> fencesFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryFence> fencesVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryFenceGate> fenceGates = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryFenceGate> fenceGatesFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryFenceGate> fenceGatesVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryStairs> stairs = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryStairs> stairsFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryStairs> stairsVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryDoor> doors = new EnumMap<>(EnumForestryWoodType.class);

	public final BlockSapling saplingGE;
	public final BlockForestryLeaves leaves;
	public final Map<TreeDefinition, BlockDefaultLeaves> leavesDefault = new EnumMap<>(TreeDefinition.class);
	public final Map<TreeDefinition, BlockDefaultLeavesFruit> leavesDefaultFruit = new EnumMap<>(TreeDefinition.class);
	public final Map<TreeDefinition, BlockDecorativeLeaves> leavesDecorative = new EnumMap<>(TreeDefinition.class);
	public final Map<String, BlockFruitPod> podsMap;

	public final BlockArboriculture treeChest;

	public BlockRegistryArboriculture() {
		// Wood blocks

		for (EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			//logs
			BlockForestryLog log = new BlockForestryLog(false, woodType);
			registerBlock(log, new ItemBlockWood<>(log), woodType.toString() + "_log");
			logs.put(woodType, log);

			//logs fireproof
			BlockForestryLog fireproofLog = new BlockForestryLog(true, woodType);
			registerBlock(fireproofLog, new ItemBlockWood<>(fireproofLog), woodType.toString() + "_fireproof_log");
			logsFireproof.put(woodType, fireproofLog);

			//planks
			BlockForestryPlank plank = new BlockForestryPlank(false, woodType);
			registerBlock(plank, new ItemBlockWood<>(plank), woodType.toString() + "_planks");
			planks.put(woodType, plank);

			//planks fireproof
			BlockForestryPlank fireproofPlank = new BlockForestryPlank(true, woodType);
			registerBlock(fireproofPlank, new ItemBlockWood<>(fireproofPlank), woodType.toString() + "_fireproof_planks");
			planksFireproof.put(woodType, fireproofPlank);

			//fences
			BlockForestryFence fence = new BlockForestryFence(false, woodType);
			registerBlock(fence, new ItemBlockWood<>(fence), woodType.toString() + "_fence");
			fences.put(woodType, fence);

			//fences fireproof
			BlockForestryFence fireproofFence = new BlockForestryFence(true, woodType);
			registerBlock(fireproofFence, new ItemBlockWood<>(fireproofFence), woodType.toString() + "_fireproof_fence");
			fencesFireproof.put(woodType, fireproofFence);

			//doors
			BlockForestryDoor door = new BlockForestryDoor(woodType);
			registerBlock(door, new ItemBlockWoodDoor(door), woodType.toString() + "_door");
			doors.put(woodType, door);
		}

		for (EnumVanillaWoodType woodType : EnumVanillaWoodType.VALUES) {
			//planks
			BlockForestryPlank fireproofPlank = new BlockForestryPlank(true, woodType);
			registerBlock(fireproofPlank, new ItemBlockWood<>(fireproofPlank), woodType.toString() + "_fireproof_planks");
			planksVanillaFireproof.put(woodType, fireproofPlank);

			//logs
			BlockForestryLog fireproofLog = new BlockForestryLog(true, woodType);
			registerBlock(fireproofLog, new ItemBlockWood<>(fireproofLog), woodType.toString() + "_fireproof_log");
			logsVanillaFireproof.put(woodType, fireproofLog);

			//fences
			BlockForestryFence fireproofFence = new BlockForestryFence(true, woodType);
			registerBlock(fireproofFence, new ItemBlockWood<>(fireproofFence), woodType.toString() + "_fireproof_fence");
			fencesVanillaFireproof.put(woodType, fireproofFence);
		}

		for (Map.Entry<EnumForestryWoodType, BlockForestryPlank> entry : planks.entrySet()) {
			EnumForestryWoodType woodType = entry.getKey();
			BlockForestryPlank plank = entry.getValue();

			//slabs
			BlockForestrySlab slab = new BlockForestrySlab(plank);
			registerBlock(slab, new ItemBlockWoodSlab(slab), woodType.toString() + "_slab");
			slabs.put(woodType, slab);

			//stairs
			BlockForestryStairs stair = new BlockForestryStairs(plank);
			registerBlock(stair, new ItemBlockWood<>(stair), woodType.toString() + "_stairs");
			stairs.put(woodType, stair);
		}

		for (Map.Entry<EnumForestryWoodType, BlockForestryPlank> entry : planksFireproof.entrySet()) {
			EnumForestryWoodType woodType = entry.getKey();
			BlockForestryPlank plank = entry.getValue();

			//slabs
			BlockForestrySlab slab = new BlockForestrySlab(plank);
			registerBlock(slab, new ItemBlockWoodSlab(slab), woodType.toString() + "_fireproof_slab");
			slabsFireproof.put(woodType, slab);

			//stairs
			BlockForestryStairs stair = new BlockForestryStairs(plank);
			registerBlock(stair, new ItemBlockWood<>(stair), woodType.toString() + "_fireproof_stairs");
			stairsFireproof.put(woodType, stair);
		}

		for (Map.Entry<EnumVanillaWoodType, BlockForestryPlank> entry : planksVanillaFireproof.entrySet()) {
			EnumVanillaWoodType woodType = entry.getKey();
			BlockForestryPlank plank = entry.getValue();

			//slabs
			BlockForestrySlab slab = new BlockForestrySlab(plank);
			registerBlock(slab, new ItemBlockWoodSlab(slab), woodType.toString() + "_fireproof_slab");
			slabsVanillaFireproof.put(woodType, slab);

			//stairs
			BlockForestryStairs stair = new BlockForestryStairs(plank);
			registerBlock(stair, new ItemBlockWood<>(stair), woodType.toString() + "_fireproof_stairs");
			stairsVanillaFireproof.put(woodType, stair);
		}

		for (EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			BlockForestryFenceGate fenceGate = new BlockForestryFenceGate(false, woodType);
			registerBlock(fenceGate, new ItemBlockWood<>(fenceGate), woodType.toString() + "_fence_gate");
			fenceGates.put(woodType, fenceGate);

			BlockForestryFenceGate fenceGateFireproof = new BlockForestryFenceGate(true, woodType);
			registerBlock(fenceGateFireproof, new ItemBlockWood<>(fenceGateFireproof), woodType.toString() + "_fence_gate_fireproof");
			fenceGatesFireproof.put(woodType, fenceGateFireproof);
		}

		for (EnumVanillaWoodType woodType : EnumVanillaWoodType.VALUES) {
			BlockForestryFenceGate fenceGateFireproof = new BlockForestryFenceGate(true, woodType);
			registerBlock(fenceGateFireproof, new ItemBlockWood<>(fenceGateFireproof), woodType.toString() + "_fence_gate_fireproof");
			fenceGatesVanillaFireproof.put(woodType, fenceGateFireproof);
		}

		// Saplings
		saplingGE = new BlockSapling();
		registerBlock(saplingGE, "sapling_ge");

		// Leaves
		leaves = new BlockForestryLeaves();
		registerBlock(leaves, new ItemBlockLeaves(leaves), "leaves");

		for (TreeDefinition definition : TreeDefinition.VALUES) {
			//decorative
			BlockDecorativeLeaves decorativeLeaves = new BlockDecorativeLeaves(definition);
			//TODO block name might be a bit rubbish
			registerBlock(decorativeLeaves, new ItemBlockDecorativeLeaves(decorativeLeaves), definition.getName() + "_decorative_leaves");
			leavesDecorative.put(definition, decorativeLeaves);

			//default
			BlockDefaultLeaves defaultLeaves = new BlockDefaultLeaves(definition);
			registerBlock(defaultLeaves, new ItemBlockLeaves(leaves), definition.getName() + "_default_leaves");
			leavesDefault.put(definition, defaultLeaves);

			//default fruit leaves
			BlockDefaultLeavesFruit defaultLeavesFruit = new BlockDefaultLeavesFruit(definition);
			registerBlock(defaultLeavesFruit, new ItemBlockLeaves(defaultLeavesFruit), definition.getName() + "_default_leaves_fruit");
			leavesDefaultFruit.put(definition, defaultLeavesFruit);
		}

		// Pods
		podsMap = new HashMap<>();
		for (BlockFruitPod pod : BlockFruitPod.create()) {
			IAlleleFruit fruit = pod.getFruit();
			registerBlock(pod, "pods." + fruit.getModelName());
			podsMap.put(fruit.getRegistryName().toString(), pod);
		}

		// Machines
		treeChest = new BlockArboriculture(BlockTypeArboricultureTesr.ARB_CHEST);
		registerBlock(treeChest, new ItemBlockBase<>(treeChest, new Item.Properties().group(ItemGroups.tabArboriculture), BlockTypeArboricultureTesr.ARB_CHEST), "tree_chest");
	}

	//TODO probably slow etc
	public ItemStack getDecorativeLeaves(String speciesUid) {
		Optional<BlockDecorativeLeaves> block = leavesDecorative.entrySet().stream()
			.filter(e -> e.getKey().getUID().equals(speciesUid))
			.findFirst()
			.flatMap(e -> Optional.of(e.getValue()));

		return block.map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	@Nullable
	public BlockState getDefaultLeaves(String speciesUid) {
		Optional<BlockDefaultLeaves> block = leavesDefault.entrySet().stream()
			.filter(e -> e.getKey().getUID().equals(speciesUid))
			.findFirst()
			.flatMap(e -> Optional.of(e.getValue()));

		return block.map(Block::getDefaultState).orElse(null);
	}

	@Nullable
	public BlockState getDefaultLeavesFruit(String speciesUid) {
		Optional<BlockDefaultLeavesFruit> block = leavesDefaultFruit.entrySet().stream()
			.filter(e -> e.getKey().getUID().equals(speciesUid))
			.findFirst()
			.map(Map.Entry::getValue);

		return block.map(Block::getDefaultState).orElse(null);
	}

	@Nullable
	public BlockFruitPod getFruitPod(IAlleleFruit fruit) {
		return podsMap.get(fruit.getRegistryName().toString());
	}

	public Collection<BlockFruitPod> getPods() {
		return podsMap.values();
	}
}
