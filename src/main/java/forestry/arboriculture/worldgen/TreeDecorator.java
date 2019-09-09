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
//package forestry.arboriculture.worldgen;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.util.Direction;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraft.world.biome.Biome;
//
//import net.minecraftforge.common.IPlantable;
//import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
//
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//
//import forestry.api.arboriculture.IGrowthProvider;
//import forestry.api.arboriculture.TreeManager;
//import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
//import forestry.api.arboriculture.genetics.ITree;
//import forestry.api.arboriculture.genetics.ITreeGenome;
//import forestry.api.arboriculture.genetics.TreeChromosomes;
//import forestry.api.genetics.AlleleManager;
//import forestry.api.genetics.IAllele;
//import forestry.arboriculture.TreeConfig;
//import forestry.arboriculture.commands.TreeGenHelper;
//import forestry.core.utils.BlockUtil;
//
//public class TreeDecorator {
//	private static final List<IAlleleTreeSpecies> SPECIES = new ArrayList<>();
//	private static final Map<ResourceLocation, Set<ITree>> biomeCache = new HashMap<>();
//
//	@SubscribeEvent
//	public void decorateTrees(Decorate event) {
//		if (event.getType() == Decorate.EventType.TREE) {
//			decorateTrees(event.getWorld(), event.getRand(), event.getPos().getX() + 8, event.getPos().getZ() + 8);
//		}
//	}
//
//	public static void decorateTrees(World world, Random rand, int worldX, int worldZ) {
//		float globalRarity = TreeConfig.getSpawnRarity(null);
//		if (globalRarity <= 0.0F || !TreeConfig.isValidDimension(null, world.provider.getDimension())) {
//			return;
//		}
//		if (biomeCache.isEmpty()) {
//			generateBiomeCache(world, rand);
//		}
//		for (int tries = 0; tries < 4 + rand.nextInt(2); tries++) {
//			int x = worldX + rand.nextInt(16);
//			int z = worldZ + rand.nextInt(16);
//
//			BlockPos pos = new BlockPos(x, 0, z);
//			Biome biome = world.getBiome(pos);
//			Set<ITree> trees = biomeCache.computeIfAbsent(biome.getRegistryName(), k -> new HashSet<>());
//			for (ITree tree : trees) {
//				String treeUID = tree.getGenome().getPrimary().getRegistryName().toString();
//				if (!TreeConfig.isValidDimension(treeUID, world.getDimension().getType())) {
//					continue;
//				}
//				IAlleleTreeSpecies species = tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES);
//				if (TreeConfig.getSpawnRarity(species.getRegistryName()) * globalRarity >= rand.nextFloat()) {
//					pos = getValidPos(world, x, z, tree);
//
//					if (pos == null) {
//						continue;
//					}
//
//					if (species.getGrowthProvider().canSpawn(tree, world, pos)) {
//						if (TreeGenHelper.generateTree(tree, world, pos)) {
//							return;
//						}
//					}
//				}
//			}
//		}
//	}
//
//	@Nullable
//	private static BlockPos getValidPos(World world, int x, int z, ITree tree) {
//		// get to the ground
//		final BlockPos topPos = world.getHeight(new BlockPos(x, 0, z));
//		if (topPos.getY() == 0) {
//			return null;
//		}
//
//		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(topPos);
//
//		BlockState blockState = world.getBlockState(pos);
//		while (BlockUtil.canReplace(blockState, world, pos)) {
//			pos.move(Direction.DOWN);
//			if (pos.getY() <= 0) {
//				return null;
//			}
//			blockState = world.getBlockState(pos);
//		}
//		if (tree instanceof IPlantable && blockState.getBlock().canSustainPlant(blockState, world, pos, Direction.UP, (IPlantable) tree)) {
//			return pos.up();
//		}
//		return null;
//	}
//
//	private static List<IAlleleTreeSpecies> getSpecies() {
//		if (!SPECIES.isEmpty()) {
//			return SPECIES;
//		}
//		for (IAllele allele : AlleleManager.geneticRegistry.getRegisteredAlleles(TreeChromosomes.SPECIES)) {
//			if (allele instanceof IAlleleTreeSpecies) {
//				IAlleleTreeSpecies alleleTreeSpecies = (IAlleleTreeSpecies) allele;
//				if (alleleTreeSpecies.getRarity() > 0) {
//					SPECIES.add(alleleTreeSpecies);
//				}
//			}
//		}
//		return SPECIES;
//	}
//
//	private static void generateBiomeCache(World world, Random rand) {
//		for (IAlleleTreeSpecies species : getSpecies()) {
//			IAllele[] template = TreeManager.treeRoot.getTemplate(species);
//			ITreeGenome genome = TreeManager.treeRoot.templateAsGenome(template);
//			ITree tree = TreeManager.treeRoot.getTree(world, genome);
//			String treeUID = genome.getPrimary().getUID();
//			IGrowthProvider growthProvider = species.getGrowthProvider();
//			for (Biome biome : Biome.REGISTRY) {
//				Set<ITree> trees = biomeCache.computeIfAbsent(biome.getRegistryName(), k -> new HashSet<>());
//				if (growthProvider.isBiomeValid(tree, biome) && TreeConfig.isValidBiome(treeUID, biome)) {
//					trees.add(tree);
//				}
//			}
//		}
//	}
//}
