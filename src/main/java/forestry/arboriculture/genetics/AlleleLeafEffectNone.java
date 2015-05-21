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
package forestry.arboriculture.genetics;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.core.vect.Vect;

public class AlleleLeafEffectNone extends AlleleCategorized implements IAlleleLeafEffect {

	private static final int[] DEFAULT_EFFECT_AREA = new int[]{12, 12, 12};

	public AlleleLeafEffectNone() {
		super("forestry", "leaves", "none", true);
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
	public String getUnlocalizedName() {
		return "for.arboriculture.effect.none";
	}

	@Override
	public IEffectData doEffect(ITreeGenome genome, IEffectData storedData, World world, int x, int y, int z) {
		return storedData;
	}

	protected static AxisAlignedBB getBounding(int x, int y, int z, float modifier) {
		int[] areaAr = DEFAULT_EFFECT_AREA;
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(modifier);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		Vect min = new Vect(x + offset.x, y + offset.y, y + offset.z);
		Vect max = new Vect(x + offset.x + area.x, y + offset.y + area.y, y + offset.z + area.z);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

}
