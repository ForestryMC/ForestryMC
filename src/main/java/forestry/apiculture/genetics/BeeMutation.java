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

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.IAllele;
import forestry.core.genetics.mutations.Mutation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeeMutation extends Mutation implements IBeeMutation, IBeeMutationBuilder {

	public BeeMutation(IAlleleBeeSpecies bee0, IAlleleBeeSpecies bee1, IAllele[] result, int chance) {
		super(bee0, bee1, result, chance);
	}

	@Override
	public IBeeMutation build() {
		return this;
	}

	@Override
	public IBeeRoot getRoot() {
		return BeeManager.beeRoot;
	}

	@Override
	public float getChance(IBeeHousing housing, IAlleleBeeSpecies allele0, IAlleleBeeSpecies allele1, IBeeGenome genome0, IBeeGenome genome1) {
		World world = housing.getWorldObj();
		BlockPos housingPos = housing.getCoordinates();

		float processedChance = super.getChance(world, housingPos, allele0, allele1, genome0, genome1, housing);
		if (processedChance <= 0) {
			return 0;
		}

		IBeeModifier beeHousingModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		IBeeModifier beeModeModifier = BeeManager.beeRoot.getBeekeepingMode(world).getBeeModifier();

		processedChance *= beeHousingModifier.getMutationModifier(genome0, genome1, processedChance);
		processedChance *= beeModeModifier.getMutationModifier(genome0, genome1, processedChance);

		return processedChance;
	}

}
