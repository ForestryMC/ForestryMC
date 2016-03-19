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
package forestry.greenhouse.multiblock;

import forestry.api.greenhouse.IInternalBlockFace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class InternalBlockFace implements IInternalBlockFace {

	private final EnumFacing face;
	private final BlockPos pos;
	private boolean isTested;
	
	public InternalBlockFace(EnumFacing face, BlockPos pos, boolean isTested) {
		this.face = face;
		this.pos = pos;
		this.isTested = isTested;
	}
	
	@Override
	public boolean isTested() {
		return isTested;
	}
	
	@Override
	public void setTested(boolean tested) {
		this.isTested = tested;
	}

	@Override
	public EnumFacing getFace() {
		return face;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

}
