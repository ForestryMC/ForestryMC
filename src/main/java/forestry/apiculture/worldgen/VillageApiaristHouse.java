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
package forestry.apiculture.worldgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.flowers.Flower;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.arboriculture.PluginArboriculture;
import forestry.core.PluginCore;
import forestry.core.blocks.BlockCore;
import forestry.core.config.Constants;
import forestry.core.tiles.TileUtil;
import forestry.plugins.ForestryPluginUids;

public class VillageApiaristHouse extends StructureVillagePieces.House1 {

	private static final Random random = new Random();

	private int averageGroundLevel = -1;
	private boolean isInDesert = false;
	private IBlockState planks;
	private IBlockState logs;
	private IBlockState stairs;
	private IBlockState fence;
	private IBlockState door;
	private IBlockState fenceGate;

	@SuppressWarnings("unused")
	public VillageApiaristHouse() {
		createBuildingBlocks(random);
	}

	public VillageApiaristHouse(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing) {
		super(startPiece, componentType, random, boundingBox, facing);

		isInDesert = startPiece.inDesert;

		createBuildingBlocks(random);
	}

	private void createBuildingBlocks(Random random) {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {

			boolean fireproof = random.nextInt(4) == 0;

			EnumWoodType woodType = EnumWoodType.getRandom(random);

			IWoodAccess woodAccess = TreeManager.woodAccess;
			this.logs = woodAccess.getLogBlock(woodType, fireproof).withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
			this.planks = woodAccess.getPlanksBlock(woodType, fireproof);
			this.stairs = woodAccess.getStairsBlock(woodType, fireproof);
			this.fence = woodAccess.getFenceBlock(woodType, fireproof);
			this.door = woodAccess.getDoorBlock(woodType);
			this.fenceGate = woodAccess.getFenceGateBlock(woodType, fireproof);
		} else {
			if (random.nextInt(6) < 4) {
				this.logs = getRandomVariant(random, Blocks.LOG.getDefaultState(), BlockOldLog.VARIANT);
			} else {
				this.logs = getRandomVariant(random, Blocks.LOG2.getDefaultState(), BlockNewLog.VARIANT);
			}
			this.planks = getRandomVariant(random, Blocks.PLANKS.getDefaultState(), BlockPlanks.VARIANT);

			List<Block> stairs = Arrays.asList(
					Blocks.ACACIA_STAIRS, Blocks.BIRCH_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.SPRUCE_STAIRS
			);
			this.stairs = stairs.get(random.nextInt(stairs.size())).getDefaultState();

			List<Block> fences = Arrays.asList(
				Blocks.ACACIA_FENCE, Blocks.BIRCH_FENCE, Blocks.DARK_OAK_FENCE, Blocks.JUNGLE_FENCE, Blocks.SPRUCE_FENCE
			);
			this.fence = fences.get(random.nextInt(fences.size())).getDefaultState();

			this.door = Blocks.OAK_DOOR.getDefaultState();
			this.fenceGate = Blocks.OAK_FENCE_GATE.getDefaultState();
		}
	}

	private static <T extends Comparable<T>> IBlockState getRandomVariant(Random random, IBlockState defaultState, IProperty<T> variantProperty) {
		Collection<T> allowedValues = variantProperty.getAllowedValues();
		List<T> allowedValuesList = new ArrayList<>(allowedValues);
		T randomValue = allowedValuesList.get(random.nextInt(allowedValuesList.size()));
		return defaultState.withProperty(variantProperty, randomValue);
	}

