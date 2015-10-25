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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
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
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFlower;
import forestry.apiculture.gadgets.TileApiary;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.worldgen.BlockTypeLog;
import forestry.arboriculture.worldgen.BlockTypeVanillaStairs;
import forestry.arboriculture.worldgen.BlockTypeWood;
import forestry.arboriculture.worldgen.BlockTypeWoodStairs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.utils.WorldUtils;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.BlockTypeTileForestry;
import forestry.core.worldgen.IBlockType;
import forestry.plugins.PluginManager;

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

	public ComponentVillageBeeHouse(StructureVillagePieces.Start startPiece, int componentType, Random random,
			StructureBoundingBox boundingBox, int coordBaseMode, EnumFacing side) {
		super(startPiece, componentType, random, boundingBox, side);

		isInDesert = startPiece.inDesert;

		createBuildingBlocks(random);
	}

	private void createBuildingBlocks(Random random) {
		if (PluginManager.Module.ARBORICULTURE.isEnabled()) {

			boolean fireproof = (random.nextInt(4) == 0);

			WoodType roofWood = WoodType.getRandom(random);

			logs = new BlockTypeLog(WoodType.getRandom(random).getLog(fireproof));
			planks = new BlockTypeWood(roofWood.getPlanks(fireproof));
			stairs = new BlockTypeWoodStairs(roofWood.getStairs(fireproof));
			fence = new BlockTypeWood(WoodType.getRandom(random).getFence(fireproof));
		} else {
			int roofMeta = random.nextInt(16);

			logs = new BlockType(Blocks.log, random.nextInt(4));
			planks = new BlockType(Blocks.planks, roofMeta);
			stairs = new BlockTypeVanillaStairs(roofMeta);
			fence = new BlockType(Blocks.oak_fence, 0);
		}
	}

	@SuppressWarnings("rawtypes")
	public static ComponentVillageBeeHouse buildComponent(StructureVillagePieces.Start startPiece, List par1List,
			Random random, int par3, int par4, int par5, EnumFacing side, int par7) {
		StructureBoundingBox bbox = StructureBoundingBox.func_175899_a(par3, par4, par5, 0, 0, 0);
		if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(par1List, bbox) != null) {
			return null;
		}

		return new ComponentVillageBeeHouse(startPiece, par7, random, bbox, par7, side);
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

		fillWithAir(world, structBoundingBox, 1, 1, 1, 7, 4, 4);
		fillWithAir(world, structBoundingBox, 2, 1, 6, 8, 4, 10);

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
		fillWithBlocks(world, structBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(),
				Blocks.planks.getDefaultState(), false);

		fillWithBlocks(world, structBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(),
				Blocks.cobblestone.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 8, 0, 0, 8, 3, 5, Blocks.cobblestone.getDefaultState(),
				Blocks.cobblestone.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(),
				Blocks.cobblestone.getDefaultState(), false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 5, 7, 1, 5, Blocks.cobblestone.getDefaultState(),
				Blocks.cobblestone.getDefaultState(), false);

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

		setBlockState(world, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structBoundingBox);

		setBlockState(world, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structBoundingBox);

		// Windows garden side
		setBlockState(world, Blocks.glass_pane.getDefaultState(), 2, 2, 5, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getDefaultState(), 3, 2, 5, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getDefaultState(), 4, 2, 5, structBoundingBox);

		setBlockState(world, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structBoundingBox);
		setBlockState(world, Blocks.glass_pane.getDefaultState(), 6, 2, 5, structBoundingBox);

		// Escritoire
		if (random.nextInt(2) == 0) {
			IBlockType escritoireBlock = new BlockTypeTileForestry(ForestryBlock.core,
					Defaults.DEFINITION_ESCRITOIRE_META);
			escritoireBlock.setDirection(getRotatedDirection(EnumFacing.EAST));
			placeBlockAtCurrentPosition(world, escritoireBlock, 1, 1, 3, structBoundingBox);
		}

		setBlockState(world, Blocks.air.getDefaultState(), 2, 1, 0, structBoundingBox);
		setBlockState(world, Blocks.air.getDefaultState(), 2, 2, 0, structBoundingBox);
		placeDoorCurrentPosition(world, structBoundingBox, random, 2, 1, 0,
				EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));

		if (isAirBlockAtCurrentPosition(world, 2, 0, -1, structBoundingBox)
				&& !isAirBlockAtCurrentPosition(world, 2, -1, -1, structBoundingBox)) {
			setBlockState(world, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 2,
					0, -1, structBoundingBox);
		}

		setBlockState(world, Blocks.air.getDefaultState(), 6, 1, 5, structBoundingBox);
		setBlockState(world, Blocks.air.getDefaultState(), 6, 2, 5, structBoundingBox);

		// Candles / Lighting
		setBlockState(world, Blocks.torch.getDefaultState(), 2, 3, 4, structBoundingBox);
		setBlockState(world, Blocks.torch.getDefaultState(), 6, 3, 4, structBoundingBox);
		setBlockState(world, Blocks.torch.getDefaultState(), 2, 3, 1, structBoundingBox);
		setBlockState(world, Blocks.torch.getDefaultState(), 6, 3, 1, structBoundingBox);

		placeDoorCurrentPosition(world, structBoundingBox, random, 6, 1, 5,
				EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 9; ++j) {
				clearCurrentPositionBlocksUpwards(world, j, 7, i, structBoundingBox);
				replaceAirAndLiquidDownwards(world, Blocks.cobblestone.getStateFromMeta(0), j, -1, i,
						structBoundingBox);
			}
		}

		generateChestContents(world, structBoundingBox, random, 7, 1, 4,
				ChestGenHooks.getItems(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, random),
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

	private void plantFlowerGarden(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX,
			int maxY, int maxZ) {

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

						BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
						if (!Blocks.red_flower.canBlockStay(world, pos, world.getBlockState(pos))) {
							continue;
						}

						IFlower flower = FlowerManager.flowerRegistry
								.getRandomPlantableFlower(FlowerManager.FlowerTypeVanilla, world.rand);
						setBlockState(world, flower.getBlock().getStateFromMeta(flower.getMeta()), j, i, k, box);
					}
				}
			}
		}
	}

	private void buildApiaries(World world, StructureBoundingBox box) {
		populateApiary(world, box, 3, 1, 8);
		populateApiary(world, box, 6, 1, 8);
	}

	private void populateApiary(World world, StructureBoundingBox box, int x, int y, int z) {
		int xCoord = getXWithOffset(x, z);
		int yCoord = getYWithOffset(y);
		int zCoord = getZWithOffset(x, z);

		if (!box.isVecInside(new Vec3i(xCoord, yCoord, zCoord))
				|| ForestryBlock.apiculture.isBlockEqual(world, new BlockPos(xCoord, yCoord, zCoord))
				|| !WorldUtils.blockExists(world, new BlockPos(xCoord, yCoord - 1, zCoord))) {
			return;
		}

		BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
		world.setBlockState(pos, ForestryBlock.apiculture.block().getStateFromMeta(Defaults.DEFINITION_APIARY_META),
				Defaults.FLAG_BLOCK_SYNCH);
		ForestryBlock.apiculture.block().onBlockAdded(world, pos, world.getBlockState(pos));

		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileApiary)) {
			return;
		}

		TileApiary apiary = (TileApiary) tile;

		ItemStack randomVillagePrincess = getRandomVillageBeeStack(world, xCoord, yCoord, zCoord, EnumBeeType.PRINCESS);
		apiary.getBeeInventory().setQueen(randomVillagePrincess);

		ItemStack randomVillageDrone = getRandomVillageBeeStack(world, xCoord, yCoord, zCoord, EnumBeeType.DRONE);
		apiary.getBeeInventory().setDrone(randomVillageDrone);

		for (int i = TileApiary.ApiaryInventory.SLOT_FRAMES_1; i < TileApiary.ApiaryInventory.SLOT_FRAMES_1
				+ TileApiary.ApiaryInventory.SLOT_FRAMES_COUNT; i++) {
			ItemStack randomFrame = getRandomFrame(world.rand);
			apiary.setInventorySlotContents(i, randomFrame);
		}
	}

	private static ItemStack getRandomFrame(Random random) {
		float roll = random.nextFloat();
		if (roll < 0.2f) {
			return ForestryItem.frameUntreated.getItemStack();
		} else if (roll < 0.4f) {
			return ForestryItem.frameImpregnated.getItemStack();
		} else if (roll < 0.6) {
			return ForestryItem.frameProven.getItemStack();
		} else {
			return null;
		}
	}

	private static ItemStack getRandomVillageBeeStack(World world, int xCoord, int yCoord, int zCoord,
			EnumBeeType beeType) {
		IBee randomVillageBee = getRandomVillageBee(world, xCoord, yCoord, zCoord);
		return BeeManager.beeRoot.getMemberStack(randomVillageBee, beeType.ordinal());
	}

	private static IBee getRandomVillageBee(World world, int xCoord, int yCoord, int zCoord) {

		// Get current biome
		BiomeGenBase biome = world.getBiomeGenForCoords(new BlockPos(xCoord, yCoord, zCoord));

		ArrayList<IBeeGenome> candidates;
		if (BeeManager.villageBees[1] != null && BeeManager.villageBees[1].size() > 0
				&& world.rand.nextDouble() < 0.2) {
			candidates = BeeManager.villageBees[1];
		} else {
			candidates = BeeManager.villageBees[0];
		}

		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome, new BlockPos(xCoord, yCoord, zCoord));
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(biome.rainfall);

		// Add bees that can live in this environment
		List<IBeeGenome> valid = new ArrayList<IBeeGenome>();
		for (IBeeGenome genome : candidates) {
			if (checkBiomeHazard(genome, biomeTemperature, biomeHumidity)) {
				valid.add(genome);
			}
		}

		// No valid ones found, return any of the common ones.
		if (valid.isEmpty()) {
			return BeeManager.beeRoot.getBee(world,
					BeeManager.villageBees[0].get(world.rand.nextInt(BeeManager.villageBees[0].size())));
		}

		return BeeManager.beeRoot.getBee(world, valid.get(world.rand.nextInt(valid.size())));
	}

	private static boolean checkBiomeHazard(IBeeGenome genome, EnumTemperature biomeTemperature,
			EnumHumidity biomeHumidity) {
		IAlleleBeeSpecies species = genome.getPrimary();
		return AlleleManager.climateHelper.isWithinLimits(biomeTemperature, biomeHumidity, species.getTemperature(),
				genome.getToleranceTemp(), species.getHumidity(), genome.getToleranceHumid());
	}

	private void fillBoxWith(World world, StructureBoundingBox box, int par3, int par4, int par5, int par6, int par7,
			int par8, IBlockType block, boolean replace) {

		for (int var14 = par4; var14 <= par7; ++var14) {
			for (int var15 = par3; var15 <= par6; ++var15) {
				for (int var16 = par5; var16 <= par8; ++var16) {
					if (!replace || !isAirBlockAtCurrentPosition(world, var15, var14, var16, box)) {
						placeBlockAtCurrentPosition(world, block, var15, var14, var16, box);
					}
				}
			}
		}
	}

	private void placeBlockAtCurrentPosition(World world, IBlockType block, int par4, int par5, int par6,
			StructureBoundingBox par7StructureBoundingBox) {
		int x = getXWithOffset(par4, par6);
		int y = getYWithOffset(par5);
		int z = getZWithOffset(par4, par6);

		if (par7StructureBoundingBox.isVecInside(new Vec3i(x, y, z))) {
			block.setBlock(world, new BlockPos(x, y, z));
		}
	}

	@Override
	protected int func_180779_c(int villagerCount, int proferssion) {
		if (villagerCount <= 0) {
			return Defaults.ID_VILLAGER_BEEKEEPER;
		} else {
			return Defaults.ID_VILLAGER_LUMBERJACK;
		}
	}

	private boolean isAirBlockAtCurrentPosition(World world, int x, int y, int z, StructureBoundingBox box) {
		return getBlockStateFromPos(world, x, y, z, box).getBlock().isAir(world, new BlockPos(x, y, z));
	}

	/** rotates a direction according to the way the structure is facing **/
	private EnumFacing getRotatedDirection(EnumFacing direction) {
		int stairDirection = 5 - direction.ordinal();
		int meta = getMetadataWithOffset(Blocks.oak_stairs, stairDirection);
		return EnumFacing.getFront(5 - meta);
	}

}
