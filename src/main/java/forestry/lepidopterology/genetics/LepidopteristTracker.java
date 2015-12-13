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
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.core.genetics.BreedingTracker;

public class LepidopteristTracker extends BreedingTracker implements ILepidopteristTracker {

	/** Required for creation from map storage */
	public LepidopteristTracker(String s) {
		this(s, null, null);
	}

	public LepidopteristTracker(String s, GameProfile player, World world) {
		super(s, player, world);
	}

	@Override
	protected IBreedingTracker getBreedingTracker(EntityPlayer player) {
		return ButterflyManager.butterflyRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
	}

	@Override
	protected String speciesRootUID() {
		return ButterflyHelper.UID;
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
