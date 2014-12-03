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
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import net.minecraftforge.common.ChestGenHooks;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.apiculture.gadgets.TileBeehouse;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginApiculture;

public class ComponentVillageBeeHouse extends StructureVillagePieces.House1 {

	protected final ItemStack[] buildingBlocks = new ItemStack[]{ForestryBlock.planks1.getItemStack(1, 15), new ItemStack(Blocks.log, 1, 0),};
	protected int averageGroundLevel = -1;
	protected boolean isInDesert = false;
	protected boolean hasChest = false;

	public ComponentVillageBeeHouse() {
		// Populate buildingblocks with some defaults so it doesn't explode.
		buildingBlocks[0] = ForestryBlock.planks1.getItemStack();
		buildingBlocks[1] = ForestryBlock.log1.getItemStack();
	}

	public ComponentVillageBeeHouse(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, int coordBaseMode) {
		super(startPiece, componentType, random, boundingBox, coordBaseMode);

		isInDesert = startPiece.inDesert;

		buildingBlocks[0] = ForestryBlock.planks1.getItemStack(1, random.nextInt(16));

		Block woodBlock;
		switch (random.nextInt(4)) {
		case 1:
			woodBlock = ForestryBlock.log2.block();
			break;
		case 2:
			woodBlock = ForestryBlock.log3.block();
			break;
		case 3:
			woodBlock = ForestryBlock.log4.block();
			break;
		default:
			woodBlock = ForestryBlock.log1.block();
			break;
		}

		buildingBlocks[1] = new ItemStack(woodBlock, 1, random.nextInt(4));
	}

