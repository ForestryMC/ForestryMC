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

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import forestry.core.blocks.BlockResourceOre;
import forestry.core.config.Config;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

public class WorldGenerator implements IWorldGenerator {

	private WorldGenMinableMeta apatiteGenerator;
	private WorldGenMinableMeta copperGenerator;
	private WorldGenMinableMeta tinGenerator;

	public WorldGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		generateWorld(random, chunkX, chunkZ, world);
	}

	@SubscribeEvent
	public void populateChunk(PopulateChunkEvent.Post event) {
		// / PLUGIN WORLD GENERATION
		PluginManager.populateChunk(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated);
	}

	public void retroGen(Random random, int chunkX, int chunkZ, World world) {
		generateWorld(random, chunkX, chunkZ, world);
		PluginManager.populateChunkRetroGen(world, random, chunkX, chunkZ);
		world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
	}

	private void generateWorld(Random random, int chunkX, int chunkZ, World world) {

		if (apatiteGenerator == null) {
			apatiteGenerator = new WorldGenMinableMeta(PluginCore.blocks.resources, BlockResourceOre.ResourceType.APATITE.ordinal(), 36);
			copperGenerator = new WorldGenMinableMeta(PluginCore.blocks.resources, BlockResourceOre.ResourceType.COPPER.ordinal(), 6);
			tinGenerator = new WorldGenMinableMeta(PluginCore.blocks.resources, BlockResourceOre.ResourceType.TIN.ordinal(), 6);
		}

		// shift to world coordinates
		chunkX = chunkX << 4;
		chunkZ = chunkZ << 4;

		// / APATITE
		if (Config.generateApatiteOre) {
			if (random.nextFloat() < 0.8f) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(world.getActualHeight() - 72) + 56; // Does not generate below y = 64
				int randPosZ = chunkZ + random.nextInt(16);
				apatiteGenerator.generate(world, random, randPosX, randPosY, randPosZ);
			}
		}

		// / COPPER
		if (Config.generateCopperOre) {
			for (int i = 0; i < 20; i++) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(76) + 32;
				int randPosZ = chunkZ + random.nextInt(16);
				copperGenerator.generate(world, random, randPosX, randPosY, randPosZ);
			}
		}

		// / TIN
		if (Config.generateTinOre) {
			for (int i = 0; i < 18; i++) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(76) + 16;
				int randPosZ = chunkZ + random.nextInt(16);
				tinGenerator.generate(world, random, randPosX, randPosY, randPosZ);
			}
		}
	}

}
