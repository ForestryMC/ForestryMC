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

public class FeatureSilverLime extends FeatureTree {
    public FeatureSilverLime(ITreeGenData tree) {
        super(tree, 6, 4);
    }

    @Override
    public boolean generate(
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
        FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);
        BlockPos pos = startPos.add(0, 3 + rand.nextInt(1), 0);
        return FeatureHelper.generateBranches(
                world,
                rand,
                wood,
                pos,
                girth,
                0.25f,
                0.10f,
                Math.round(height * 0.25f),
                2,
                0.5f
        );
    }

    @Override
    protected void generateLeaves(
            IWorld world,
            Random rand,
            TreeBlockTypeLeaf leaf,
            List<BlockPos> branchEnds,
            BlockPos startPos
    ) {
        for (BlockPos branchEnd : branchEnds) {
            FeatureHelper.generateCylinderFromPos(world, leaf, branchEnd, girth, 1, FeatureHelper.EnumReplaceMode.AIR);
        }

        int leafSpawn = height + 1;

        FeatureHelper.generateCylinderFromTreeStartPos(
                world,
                leaf,
                startPos.add(0, leafSpawn--, 0),
                girth,
                girth,
                1,
                FeatureHelper.EnumReplaceMode.SOFT
        );
        float radius = 1;
        while (leafSpawn > 1) {
            FeatureHelper.generateCylinderFromTreeStartPos(
                    world,
                    leaf,
                    startPos.add(0, leafSpawn--, 0),
                    girth,
                    radius + girth,
                    1,
                    FeatureHelper.EnumReplaceMode.SOFT
            );
            radius += 0.25;
        }
    }
}
