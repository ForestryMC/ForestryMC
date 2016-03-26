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

import java.util.List;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.core.utils.GeneticsUtil;
import forestry.lepidopterology.PluginLepidopterology;

public class AIButterflyMate extends AIButterflyInteract {

    private EntityButterfly targetMate;
    private double moveSpeed;

	
	public AIButterflyMate(EntityButterfly entity) {
		super(entity);
	}

	/**
	 * Should lay egg?
	 */
	@Override
	protected boolean canInteract() {
		if(entity.getButterfly().getMate() == null && entity.canMate()){
			return true;
		}
		if (entity.cooldownEgg > 0) {
			return false;
		}

		if (entity.getButterfly().getMate() == null) {
			return false;
		}

		if (entity.worldObj.countEntities(EntityButterfly.class) > PluginLepidopterology.spawnConstraint) {
			return false;
		}

		return GeneticsUtil.canNurse(entity.getButterfly(), entity.worldObj, rest);
	}

	@Override
	public void updateTask() {
		if (continueExecuting()) {
			if(entity.getButterfly().getMate() == null){
		        entity.getLookHelper().setLookPositionWithEntity(targetMate, 10.0F, entity.getVerticalFaceSpeed());
		        entity.getNavigator().tryMoveToEntityLiving(targetMate, 0.7D);
		        
		        if (entity.cooldownMate <= 0 && entity.getDistanceSqToEntity(targetMate) < 9.0D){
		        	entity.getButterfly().mate(targetMate.getButterfly());
		        	entity.cooldownMate = EntityButterfly.COOLDOWNS;
		        }
			}else{
				IPollinatable tile = GeneticsUtil.getOrCreatePollinatable(null, entity.worldObj, rest);
				if (tile instanceof IButterflyNursery) {
					IButterflyNursery nursery = (IButterflyNursery) tile;
					if (nursery.canNurse(entity.getButterfly())) {
						nursery.setCaterpillar(entity.getButterfly().spawnCaterpillar(entity.worldObj, nursery));
						//				Log.finest("A butterfly '%s' laid an egg at %s/%s/%s.", entity.getButterfly().getIdent(), rest.posX, rest.posY, rest.posZ);
						if (entity.getRNG().nextFloat() < 1.0f / entity.getButterfly().getGenome().getFertility()) {
							entity.setHealth(0);
						}
					}
				}
				setHasInteracted();
				entity.cooldownEgg = EntityButterfly.COOLDOWNS;
			}
		}
	}
	
    @Override
	public boolean shouldExecute(){
    	if(!super.shouldExecute()){
    		return false;
    	}
    	if(entity.getButterfly().getMate() == null){
	        if (!entity.canMate()){
	            return false;
	        }else{
	            targetMate = getNearbyMate();
	            return targetMate != null;
	        }
    	}
    	return true;
    }

    @Override
	public boolean continueExecuting(){
    	if(!super.continueExecuting()){
    		return false;
    	}
    	if(entity.getButterfly().getMate() == null){
    		return targetMate.isEntityAlive() && targetMate.canMate();
    	}
    	return true;
    }

    @Override
	public void resetTask(){
    	super.resetTask();
    	
        targetMate = null;
    }

    private EntityButterfly getNearbyMate(){
        float f = 8.0F;
        List<EntityButterfly> nextButterflys = entity.worldObj.getEntitiesWithinAABB(EntityButterfly.class, this.entity.getEntityBoundingBox().expand(f, f, f));
        double d0 = Double.MAX_VALUE;
        EntityButterfly nextButterfly = null;

        for (EntityButterfly butterfly : nextButterflys){
            if (this.entity.canMateWith(butterfly) && this.entity.getDistanceSqToEntity(butterfly) < d0){
                nextButterfly = butterfly;
                d0 = this.entity.getDistanceSqToEntity(butterfly);
            }
        }

        return nextButterfly;
    }
}
