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
package forestry.apiculture.commands;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.core.commands.IStatsSaveHelper;
import forestry.core.utils.StringUtil;

public class BeeStatsSaveHelper implements IStatsSaveHelper {

	@Override
	public String getUnlocalizedSaveStatsString() {
		return "for.chat.command.forestry.bee.save.stats";
	}

	@Override
	public void addExtraInfo(Collection<String> statistics, IBreedingTracker breedingTracker) {
		IApiaristTracker tracker = (IApiaristTracker) breedingTracker;
		String discoveredLine = StatCollector.translateToLocal("for.chat.command.forestry.stats.save.key.discovered") + ":";
		statistics.add(discoveredLine);
		statistics.add(StringUtil.line(discoveredLine.length()));

		String queen = StatCollector.translateToLocal("for.bees.grammar.queen.type");
		String princess = StatCollector.translateToLocal("for.bees.grammar.princess.type");
		String drone = StatCollector.translateToLocal("for.bees.grammar.drone.type");
		statistics.add(queen + ":\t\t" + tracker.getQueenCount());
		statistics.add(princess + ":\t" + tracker.getPrincessCount());
		statistics.add(drone + ":\t\t" + tracker.getDroneCount());
		statistics.add("");
	}

	@Override
	public Collection<IAlleleSpecies> getSpecies() {
		Collection<IAlleleSpecies> species = new ArrayList<>();
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleBeeSpecies) {
				species.add((IAlleleBeeSpecies) allele);
			}
		}
		return species;
	}

	@Override
	public String getFileSuffix() {
		return "bees";
	}

	@Override
	public IBreedingTracker getBreedingTracker(World world, GameProfile gameProfile) {
		return BeeManager.beeRoot.getBreedingTracker(world, gameProfile);
	}

}
