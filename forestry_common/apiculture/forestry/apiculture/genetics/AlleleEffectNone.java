/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.world.World;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.Allele;
import forestry.plugins.PluginApiculture;

public class AlleleEffectNone extends Allele implements IAlleleBeeEffect {

	public AlleleEffectNone(String uid) {
		super(uid, true);
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
		return doEffect(genome, storedData, housing.getWorld(), housing.getBiomeId(), housing.getXCoord(), housing.getYCoord(), housing.getZCoord());
	}

	protected IEffectData doEffect(IBeeGenome genome, IEffectData storedData, World world, int biomeid, int x, int y, int z) {
		return storedData;
	}

	@Override
	public String getName() {
		return "None";
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		int[] area = genome.getTerritory();
		area[0] *= housing.getTerritoryModifier(genome, 1f);
		area[1] *= housing.getTerritoryModifier(genome, 1f);
		area[2] *= housing.getTerritoryModifier(genome, 1f);

		if (area[0] < 1)
			area[0] = 1;
		if (area[1] < 1)
			area[1] = 1;
		if (area[2] < 1)
			area[2] = 1;

		PluginApiculture.proxy.addBeeHiveFX("particles/swarm_bee", housing.getWorld(), housing.getXCoord(), housing.getYCoord(),
				housing.getZCoord(), genome.getPrimary().getIconColour(0), area[0], area[1], area[2]);
		return storedData;
	}

}
