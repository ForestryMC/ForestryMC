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
package forestry.worktable.blocks;

import forestry.core.blocks.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockWorktable extends BlockBase<BlockTypeWorktable> {
    public BlockWorktable(BlockTypeWorktable worktable) {
        super(worktable, Block.Properties.create(Material.IRON).harvestLevel(0).harvestTool(ToolType.PICKAXE));
    }
}
