/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.lepidopterology.genetics;

import forestry.api.lepidopterology.IButterflyNursery;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.mutation.IMutation;
import net.minecraft.world.World;

public interface IButterflyMutation extends IMutation {
    float getChance(
            World world,
            IButterflyNursery housing,
            IAllele allele0,
            IAllele allele1,
            IGenome genome0,
            IGenome genome1
    );
}
