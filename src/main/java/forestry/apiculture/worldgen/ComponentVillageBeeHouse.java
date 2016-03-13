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
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import net.minecraftforge.common.ChestGenHooks;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.flowers.Flower;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.apiculture.tiles.TileApiary;
import forestry.arboriculture.worldgen.BlockTypeLog;
import forestry.arboriculture.worldgen.BlockTypeVanillaStairs;
import forestry.arboriculture.worldgen.BlockTypeWood;
import forestry.arboriculture.worldgen.BlockTypeWoodStairs;
import forestry.core.PluginCore;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.config.Constants;
import forestry.core.tiles.TileUtil;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.BlockTypeTileForestry;
import forestry.core.worldgen.IBlockType;
import forestry.plugins.ForestryPluginUids;

public class ComponentVillageBeeHouse extends StructureVillagePieces.House1 {

	private static final Random random = new Random();

	private int averageGroundLevel = -1;
	private boolean isInDesert = false;
	private IBlockType planks;
	private IBlockType logs;
	private IBlockType stairs;
	private IBlockType fence;

	public ComponentVillageBeeHouse() {
		createBuildingBlocks(random);
	}

	public ComponentVillageBeeHouse(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing coordBaseMode) {
		super(startPiece, componentType, random, boundingBox, coordBaseMode);

		isInDesert = startPiece.inDesert;

		createBuildingBlocks(random);
	}

	private void createBuildingBlocks(Random random) {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {

			boolean fireproof = random.nextInt(4) == 0;

			EnumWoodType roofWood = EnumWoodType.getRandom(random);
			EnumWoodType logWood = EnumWoodType.getRandom(random);
			EnumWoodType fenceWood = EnumWoodType.getRandom(random);

			logs = new BlockTypeLog(TreeManager.woodItemAccess.getLog(logWood, fireproof));
			planks = new BlockTypeWood(TreeManager.woodItemAccess.getPlanks(roofWood, fireproof));
			stairs = new BlockTypeWoodStairs(TreeManager.woodItemAccess.getStairs(roofWood, fireproof));
			fence = new BlockTypeWood(TreeManager.woodItemAccess.getFence(fenceWood, fireproof));
		} else {
			int roofMeta = random.nextInt(16);

			logs = new BlockType(Blocks.log, random.nextInt(4));
			planks = new BlockType(Blocks.planks, roofMeta);
			stairs = new BlockTypeVanillaStairs(roofMeta);
			fence = new BlockType(Blocks.oak_fence, 0);
		}
	}

