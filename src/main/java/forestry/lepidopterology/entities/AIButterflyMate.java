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
import forestry.plugins.PluginLepidopterology;

public class AIButterflyMate extends AIButterflyInteract {

	public AIButterflyMate(EntityButterfly entity) {
		super(entity);
	}

	/**
	 * Should lay egg?
	 */
	@Override
	protected boolean canInteract() {
		if (entity.cooldownEgg > 0) {
			return false;
		}

		if (entity.getButterfly().getMate() == null) {
			return false;
		}

		if (entity.worldObj.countEntities(EntityButterfly.class) > PluginLepidopterology.spawnConstraint) {
			return false;
		}

		return GeneticsUtil.canNurse(entity.getButterfly(), entity.worldObj, rest.posX, rest.posY, rest.posZ);
	}

	@Override
	public void updateTask() {
		if (continueExecuting()) {
			IPollinatable tile = GeneticsUtil.getOrCreatePollinatable(null, entity.worldObj, rest.posX, rest.posY, rest.posZ);
			if (tile instanceof IButterflyNursery) {
				IButterflyNursery nursery = (IButterflyNursery) tile;
				if (nursery.canNurse(entity.getButterfly())) {
					nursery.setCaterpillar(entity.getButterfly().spawnCaterpillar(nursery));
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
