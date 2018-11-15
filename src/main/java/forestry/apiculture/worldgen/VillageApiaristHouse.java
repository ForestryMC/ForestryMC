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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import net.minecraftforge.registries.ForgeRegistry;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.flowers.Flower;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.ModuleCore;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class VillageApiaristHouse extends StructureVillagePieces.House1 {

	private static final Random random = new Random();

	private final Materials materials;
	private int averageGroundLevel = -1;

	@SuppressWarnings("unused")
	public VillageApiaristHouse() {
		this.materials = new Materials(random);
	}

	public VillageApiaristHouse(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing) {
		super(startPiece, componentType, random, boundingBox, facing);

		this.materials = new Materials(random);
	}

	@Nullable
	public static VillageApiaristHouse buildComponent(StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) {
		StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX, structureMinY, structureMinZ, -4, 0, 0, 12, 9, 12, facing);
		if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(pieces, bbox) != null) {
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

			boundingBox.offset(0, averageGroundLevel - boundingBox.maxY + 9 - 1, 0);
		}

		fillWithBlocks(world, structBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

		// Garden
		buildGarden(world, structBoundingBox);

		// Garden fence
		fillWithBlocks(world, structBoundingBox, 1, 1, 6, 1, 1, 10, materials.fence, materials.fence, false);
		fillWithBlocks(world, structBoundingBox, 8, 1, 6, 8, 1, 10, materials.fence, materials.fence, false);
		fillWithBlocks(world, structBoundingBox, 2, 1, 10, 7, 1, 10, materials.fence, materials.fence, false);

		setBlockState(world, materials.fenceGate.withProperty(BlockHorizontal.FACING, EnumFacing.EAST), 8, 1, 8, structBoundingBox);
		setBlockState(world, materials.fenceGate.withProperty(BlockHorizontal.FACING, EnumFacing.EAST), 1, 1, 8, structBoundingBox);
		setBlockState(world, materials.fenceGate.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH), 4, 1, 10, structBoundingBox);

		// Flowers
		plantFlowerGarden(world, structBoundingBox, 2, 1, 5, 7, 1, 9);

		// Apiaries
		buildApiaries(world, structBoundingBox);

		// Floor
		IBlockState slabFloor = materials.slabs.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
		fillWithBlocks(world, structBoundingBox, 2, 0, 1, 6, 0, 4, slabFloor, slabFloor, false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 1, 1, 0, 4, materials.planks, materials.planks, false);
		fillWithBlocks(world, structBoundingBox, 7, 0, 1, 7, 0, 4, materials.planks, materials.planks, false);

		IBlockState cobblestoneState = Blocks.COBBLESTONE.getDefaultState();
		fillWithBlocks(world, structBoundingBox, 0, 0, 0, 0, 2, 5, cobblestoneState, cobblestoneState, false);
		fillWithBlocks(world, structBoundingBox, 8, 0, 0, 8, 2, 5, cobblestoneState, cobblestoneState, false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 0, 7, 1, 0, cobblestoneState, cobblestoneState, false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 5, 7, 1, 5, cobblestoneState, cobblestoneState, false);

		fillWithBlocks(world, structBoundingBox, 0, 3, 0, 0, 3, 5, materials.planks, materials.planks, false);
		fillWithBlocks(world, structBoundingBox, 8, 3, 0, 8, 3, 5, materials.planks, materials.planks, false);
		fillWithBlocks(world, structBoundingBox, 1, 2, 0, 7, 3, 0, materials.planks, materials.planks, false);
		fillWithBlocks(world, structBoundingBox, 1, 2, 5, 7, 3, 5, materials.planks, materials.planks, false);

		// Ceiling
		IBlockState slabCeiling = materials.slabs.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
		fillWithBlocks(world, structBoundingBox, 1, 4, 1, 7, 4, 4, slabCeiling, slabCeiling, false);

		IBlockState logBracing = materials.logs.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
		fillWithBlocks(world, structBoundingBox, 0, 4, 1, 8, 4, 1, logBracing, logBracing, false);
		fillWithBlocks(world, structBoundingBox, 0, 4, 4, 8, 4, 4, logBracing, logBracing, false);

		fillWithBlocks(world, structBoundingBox, 0, 5, 2, 8, 5, 3, materials.planks, materials.planks, false);

		setBlockState(world, materials.planks, 0, 4, 2, structBoundingBox);
		setBlockState(world, materials.planks, 0, 4, 3, structBoundingBox);
		setBlockState(world, materials.planks, 8, 4, 2, structBoundingBox);
		setBlockState(world, materials.planks, 8, 4, 3, structBoundingBox);

		buildRoof(world, structBoundingBox);

		// sides of windows
		setBlockState(world, materials.planks, 0, 2, 1, structBoundingBox);
		setBlockState(world, materials.planks, 0, 2, 4, structBoundingBox);
		setBlockState(world, materials.planks, 8, 2, 1, structBoundingBox);
		setBlockState(world, materials.planks, 8, 2, 4, structBoundingBox);

		IBlockState glassPaneState = Blocks.GLASS_PANE.getDefaultState();

		// Windows on east side
		setBlockState(world, glassPaneState, 0, 2, 2, structBoundingBox);
		setBlockState(world, glassPaneState, 0, 2, 3, structBoundingBox);
		// stairs over window
		IBlockState eastStairs = materials.stairs.withProperty(BlockStairs.FACING, EnumFacing.EAST);
		setBlockState(world, eastStairs, -1, 3, 2, structBoundingBox);
		setBlockState(world, eastStairs, -1, 3, 3, structBoundingBox);

		// Windows on west side
		setBlockState(world, glassPaneState, 8, 2, 2, structBoundingBox);
		setBlockState(world, glassPaneState, 8, 2, 3, structBoundingBox);
		// stairs over window
		IBlockState westStairs = materials.stairs.withProperty(BlockStairs.FACING, EnumFacing.WEST);
		setBlockState(world, westStairs, 9, 3, 2, structBoundingBox);
		setBlockState(world, westStairs, 9, 3, 3, structBoundingBox);

		// Windows garden side
		setBlockState(world, glassPaneState, 2, 2, 5, structBoundingBox);
		setBlockState(world, glassPaneState, 3, 2, 5, structBoundingBox);
		setBlockState(world, glassPaneState, 4, 2, 5, structBoundingBox);

		// Windows front side
		setBlockState(world, glassPaneState, 5, 2, 0, structBoundingBox);
		setBlockState(world, glassPaneState, 6, 2, 5, structBoundingBox);

		// Escritoire
		if (random.nextInt(2) == 0) {
			IBlockState escritoireBlock = ModuleCore.getBlocks().escritoire.getDefaultState().withProperty(BlockBase.FACING, EnumFacing.EAST);
			setBlockState(world, escritoireBlock, 1, 1, 3, structBoundingBox);
		}

		IBlockState airState = Blocks.AIR.getDefaultState();

		this.setBlockState(world, materials.door.withProperty(BlockDoor.FACING, EnumFacing.NORTH), 2, 1, 0, structBoundingBox);
		this.setBlockState(world, materials.door.withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2, 2, 0, structBoundingBox);

		this.setBlockState(world, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH), 2, 3, 1, structBoundingBox);

		if (isAirBlockAtCurrentPosition(world, new BlockPos(2, 0, -1), structBoundingBox) && !isAirBlockAtCurrentPosition(world, new BlockPos(2, -1, -1), structBoundingBox)) {
			setBlockState(world, materials.stairs.withProperty(BlockStairs.FACING, EnumFacing.NORTH), 2, 0, -1, structBoundingBox);
		}

		setBlockState(world, airState, 6, 1, 5, structBoundingBox);
		setBlockState(world, airState, 6, 2, 5, structBoundingBox);

		this.setBlockState(world, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH), 6, 3, 4, structBoundingBox);

		this.setBlockState(world, materials.door.withProperty(BlockDoor.FACING, EnumFacing.SOUTH), 6, 1, 5, structBoundingBox);
		this.setBlockState(world, materials.door.withProperty(BlockDoor.FACING, EnumFacing.SOUTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 6, 2, 5, structBoundingBox);

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 9; ++j) {
				clearCurrentPositionBlocksUpwards(world, j, 7, i, structBoundingBox);
				replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), j, -1, i, structBoundingBox);
			}
		}

		generateChest(world, structBoundingBox, random, 7, 1, 3, Constants.VILLAGE_NATURALIST_LOOT_KEY);

		// Inside Corners
		fillWithBlocks(world, structBoundingBox, 1, 1, 1, 1, 3, 1, materials.fence, materials.fence, false);
		fillWithBlocks(world, structBoundingBox, 1, 1, 4, 1, 3, 4, materials.fence, materials.fence, false);
		fillWithBlocks(world, structBoundingBox, 7, 1, 1, 7, 3, 1, materials.fence, materials.fence, false);
		fillWithBlocks(world, structBoundingBox, 7, 1, 4, 7, 3, 4, materials.fence, materials.fence, false);

		spawnVillagers(world, boundingBox, 2, 1, 2, 2);

		return true;
	}

	private void buildRoof(World world, StructureBoundingBox structBoundingBox) {
		for (int z = -1; z <= 2; ++z) {
			for (int x = 0; x <= 8; ++x) {
				IBlockState northStairs = materials.stairs.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
				setBlockState(world, northStairs, x, 4 + z, z, structBoundingBox);

				IBlockState southStairs = materials.stairs.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
				setBlockState(world, southStairs, x, 4 + z, 5 - z, structBoundingBox);
			}
		}
	}

	private void buildGarden(World world, StructureBoundingBox box) {

		Block ground = Blocks.DIRT;
		if (structureType == 1) { // desert
			ground = Blocks.SAND;
		}

		for (int i = 1; i <= 8; i++) {
			for (int j = 6; j <= 10; j++) {
				replaceAirAndLiquidDownwards(world, ground.getDefaultState(), i, 0, j, box);
			}
		}
	}

	private void plantFlowerGarden(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

		if (structureType == 1) { // desert
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
						if (flower != null) {
							setBlockState(world, flower.getBlockState(), j, i, k, box);
						}
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
		if (ModuleApiculture.getBlocks().beeHouse == blockState.getBlock() || !world.isBlockLoaded(posNew.down())) {
			return;
		}

		IBlockState beeHouseDefaultState = ModuleApiculture.getBlocks().beeHouse.getDefaultState();
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
		Biome biome = world.getBiome(pos);

		List<IBeeGenome> candidates;
		if (BeeManager.uncommonVillageBees != null && !BeeManager.uncommonVillageBees.isEmpty() && world.rand.nextDouble() < 0.2) {
			candidates = BeeManager.uncommonVillageBees;
		} else {
			candidates = BeeManager.commonVillageBees;
		}

		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome, pos);
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
		ForgeRegistry<VillagerRegistry.VillagerProfession> registry = (ForgeRegistry<VillagerRegistry.VillagerProfession>) ForgeRegistries.VILLAGER_PROFESSIONS;

		if (villagerCount <= 0) {
			return registry.getID(ModuleApiculture.villagerApiarist);
		} else if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			return registry.getID(ModuleArboriculture.villagerArborist);
		} else {
			return currentVillagerProfession;
		}
	}

	private boolean isAirBlockAtCurrentPosition(World world, BlockPos pos, StructureBoundingBox box) {
		IBlockState blockStateFromPos = getBlockStateFromPos(world, pos.getX(), pos.getY(), pos.getZ(), box);
		return blockStateFromPos.getBlock().isAir(blockStateFromPos, world, pos);
	}

	private static class Materials {
		public final IBlockState planks;
		public final IBlockState slabs;
		public final IBlockState logs;
		public final IBlockState stairs;
		public final IBlockState fence;
		public final IBlockState door;
		public final IBlockState fenceGate;

		public Materials(Random random) {
			IWoodType woodType;
			boolean fireproof;

			if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
				woodType = EnumForestryWoodType.getRandom(random);
				fireproof = random.nextInt(4) == 0;
			} else {
				woodType = EnumVanillaWoodType.getRandom(random);
				fireproof = false;
			}

			IWoodAccess woodAccess = TreeManager.woodAccess;
			this.logs = woodAccess.getBlock(woodType, WoodBlockKind.LOG, fireproof).withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
			this.planks = woodAccess.getBlock(woodType, WoodBlockKind.PLANKS, fireproof);
			this.slabs = woodAccess.getBlock(woodType, WoodBlockKind.SLAB, fireproof);
			this.stairs = woodAccess.getBlock(woodType, WoodBlockKind.STAIRS, fireproof);
			this.fence = woodAccess.getBlock(woodType, WoodBlockKind.FENCE, fireproof);
			this.door = woodAccess.getBlock(woodType, WoodBlockKind.DOOR, false);
			this.fenceGate = woodAccess.getBlock(woodType, WoodBlockKind.FENCE_GATE, fireproof);
		}
	}
}
