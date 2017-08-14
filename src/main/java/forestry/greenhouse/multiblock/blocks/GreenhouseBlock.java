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

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.api.greenhouse.IBlankBlock;
import forestry.api.greenhouse.IGreenhouseBlock;
import forestry.api.greenhouse.IGreenhouseBlockStorage;
import forestry.api.greenhouse.IGreenhouseProvider;

public abstract class GreenhouseBlock<P extends IGreenhouseBlock> implements IGreenhouseBlock {
	protected final BlockPos pos;
	protected final IGreenhouseProvider provider;
	protected final IGreenhouseBlockStorage storage;
	@Nullable
	protected P root;
	@Nullable
	protected EnumFacing rootFace;

	public GreenhouseBlock(IGreenhouseProvider provider, BlockPos pos) {
		this.provider = provider;
		this.storage = provider.getStorage();
		this.pos = pos;
		this.root = null;
		this.rootFace = null;
	}

	public GreenhouseBlock(IGreenhouseProvider provider, BlockPos pos, @Nullable EnumFacing rootFace, @Nullable P root) {
		this.provider = provider;
		this.storage = provider.getStorage();
		this.pos = pos;
		this.root = root;
		this.rootFace = rootFace;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Nullable
	@Override
	public P getRoot() {
		return root;
	}

	public void setRoot(@Nullable P root) {
		this.root = root;
	}

	@Nullable
	@Override
	public EnumFacing getRootFace() {
		return rootFace;
	}

	@Override
	public IGreenhouseProvider getProvider() {
		return provider;
	}

	@Override
	public void onNeighborRemoved(IBlankBlock changedBlock, EnumFacing facing, boolean forcedRemove, @Nullable Set<IGreenhouseBlock> blocksToCheck) {
	}

	public void setRootFace(@Nullable EnumFacing rootFace) {
		this.rootFace = rootFace;
	}

	@Override
	public int hashCode() {
		return pos.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IGreenhouseBlock)) {
			return false;
		}
		IGreenhouseBlock logicBlock = (IGreenhouseBlock) obj;
		BlockPos blockPos = logicBlock.getPos();
		return pos.equals(blockPos);
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
	}
}