	/*@Override
	 protected void func_143012_a(NBTTagCompound par1nbtTagCompound) {
	 super.func_143012_a(par1nbtTagCompound);
	 par1nbtTagCompound.setBoolean("Chest", this.hasChest);
	 }

	 @Override
	 protected void func_143011_b(NBTTagCompound par1nbtTagCompound) {
	 super.func_143011_b(par1nbtTagCompound);
	 this.hasChest = par1nbtTagCompound.getBoolean("Chest");
	 }*/
	@SuppressWarnings("rawtypes")
	public static ComponentVillageBeeHouse buildComponent(StructureVillagePieces.Start startPiece, List par1List, Random random, int par3, int par4, int par5,
			int par6, int par7) {
		StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 9, 6, par6);
		return canVillageGoDeeper(bbox) && StructureComponent.findIntersecting(par1List, bbox) == null ? new ComponentVillageBeeHouse(startPiece, par7, random,
				bbox, par6) : null;
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox structBoundingBox) {

		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, structBoundingBox);
			if (this.averageGroundLevel < 0)
				return true;

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 8 - 1, 0);
		}

		this.fillWithBlocks(world, structBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.air, Blocks.air, false);
		this.fillWithBlocks(world, structBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.air, Blocks.air, false);

		// Garden
		buildGarden(world, structBoundingBox);

		// Garden fence
		this.fillWithBlocks(world, structBoundingBox, 1, 1, 6, 1, 1, 10, Blocks.fence, Blocks.fence, false);
		this.fillWithBlocks(world, structBoundingBox, 8, 1, 6, 8, 1, 10, Blocks.fence, Blocks.fence, false);
		this.fillWithBlocks(world, structBoundingBox, 2, 1, 10, 7, 1, 10, Blocks.fence, Blocks.fence, false);

		// Flowers
		plantFlowerGarden(world, structBoundingBox, 2, 1, 5, 7, 1, 9);

		// Apiaries
		buildApiaries(world, structBoundingBox, 3, 1, 4, 6, 1, 8);

		// Floor
		this.fillWithBlocks(world, structBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks, Blocks.planks, false);

		this.fillWithBlocks(world, structBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
		this.fillWithBlocks(world, structBoundingBox, 8, 0, 0, 8, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
		this.fillWithBlocks(world, structBoundingBox, 1, 0, 0, 7, 1, 0, Blocks.cobblestone, Blocks.cobblestone, false);
		this.fillWithBlocks(world, structBoundingBox, 1, 0, 5, 7, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);

		this.fillBoxWith(world, structBoundingBox, 1, 2, 0, 7, 3, 0, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 1, 2, 5, 7, 3, 5, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 0, 4, 1, 8, 4, 1, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 0, 4, 4, 8, 4, 4, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 0, 5, 2, 8, 5, 3, buildingBlocks[0], false);

		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 0, 4, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 0, 4, 3, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 8, 4, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 8, 4, 3, structBoundingBox);

		buildRoof(world, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 0, 2, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 0, 2, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 8, 2, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 8, 2, 4, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 3, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 3, structBoundingBox);

		// Windows garden side
		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 5, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 3, 2, 5, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 2, 5, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 0, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 5, structBoundingBox);

		// Table/Bench
		if (random.nextInt(10) < 1)
			this.placeBlockAtCurrentPosition(world, ForestryBlock.core.getItemStack(1, Defaults.DEFINITION_ESCRITOIRE_META), 1, 1, 3, structBoundingBox);
		else
			this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 1, 1, 3, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 1, 0, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 2, 0, structBoundingBox);
		this.placeDoorAtCurrentPosition(world, structBoundingBox, random, 2, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));

		if (isAirBlockAtCurrentPosition(world, 2, 0, -1, structBoundingBox)
				&& !isAirBlockAtCurrentPosition(world, 2, -1, -1, structBoundingBox))
			this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs,
					this.getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 0, -1, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 1, 5, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 2, 5, structBoundingBox);

		// Candles / Lighting
		this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 3, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 3, 1, structBoundingBox);

		this.placeDoorAtCurrentPosition(world, structBoundingBox, random, 6, 1, 5, this.getMetadataWithOffset(Blocks.wooden_door, 1));

		for (int i = 0; i < 5; ++i)
			for (int j = 0; j < 9; ++j) {
				this.clearCurrentPositionBlocksUpwards(world, j, 7, i, structBoundingBox);
				this.func_151554_b(world, Blocks.cobblestone, 0, j, -1, i, structBoundingBox);
			}

		this.generateStructureChestContents(world, structBoundingBox, random, 7, 1, 4,
				ChestGenHooks.getItems(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, random),
				random.nextInt(4) + random.nextInt(4) + 5);

		spawnVillagers(world, boundingBox, 7, 1, 1, 2);

		return true;
	}

	private void buildRoof(World world, StructureBoundingBox structBoundingBox) {
		int rotatedMetaDoor = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
		int rotatedMetaGarden = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

		for (int i = -1; i <= 2; ++i)
			for (int j = 0; j <= 8; ++j) {
				this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, rotatedMetaDoor, j, 4 + i, i, structBoundingBox);
				this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, rotatedMetaGarden, j, 4 + i, 5 - i, structBoundingBox);
			}
	}

	protected void buildGarden(World world, StructureBoundingBox box) {

		Block ground = Blocks.dirt;
		if (isInDesert)
			ground = Blocks.sand;

		for (int i = 1; i <= 8; i++)
			for (int j = 6; j <= 10; j++)
				func_151554_b(world, ground, 0, i, 0, j, box);
	}

	protected void plantFlowerGarden(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

		if (isInDesert) {
			placeBlockAtCurrentPosition(world, Blocks.cactus, 0, 4, 1, 7, box);
			return;
		}

		for (int i = minY; i <= maxY; ++i)
			for (int j = minX; j <= maxX; ++j)
				for (int k = minZ; k <= maxZ; ++k)
					if (world.rand.nextBoolean()) {
						int xCoord = this.getXWithOffset(j, k);
						int yCoord = this.getYWithOffset(i);
						int zCoord = this.getZWithOffset(j, k);

						if (!Blocks.red_flower.canBlockStay(world, xCoord, yCoord, zCoord))
							continue;

						ItemStack flower = FlowerManager.plainFlowers.get(world.rand.nextInt(FlowerManager.plainFlowers.size()));
						this.placeBlockAtCurrentPosition(world, flower, j, i, k, box);
					}
	}

	protected void buildApiaries(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		populateApiary(world, box, 3, 1, 8);
		populateApiary(world, box, 6, 1, 8);
	}

	private void populateApiary(World world, StructureBoundingBox box, int x, int y, int z) {
		int xCoord = this.getXWithOffset(x, z);
		int yCoord = this.getYWithOffset(y);
		int zCoord = this.getZWithOffset(x, z);

		if (box.isVecInside(xCoord, yCoord, zCoord) && !ForestryBlock.apiculture.isBlockEqual(world, xCoord, yCoord, zCoord)
				&& world.blockExists(xCoord, yCoord - 1, zCoord)) {

			world.setBlock(xCoord, yCoord, zCoord, ForestryBlock.apiculture.block(), Defaults.DEFINITION_APIARY_META,
					Defaults.FLAG_BLOCK_SYNCH);
			ForestryBlock.apiculture.block().onBlockAdded(world, xCoord, yCoord, zCoord);

			TileEntity tile = world.getTileEntity(xCoord, yCoord, zCoord);
			if (tile instanceof TileBeehouse) {
				TileBeehouse apiary = ((TileBeehouse) tile);
				apiary.initialize();
				apiary.setSlotContents(TileBeehouse.SLOT_QUEEN,
						PluginApiculture.beeInterface.getMemberStack(getVillageBee(world, xCoord, yCoord, zCoord), EnumBeeType.PRINCESS.ordinal()));
				apiary.setSlotContents(TileBeehouse.SLOT_DRONE,
						PluginApiculture.beeInterface.getMemberStack(getVillageBee(world, xCoord, yCoord, zCoord), EnumBeeType.DRONE.ordinal()));

				for (int i = TileBeehouse.SLOT_FRAMES_1; i < TileBeehouse.SLOT_FRAMES_1 + TileBeehouse.SLOT_FRAMES_COUNT; i++) {
					float roll = world.rand.nextFloat();
					if (roll < 0.2f)
						apiary.setSlotContents(i, ForestryItem.frameUntreated.getItemStack());
					else if (roll < 0.4f)
						apiary.setSlotContents(i, ForestryItem.frameImpregnated.getItemStack());
					else if (roll < 0.6)
						apiary.setSlotContents(i, ForestryItem.frameProven.getItemStack());
				}

			}
		}
	}

	private IBee getVillageBee(World world, int xCoord, int yCoord, int zCoord) {

		// Get current biome
		BiomeGenBase biome = world.getBiomeGenForCoords(xCoord, zCoord);

		ArrayList<IBeeGenome> candidates;
		if (BeeManager.villageBees[1] != null && BeeManager.villageBees[1].size() > 0 && world.rand.nextDouble() < 0.2)
			candidates = BeeManager.villageBees[1];
		else
			candidates = BeeManager.villageBees[0];

		// Add bees that can live in this environment
		ArrayList<IBeeGenome> valid = new ArrayList<IBeeGenome>();
		for (IBeeGenome genome : candidates)
			if (checkBiomeHazard(genome, biome.temperature, biome.rainfall))
				valid.add(genome);

		// No valid ones found, return any of the common ones.
		if (valid.isEmpty())
			return PluginApiculture.beeInterface.getBee(world, BeeManager.villageBees[0].get(world.rand.nextInt(BeeManager.villageBees[0].size())));

		return PluginApiculture.beeInterface.getBee(world, valid.get(world.rand.nextInt(valid.size())));
	}

	private boolean checkBiomeHazard(IBeeGenome genome, float temperature, float humidity) {

		EnumTemperature beeTemperature = genome.getPrimary().getTemperature();
		EnumTolerance temperatureTolerance = genome.getToleranceTemp();

		Collection<EnumTemperature> toleratedTemperatures = AlleleManager.climateHelper.getToleratedTemperature(beeTemperature, temperatureTolerance);
		boolean validTemp = false;

		validTemp = toleratedTemperatures.contains(EnumTemperature.getFromValue(temperature));

		if (!validTemp)
			return false;

		EnumHumidity beeHumidity = genome.getPrimary().getHumidity();
		EnumTolerance humidityTolerance = genome.getToleranceHumid();

		Collection<EnumHumidity> toleratedHumidity = AlleleManager.climateHelper.getToleratedHumidity(beeHumidity, humidityTolerance);

		boolean validHumidity = false;

		validHumidity = toleratedHumidity.contains(EnumHumidity.getFromValue(humidity));

		return validHumidity;
	}

	protected void fillBoxWith(World world, StructureBoundingBox box, int par3, int par4, int par5, int par6, int par7, int par8, ItemStack buildingBlock,
			boolean replace) {

		for (int var14 = par4; var14 <= par7; ++var14)
			for (int var15 = par3; var15 <= par6; ++var15)
				for (int var16 = par5; var16 <= par8; ++var16)
					if (!replace || !isAirBlockAtCurrentPosition(world, var15, var14, var16, box))
						this.placeBlockAtCurrentPosition(world, buildingBlock, var15, var14, var16, box);
	}

	protected void placeBlockAtCurrentPosition(World world, ItemStack buildingBlock, int par4, int par5, int par6, StructureBoundingBox par7StructureBoundingBox) {
		int var8 = this.getXWithOffset(par4, par6);
		int var9 = this.getYWithOffset(par5);
		int var10 = this.getZWithOffset(par4, par6);

		if (par7StructureBoundingBox.isVecInside(var8, var9, var10))
			world.setBlock(var8, var9, var10, StackUtils.getBlock(buildingBlock), buildingBlock.getItemDamage(), Defaults.FLAG_BLOCK_SYNCH);
	}

	@Override
	protected int getVillagerType(int villagerCount) {
		if (villagerCount <= 0)
			return Defaults.ID_VILLAGER_BEEKEEPER;
		else
			return Defaults.ID_VILLAGER_LUMBERJACK;
	}

	private boolean isAirBlockAtCurrentPosition(World world, int x, int y, int z, StructureBoundingBox box) {
		// TODO: replace with isAir
		return getBlockAtCurrentPosition(world, x, y, z, box) == Blocks.air;
	}

}
