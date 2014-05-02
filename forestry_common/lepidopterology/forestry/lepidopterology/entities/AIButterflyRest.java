/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
