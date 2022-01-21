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
package forestry.farming.logic.crops;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;

public abstract class Crop implements ICrop {
	private final Level world;
	protected final BlockPos position;

	protected Crop(Level world, BlockPos position) {
		this.world = world;
		this.position = position;
	}

	protected abstract boolean isCrop(Level world, BlockPos pos);

	protected abstract NonNullList<ItemStack> harvestBlock(Level world, BlockPos pos);

	@Nullable
	@Override
	public NonNullList<ItemStack> harvest() {
		if (!isCrop(world, position)) {
			return null;
		}

		return harvestBlock(world, position);
	}

	@Override
	public BlockPos getPosition() {
		return position;
	}
}
