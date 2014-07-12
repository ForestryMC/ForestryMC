/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.Allele;
import forestry.core.utils.Vect;

public class AlleleLeafEffectNone extends Allele implements IAlleleLeafEffect {

	int[] DEFAULT_EFFECT_AREA = new int[] { 12, 12, 12 };

	public AlleleLeafEffectNone(String uid) {
		super(uid, true);
	}

	@Override
	public boolean isCombinable() {
		return true;
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		return storedData;
	}

	@Override
	public String getName() {
		return "None";
	}

	@Override
	public IEffectData doEffect(ITreeGenome genome, IEffectData storedData, World world, int x, int y, int z) {
		return storedData;
	}

	protected AxisAlignedBB getBounding(int x, int y, int z, float modifier) {
		int[] areaAr = DEFAULT_EFFECT_AREA;
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(modifier);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		Vect min = new Vect(x + offset.x, y + offset.y, y + offset.z);
		Vect max = new Vect(x + offset.x + area.x, y + offset.y + area.y, y + offset.z + area.z);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

}
