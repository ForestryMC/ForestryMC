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
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IBreedingTracker;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.worldgen.FeatureArboriculture;
import forestry.core.worldgen.FeatureBase;

public class TileSapling extends TileTreeContainer {
	public static final ModelProperty<IAlleleTreeSpecies> TREE_SPECIES = new ModelProperty<IAlleleTreeSpecies>();

	private int timesTicked = 0;

	public TileSapling() {
		super(ModuleArboriculture.getTiles().sapling);
	}

	/* SAVING & LOADING */
	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);

		timesTicked = compoundNBT.getInt("TT");
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);

		compoundNBT.putInt("TT", timesTicked);
		return compoundNBT;
	}

	@Override
	public void onBlockTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		timesTicked++;
		tryGrow(rand, false);
	}

	private static int getRequiredMaturity(World world, ITree tree) {
		ITreekeepingMode treekeepingMode = TreeManager.treeRoot.getTreekeepingMode(world);
		float maturationModifier = treekeepingMode.getMaturationModifier(tree.getGenome(), 1f);
		return Math.round(tree.getRequiredMaturity() * maturationModifier);
	}

	public boolean canAcceptBoneMeal(Random rand) {
		ITree tree = getTree();

		if (tree == null) {
			return false;
		}

		int maturity = getRequiredMaturity(world, tree);
		if (timesTicked < maturity) {
			return true;
		}

		Feature generator = tree.getTreeGenerator(world, getPos(), true);
		if (generator instanceof FeatureArboriculture) {
			FeatureArboriculture arboricultureGenerator = (FeatureArboriculture) generator;
			arboricultureGenerator.preGenerate(world, rand, getPos());
			return arboricultureGenerator.getValidGrowthPos(world, getPos()) != null;
		} else {
			return true;
		}
	}

	public void tryGrow(Random random, boolean bonemealed) {
		ITree tree = getTree();

		if (tree == null) {
			return;
		}

		int maturity = getRequiredMaturity(world, tree);
		if (timesTicked < maturity) {
			if (bonemealed) {
				timesTicked = maturity;
			}
			return;
		}

		Feature generator = tree.getTreeGenerator(world, getPos(), bonemealed);
		final boolean generated;
		if (generator instanceof FeatureBase) {
			generated = ((FeatureBase) generator).place(world, random, getPos(), false);
		} else {
			generated = generator.place(world, world.getChunkProvider().getChunkGenerator(), random, getPos(), IFeatureConfig.NO_FEATURE_CONFIG);
		}

		if (generated) {
			IBreedingTracker breedingTracker = TreeManager.treeRoot.getBreedingTracker(world, getOwnerHandler().getOwner());
			breedingTracker.registerBirth(tree);
		}
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		ITree tree = getTree();
		if (tree == null) {
			return EmptyModelData.INSTANCE;
		}
		return new ModelDataMap.Builder().withInitial(TREE_SPECIES, tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES)).build();
	}
}
