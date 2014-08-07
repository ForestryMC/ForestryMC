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

import forestry.core.utils.Utils;
import forestry.lepidopterology.entities.EntityButterfly.EnumButterflyState;

public class AIButterflyRest extends AIButterflyBase {

	public AIButterflyRest(EntityButterfly entity) {
		super(entity);
		setMutexBits(3);
	}
	
	@Override
	public boolean shouldExecute() {
		
		if(entity.getExhaustion() < EntityButterfly.EXHAUSTION_REST
				&& entity.canFly())
			return false;
		
		if(!entity.worldObj.isAirBlock((int)entity.posX, ((int)Math.floor(entity.posY)), (int)entity.posZ))
			return false;
		
		ChunkCoordinates rest = new ChunkCoordinates((int)entity.posX, ((int)Math.floor(entity.posY)) - 1, (int)entity.posZ);
		if(entity.worldObj.isAirBlock(rest.posX, rest.posY, rest.posZ))
			return false;
		if(Utils.isLiquidBlock(entity.worldObj, rest.posX, rest.posY, rest.posZ))
			return false;
		if(!entity.getButterfly().isAcceptedEnvironment(entity.worldObj, rest.posX, rest.posY, rest.posZ))
			return false;

		entity.setDestination(null);
		entity.setState(EnumButterflyState.RESTING);
		return true;
	}

	@Override
	public boolean continueExecuting() {
		if(entity.getExhaustion() <= 0 && entity.canFly())
			return false;
		if(entity.isInWater())
			return false;
		
		return true;
	}
	
	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
	}

	@Override
	public void updateTask() {
		entity.changeExhaustion(-1);
	}
}
