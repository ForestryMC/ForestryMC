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
package forestry.arboriculture.commands;

import java.util.Collection;

import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IBreedingTracker;
import forestry.core.commands.IStatsSaveHelper;

import genetics.utils.AlleleUtils;

public class TreeStatsSaveHelper implements IStatsSaveHelper {

	@Override
	public String getUnlocalizedSaveStatsString() {
		return "for.chat.command.forestry.tree.save.stats";
	}

	@Override
	public void addExtraInfo(Collection<String> statistics, IBreedingTracker breedingTracker) {
	}

	@Override
	public Collection<IAlleleTreeSpecies> getSpecies() {
		return AlleleUtils.filteredAlleles(TreeChromosomes.SPECIES);
	}

	@Override
	public String getFileSuffix() {
		return "trees";
	}

	@Override
	public IBreedingTracker getBreedingTracker(Level world, GameProfile gameProfile) {
		return TreeManager.treeRoot.getBreedingTracker(world, gameProfile);
	}

}
