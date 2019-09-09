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

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.core.commands.SpeciesNotFoundException;
import forestry.core.utils.BlockUtil;
import forestry.core.worldgen.FeatureBase;

public final class TreeGenHelper {

	public static Feature getWorldGen(ResourceLocation treeName, PlayerEntity player, BlockPos pos) throws SpeciesNotFoundException {
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
		ChunkGenerator<? extends GenerationSettings> generator = world.getChunkProvider().getChunkGenerator();

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

		for (ResourceLocation uid : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredNames()) {
			if (!uid.equals(speciesName)) {
				continue;
			}

			Optional<IAllele> optionalAllele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(uid);
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
			for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(TreeChromosomes.SPECIES)) {
				if (allele instanceof IAlleleTreeSpecies && allele.getDisplayName().getFormattedText().replaceAll("\\s", "").equals(speciesName.toString())) {
					species = (IAlleleTreeSpecies) allele;
					break;
				}
			}
		}

		if (species == null) {
			throw new SpeciesNotFoundException(speciesName);
		}

		IAllele[] template = TreeManager.treeRoot.getTemplates().getTemplate(species.getRegistryName().toString());

		return TreeManager.treeRoot.getKaryotype().templateAsGenome(template);
	}
}
