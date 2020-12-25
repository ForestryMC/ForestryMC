/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.genetics.mutations;

import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.DayMonth;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Calendar;

public class MutationConditionTimeLimited implements IMutationCondition {

    private final DayMonth start;
    private final DayMonth end;

    public MutationConditionTimeLimited(int startMonth, int startDay, int endMonth, int endDay) {
        this.start = new DayMonth(startDay, startMonth);
        this.end = new DayMonth(endDay, endMonth);
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
        DayMonth now = new DayMonth();

        // If we are equal to start day, return 1.
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == start.day &&
            Calendar.getInstance().get(Calendar.MONTH) + 1 == start.month) {
            return 1;
        }

        // Equal to end date, return 1
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == end.day &&
            Calendar.getInstance().get(Calendar.MONTH) + 1 == end.month) {
            return 1;
        }

        // Still a chance we are in between
        if (now.between(start, end)) {
            return 1;
        }

        // Now we finally failed.
        return 0;
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("for.mutation.condition.date", start.toString(), end.toString());
    }
}
