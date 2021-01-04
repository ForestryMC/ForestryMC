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

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class FeatureCocobolo extends FeatureTree {
    public FeatureCocobolo(ITreeGenData tree) {
        super(tree, 8, 8);
    }

    @Override
    public boolean func_241855_a(
            ISeedReader world,
            ChunkGenerator generator,
            Random rand,
            BlockPos pos,
            NoFeatureConfig config
    ) {
        return place(world, rand, pos, false);
    }

    @Override
    public Set<BlockPos> generateTrunk(IWorld world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
        return FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);
    }

    @Override
    protected void generateLeaves(
            IWorld world,
            Random rand,
            TreeBlockTypeLeaf leaf,
            List<BlockPos> branchEnds,
            BlockPos startPos
    ) {
        int leafSpawn = height;

        for (BlockPos treeTop : branchEnds) {
            FeatureHelper.addBlock(world, treeTop.up(), leaf, FeatureHelper.EnumReplaceMode.AIR);
        }
        leafSpawn--;
        FeatureHelper.generateCylinderFromTreeStartPos(
                world,
                leaf,
                startPos.add(0, leafSpawn--, 0),
                girth,
                1 + girth,
                1,
                FeatureHelper.EnumReplaceMode.SOFT
        );

        if (height > 10) {
            FeatureHelper.generateCylinderFromTreeStartPos(
                    world,
                    leaf,
                    startPos.add(0, leafSpawn--, 0),
                    girth,
                    2 + girth,
                    1,
                    FeatureHelper.EnumReplaceMode.SOFT
            );
        }
        FeatureHelper.generateCylinderFromTreeStartPos(
                world,
                leaf,
                startPos.add(0, leafSpawn, 0),
                girth,
                girth,
                1,
                FeatureHelper.EnumReplaceMode.SOFT
        );

        leafSpawn--;

        while (leafSpawn > 4) {
            int offset = 1;
            if (rand.nextBoolean()) {
                offset = -1;
            }

            float radius = (leafSpawn % 2 == 0) ? 2 + girth : girth;
            FeatureHelper.generateCylinderFromTreeStartPos(
                    world,
                    leaf,
                    startPos.add(offset, leafSpawn, offset),
                    girth,
                    radius,
                    1,
                    FeatureHelper.EnumReplaceMode.AIR
            );

            leafSpawn--;
        }
    }
}
