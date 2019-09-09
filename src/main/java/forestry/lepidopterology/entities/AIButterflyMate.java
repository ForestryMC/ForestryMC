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
import java.util.List;

import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.core.utils.GeneticsUtil;

public class AIButterflyMate extends AIButterflyInteract {
	@Nullable
	private EntityButterfly targetMate;

	public AIButterflyMate(EntityButterfly entity) {
		super(entity);
	}

	@Override
	protected boolean canInteract() {
		if (entity.getButterfly().getMate() == null && entity.canMate()) {
			return true;
		}
		if (entity.cooldownEgg > 0) {
			return false;
		}

		if (entity.getButterfly().getMate() == null) {
			return false;
		}

		//TODO I think this needs a server world. Check what vanilla spawn cap code does?
		if (false) {//entity.world.countEntities(EntityButterfly.class) > ModuleLepidopterology.spawnConstraint) {
			return false;
		}

		return rest != null && GeneticsUtil.canNurse(entity.getButterfly(), entity.world, rest);
	}

	@Override
	public void tick() {
		if (shouldContinueExecuting()) {
			if (entity.getButterfly().getMate() == null && targetMate != null) {
				if (entity.cooldownMate <= 0 && entity.getDistance(targetMate) < 9.0D) {
					entity.getButterfly().mate(targetMate.getButterfly().getGenome());
					targetMate.getButterfly().mate(entity.getButterfly().getGenome());
					entity.cooldownMate = EntityButterfly.COOLDOWNS;
				}
			} else if (rest != null) {
				IButterflyNursery nursery = GeneticsUtil.getOrCreateNursery(null, entity.world, rest, false);
				if (nursery != null) {
					if (nursery.canNurse(entity.getButterfly())) {
						nursery.setCaterpillar(entity.getButterfly().spawnCaterpillar(entity.world, nursery));
						//Log.finest("A butterfly '%s' laid an egg at %s/%s/%s.", entity.getButterfly().getIdent(), rest.posX, rest.posY, rest.posZ);
						if (entity.getRNG().nextFloat() < 1.0f / entity.getButterfly().getGenome().getActiveValue(ButterflyChromosomes.FERTILITY)) {
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
	public boolean shouldExecute() {
		if (!super.shouldExecute()) {
			return false;
		}
		if (entity.getButterfly().getMate() == null) {
			if (!entity.canMate()) {
				return false;
			} else {
				targetMate = getNearbyMate();
				return targetMate != null;
			}
		}
		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (!super.shouldContinueExecuting()) {
			return false;
		}
		if (entity.getButterfly().getMate() == null) {
			return targetMate != null && targetMate.isAlive() && targetMate.canMate();
		}
		return true;
	}

	@Override
	public void resetTask() {
		super.resetTask();

		targetMate = null;
	}

	@Nullable
	private EntityButterfly getNearbyMate() {
		float f = 8.0F;
		List<EntityButterfly> nextButterflys = entity.world.getEntitiesWithinAABB(EntityButterfly.class, this.entity.getBoundingBox().expand(f, f, f));
		double d0 = Double.MAX_VALUE;
		EntityButterfly nextButterfly = null;

		for (EntityButterfly butterfly : nextButterflys) {
			if (this.entity.canMateWith(butterfly) && this.entity.getDistance(butterfly) < d0) {
				nextButterfly = butterfly;
				d0 = this.entity.getDistance(butterfly);
			}
		}

		return nextButterfly;
	}
}
