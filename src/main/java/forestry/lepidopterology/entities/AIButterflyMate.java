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

import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.core.utils.GeneticsUtil;

import net.minecraft.tileentity.TileEntity;

public class AIButterflyMate extends AIButterflyInteract {

	public AIButterflyMate(EntityButterfly entity) {
		super(entity);
	}

	/**
	 * Should lay egg?
	 * @return
	 */
	@Override
	protected boolean canInteract() {
		if (entity.cooldownEgg > 0)
			return false;

		if (entity.getButterfly().getMate() == null)
			return false;

		TileEntity tile = entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
		IButterflyNursery nursery = null;

		if (tile instanceof IButterflyNursery)
			nursery = (IButterflyNursery) tile;
		else {
			IPollinatable pollinatable = GeneticsUtil.getOrCreatePollinatable(null, entity.worldObj, rest.posX, rest.posY, rest.posZ);
			if (pollinatable instanceof IButterflyNursery) {
				nursery = (IButterflyNursery) pollinatable;
			}
		}

		if (nursery == null)
			return false;

		return nursery.canNurse(entity.getButterfly());
	}

	@Override
	public void updateTask() {
		if (continueExecuting()) {
			IButterflyNursery tile = (IButterflyNursery) entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
			if (tile.canNurse(entity.getButterfly())) {
				tile.setCaterpillar(entity.getButterfly().spawnCaterpillar(tile));
//				Proxies.log.finest("A butterfly '%s' laid an egg at %s/%s/%s.", entity.getButterfly().getIdent(), rest.posX, rest.posY, rest.posZ);
				if (entity.getRNG().nextFloat() < 1.0f / entity.getButterfly().getGenome().getFertility())
					entity.setHealth(0);
			}
			setHasInteracted();
			entity.cooldownEgg = EntityButterfly.COOLDOWNS;
		}
	}

}
