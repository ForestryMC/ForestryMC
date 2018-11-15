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

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.arboriculture.worldgen.WorldGenArboriculture;
import forestry.core.worldgen.WorldGenBase;

public class TileSapling extends TileTreeContainer {
	private int timesTicked = 0;

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		timesTicked = nbttagcompound.getInteger("TT");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("TT", timesTicked);
		return nbttagcompound;
	}

	@Override
	public void onBlockTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
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

		WorldGenerator generator = tree.getTreeGenerator(world, getPos(), true);
		if (generator instanceof WorldGenArboriculture) {
			WorldGenArboriculture arboricultureGenerator = (WorldGenArboriculture) generator;
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

		WorldGenerator generator = tree.getTreeGenerator(world, getPos(), bonemealed);
		final boolean generated;
		if (generator instanceof WorldGenBase) {
			generated = ((WorldGenBase) generator).generate(world, random, getPos(), false);
		} else {
			generated = generator.generate(world, random, getPos());
		}

		if (generated) {
			IBreedingTracker breedingTracker = TreeManager.treeRoot.getBreedingTracker(world, getOwnerHandler().getOwner());
			breedingTracker.registerBirth(tree);
		}
	}

}
