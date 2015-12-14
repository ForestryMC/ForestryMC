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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

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
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFlower;
import forestry.apiculture.blocks.BlockApicultureType;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.apiculture.tiles.TileApiary;
import forestry.arboriculture.worldgen.BlockTypeLog;
import forestry.arboriculture.worldgen.BlockTypeVanillaStairs;
import forestry.arboriculture.worldgen.BlockTypeWood;
import forestry.arboriculture.worldgen.BlockTypeWoodStairs;
import forestry.core.blocks.BlockCoreType;
import forestry.core.config.Constants;
import forestry.core.tiles.TileUtil;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.BlockTypeTileForestry;
import forestry.core.worldgen.IBlockType;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginCore;
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

	public ComponentVillageBeeHouse(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, int coordBaseMode) {
		super(startPiece, componentType, random, boundingBox, coordBaseMode);

		isInDesert = startPiece.inDesert;

		createBuildingBlocks(random);
	}

	private void createBuildingBlocks(Random random) {
		if (PluginManager.Module.ARBORICULTURE.isEnabled()) {

			boolean fireproof = (random.nextInt(4) == 0);

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
			fence = new BlockType(Blocks.fence, 0);
		}
	}

	@SuppressWarnings("rawtypes")
	public static ComponentVillageBeeHouse buildComponent(StructureVillagePieces.Start startPiece, List par1List, Random random, int par3, int par4, int par5, int par6, int par7) {
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

		fillWithBlocks(world, structBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.air, Blocks.air, false);
		fillWithBlocks(world, structBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.air, Blocks.air, false);

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
		fillWithBlocks(world, structBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks, Blocks.planks, false);

		fillWithBlocks(world, structBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
		fillWithBlocks(world, structBoundingBox, 8, 0, 0, 8, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 0, 7, 1, 0, Blocks.cobblestone, Blocks.cobblestone, false);
		fillWithBlocks(world, structBoundingBox, 1, 0, 5, 7, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);

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

		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 3, structBoundingBox);

		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 2, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 3, structBoundingBox);

		// Windows garden side
		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 5, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 3, 2, 5, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 2, 5, structBoundingBox);

		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 0, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 5, structBoundingBox);

		// Escritoire
		if (random.nextInt(2) == 0) {
			IBlockType escritoireBlock = new BlockTypeTileForestry(PluginCore.blocks.core, BlockCoreType.ESCRITOIRE.ordinal());
			escritoireBlock.setDirection(getRotatedDirection(ForgeDirection.EAST));
			placeBlockAtCurrentPosition(world, escritoireBlock, 1, 1, 3, structBoundingBox);
		}

		placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 1, 0, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 2, 0, structBoundingBox);
		placeDoorAtCurrentPosition(world, structBoundingBox, random, 2, 1, 0, getMetadataWithOffset(Blocks.wooden_door, 1));

		if (isAirBlockAtCurrentPosition(world, 2, 0, -1, structBoundingBox) && !isAirBlockAtCurrentPosition(world, 2, -1, -1, structBoundingBox)) {
			placeBlockAtCurrentPosition(world, Blocks.stone_stairs, getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 0, -1, structBoundingBox);
		}

		placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 1, 5, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 2, 5, structBoundingBox);

		// Candles / Lighting
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 4, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 3, 4, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 1, structBoundingBox);
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 3, 1, structBoundingBox);

		placeDoorAtCurrentPosition(world, structBoundingBox, random, 6, 1, 5, getMetadataWithOffset(Blocks.wooden_door, 1));

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 9; ++j) {
				clearCurrentPositionBlocksUpwards(world, j, 7, i, structBoundingBox);
				func_151554_b(world, Blocks.cobblestone, 0, j, -1, i, structBoundingBox);
			}
		}

		generateStructureChestContents(world, structBoundingBox, random, 7, 1, 4,
				ChestGenHooks.getItems(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, random),
				random.nextInt(4) + random.nextInt(4) + 5);

		spawnVillagers(world, boundingBox, 7, 1, 1, 2);

		return true;
	}

	private void buildRoof(World world, StructureBoundingBox structBoundingBox) {
		for (int i = -1; i <= 2; ++i) {
			for (int j = 0; j <= 8; ++j) {
				stairs.setDirection(getRotatedDirection(ForgeDirection.NORTH));
				placeBlockAtCurrentPosition(world, stairs, j, 4 + i, i, structBoundingBox);

				stairs.setDirection(getRotatedDirection(ForgeDirection.SOUTH));
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
				func_151554_b(world, ground, 0, i, 0, j, box);
			}
		}
	}

	private void plantFlowerGarden(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

		if (isInDesert) {
			placeBlockAtCurrentPosition(world, Blocks.cactus, 0, 4, 1, 7, box);
			return;
		}

		for (int i = minY; i <= maxY; ++i) {
			for (int j = minX; j <= maxX; ++j) {
				for (int k = minZ; k <= maxZ; ++k) {
					if (world.rand.nextBoolean()) {
						int xCoord = getXWithOffset(j, k);
						int yCoord = getYWithOffset(i);
						int zCoord = getZWithOffset(j, k);

						if (!Blocks.red_flower.canBlockStay(world, xCoord, yCoord, zCoord)) {
							continue;
						}

						IFlower flower = FlowerManager.flowerRegistry.getRandomPlantableFlower(FlowerManager.FlowerTypeVanilla, world.rand);
						placeBlockAtCurrentPosition(world, flower.getBlock(), flower.getMeta(), j, i, k, box);
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

		if (!box.isVecInside(xCoord, yCoord, zCoord)) {
			return;
		}

		Block block = world.getBlock(xCoord, yCoord, zCoord);
		if (PluginApiculture.blocks.apiculture == block || !world.blockExists(xCoord, yCoord - 1, zCoord)) {
			return;
		}

		world.setBlock(xCoord, yCoord, zCoord, PluginApiculture.blocks.apiculture, BlockApicultureType.APIARY.ordinal(), Constants.FLAG_BLOCK_SYNCH);
		PluginApiculture.blocks.apiculture.onBlockAdded(world, xCoord, yCoord, zCoord);

		TileApiary apiary = TileUtil.getTile(world, xCoord, yCoord, zCoord, TileApiary.class);
		if (apiary == null) {
			return;
		}

		ItemStack randomVillagePrincess = getRandomVillageBeeStack(world, xCoord, yCoord, zCoord, EnumBeeType.PRINCESS);
		apiary.getBeeInventory().setQueen(randomVillagePrincess);

		ItemStack randomVillageDrone = getRandomVillageBeeStack(world, xCoord, yCoord, zCoord, EnumBeeType.DRONE);
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

	private static ItemStack getRandomVillageBeeStack(World world, int xCoord, int yCoord, int zCoord, EnumBeeType beeType) {
		IBee randomVillageBee = getRandomVillageBee(world, xCoord, yCoord, zCoord);
		return BeeManager.beeRoot.getMemberStack(randomVillageBee, beeType.ordinal());
	}

	private static IBee getRandomVillageBee(World world, int xCoord, int yCoord, int zCoord) {

		// Get current biome
		BiomeGenBase biome = world.getBiomeGenForCoords(xCoord, zCoord);

		ArrayList<IBeeGenome> candidates;
		if (BeeManager.villageBees[1] != null && BeeManager.villageBees[1].size() > 0 && world.rand.nextDouble() < 0.2) {
			candidates = BeeManager.villageBees[1];
		} else {
			candidates = BeeManager.villageBees[0];
		}

		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome, xCoord, yCoord, zCoord);
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
			return BeeManager.beeRoot.getBee(world, BeeManager.villageBees[0].get(world.rand.nextInt(BeeManager.villageBees[0].size())));
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
					if (!replace || !isAirBlockAtCurrentPosition(world, var15, var14, var16, box)) {
						placeBlockAtCurrentPosition(world, block, var15, var14, var16, box);
					}
				}
			}
		}
	}

	private void placeBlockAtCurrentPosition(World world, IBlockType block, int par4, int par5, int par6, StructureBoundingBox par7StructureBoundingBox) {
		int x = getXWithOffset(par4, par6);
		int y = getYWithOffset(par5);
		int z = getZWithOffset(par4, par6);

		if (par7StructureBoundingBox.isVecInside(x, y, z)) {
			block.setBlock(world, x, y, z);
		}
	}

	@Override
	protected int getVillagerType(int villagerCount) {
		if (villagerCount <= 0) {
			return Constants.ID_VILLAGER_BEEKEEPER;
		} else {
			return Constants.ID_VILLAGER_LUMBERJACK;
		}
	}

	private boolean isAirBlockAtCurrentPosition(World world, int x, int y, int z, StructureBoundingBox box) {
		return getBlockAtCurrentPosition(world, x, y, z, box).isAir(world, x, y, z);
	}

	/** rotates a direction according to the way the structure is facing **/
	private ForgeDirection getRotatedDirection(ForgeDirection direction) {
		int stairDirection = 5 - direction.ordinal();
		int meta = getMetadataWithOffset(Blocks.oak_stairs, stairDirection);
		return ForgeDirection.getOrientation(5 - meta);
	}

}
