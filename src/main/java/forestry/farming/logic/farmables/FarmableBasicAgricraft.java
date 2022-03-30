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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;
import forestry.farming.logic.crops.CropBasicAgriCraft;

public class FarmableBasicAgricraft extends FarmableBase {
	public FarmableBasicAgricraft(ItemStack germling, BlockState plantedState, BlockState matureState, boolean replant) {
		super(germling, plantedState, matureState, replant);
	}

	@Override
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		if (blockState != matureState) {
			return null;
		}
		return new CropBasicAgriCraft(world, blockState, pos);
	}
}
