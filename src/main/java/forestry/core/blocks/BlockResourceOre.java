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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.common.ToolType;

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

	//TODO - loot tables (or whatever is necessary for ore
	//	@Override
	//	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
	//		EnumResourceType type = state.getComb(ORE_RESOURCES);
	//		switch (type) {
	//			case APATITE: {
	//				int fortuneModifier = RANDOM.nextInt(fortune + 2) - 1;
	//				if (fortuneModifier < 0) {
	//					fortuneModifier = 0;
	//				}
	//
	//				int amount = (2 + RANDOM.nextInt(5)) * (fortuneModifier + 1);
	//				if (amount > 0) {
	//					drops.add(ModuleCore.getItems().apatite.getItemStack(amount));
	//				}
	//				break;
	//			}
	//			case TIN: {
	//				drops.add(new ItemStack(this, 1, damageDropped(state)));
	//				break;
	//			}
	//			case COPPER: {
	//				drops.add(new ItemStack(this, 1, damageDropped(state)));
	//				break;
	//			}
	//		}
	//	}
}
