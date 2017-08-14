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
package forestry.greenhouse.multiblock.blocks.blank;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.greenhouse.api.greenhouse.IBlankBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlockHandler;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.multiblock.blocks.GreenhouseBlock;

public class BlankBlock extends GreenhouseBlock<IBlankBlock> implements IBlankBlock {
	private final boolean[] faces;
	private boolean hasWall;
	private boolean isValid;

	public BlankBlock(IGreenhouseProvider provider, BlockPos pos, EnumFacing rootFace, IBlankBlock root) {
		super(provider, pos, rootFace, root);
		this.faces = new boolean[6];
		if (rootFace != null) {
			this.faces[rootFace.ordinal()] = true;
		}
	}

	@Override
	public boolean isFaceTested(EnumFacing face) {
		return faces[face.ordinal()];
	}

	@Override
	public void setFaceTested(EnumFacing face, boolean isTested) {
		faces[face.ordinal()] = isTested;
	}

	@Override
	public boolean isNearWall() {
		return hasWall;
	}

	@Override
	public void setNearWall(boolean hasWall) {
		this.hasWall = hasWall;
	}

	@Override
	public void validate() {
		isValid = true;
	}

	@Override
	public void invalidate(boolean chunkUnloading) {
		isValid = false;
	}

	@Override
	public boolean isValid() {
		return isValid;
	}

	@Override
	public IGreenhouseBlockHandler getHandler() {
		return BlankBlockHandler.getInstance();
	}
}
