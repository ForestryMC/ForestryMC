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

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import forestry.core.PluginCore;
import forestry.core.blocks.BlockResourceOre;
import forestry.core.blocks.EnumResourceType;
import forestry.core.config.Config;
import forestry.plugins.PluginManager;

public class WorldGenerator implements IWorldGenerator {

	private WorldGenMinable apatiteGenerator;
	private WorldGenMinable copperGenerator;
	private WorldGenMinable tinGenerator;

	public WorldGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		generateWorld(random, chunkX, chunkZ, world);
	}

	@SubscribeEvent
	public void populateChunk(PopulateChunkEvent.Post event) {
		// / PLUGIN WORLD GENERATION
		PluginManager.populateChunk(event.getGen(), event.getWorld(), event.getRand(), event.getChunkX(), event.getChunkZ(), event.isHasVillageGenerated());
	}

	public void retroGen(Random random, int chunkX, int chunkZ, World world) {
		generateWorld(random, chunkX, chunkZ, world);
		PluginManager.populateChunkRetroGen(world, random, chunkX, chunkZ);
		world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
	}

	private void generateWorld(Random random, int chunkX, int chunkZ, World world) {

		if (apatiteGenerator == null) {
			BlockResourceOre resourcesBlock = PluginCore.blocks.resources;

			IBlockState apatiteBlockState = resourcesBlock.getStateFromMeta(EnumResourceType.APATITE.getMeta());
			IBlockState copperBlockState = resourcesBlock.getStateFromMeta(EnumResourceType.COPPER.getMeta());
			IBlockState tinBlockState = resourcesBlock.getStateFromMeta(EnumResourceType.TIN.getMeta());
			apatiteGenerator = new WorldGenMinable(apatiteBlockState, 36);
			copperGenerator = new WorldGenMinable(copperBlockState, 6);
			tinGenerator = new WorldGenMinable(tinBlockState, 6);
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
				apatiteGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
			}
		}

		// / COPPER
		if (Config.generateCopperOre) {
			for (int i = 0; i < 20; i++) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(76) + 32;
				int randPosZ = chunkZ + random.nextInt(16);
				copperGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
			}
		}

		// / TIN
		if (Config.generateTinOre) {
			for (int i = 0; i < 18; i++) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(76) + 16;
				int randPosZ = chunkZ + random.nextInt(16);
				tinGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
			}
		}
	}

}
