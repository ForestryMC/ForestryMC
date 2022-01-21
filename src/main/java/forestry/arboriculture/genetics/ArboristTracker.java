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
package forestry.arboriculture.genetics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.genetics.BreedingTracker;

import genetics.api.individual.IIndividual;

public class ArboristTracker extends BreedingTracker implements IArboristTracker {

	/**
	 * Required for creation from map storage
	 */
	public ArboristTracker() {
		super(ModuleArboriculture.treekeepingMode);
	}

	public ArboristTracker(CompoundTag tag) {
		super(ModuleArboriculture.treekeepingMode, tag);
	}

	@Override
	protected IBreedingTracker getBreedingTracker(Player player) {
		return TreeManager.treeRoot.getBreedingTracker(player.level, player.getGameProfile());
	}

	@Override
	protected String speciesRootUID() {
		return TreeRoot.UID;
	}

	@Override
	public void registerPickup(IIndividual individual) {
	}

}
