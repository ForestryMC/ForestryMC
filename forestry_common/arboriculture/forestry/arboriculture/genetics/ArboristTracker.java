/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import net.minecraft.entity.player.EntityPlayer;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.IArboristTracker;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.core.genetics.BreedingTracker;
import forestry.plugins.PluginArboriculture;

public class ArboristTracker extends BreedingTracker implements IArboristTracker {

	public ArboristTracker(String s) {
		this(s, null);
	}

	public ArboristTracker(String s, GameProfile player) {
		super(s, player);
	}

	@Override
	protected IBreedingTracker getCommonTracker(EntityPlayer player) {
		return PluginArboriculture.treeInterface.getBreedingTracker(player.worldObj, null);
	}

	@Override
	protected String getPacketTag() {
		return TreeHelper.UID;
	}

	@Override
	public void registerPickup(IIndividual individual) {
	}

}
