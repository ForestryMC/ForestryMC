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

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.EnumErrorCode;
import forestry.core.genetics.Allele;
import forestry.core.genetics.EffectData;
import forestry.core.vect.Vect;
import forestry.plugins.PluginApiculture;
import net.minecraft.util.AxisAlignedBB;

public abstract class AlleleEffectThrottled extends Allele implements IAlleleBeeEffect {

	private boolean isCombinable = false;
	private final int throttle;
	private boolean requiresWorkingQueen = false;

	public AlleleEffectThrottled(String uid, String name, boolean isDominant, int throttle, boolean requiresWorking, boolean isCombinable) {
		super(uid, isDominant);
		this.name = "apiculture.effect." + name;
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
		if (storedData instanceof EffectData)
			return storedData;

		return new EffectData(1, 0);
	}

	public boolean isHalted(IEffectData storedData, IBeeHousing housing) {

		if (requiresWorkingQueen && housing.getErrorState() != EnumErrorCode.OK)
			return true;

		int throt = storedData.getInteger(0);
		throt++;
		storedData.setInteger(0, throt);

		if (throt < getThrottle())
			return true;

		// Reset since we are done throttling.
		storedData.setInteger(0, 0);
		return false;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		int[] area = getModifiedArea(genome, housing);

		PluginApiculture.proxy.addBeeHiveFX("particles/swarm_bee", housing.getWorld(), housing.getXCoord(), housing.getYCoord(),
				housing.getZCoord(), genome.getPrimary().getIconColour(0), area[0], area[1], area[2]);
		return storedData;
	}

	protected int[] getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
		int[] area = genome.getTerritory();
		area[0] *= housing.getTerritoryModifier(genome, 1f) * 3;
		area[1] *= housing.getTerritoryModifier(genome, 1f) * 3;
		area[2] *= housing.getTerritoryModifier(genome, 1f) * 3;

		if (area[0] < 1)
			area[0] = 1;
		if (area[1] < 1)
			area[1] = 1;
		if (area[2] < 1)
			area[2] = 1;

		return area;
	}

	protected AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing, float modifier) {
		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(modifier);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		// Radioactivity hurts players and mobs
		Vect min = new Vect(housing.getXCoord() + offset.x, housing.getYCoord() + offset.y, housing.getZCoord() + offset.z);
		Vect max = new Vect(housing.getXCoord() + offset.x + area.x, housing.getYCoord() + offset.y + area.y, housing.getZCoord() + offset.z + area.z);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}
}
