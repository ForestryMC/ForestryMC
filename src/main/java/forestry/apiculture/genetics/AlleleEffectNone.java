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

import forestry.core.utils.StringUtil;
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
		return StringUtil.localize("apiculture.effect.none");
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
