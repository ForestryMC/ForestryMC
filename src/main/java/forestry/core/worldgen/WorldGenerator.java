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
package forestry.core.worldgen;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.IWorldGenerator;

import forestry.core.ModuleCore;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.blocks.EnumResourceType;
import forestry.core.config.Config;
import forestry.modules.ModuleManager;

//import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
//import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class WorldGenerator implements IWorldGenerator {
	//TODO - worldgen
	//	@Nullable
	//	private OreFeature apatiteGenerator;
	//	@Nullable
	//	private OreFeature copperGenerator;
	//	@Nullable
	//	private OreFeature tinGenerator;

	public WorldGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator, AbstractChunkProvider chunkProvider) {
		generateWorld(random, chunkX, chunkZ, world);
	}

	//TODO - I think we just register decorators and generators now?
	//needs more investigation
	//	@SubscribeEvent
	//	public void populateChunk(PopulateChunkEvent.Post event) {
	//		// / PLUGIN WORLD GENERATION
	//		ForgeRegistries.DECORATORS.register();
	//		ModuleManager.getInternalHandler().populateChunk(event.getGen(), event.getWorld(), event.getRand(), event.getChunkX(), event.getChunkZ(), event.isHasVillageGenerated());
	//	}
	//
	//	@SubscribeEvent
	//	public void decorateBiome(DecorateBiomeEvent.Post event) {
	//		ForgeRegistries.CHUNK_GENERATOR_TYPES.register();
	//		ModuleManager.getInternalHandler().decorateBiome(event.getWorld(), event.getRand(), event.getPos());
	//	}

	public void retroGen(Random random, int chunkX, int chunkZ, World world) {
		generateWorld(random, chunkX, chunkZ, world);
		ModuleManager.getInternalHandler().populateChunkRetroGen(world, random, chunkX, chunkZ);
		world.getChunk(chunkX, chunkZ).markDirty();
	}

	private void generateWorld(Random random, int chunkX, int chunkZ, World world) {
		if (!Config.isValidOreDim(DimensionType.getKey(world.getDimension().getType()))) {
			return;
		}

		if (false) {//apatiteGenerator == null || copperGenerator == null || tinGenerator == null) {
			BlockRegistryCore registry = ModuleCore.getBlocks();

			BlockState apatiteBlockState = registry.resourceOre.get(EnumResourceType.APATITE).getDefaultState();
			BlockState copperBlockState = registry.resourceOre.get(EnumResourceType.COPPER).getDefaultState();
			BlockState tinBlockState = registry.resourceOre.get(EnumResourceType.TIN).getDefaultState();
			//			apatiteGenerator = new OreFeature(apatiteBlockState, 36);
			//			copperGenerator = new OreFeature(copperBlockState, 6);
			//			tinGenerator = new OreFeature(tinBlockState, 6);
		}

		// shift to world coordinates
		int x = chunkX << 4;
		int y = chunkZ << 4;

		// / APATITE
		//TODO - worldgen
		//		if (Config.generateApatiteOre) {
		//			final int lowest = Math.round(world.getActualHeight() * 0.22f); // 56
		//			final int range = Math.round(world.getActualHeight() * 0.72f); // 184
		//			if (random.nextFloat() < 0.8f) {
		//				int randPosX = x + random.nextInt(16);
		//				int randPosY = random.nextInt(range) + lowest;
		//				int randPosZ = y + random.nextInt(16);
		//				apatiteGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
		//			}
		//		}
		//
		//		// / COPPER
		//		if (Config.generateCopperOre) {
		//			for (int i = 0; i < 20; i++) {
		//				final int lowest = Math.round(world.getActualHeight() / 8f); // 32
		//				final int range = Math.round(world.getActualHeight() * 0.297f); // 76
		//				int randPosX = x + random.nextInt(16);
		//				int randPosY = random.nextInt(range) + lowest;
		//				int randPosZ = y + random.nextInt(16);
		//				copperGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
		//			}
		//		}
		//
		//		// / TIN
		//		if (Config.generateTinOre) {
		//			for (int i = 0; i < 18; i++) {
		//				final int lowest = Math.round(world.getActualHeight() / 16f); // 16
		//				final int range = Math.round(world.getActualHeight() * 0.297f); // 76
		//				int randPosX = x + random.nextInt(16);
		//				int randPosY = random.nextInt(range) + lowest;
		//				int randPosZ = y + random.nextInt(16);
		//				tinGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
		//			}
		//		}
	}

}
