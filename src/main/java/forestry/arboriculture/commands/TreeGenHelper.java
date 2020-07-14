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
package forestry.arboriculture.commands;

public final class TreeGenHelper {

	/*public static Feature getWorldGen(ResourceLocation treeName, PlayerEntity player, BlockPos pos) throws SpeciesNotFoundException {
		IGenome treeGenome = getTreeGenome(treeName);
		ITree tree = TreeManager.treeRoot.getTree(player.world, treeGenome);
		return tree.getTreeGenerator(player.world, pos, true);
	}

	public static <FC extends IFeatureConfig> boolean generateTree(Feature<FC> feature, ChunkGenerator<? extends GenerationSettings> generator, World world, BlockPos pos, FC config) {
		if (pos.getY() > 0 && world.isAirBlock(pos.down())) {
			pos = BlockUtil.getNextSolidDownPos(world, pos);
		} else {
			pos = BlockUtil.getNextReplaceableUpPos(world, pos);
		}
		if (pos == null) {
			return false;
		}

		BlockState blockState = world.getBlockState(pos);
		if (BlockUtil.canPlaceTree(blockState, world, pos)) {
			if (feature instanceof FeatureBase) {
				return ((FeatureBase) feature).place(world, world.rand, pos, true);
			} else {
				return feature.place(world, generator, world.rand, pos, config);
			}
		}
		return false;
	}

	public static boolean generateTree(ITree tree, World world, BlockPos pos) {
		Feature<NoFeatureConfig> gen = tree.getTreeGenerator(world, pos, true);
		ChunkGenerator<? extends GenerationSettings> generator = ((ServerChunkProvider) world.getChunkProvider()).getChunkGenerator();

		BlockState blockState = world.getBlockState(pos);
		if (BlockUtil.canPlaceTree(blockState, world, pos)) {
			if (gen instanceof FeatureBase) {
				return ((FeatureBase) gen).place(world, world.rand, pos, true);
			} else {
				return gen.place(world, generator, world.rand, pos, IFeatureConfig.NO_FEATURE_CONFIG);
			}
		}
		return false;
	}

	private static IGenome getTreeGenome(ResourceLocation speciesName) throws SpeciesNotFoundException {
		IAlleleTreeSpecies species = null;

		for (ResourceLocation uid : AlleleUtils.getRegisteredNames()) {
			if (!uid.equals(speciesName)) {
				continue;
			}

			Optional<IAllele> optionalAllele = AlleleUtils.getAllele(uid);
			if (!optionalAllele.isPresent()) {
				continue;
			}
			IAllele allele = optionalAllele.get();
			if (allele instanceof IAlleleTreeSpecies) {
				species = (IAlleleTreeSpecies) allele;
				break;
			}
		}

		if (species == null) {
			species = AlleleUtils.filteredStream(TreeChromosomes.SPECIES)
				.filter(allele -> {
					String displayName = allele.getDisplayName().getString().replaceAll("\\s", "");
					return displayName.equals(speciesName.toString());
				})
				.findFirst()
				.orElse(null);
		}

		if (species == null) {
			throw new SpeciesNotFoundException(speciesName);
		}

		IAllele[] template = TreeManager.treeRoot.getTemplates().getTemplate(species.getRegistryName().toString());

		return TreeManager.treeRoot.getKaryotype().templateAsGenome(template);
	}*/
}
