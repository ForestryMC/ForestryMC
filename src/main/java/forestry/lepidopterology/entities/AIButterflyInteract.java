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

import net.minecraft.util.ChunkCoordinates;

public abstract class AIButterflyInteract extends AIButterflyBase {

	protected ChunkCoordinates rest;

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

		rest = new ChunkCoordinates((int) entity.posX, ((int) Math.floor(entity.posY)) - 1, (int) entity.posZ);
		if (entity.worldObj.isAirBlock(rest.posX, rest.posY, rest.posZ)) {
			return false;
		}

		canInteract = canInteract();

		return canInteract;
	}

	protected abstract boolean canInteract();

	@Override
	public boolean continueExecuting() {
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