	public static VillageApiaristHouse buildComponent(StructureVillagePieces.Start startPiece, List<StructureComponent> par1List, Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) {
		StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX, structureMinY, structureMinZ, 0, 0, 0, 10, 9, 11, facing);
		if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(par1List, bbox) != null) {
			return null;
		}

		return new VillageApiaristHouse(startPiece, componentType, random, bbox, facing);
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox structBoundingBox) {

		if (averageGroundLevel < 0) {
			averageGroundLevel = getAverageGroundLevel(world, structBoundingBox);
			if (averageGroundLevel < 0) {
				return true;
			}

			boundingBox.offset(0, averageGroundLevel - boundingBox.maxY + 8 - 1, 0);
		}

		fillWithBlocks(world, structBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

		// Garden
		buildGarden(world, structBoundingBox);

		// Garden fence
		fillWithBlocks(world, structBoundingBox, 1, 1, 6, 1, 1, 10, fence, fence, false);
		fillWithBlocks(world, structBoundingBox, 8, 1, 6, 8, 1, 10, fence, fence, false);
		fillWithBlocks(world, structBoundingBox, 2, 1, 10, 7, 1, 10, fence, fence, false);

		setBlockState(world, fenceGate.withProperty(BlockFenceGate.FACING, EnumFacing.EAST), 8, 1, 8, structBoundingBox);
		setBlockState(world, fenceGate.withProperty(BlockFenceGate.FACING, EnumFacing.EAST), 1, 1, 8, structBoundingBox);
		setBlockState(world, fenceGate.withProperty(BlockFenceGate.FACING, EnumFacing.NORTH), 4, 1, 10, structBoundingBox);

		// Flowers
		plantFlowerGarden(world, structBoundingBox, 2, 1, 5, 7, 1, 9);

		// Apiaries
		buildApiaries(world, structBoundingBox);

		// Floor
		fillWithBlocks(world, structBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);

		IBlockState cobblestoneState = Blocks.COBBLESTONE.getDefaultState();
		fillWithBlocks(world, structBoundingBox, 0, 0, 0, 0, 3, 5, cobblestoneState, cobblestoneState, false);
		fillWithBlocks(world, structBoundingBox, 8, 0, 0, 8, 3, 5, cobblestoneState, cobblestoneState, false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 0, 7, 1, 0, cobblestoneState, cobblestoneState, false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 5, 7, 1, 5, cobblestoneState, cobblestoneState, false);

		fillWithBlocks(world, structBoundingBox, 1, 2, 0, 7, 3, 0, planks, planks, false);
		fillWithBlocks(world, structBoundingBox, 1, 2, 5, 7, 3, 5, planks, planks, false);
		fillWithBlocks(world, structBoundingBox, 0, 4, 1, 8, 4, 1, planks, planks, false);
		fillWithBlocks(world, structBoundingBox, 0, 4, 4, 8, 4, 4, planks, planks, false);
		fillWithBlocks(world, structBoundingBox, 0, 5, 2, 8, 5, 3, planks, planks, false);

		setBlockState(world, planks, 0, 4, 2, structBoundingBox);
		setBlockState(world, planks, 0, 4, 3, structBoundingBox);
		setBlockState(world, planks, 8, 4, 2, structBoundingBox);
		setBlockState(world, planks, 8, 4, 3, structBoundingBox);

		buildRoof(world, structBoundingBox);

		setBlockState(world, logs, 0, 2, 1, structBoundingBox);
		setBlockState(world, logs, 0, 2, 4, structBoundingBox);
		setBlockState(world, logs, 8, 2, 1, structBoundingBox);
		setBlockState(world, logs, 8, 2, 4, structBoundingBox);
		
		IBlockState glassPaneState = Blocks.GLASS_PANE.getDefaultState();
		setBlockState(world, glassPaneState, 0, 2, 2, structBoundingBox);
		setBlockState(world, glassPaneState, 0, 2, 3, structBoundingBox);

		setBlockState(world, glassPaneState, 8, 2, 2, structBoundingBox);
		setBlockState(world, glassPaneState, 8, 2, 3, structBoundingBox);

		// Windows garden side
		setBlockState(world, glassPaneState, 2, 2, 5, structBoundingBox);
		setBlockState(world, glassPaneState, 3, 2, 5, structBoundingBox);
		setBlockState(world, glassPaneState, 4, 2, 5, structBoundingBox);

		setBlockState(world, glassPaneState, 5, 2, 0, structBoundingBox);
		setBlockState(world, glassPaneState, 6, 2, 5, structBoundingBox);

		// Escritoire
		if (random.nextInt(2) == 0) {
			IBlockState escritoireBlock = PluginCore.blocks.escritoire.getDefaultState().withProperty(BlockCore.FACING, EnumFacing.EAST);
			setBlockState(world, escritoireBlock, 1, 1, 3, structBoundingBox);
		}

		IBlockState airState = Blocks.AIR.getDefaultState();

		this.setBlockState(world, this.door.withProperty(BlockDoor.FACING, EnumFacing.NORTH), 2, 1, 0, structBoundingBox);
		this.setBlockState(world, this.door.withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2, 2, 0, structBoundingBox);

		this.setBlockState(world, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH), 2, 3, 1, structBoundingBox);

		if (isAirBlockAtCurrentPosition(world, new BlockPos(2, 0, -1), structBoundingBox) && !isAirBlockAtCurrentPosition(world, new BlockPos(2, -1, -1), structBoundingBox)) {
			setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), 2, 0, -1, structBoundingBox);
		}

		setBlockState(world, airState, 6, 1, 5, structBoundingBox);
		setBlockState(world, airState, 6, 2, 5, structBoundingBox);

		this.setBlockState(world, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH), 6, 3, 4, structBoundingBox);

		this.setBlockState(world, this.door.withProperty(BlockDoor.FACING, EnumFacing.SOUTH), 6, 1, 5, structBoundingBox);
		this.setBlockState(world, this.door.withProperty(BlockDoor.FACING, EnumFacing.SOUTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 6, 2, 5, structBoundingBox);

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 9; ++j) {
				clearCurrentPositionBlocksUpwards(world, j, 7, i, structBoundingBox);
				replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), j, -1, i, structBoundingBox);
			}
		}

		generateChest(world, structBoundingBox, random, 7, 1, 4, Constants.VILLAGE_NATURALIST_LOOT_KEY);

		spawnVillagers(world, boundingBox, 2, 1, 2, 2);

		return true;
	}

	private void buildRoof(World world, StructureBoundingBox structBoundingBox) {
		for (int i = -1; i <= 2; ++i) {
			for (int j = 0; j <= 8; ++j) {
				IBlockState northStairs = stairs.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
				setBlockState(world, northStairs, j, 4 + i, i, structBoundingBox);

				IBlockState southStairs = stairs.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
				setBlockState(world, southStairs, j, 4 + i, 5 - i, structBoundingBox);
			}
		}
	}

	private void buildGarden(World world, StructureBoundingBox box) {

		Block ground = Blocks.DIRT;
		if (isInDesert) {
			ground = Blocks.SAND;
		}

		for (int i = 1; i <= 8; i++) {
			for (int j = 6; j <= 10; j++) {
				replaceAirAndLiquidDownwards(world, ground.getDefaultState(), i, 0, j, box);
			}
		}
	}

	private void plantFlowerGarden(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

		if (isInDesert) {
			setBlockState(world, Blocks.CACTUS.getDefaultState(), 4, 1, 7, box);
			return;
		}

		for (int i = minY; i <= maxY; ++i) {
			for (int j = minX; j <= maxX; ++j) {
				for (int k = minZ; k <= maxZ; ++k) {
					if (world.rand.nextBoolean()) {
						int xCoord = getXWithOffset(j, k);
						int yCoord = getYWithOffset(i);
						int zCoord = getZWithOffset(j, k);

						BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
						IBlockState blockState = world.getBlockState(pos);
						if (!Blocks.RED_FLOWER.canBlockStay(world, pos, blockState)) {
							continue;
						}

						FlowerRegistry flowerRegistry = (FlowerRegistry) FlowerManager.flowerRegistry;
						Flower flower = flowerRegistry.getRandomPlantableFlower(FlowerManager.FlowerTypeVanilla, world.rand);
						setBlockState(world, flower.getBlock().getStateFromMeta(flower.getMeta()), j, i, k, box);
					}
				}
			}
		}
	}

	private void buildApiaries(World world, StructureBoundingBox box) {
		populateApiary(world, box, new BlockPos(3, 1, 8));
		populateApiary(world, box, new BlockPos(6, 1, 8));
	}

	private void populateApiary(World world, StructureBoundingBox box, BlockPos pos) {
		int xCoord = getXWithOffset(pos.getX(), pos.getZ());
		int yCoord = getYWithOffset(pos.getY());
		int zCoord = getZWithOffset(pos.getX(), pos.getZ());
		BlockPos posNew = new BlockPos(xCoord, yCoord, zCoord);

		if (!box.isVecInside(posNew)) {
			return;
		}

		IBlockState blockState = world.getBlockState(posNew);
		if (PluginApiculture.blocks.beeHouse == blockState.getBlock() || !world.isBlockLoaded(posNew.down())) {
			return;
		}

		IBlockState beeHouseDefaultState = PluginApiculture.blocks.beeHouse.getDefaultState();
		world.setBlockState(posNew, beeHouseDefaultState, Constants.FLAG_BLOCK_SYNC);

		TileBeeHouse beeHouse = TileUtil.getTile(world, posNew, TileBeeHouse.class);
		if (beeHouse == null) {
			return;
		}

		ItemStack randomVillagePrincess = getRandomVillageBeeStack(world, posNew, EnumBeeType.PRINCESS);
		beeHouse.getBeeInventory().setQueen(randomVillagePrincess);

		ItemStack randomVillageDrone = getRandomVillageBeeStack(world, posNew, EnumBeeType.DRONE);
		beeHouse.getBeeInventory().setDrone(randomVillageDrone);
	}

	private static ItemStack getRandomVillageBeeStack(World world, BlockPos pos, EnumBeeType beeType) {
		IBee randomVillageBee = getRandomVillageBee(world, pos);
		return BeeManager.beeRoot.getMemberStack(randomVillageBee, beeType);
	}

	private static IBee getRandomVillageBee(World world, BlockPos pos) {

		// Get current biome
		BiomeGenBase biome = world.getBiomeGenForCoords(pos);

		List<IBeeGenome> candidates;
		if (BeeManager.uncommonVillageBees != null && !BeeManager.uncommonVillageBees.isEmpty() && world.rand.nextDouble() < 0.2) {
			candidates = BeeManager.uncommonVillageBees;
		} else {
			candidates = BeeManager.commonVillageBees;
		}

		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome, world, pos);
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(biome.getRainfall());

		// Add bees that can live in this environment
		List<IBeeGenome> valid = new ArrayList<>();
		for (IBeeGenome genome : candidates) {
			if (checkBiomeHazard(genome, biomeTemperature, biomeHumidity)) {
				valid.add(genome);
			}
		}

		// No valid ones found, return any of the common ones.
		if (valid.isEmpty()) {
			int index = world.rand.nextInt(BeeManager.commonVillageBees.size());
			IBeeGenome genome = BeeManager.commonVillageBees.get(index);
			return BeeManager.beeRoot.getBee(genome);
		}

		return BeeManager.beeRoot.getBee(valid.get(world.rand.nextInt(valid.size())));
	}

	private static boolean checkBiomeHazard(IBeeGenome genome, EnumTemperature biomeTemperature, EnumHumidity biomeHumidity) {
		IAlleleBeeSpecies species = genome.getPrimary();
		return AlleleManager.climateHelper.isWithinLimits(biomeTemperature, biomeHumidity,
				species.getTemperature(), genome.getToleranceTemp(),
				species.getHumidity(), genome.getToleranceHumid());
	}

	@Override
	protected int chooseProfession(int villagerCount, int currentVillagerProfession) {
		FMLControlledNamespacedRegistry<VillagerRegistry.VillagerProfession> registry = (FMLControlledNamespacedRegistry<VillagerRegistry.VillagerProfession>) VillagerRegistry.instance().getRegistry();

		if (villagerCount <= 0) {
			return registry.getId(PluginApiculture.villagerApiarist);
		} else if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {
			return registry.getId(PluginArboriculture.villagerArborist);
		} else {
			return currentVillagerProfession;
		}
	}

	private boolean isAirBlockAtCurrentPosition(World world, BlockPos pos, StructureBoundingBox box) {
		IBlockState blockStateFromPos = getBlockStateFromPos(world, pos.getX(), pos.getY(), pos.getZ(), box);
		return blockStateFromPos.getBlock().isAir(blockStateFromPos, world, pos);
	}
}
