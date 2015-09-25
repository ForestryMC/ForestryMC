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
package forestry.apiculture.genetics.alleles;

import net.minecraft.util.AxisAlignedBB;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.BeeHousingModifier;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.core.utils.vect.MutableVect;
import forestry.core.utils.vect.Vect;
import forestry.plugins.PluginApiculture;

public class AlleleEffectNone extends AlleleCategorized implements IAlleleBeeEffect {

	public AlleleEffectNone(String valueName, boolean isDominant) {
		super("forestry", "effect", valueName, isDominant);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		return null;
	}

	@Override
	public boolean isCombinable() {
		return false;
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		PluginApiculture.proxy.addBeeHiveFX("particles/swarm_bee", housing.getWorld(), housing.getCoordinates(), genome.getPrimary().getIconColour(0));
		return storedData;
	}

	protected Vect getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = new BeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1f);

		MutableVect area = new MutableVect(genome.getTerritory());
		area.multiply(territoryModifier);

		if (area.x < 1) {
			area.x = 1;
		}
		if (area.y < 1) {
			area.y = 1;
		}
		if (area.z < 1) {
			area.z = 1;
		}

		return new Vect(area);
	}

	protected AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = new BeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1.0f);

		MutableVect area = new MutableVect(genome.getTerritory());
		area.multiply(territoryModifier);
		Vect offset = new Vect(area).multiply(-1 / 2.0f);

		Vect min = new Vect(housing.getCoordinates()).add(offset);
		Vect max = min.add(area);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

}
