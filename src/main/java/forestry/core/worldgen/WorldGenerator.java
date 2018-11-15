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

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import forestry.core.ModuleCore;
import forestry.core.blocks.BlockResourceOre;
import forestry.core.blocks.EnumResourceType;
import forestry.core.config.Config;
import forestry.modules.ModuleManager;

public class WorldGenerator implements IWorldGenerator {
	@Nullable
	private WorldGenMinable apatiteGenerator;
	@Nullable
	private WorldGenMinable copperGenerator;
	@Nullable
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
		ModuleManager.getInternalHandler().populateChunk(event.getGen(), event.getWorld(), event.getRand(), event.getChunkX(), event.getChunkZ(), event.isHasVillageGenerated());
	}

	@SubscribeEvent
	public void decorateBiome(DecorateBiomeEvent.Post event) {
		ModuleManager.getInternalHandler().decorateBiome(event.getWorld(), event.getRand(), event.getPos());
	}

	public void retroGen(Random random, int chunkX, int chunkZ, World world) {
		generateWorld(random, chunkX, chunkZ, world);
		ModuleManager.getInternalHandler().populateChunkRetroGen(world, random, chunkX, chunkZ);
		world.getChunk(chunkX, chunkZ).markDirty();
	}

	private void generateWorld(Random random, int chunkX, int chunkZ, World world) {
		if (!Config.isValidOreDim(world.provider.getDimension())) {
			return;
		}

		if (apatiteGenerator == null || copperGenerator == null || tinGenerator == null) {
			BlockResourceOre resourcesBlock = ModuleCore.getBlocks().resources;

			IBlockState apatiteBlockState = resourcesBlock.getDefaultState().withProperty(BlockResourceOre.ORE_RESOURCES, EnumResourceType.APATITE);
			IBlockState copperBlockState = resourcesBlock.getDefaultState().withProperty(BlockResourceOre.ORE_RESOURCES, EnumResourceType.COPPER);
			IBlockState tinBlockState = resourcesBlock.getDefaultState().withProperty(BlockResourceOre.ORE_RESOURCES, EnumResourceType.TIN);
			apatiteGenerator = new WorldGenMinable(apatiteBlockState, 36);
			copperGenerator = new WorldGenMinable(copperBlockState, 6);
			tinGenerator = new WorldGenMinable(tinBlockState, 6);
		}

		// shift to world coordinates
		int x = chunkX << 4;
		int y = chunkZ << 4;

		// / APATITE
		if (Config.generateApatiteOre) {
			final int lowest = Math.round(world.getActualHeight() * 0.22f); // 56
			final int range = Math.round(world.getActualHeight() * 0.72f); // 184
			if (random.nextFloat() < 0.8f) {
				int randPosX = x + random.nextInt(16);
				int randPosY = random.nextInt(range) + lowest;
				int randPosZ = y + random.nextInt(16);
				apatiteGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
			}
		}

		// / COPPER
		if (Config.generateCopperOre) {
			for (int i = 0; i < 20; i++) {
				final int lowest = Math.round(world.getActualHeight() / 8f); // 32
				final int range = Math.round(world.getActualHeight() * 0.297f); // 76
				int randPosX = x + random.nextInt(16);
				int randPosY = random.nextInt(range) + lowest;
				int randPosZ = y + random.nextInt(16);
				copperGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
			}
		}

		// / TIN
		if (Config.generateTinOre) {
			for (int i = 0; i < 18; i++) {
				final int lowest = Math.round(world.getActualHeight() / 16f); // 16
				final int range = Math.round(world.getActualHeight() * 0.297f); // 76
				int randPosX = x + random.nextInt(16);
				int randPosY = random.nextInt(range) + lowest;
				int randPosZ = y + random.nextInt(16);
				tinGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
			}
		}
	}

}
