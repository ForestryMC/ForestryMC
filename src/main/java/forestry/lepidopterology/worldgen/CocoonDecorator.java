///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.lepidopterology.worldgen;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.gen.ChunkGenerator;
//
//import net.minecraftforge.common.BiomeDictionary;
//import net.minecraftforge.common.BiomeDictionary.Type;
//import net.minecraftforge.common.util.EnumHelper;
//import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
//import net.minecraftforge.event.terraingen.TerrainGen;
//
//import forestry.api.lepidopterology.ButterflyManager;
//import forestry.api.lepidopterology.IButterfly;
//import forestry.core.config.Constants;
//import forestry.core.tiles.TileUtil;
//import forestry.core.utils.BlockUtil;
//import forestry.lepidopterology.ModuleLepidopterology;
//import forestry.lepidopterology.tiles.TileCocoon;
//
//public abstract class CocoonDecorator {
//
//	private static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_COCOONS", new Class[0]);
////TODO worldgen
//	public static void decorateCocoons(ChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
//		if (!TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, EVENT_TYPE)) {
//			return;
//		}
//
//		decorateCocoons(world, rand, chunkX, chunkZ);
//	}
//
//	public static void decorateCocoons(World world, Random rand, int chunkX, int chunkZ) {
//		List<IButterfly> butterflys = ButterflyManager.butterflyRoot.getIndividualTemplates();
//
//		Collections.shuffle(butterflys, rand);
//		for (IButterfly butterfly : butterflys) {
//			if (genCocoon(world, rand, chunkX, chunkZ, butterfly)) {
//				return;
//			}
//		}
//	}
//
//	public static boolean genCocoon(World world, Random rand, int chunkX, int chunkZ, IButterfly butterfly) {
//		if (butterfly.getGenome().getPrimary().getRarity() * ModuleLepidopterology.getGenerateCocoonsAmount() < rand.nextFloat() * 100.0f) {
//			return false;
//		}
//
//		int worldX = chunkX * 16;
//		int worldZ = chunkZ * 16;
//
//		Biome biome = world.getBiome(new BlockPos(worldX, 0, worldZ));
//
//		Set<Type> types = BiomeDictionary.getTypes(biome);
//		Set<Type> speciesTypes = butterfly.getGenome().getPrimary().getSpawnBiomes();
//
//		boolean biomeTypesGood = false;
//		for (Type type : types) {
//			if (speciesTypes.isEmpty() || speciesTypes.contains(type)) {
//				biomeTypesGood = true;
//			}
//		}
//		if (!biomeTypesGood) {
//			return false;
//		}
//
//		for (int tries = 0; tries < 4; tries++) {
//			int x = worldX + rand.nextInt(16);
//			int z = worldZ + rand.nextInt(16);
//
//			if (tryGenCocoon(world, x, z, butterfly)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	private static boolean tryGenCocoon(World world, int x, int z, IButterfly butterfly) {
//
//		int y = getYForCocoon(world, x, z);
//
//		if (y < 0) {
//			return false;
//		}
//
//		if (!isValidLocation(world, new BlockPos(x, y, z))) {
//			return false;
//		}
//
//		return setCocoon(world, new BlockPos(x, y, z), butterfly);
//	}
//
//	private static boolean setCocoon(World world, BlockPos pos, IButterfly butterfly) {
//		Block cocoonBlock = ModuleLepidopterology.getBlocks().solidCocoon;
//		boolean placed = world.setBlockState(pos, cocoonBlock.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
//		if (!placed) {
//			return false;
//		}
//
//		BlockState state = world.getBlockState(pos);
//		if (!Block.isEqualTo(cocoonBlock, state.getBlock())) {
//			return false;
//		}
//
//		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
//		if (cocoon != null) {
//			cocoon.setCaterpillar(butterfly);
//		} else {
//			return false;
//		}
//
//		cocoonBlock.onBlockAdded(world, pos, state);
//		world.markBlockRangeForRenderUpdate(pos, pos);
//
//		return true;
//	}
//
//	private static int getYForCocoon(World world, int x, int z) {
//		int y = world.getHeight(new BlockPos(x, 0, z)).getY() - 1;
//		BlockPos pos = new BlockPos(x, y, z);
//		BlockState blockState = world.getBlockState(pos);
//		if (!blockState.getBlock().isLeaves(blockState, world, pos)) {
//			return -1;
//		}
//
//		do {
//			pos = pos.down();
//			blockState = world.getBlockState(pos);
//		} while (blockState.getBlock().isLeaves(blockState, world, pos));
//
//		return y;
//	}
//
//	public static boolean isValidLocation(World world, BlockPos pos) {
//		BlockPos posAbove = pos.up();
//		BlockState blockStateAbove = world.getBlockState(posAbove);
//		Block blockAbove = blockStateAbove.getBlock();
//		if (!blockAbove.isLeaves(blockStateAbove, world, posAbove)) {
//			return false;
//		}
//		BlockPos posBelow = pos.down();
//		BlockState blockStateBelow = world.getBlockState(posBelow);
//		return BlockUtil.canReplace(blockStateBelow, world, posBelow);
//	}
//}
