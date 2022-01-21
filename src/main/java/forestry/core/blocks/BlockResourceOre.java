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

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.ToolType;

public class BlockResourceOre extends Block {
	private final EnumResourceType type;

	public BlockResourceOre(EnumResourceType type) {
		super(Block.Properties.of(Material.STONE)
				.strength(3f, 5f)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(1));
		this.type = type;
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch) {
		Random rand = reader instanceof Level ? ((Level) reader).random : new Random();
		if (type == EnumResourceType.APATITE) {
			return Mth.nextInt(rand, 1, 4);
		}
		return super.getExpDrop(state, reader, pos, fortune, silktouch);
	}
}
