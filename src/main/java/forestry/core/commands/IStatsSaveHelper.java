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
package forestry.core.commands;

import java.util.Collection;

import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;

public interface IStatsSaveHelper {

	String getUnlocalizedSaveStatsString();

	void addExtraInfo(Collection<String> statistics, IBreedingTracker breedingTracker);

	Collection<IAlleleSpecies> getSpecies();

	String getFileSuffix();

	IBreedingTracker getBreedingTracker(World world, GameProfile gameProfile);
}
