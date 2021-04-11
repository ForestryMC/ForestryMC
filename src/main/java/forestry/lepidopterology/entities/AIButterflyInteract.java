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
package forestry.lepidopterology.entities;

import javax.annotation.Nullable;
import java.util.EnumSet;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public abstract class AIButterflyInteract extends AIButterflyBase {
	@Nullable
	protected BlockPos rest;

	private boolean canInteract = false;
	private boolean hasInteracted = false;

	protected AIButterflyInteract(EntityButterfly entity) {
		super(entity);
		setFlags(EnumSet.of(Flag.MOVE));
		//		setMutexBits(3);	TODO mutex
	}

	@Override
	public boolean canUse() {
		if (entity.getState() != EnumButterflyState.RESTING) {
			return false;
		}
		Vector3d pos = entity.position();
		rest = new BlockPos((int) pos.x, (int) Math.floor(pos.y) - 1, (int) pos.z);
		if (entity.level.isEmptyBlock(rest)) {
			return false;
		}

		canInteract = canInteract();

		return canInteract;
	}

	protected abstract boolean canInteract();

	@Override
	public boolean canContinueToUse() {
		return canInteract && !hasInteracted;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
		canInteract = false;
		hasInteracted = false;
		rest = null;
	}

	protected void setHasInteracted() {
		hasInteracted = true;
	}

}
