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
package forestry.arboriculture.worldgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.commands.TreeGenHelper;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;

public class TreeDecorator {

	private static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_TREES", new Class[0]);
	private static final List<IAlleleTreeSpecies> SPECIES = new ArrayList<>();
	
	@SubscribeEvent
	public void decorateTrees(Decorate event) {
		if(event.getType() == Decorate.EventType.TREE){
			decorateTrees(event.getWorld(), event.getRand(), event.getPos().getX() + 8, event.getPos().getZ() + 8);
		}
	}

	public static void decorateTrees(World world, Random rand, int worldX, int worldZ) {
		List<IAlleleTreeSpecies> trees = getSpecies();

		Collections.shuffle(trees, rand);

		for (int tries = 0; tries < 4; tries++) {
			int x = worldX + rand.nextInt(16);
			int z = worldZ + rand.nextInt(16);

			BlockPos pos = new BlockPos(x, 0, z);
			if (!world.isBlockLoaded(pos)) {
				Log.error("tried to generate a hive in an unloaded area.");
				return;
			}

			for (IAlleleTreeSpecies species : trees) {
				if (species.getRarity()  >= rand.nextFloat()) {
					IAllele[] template = TreeManager.treeRoot.getTemplate(species);
					ITreeGenome genome = TreeManager.treeRoot.templateAsGenome(template);
					ITree tree = TreeManager.treeRoot.getTree(world, genome);
					pos = getValidPos(world, x, z, tree);

					if(pos == null){
						continue;
					}
					
					if (species.getGrowthProvider().canSpawn(genome, world, pos)) {
						if (TreeGenHelper.generateTree(tree, world, pos)) {
							return;
						}
					}
				}
			}
		}
	}
	
	private static BlockPos getValidPos(World world, int x, int z, ITree tree){
		// get to the ground
		final BlockPos topPos = world.getHeight(new BlockPos(x, 0, z));
		if (topPos.getY() == 0) {
			return null;
		}

		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(topPos);

		IBlockState blockState = world.getBlockState(pos);
		IBlockState downState = world.getBlockState(pos.down());
		while (BlockUtil.canReplace(blockState, world, pos)) {
			pos.move(EnumFacing.DOWN);
			if (pos.getY() <= 0) {
				return null;
			}
			blockState = world.getBlockState(pos);
			downState = world.getBlockState(pos.down());
		}
		if(tree instanceof IPlantable && blockState.getBlock().canSustainPlant(blockState, world, pos, EnumFacing.UP, (IPlantable) tree)){
			return pos.up();
		}
		return null;
	}
	
	private static List<IAlleleTreeSpecies> getSpecies(){
		if(!SPECIES.isEmpty()){
			return SPECIES;
		}
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumTreeChromosome.SPECIES)) {
			if (allele instanceof IAlleleTreeSpecies) {
				IAlleleTreeSpecies alleleTreeSpecies = (IAlleleTreeSpecies) allele;
				if(alleleTreeSpecies.getRarity() > 0){
					SPECIES.add(alleleTreeSpecies);
				}
			}
		}
		return SPECIES;
	}
}
