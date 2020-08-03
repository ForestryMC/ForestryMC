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
package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class BlockResourceOre extends Block {
    private final EnumResourceType type;

    public BlockResourceOre(EnumResourceType type) {
        super(Block.Properties.create(Material.ROCK)
                .hardnessAndResistance(3f, 5f)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1));
        this.type = type;
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
        Random rand = reader instanceof World ? ((World) reader).rand : new Random();
        if (type == EnumResourceType.APATITE) {
            return MathHelper.nextInt(rand, 1, 4);
        }
        return super.getExpDrop(state, reader, pos, fortune, silktouch);
    }
}
