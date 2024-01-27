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
package forestry.arboriculture.tiles;

import javax.annotation.Nonnull;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IBreedingTracker;
import forestry.arboriculture.features.ArboricultureTiles;
import forestry.arboriculture.worldgen.FeatureArboriculture;
import forestry.core.utils.WorldUtils;
import forestry.core.worldgen.FeatureBase;

public class TileSapling extends TileTreeContainer {
	public static final ModelProperty<IAlleleTreeSpecies> TREE_SPECIES = new ModelProperty<>();

	private int timesTicked = 0;

	public TileSapling(BlockPos pos, BlockState state) {
		super(ArboricultureTiles.SAPLING.tileType(), pos, state);
	}

	/* SAVING & LOADING */
	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);

		timesTicked = compoundNBT.getInt("TT");
	}

	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);

		compoundNBT.putInt("TT", timesTicked);
	}

	@Override
	public void onBlockTick(Level worldIn, BlockPos pos, BlockState state, RandomSource rand) {
		timesTicked++;
		tryGrow(rand, false);
	}

	private static int getRequiredMaturity(Level world, ITree tree) {
		ITreekeepingMode treekeepingMode = TreeManager.treeRoot.getTreekeepingMode(world);
		float maturationModifier = treekeepingMode.getMaturationModifier(tree.getGenome(), 1f);
		return Math.round(tree.getRequiredMaturity() * maturationModifier);
	}

	public boolean canAcceptBoneMeal(RandomSource rand) {
		ITree tree = getTree();

		if (tree == null) {
			return false;
		}

		int maturity = getRequiredMaturity(level, tree);
		if (timesTicked < maturity) {
			return true;
		}

		Feature generator = tree.getTreeGenerator(WorldUtils.asServer(level), getBlockPos(), true);
		if (generator instanceof FeatureArboriculture arboricultureGenerator) {
			arboricultureGenerator.preGenerate(level, rand, getBlockPos());
			return arboricultureGenerator.getValidGrowthPos(level, getBlockPos()) != null;
		} else {
			return true;
		}
	}

	public void tryGrow(RandomSource random, boolean bonemealed) {
		ITree tree = getTree();

		if (tree == null) {
			return;
		}

		int maturity = getRequiredMaturity(level, tree);
		if (timesTicked < maturity) {
			if (bonemealed) {
				timesTicked = maturity;
			}
			return;
		}

		Feature<NoneFeatureConfiguration> generator = tree.getTreeGenerator(WorldUtils.asServer(level), getBlockPos(), bonemealed);
		final boolean generated;
		if (generator instanceof FeatureBase) {
			generated = ((FeatureBase) generator).place(level, random, getBlockPos(), false);
		} else {
			ServerLevel level = (ServerLevel) this.level;
			generated = generator.place(new FeaturePlaceContext<>(Optional.empty(), level, level.getChunkSource().getGenerator(), random, getBlockPos(), FeatureConfiguration.NONE));
		}

		if (generated) {
			IBreedingTracker breedingTracker = TreeManager.treeRoot.getBreedingTracker(level, getOwnerHandler().getOwner());
			breedingTracker.registerBirth(tree);
		}
	}

	@Nonnull
	@Override
	public ModelData getModelData() {
		ITree tree = getTree();
		if (tree == null) {
			return ModelData.EMPTY;
		}
		return ModelData.builder().with(TREE_SPECIES, tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES)).build();
	}
}