	public static ComponentVillageBeeHouse buildComponent(StructureVillagePieces.Start startPiece, List<StructureComponent> par1List, Random random, int par3, int par4, int par5, EnumFacing par6, int par7) {
		StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 10, 9, 11, par6);
		if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(par1List, bbox) != null) {
			return null;
		}

		return new ComponentVillageBeeHouse(startPiece, par7, random, bbox, par6);
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

		fillWithBlocks(world, structBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

		// Garden
		buildGarden(world, structBoundingBox);

		// Garden fence
		fillBoxWith(world, structBoundingBox, 1, 1, 6, 1, 1, 10, fence, false);
		fillBoxWith(world, structBoundingBox, 8, 1, 6, 8, 1, 10, fence, false);
		fillBoxWith(world, structBoundingBox, 2, 1, 10, 7, 1, 10, fence, false);

		// Flowers
		plantFlowerGarden(world, structBoundingBox, 2, 1, 5, 7, 1, 9);

		// Apiaries
		buildApiaries(world, structBoundingBox);

		// Floor
		fillWithBlocks(world, structBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);

		fillWithBlocks(world, structBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 8, 0, 0, 8, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 5, 7, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);

		fillBoxWith(world, structBoundingBox, 1, 2, 0, 7, 3, 0, planks, false);
		fillBoxWith(world, structBoundingBox, 1, 2, 5, 7, 3, 5, planks, false);
		fillBoxWith(world, structBoundingBox, 0, 4, 1, 8, 4, 1, planks, false);
		fillBoxWith(world, structBoundingBox, 0, 4, 4, 8, 4, 4, planks, false);
		fillBoxWith(world, structBoundingBox, 0, 5, 2, 8, 5, 3, planks, false);

		placeBlockAtCurrentPosition(world, planks, 0, 4, 2, structBoundingBox);
		placeBlockAtCurrentPosition(world, planks, 0, 4, 3, structBoundingBox);
		placeBlockAtCurrentPosition(world, planks, 8, 4, 2, structBoundingBox);
		placeBlockAtCurrentPosition(world, planks, 8, 4, 3, structBoundingBox);

		buildRoof(world, structBoundingBox);

		placeBlockAtCurrentPosition(world, logs, 0, 2, 1, structBoundingBox);
		placeBlockAtCurrentPosition(world, logs, 0, 2, 4, structBoundingBox);
		placeBlockAtCurrentPosition(world, logs, 8, 2, 1, structBoundingBox);
		placeBlockAtCurrentPosition(world, logs, 8, 2, 4, structBoundingBox);
		
		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 0, 2, 2, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 0, 2, 3, structBoundingBox);

		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 8, 2, 2, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 8, 2, 3, structBoundingBox);

		// Windows garden side
		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 2, 2, 5, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 3, 2, 5, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 4, 2, 5, structBoundingBox);

		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 5, 2, 0, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getStateFromMeta(0), 6, 2, 5, structBoundingBox);

		// Escritoire
		if (random.nextInt(2) == 0) {
			IBlockType escritoireBlock = new BlockTypeTileForestry(PluginCore.blocks.core, BlockTypeCoreTesr.ESCRITOIRE.ordinal());
			escritoireBlock.setDirection(getRotatedDirection(EnumFacing.EAST));
			placeBlockAtCurrentPosition(world, escritoireBlock, 1, 1, 3, structBoundingBox);
		}

		setBlockState(world, Blocks.air.getStateFromMeta(0), 2, 1, 0, structBoundingBox);
		setBlockState(world, Blocks.air.getStateFromMeta(0), 2, 2, 0, structBoundingBox);
		placeDoorCurrentPosition(world, structBoundingBox, random, 2, 1, 0, coordBaseMode);

		if (isAirBlockAtCurrentPosition(world, new BlockPos(2, 0, -1), structBoundingBox) && !isAirBlockAtCurrentPosition(world, new BlockPos(2, -1, -1), structBoundingBox)) {
			setBlockState(world, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structBoundingBox);
		}

		setBlockState(world, Blocks.air.getStateFromMeta(0), 6, 1, 5, structBoundingBox);
		setBlockState(world, Blocks.air.getStateFromMeta(0), 6, 2, 5, structBoundingBox);

		// Candles / Lighting
		setBlockState(world, Blocks.torch.getStateFromMeta(0), 2, 3, 4, structBoundingBox);
		setBlockState(world, Blocks.torch.getStateFromMeta(0), 6, 3, 4, structBoundingBox);
		setBlockState(world, Blocks.torch.getStateFromMeta(0), 2, 3, 1, structBoundingBox);
		setBlockState(world, Blocks.torch.getStateFromMeta(0), 6, 3, 1, structBoundingBox);

		placeDoorCurrentPosition(world, structBoundingBox, random, 6, 1, 5, coordBaseMode);

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 9; ++j) {
				clearCurrentPositionBlocksUpwards(world, j, 7, i, structBoundingBox);
				replaceAirAndLiquidDownwards(world, Blocks.cobblestone.getStateFromMeta(0), j, -1, i, structBoundingBox);
			}
		}

		generateChestContents(world, structBoundingBox, random, 7, 1, 4,
				ChestGenHooks.getItems(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, random),
				random.nextInt(4) + random.nextInt(4) + 5);

		spawnVillagers(world, boundingBox, 7, 1, 1, 2);

		return true;
	}

	private void buildRoof(World world, StructureBoundingBox structBoundingBox) {
		for (int i = -1; i <= 2; ++i) {
			for (int j = 0; j <= 8; ++j) {
				stairs.setDirection(getRotatedDirection(EnumFacing.NORTH));
				placeBlockAtCurrentPosition(world, stairs, j, 4 + i, i, structBoundingBox);

				stairs.setDirection(getRotatedDirection(EnumFacing.SOUTH));
				placeBlockAtCurrentPosition(world, stairs, j, 4 + i, 5 - i, structBoundingBox);
			}
		}
	}

	private void buildGarden(World world, StructureBoundingBox box) {

		Block ground = Blocks.dirt;
		if (isInDesert) {
			ground = Blocks.sand;
		}

		for (int i = 1; i <= 8; i++) {
			for (int j = 6; j <= 10; j++) {
				replaceAirAndLiquidDownwards(world, ground.getStateFromMeta(0), i, 0, j, box);
			}
		}
	}

	private void plantFlowerGarden(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

		if (isInDesert) {
			setBlockState(world, Blocks.cactus.getStateFromMeta(0), 4, 1, 7, box);
			return;
		}

		for (int i = minY; i <= maxY; ++i) {
			for (int j = minX; j <= maxX; ++j) {
				for (int k = minZ; k <= maxZ; ++k) {
					if (world.rand.nextBoolean()) {
						int xCoord = getXWithOffset(j, k);
						int yCoord = getYWithOffset(i);
						int zCoord = getZWithOffset(j, k);

						if (!Blocks.red_flower.canBlockStay(world, new BlockPos(xCoord, yCoord, zCoord), world.getBlockState(new BlockPos(xCoord, yCoord, zCoord)))) {
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
		if (PluginApiculture.blocks.apiculture == blockState.getBlock() || !world.isBlockLoaded(posNew.add(0, -1, 0))) {
			return;
		}

		world.setBlockState(posNew, PluginApiculture.blocks.apiculture.getStateFromMeta(BlockTypeApiculture.APIARY.ordinal()), Constants.FLAG_BLOCK_SYNCH);
		PluginApiculture.blocks.apiculture.onBlockAdded(world, posNew, PluginApiculture.blocks.apiculture.getStateFromMeta(BlockTypeApiculture.APIARY.ordinal()));

		TileApiary apiary = TileUtil.getTile(world, posNew, TileApiary.class);
		if (apiary == null) {
			return;
		}

		ItemStack randomVillagePrincess = getRandomVillageBeeStack(world, posNew, EnumBeeType.PRINCESS);
		apiary.getBeeInventory().setQueen(randomVillagePrincess);

		ItemStack randomVillageDrone = getRandomVillageBeeStack(world, posNew, EnumBeeType.DRONE);
		apiary.getBeeInventory().setDrone(randomVillageDrone);

		for (int i = InventoryApiary.SLOT_FRAMES_1; i < InventoryApiary.SLOT_FRAMES_1 + InventoryApiary.SLOT_FRAMES_COUNT; i++) {
			ItemStack randomFrame = getRandomFrame(world.rand);
			apiary.setInventorySlotContents(i, randomFrame);
		}
	}

	private static ItemStack getRandomFrame(Random random) {
		float roll = random.nextFloat();
		if (roll < 0.2f) {
			return PluginApiculture.items.frameUntreated.getItemStack();
		} else if (roll < 0.4f) {
			return PluginApiculture.items.frameImpregnated.getItemStack();
		} else if (roll < 0.6) {
			return PluginApiculture.items.frameProven.getItemStack();
		} else {
			return null;
		}
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

		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome, pos);
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(biome.rainfall);

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
			return BeeManager.beeRoot.getBee(world, genome);
		}

		return BeeManager.beeRoot.getBee(world, valid.get(world.rand.nextInt(valid.size())));
	}

	private static boolean checkBiomeHazard(IBeeGenome genome, EnumTemperature biomeTemperature, EnumHumidity biomeHumidity) {
		IAlleleBeeSpecies species = genome.getPrimary();
		return AlleleManager.climateHelper.isWithinLimits(biomeTemperature, biomeHumidity,
				species.getTemperature(), genome.getToleranceTemp(),
				species.getHumidity(), genome.getToleranceHumid());
	}

	private void fillBoxWith(World world, StructureBoundingBox box, int par3, int par4, int par5, int par6, int par7, int par8, IBlockType block, boolean replace) {

		for (int var14 = par4; var14 <= par7; ++var14) {
			for (int var15 = par3; var15 <= par6; ++var15) {
				for (int var16 = par5; var16 <= par8; ++var16) {
					if (!replace || !isAirBlockAtCurrentPosition(world, new BlockPos(var15, var14, var16), box)) {
						placeBlockAtCurrentPosition(world, block, var15, var14, var16, box);
					}
				}
			}
		}
	}

	private void placeBlockAtCurrentPosition(World world, IBlockType block, int x, int y, int z, StructureBoundingBox par7StructureBoundingBox) {
		int xC = getXWithOffset(x, z);
		int yC = getYWithOffset(y);
		int zC = getZWithOffset(x, z);
		BlockPos pos = new BlockPos(xC, yC, zC);

		if (par7StructureBoundingBox.isVecInside(pos)) {
			block.setBlock(world, pos);
		}
	}
	
	@Override
	protected int func_180779_c(int villagerCount, int p_180779_2_) {
		if (villagerCount <= 0) {
			return Constants.ID_VILLAGER_BEEKEEPER;
		} else {
			return Constants.ID_VILLAGER_LUMBERJACK;
		}
	}

	private boolean isAirBlockAtCurrentPosition(World world, BlockPos pos, StructureBoundingBox box) {
		return getBlockStateFromPos(world, pos.getX(), pos.getY(), pos.getZ(), box).getBlock().isAir(world, pos);
	}

	/** rotates a direction according to the way the structure is facing **/
	private EnumFacing getRotatedDirection(EnumFacing direction) {
		int stairDirection = 5 - direction.ordinal();
		int meta = getMetadataWithOffset(Blocks.oak_stairs, stairDirection);
		return EnumFacing.values()[5 - meta];
	}

}
