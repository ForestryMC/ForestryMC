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
package forestry.arboriculture.commands;

import forestry.api.arboriculture.genetics.ITree;
import genetics.commands.SpeciesNotFoundException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;

public class ForestSpawner implements ITreeSpawner {
    @Override
    public int spawn(CommandSource source, ITree tree, PlayerEntity player) throws SpeciesNotFoundException {
        Vector3d look = player.getLookVec();

        int x = (int) Math.round(player.getPosX() + 16 * look.x);
        int y = (int) Math.round(player.getPosY());
        int z = (int) Math.round(player.getPosZ() + 16 * look.z);

        for (int i = 0; i < 16; i++) {
            int spawnX = x + player.world.rand.nextInt(32) - 16;
            int spawnZ = z + player.world.rand.nextInt(32) - 16;
            BlockPos pos = new BlockPos(spawnX, y, spawnZ);

            TreeGenHelper.generateTree(tree, (ISeedReader) player.world, pos);
        }

        return 1;
    }
}
