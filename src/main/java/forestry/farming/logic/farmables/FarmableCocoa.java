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
package forestry.farming.logic.farmables;

import net.minecraft.block.BlockCocoa;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.utils.BlockUtil;

public class FarmableCocoa extends FarmableAgingCrop {

	public static final Item COCOA_SEED = Items.DYE;
	public static final int COCOA_META = 3;

	public FarmableCocoa() {
		super(new ItemStack(COCOA_SEED, 1, COCOA_META), Blocks.COCOA, new ItemStack(COCOA_SEED, 1, COCOA_META), BlockCocoa.AGE, 2, null);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return BlockUtil.tryPlantCocoaPod(world, pos);
	}
}
