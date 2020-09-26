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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class FeatureDarkOak extends FeatureTreeVanilla {
    public FeatureDarkOak(ITreeGenData tree) {
        super(tree);
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
}
