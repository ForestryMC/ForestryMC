/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
