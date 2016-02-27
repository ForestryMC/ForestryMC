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
package forestry.lepidopterology.genetics;

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyChromosome;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyTracker;
import forestry.core.genetics.BreedingTracker;

public class ButterflyTracker extends BreedingTracker<ButterflyChromosome> implements IButterflyTracker {

	/** Required for creation from map storage */
	public ButterflyTracker(String s) {
		super(s);
	}

	@Override
	protected IButterflyTracker getBreedingTracker(EntityPlayer player) {
		return ButterflyManager.butterflyRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
	}

	@Override
	protected String speciesRootUID() {
		return ButterflyRoot.UID;
	}

	@Override
	public void registerPickup(IIndividual individual) {
	}

	@Override
	public void registerCatch(IButterfly butterfly) {
		registerSpecies(butterfly.getGenome().getPrimary());
		registerSpecies(butterfly.getGenome().getSecondary());
	}

}
