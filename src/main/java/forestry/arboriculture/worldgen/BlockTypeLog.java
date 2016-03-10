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

import net.minecraft.block.BlockLog;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class BlockTypeLog extends BlockTypeWood {
	public BlockTypeLog(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public void setDirection(EnumFacing facing) {
		BlockLog.EnumAxis axis = BlockLog.EnumAxis.fromFacingAxis(facing.getAxis());
		state = state.withProperty(BlockLog.LOG_AXIS, axis);
	}
}
