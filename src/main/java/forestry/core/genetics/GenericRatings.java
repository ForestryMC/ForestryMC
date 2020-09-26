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
package forestry.core.genetics;

import forestry.core.utils.StringUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GenericRatings {

    public static ITextComponent rateMetabolism(int metabolism) {
        if (metabolism >= 19) {
            return new TranslationTextComponent("forestry.allele.highest");
        } else if (metabolism >= 16) {
            return new TranslationTextComponent("forestry.allele.higher");
        } else if (metabolism >= 13) {
            return new TranslationTextComponent("forestry.allele.high");
        } else if (metabolism >= 10) {
            return new TranslationTextComponent("forestry.allele.average");
        } else if (metabolism >= 7) {
            return new TranslationTextComponent("forestry.allele.slow");
        } else if (metabolism >= 4) {
            return new TranslationTextComponent("forestry.allele.slower");
        } else {
            return new TranslationTextComponent("forestry.allele.slowest");
        }
    }

    public static ITextComponent rateActivityTime(boolean neverSleeps, boolean naturalNocturnal) {
        ITextComponent active = naturalNocturnal
                ? new TranslationTextComponent("for.gui.nocturnal")
                : new TranslationTextComponent("for.gui.diurnal");
        if (neverSleeps) {
            active = new StringTextComponent(StringUtil.append(
                    ", ",
                    active.getString(),
                    naturalNocturnal
                            ? new TranslationTextComponent("for.gui.diurnal").getString()
                            : new TranslationTextComponent("for.gui.nocturnal").getString()
            ));
        }

        return active;
    }
}
