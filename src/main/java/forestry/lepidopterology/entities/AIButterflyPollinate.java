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
import forestry.arboriculture.genetics.pollination.FakePollinatable;
import forestry.arboriculture.genetics.pollination.ICheckPollinatable;
import forestry.core.utils.GeneticsUtil;
import forestry.plugins.PluginLepidopterology;

public class AIButterflyPollinate extends AIButterflyInteract {

	public AIButterflyPollinate(EntityButterfly entity) {
		super(entity);
	}

	/**
	 * Should pollinate?
	 */
	@Override
	protected boolean canInteract() {
		if (entity.cooldownPollination > 0 || !PluginLepidopterology.isPollinationAllowed()) {
			return false;
		}

		ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(entity.worldObj, rest.posX, rest.posY, rest.posZ);
		if (checkPollinatable == null) {
			return false;
		}

		if (!entity.getButterfly().getGenome().getFlowerProvider().isAcceptedPollinatable(entity.worldObj, new FakePollinatable(checkPollinatable))) {
			return false;
		}

		return entity.getPollen() == null || checkPollinatable.canMateWith(entity.getPollen());
	}

	@Override
	public void updateTask() {
		if (continueExecuting()) {
			ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(entity.worldObj, rest.posX, rest.posY, rest.posZ);
			if (checkPollinatable != null) {
				if (entity.getPollen() == null) {
					entity.setPollen(checkPollinatable.getPollen());
					//					Log.finest("A butterfly '%s' grabbed a pollen '%s' at %s/%s/%s.", entity.getButterfly().getIdent(), entity.getPollen().getIdent(), rest.posX, rest.posY, rest.posZ);
				} else if (checkPollinatable.canMateWith(entity.getPollen())) {
					IPollinatable realPollinatable = GeneticsUtil.getOrCreatePollinatable(null, entity.worldObj, rest.posX, rest.posY, rest.posZ);
					if (realPollinatable != null) {
						realPollinatable.mateWith(entity.getPollen());
						//						Log.finest("A butterfly '%s' unloaded pollen '%s' at %s/%s/%s.", entity.getButterfly().getIdent(), entity.getPollen().getIdent(), rest.posX, rest.posY, rest.posZ);
						entity.setPollen(null);
					}
				}
			}
			setHasInteracted();
			entity.cooldownPollination = EntityButterfly.COOLDOWNS;
		}
	}

}
