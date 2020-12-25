/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.lepidopterology;

import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterfly;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Random;

public class ButterflySpawner implements ILeafTickHandler {
    @Override
    public boolean onRandomLeafTick(ITree tree, World world, Random rand, BlockPos pos, boolean isDestroyed) {
        if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return false;
        }

        if (rand.nextFloat() >= tree.getGenome().getActiveValue(TreeChromosomes.SAPPINESS) *
                                tree.getGenome().getActiveValue(TreeChromosomes.YIELD)
        ) {
            return false;
        }

        IButterfly spawn = ButterflyManager.butterflyRoot
                .getIndividualTemplates()
                .get(rand.nextInt(ButterflyManager.butterflyRoot.getIndividualTemplates().size()));
        float rarity;
        if (!ModuleLepidopterology.spawnRaritys
                .containsKey(spawn.getGenome().getPrimary().getRegistryName().getPath())
        ) {
            rarity = spawn.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getRarity();
        } else {
            rarity = ModuleLepidopterology.spawnRaritys.get(spawn.getGenome().getPrimary().getRegistryName().getPath());
        }

        if (rand.nextFloat() >= rarity * 0.5f) {
            return false;
        }

        if (ButterflyUtils.countButterfly(world) > ModuleLepidopterology.spawnConstraint) {
            return false;
        }

        if (!spawn.canSpawn(world, pos.getX(), pos.getY(), pos.getZ())) {
            return false;
        }

        if (world.isAirBlock(pos.north())) {
            ButterflyUtils.attemptButterflySpawn(world, spawn, pos.north());
        } else if (world.isAirBlock(pos.south())) {
            ButterflyUtils.attemptButterflySpawn(world, spawn, pos.south());
        } else if (world.isAirBlock(pos.west())) {
            ButterflyUtils.attemptButterflySpawn(world, spawn, pos.west());
        } else if (world.isAirBlock(pos.east())) {
            ButterflyUtils.attemptButterflySpawn(world, spawn, pos.east());
        }

        return false;
    }
}
