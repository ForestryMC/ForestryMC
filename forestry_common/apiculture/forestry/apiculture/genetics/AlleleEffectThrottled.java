/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.util.AxisAlignedBB;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.EnumErrorCode;
import forestry.core.genetics.Allele;
import forestry.core.genetics.EffectData;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Vect;
import forestry.plugins.PluginApiculture;

public abstract class AlleleEffectThrottled extends Allele implements IAlleleBeeEffect {

	private String name;
	private boolean isCombinable = false;
	private int throttle;
	private boolean requiresWorkingQueen = false;

	public AlleleEffectThrottled(String uid, String name, boolean isDominant, int throttle, boolean requiresWorking, boolean isCombinable) {
		super(uid, isDominant);
		this.name = "apiculture.effect." + name;
		this.throttle = throttle;
		this.isCombinable = isCombinable;
		this.requiresWorkingQueen = requiresWorking;
	}

	@Override
	public String getName() {
		return StringUtil.localize(name);
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

		if (requiresWorkingQueen && housing.getErrorOrdinal() != EnumErrorCode.OK.ordinal())
			return true;

		int throttle = storedData.getInteger(0);
		throttle++;
		storedData.setInteger(0, throttle);

		if (throttle < getThrottle())
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

		return AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x, max.y, max.z);
	}
}
