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
import forestry.core.utils.GeneticsUtil;

public class AIButterflyPollinate extends AIButterflyInteract {

	public AIButterflyPollinate(EntityButterfly entity) {
		super(entity);
	}

	/**
	 * Should pollinate?
	 */
	@Override
	protected boolean canInteract() {
		if (entity.cooldownPollination > 0) {
			return false;
		}

		IPollinatable pollinatable = GeneticsUtil.getOrCreatePollinatable(null, entity.worldObj, rest.posX, rest.posY, rest.posZ);
		if (pollinatable == null) {
			return false;
		}

		if (!entity.getButterfly().getGenome().getFlowerProvider().isAcceptedPollinatable(entity.worldObj, pollinatable)) {
			return false;
		}

		return entity.getPollen() == null || pollinatable.canMateWith(entity.getPollen());
	}

	@Override
	public void updateTask() {
		if (continueExecuting()) {
			IPollinatable pollinatable = (IPollinatable) entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
			if (entity.getPollen() == null) {
				entity.setPollen(pollinatable.getPollen());
				//				Proxies.log.finest("A butterfly '%s' grabbed a pollen '%s' at %s/%s/%s.", entity.getButterfly().getIdent(), entity.getPollen().getIdent(), rest.posX, rest.posY, rest.posZ);
			} else if (pollinatable.canMateWith(entity.getPollen())) {
				pollinatable.mateWith(entity.getPollen());
				//				Proxies.log.finest("A butterfly '%s' unloaded pollen '%s' at %s/%s/%s.", entity.getButterfly().getIdent(), entity.getPollen().getIdent(), rest.posX, rest.posY, rest.posZ);
				entity.setPollen(null);
			}
			setHasInteracted();
			entity.cooldownPollination = EntityButterfly.COOLDOWNS;
		}
	}

}
