/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
