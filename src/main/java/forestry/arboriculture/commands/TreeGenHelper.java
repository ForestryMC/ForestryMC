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

import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.WorldUtils;
import forestry.core.worldgen.FeatureBase;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.commands.SpeciesNotFoundException;
import genetics.utils.AlleleUtils;

public final class TreeGenHelper {

	public static Feature<NoneFeatureConfiguration> getWorldGen(ResourceLocation treeName, Player player, BlockPos pos) throws SpeciesNotFoundException {
		IGenome treeGenome = getTreeGenome(treeName);
		ITree tree = TreeManager.treeRoot.getTree(player.level, treeGenome);
		return tree.getTreeGenerator(WorldUtils.asServer(player.level), pos, true);
	}

	public static <FC extends FeatureConfiguration> boolean generateTree(Feature<FC> feature, ChunkGenerator generator, Level world, BlockPos pos, FC config) {
		if (pos.getY() > 0 && world.isEmptyBlock(pos.below())) {
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
				return ((FeatureBase) feature).place(world, world.random, pos, true);
			} else {
				return feature.place((ServerLevel) world, generator, world.random, pos, config);
			}
		}
		return false;
	}

	public static boolean generateTree(ITree tree, WorldGenLevel world, BlockPos pos) {
		Feature<NoneFeatureConfiguration> gen = tree.getTreeGenerator(world, pos, true);

		BlockState blockState = world.getBlockState(pos);
		if (BlockUtil.canPlaceTree(blockState, world, pos)) {
			if (gen instanceof FeatureBase) {
				return ((FeatureBase) gen).place(world, world.getRandom(), pos, true);
			} else {
				return gen.place((ServerLevel) world, ((ServerChunkCache) world.getChunkSource()).getGenerator(), world.getRandom(), pos, FeatureConfiguration.NONE);
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
	}
}
