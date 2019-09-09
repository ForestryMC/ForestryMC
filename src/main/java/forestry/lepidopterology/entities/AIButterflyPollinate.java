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

import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.core.utils.GeneticsUtil;
import forestry.lepidopterology.ModuleLepidopterology;

public class AIButterflyPollinate extends AIButterflyInteract {

	public AIButterflyPollinate(EntityButterfly entity) {
		super(entity);
	}

	/**
	 * Should pollinate?
	 */
	@Override
	protected boolean canInteract() {
		if (entity.cooldownPollination > 0 || !ModuleLepidopterology.isPollinationAllowed()) {
			return false;
		}

		if (rest == null) {
			return false;
		}

		ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(entity.world, rest);
		if (checkPollinatable == null) {
			return false;
		}

		if (!entity.getButterfly().getGenome().getActiveAllele(ButterflyChromosomes.FLOWER_PROVIDER).getProvider().isAcceptedPollinatable(entity.world, checkPollinatable)) {
			return false;
		}

		return entity.getPollen() == null || checkPollinatable.canMateWith(entity.getPollen());
	}

	@Override
	public void tick() {
		if (shouldContinueExecuting() && rest != null) {
			ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(entity.world, rest);
			if (checkPollinatable != null) {
				if (entity.getPollen() == null) {
					entity.setPollen(checkPollinatable.getPollen());
					//					Log.finest("A butterfly '%s' grabbed a pollen '%s' at %s/%s/%s.", entity.getButterfly().getIdent(), entity.getPollen().getIdent(), rest.posX, rest.posY, rest.posZ);
				} else if (checkPollinatable.canMateWith(entity.getPollen())) {
					IPollinatable realPollinatable = GeneticsUtil.getOrCreatePollinatable(null, entity.world, rest, false);
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
