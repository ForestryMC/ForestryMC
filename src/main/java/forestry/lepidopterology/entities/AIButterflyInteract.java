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

import net.minecraft.util.math.BlockPos;

public abstract class AIButterflyInteract extends AIButterflyBase {
	@Nullable
	protected BlockPos rest;

	private boolean canInteract = false;
	private boolean hasInteracted = false;

	protected AIButterflyInteract(EntityButterfly entity) {
		super(entity);
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (entity.getState() != EnumButterflyState.RESTING) {
			return false;
		}

		rest = new BlockPos((int) entity.posX, (int) Math.floor(entity.posY) - 1, (int) entity.posZ);
		if (entity.world.isAirBlock(rest)) {
			return false;
		}

		canInteract = canInteract();

		return canInteract;
	}

	protected abstract boolean canInteract();

	@Override
	public boolean shouldContinueExecuting() {
		return canInteract && !hasInteracted;
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
		canInteract = false;
		hasInteracted = false;
		rest = null;
	}

	protected void setHasInteracted() {
		hasInteracted = true;
	}

}
