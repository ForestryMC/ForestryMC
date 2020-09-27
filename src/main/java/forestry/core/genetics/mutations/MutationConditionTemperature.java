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
package forestry.core.genetics.mutations;

import forestry.api.climate.IClimateProvider;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IMutationCondition;
import forestry.api.genetics.alleles.AlleleManager;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class MutationConditionTemperature implements IMutationCondition {

    private final EnumTemperature minTemperature;
    private final EnumTemperature maxTemperature;

    public MutationConditionTemperature(EnumTemperature minTemperature, EnumTemperature maxTemperature) {
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    @Override
    public float getChance(
            World world,
            BlockPos pos,
            IAllele allele0,
            IAllele allele1,
            IGenome genome0,
            IGenome genome1,
            IClimateProvider climate
    ) {
        EnumTemperature biomeTemperature = climate.getTemperature();

        if (biomeTemperature.ordinal() < minTemperature.ordinal() ||
            biomeTemperature.ordinal() > maxTemperature.ordinal()) {
            return 0;
        }
        return 1;
    }

    @Override
    public ITextComponent getDescription() {
        ITextComponent minString = AlleleManager.climateHelper.toDisplay(minTemperature);

        if (minTemperature != maxTemperature) {
            ITextComponent maxString = AlleleManager.climateHelper.toDisplay(maxTemperature);
            return new TranslationTextComponent("for.mutation.condition.temperature.range", minString, maxString);
        } else {
            return new TranslationTextComponent("for.mutation.condition.temperature.single", minString);
        }
    }
}
