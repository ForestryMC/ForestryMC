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

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.core.genetics.BreedingTracker;
import forestry.plugins.PluginLepidopterology;

public class LepidopteristTracker extends BreedingTracker implements ILepidopteristTracker {

	public LepidopteristTracker(String s) {
		this(s, null);
	}

	public LepidopteristTracker(String s, GameProfile player) {
		super(s, player);
	}

	@Override
	protected IBreedingTracker getCommonTracker(EntityPlayer player) {
		return PluginLepidopterology.butterflyInterface.getBreedingTracker(player.worldObj, null);
	}

	@Override
	protected String getPacketTag() {
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
