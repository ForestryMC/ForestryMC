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
package forestry.apiculture.genetics;

import net.minecraft.util.AxisAlignedBB;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.BeeHousingModifier;
import forestry.core.genetics.EffectData;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.core.vect.IVect;
import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.plugins.PluginApiculture;

public abstract class AlleleEffectThrottled extends AlleleCategorized implements IAlleleBeeEffect {

	private boolean isCombinable = false;
	private final int throttle;
	private boolean requiresWorkingQueen = false;

	public AlleleEffectThrottled(String name, boolean isDominant, int throttle, boolean requiresWorking, boolean isCombinable) {
		super("forestry", "effect", name, isDominant);
		this.throttle = throttle;
		this.isCombinable = isCombinable;
		this.requiresWorkingQueen = requiresWorking;
	}

	public int getThrottle() {
		return throttle;
	}

	@Override
	public boolean isCombinable() {
		return isCombinable;
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (storedData instanceof EffectData) {
			return storedData;
		}

		return new EffectData(1, 0);
	}

	public boolean isHalted(IEffectData storedData, IBeeHousing housing) {

		if (requiresWorkingQueen && housing.getErrorLogic().hasErrors()) {
			return true;
		}

		int throt = storedData.getInteger(0);
		throt++;
		storedData.setInteger(0, throt);

		if (throt < getThrottle()) {
			return true;
		}

		// Reset since we are done throttling.
		storedData.setInteger(0, 0);
		return false;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		IVect area = getModifiedArea(genome, housing);

		PluginApiculture.proxy.addBeeHiveFX("particles/swarm_bee", housing.getWorld(), housing.getCoordinates(), genome.getPrimary().getIconColour(0), area);
		return storedData;
	}

	protected IVect getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = new BeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1f);

		MutableVect area = new MutableVect(genome.getTerritory());
		area.multiply(territoryModifier * 3);

		if (area.x < 1) {
			area.x = 1;
		}
		if (area.y < 1) {
			area.y = 1;
		}
		if (area.z < 1) {
			area.z = 1;
		}

		return area;
	}

	protected AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing, float modifier) {
		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(modifier);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		Vect min = new Vect(housing.getCoordinates()).add(offset);
		Vect max = min.add(area);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}
}
