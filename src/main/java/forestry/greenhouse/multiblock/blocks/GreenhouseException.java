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
package forestry.greenhouse.multiblock.blocks;

import net.minecraft.util.math.BlockPos;

public class GreenhouseException extends Exception {

	public BlockPos pos;

	public GreenhouseException(String reason) {
		super(reason);
	}

	public GreenhouseException setPos(BlockPos pos) {
		this.pos = pos;
		return this;
	}
}
