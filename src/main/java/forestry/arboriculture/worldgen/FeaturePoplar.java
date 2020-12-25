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

public class FeaturePoplar extends FeatureTree {
    public FeaturePoplar(ITreeGenData tree) {
        super(tree, 8, 3);
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
    protected void generateLeaves(
            IWorld world,
            Random rand,
            TreeBlockTypeLeaf leaf,
            List<BlockPos> branchEnds,
            BlockPos startPos
    ) {
        int leafSpawn = height + 1;
        float leafRadius = (girth - 1.0f) / 2.0f;

        while (leafSpawn > girth - 1) {
            FeatureHelper.generateCylinderFromTreeStartPos(
                    world,
                    leaf,
                    startPos.add(0, leafSpawn--, 0),
                    girth,
                    leafRadius + girth,
                    1,
                    FeatureHelper.EnumReplaceMode.SOFT
            );
        }
    }
}
